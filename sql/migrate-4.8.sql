\set ON_ERROR_STOP

SET SESSION AUTHORIZATION 'tms';

UPDATE iris.system_attribute SET value = '4.8.0'
	WHERE name = 'database_version';

CREATE TABLE iris._gate_arm (
	name VARCHAR(10) PRIMARY KEY,
	geo_loc VARCHAR(20) REFERENCES iris.geo_loc,
	notes VARCHAR(64) NOT NULL,
	camera VARCHAR(10) REFERENCES iris._camera,
	approach VARCHAR(10) REFERENCES iris._camera,
	dms VARCHAR(10) REFERENCES iris._dms,
	open_msg VARCHAR(20) REFERENCES iris.quick_message,
	closed_msg VARCHAR(20) REFERENCES iris.quick_message
);

ALTER TABLE iris._gate_arm ADD CONSTRAINT _gate_arm_fkey
	FOREIGN KEY (name) REFERENCES iris._device_io(name) ON DELETE CASCADE;

CREATE VIEW iris.gate_arm AS SELECT
	_gate_arm.name, geo_loc, controller, pin, notes, camera, approach,
	dms, open_msg, closed_msg
	FROM iris._gate_arm JOIN iris._device_io
	ON _gate_arm.name = _device_io.name;

CREATE FUNCTION iris.gate_arm_update() RETURNS TRIGGER AS $gate_arm_update$
BEGIN
    IF TG_OP = 'INSERT' THEN
        INSERT INTO iris._device_io VALUES (NEW.name, NEW.controller, NEW.pin);
        INSERT INTO iris._gate_arm VALUES (NEW.name, NEW.geo_loc, NEW.notes,
            NEW.camera, NEW.approach, NEW.dms, NEW.open_msg, NEW.closed_msg);
        RETURN NEW;
    ELSIF TG_OP = 'UPDATE' THEN
	UPDATE iris._device_io SET controller = NEW.controller, pin = NEW.pin
	WHERE name = OLD.name;
        UPDATE iris._gate_arm SET geo_loc = NEW.geo_loc, notes = NEW.notes,
            camera = NEW.camera, approach = NEW.approach, dms = NEW.dms,
            open_msg = NEW.open_msg, closed_msg = NEW.closed_msg
	WHERE name = OLD.name;
        RETURN NEW;
    ELSIF TG_OP = 'DELETE' THEN
        DELETE FROM iris._device_io WHERE name = OLD.name;
        IF FOUND THEN
            RETURN OLD;
        ELSE
            RETURN NULL;
	END IF;
    END IF;
    RETURN NEW;
END;
$gate_arm_update$ LANGUAGE plpgsql;

CREATE TRIGGER gate_arm_update_trig
    INSTEAD OF INSERT OR UPDATE OR DELETE ON iris.gate_arm
    FOR EACH ROW EXECUTE PROCEDURE iris.gate_arm_update();

CREATE VIEW gate_arm_view AS
	SELECT g.name, g.notes, g.geo_loc,
	l.roadway, l.road_dir, l.cross_mod, l.cross_street, l.cross_dir,
	l.lat, l.lon,
	g.controller, g.pin, ctr.comm_link, ctr.drop_id, ctr.active,
	g.camera, g.approach, g.dms, g.open_msg, g.closed_msg
	FROM iris.gate_arm g
	LEFT JOIN geo_loc_view l ON g.geo_loc = l.name
	LEFT JOIN iris.controller ctr ON g.controller = ctr.name;
GRANT SELECT ON gate_arm_view TO PUBLIC;

DROP VIEW controller_report;
DROP VIEW controller_device_view;
DROP VIEW iris.controller_device;

CREATE VIEW iris.controller_gate_arm AS
	SELECT dio.name, dio.controller, dio.pin, g.geo_loc
	FROM iris._device_io dio
	JOIN iris.gate_arm g ON dio.name = g.name;

CREATE VIEW iris.controller_device AS
	SELECT * FROM iris.controller_dms UNION ALL
	SELECT * FROM iris.controller_lane_marking UNION ALL
	SELECT * FROM iris.controller_weather_sensor UNION ALL
	SELECT * FROM iris.controller_lcs UNION ALL
	SELECT * FROM iris.controller_meter UNION ALL
	SELECT * FROM iris.controller_warning_sign UNION ALL
	SELECT * FROM iris.controller_camera UNION ALL
	SELECT * FROM iris.controller_gate_arm;

CREATE VIEW controller_device_view AS
	SELECT d.name, d.controller, d.pin, d.geo_loc,
	trim(l.roadway || ' ' || l.road_dir) AS corridor,
	trim(trim(' @' FROM l.cross_mod || ' ' || l.cross_street)
		|| ' ' || l.cross_dir) AS cross_loc
	FROM iris.controller_device d
	JOIN geo_loc_view l ON d.geo_loc = l.name;
GRANT SELECT ON controller_device_view TO PUBLIC;

CREATE VIEW controller_report AS
	SELECT c.name, c.comm_link, c.drop_id, cab.mile, cab.geo_loc,
	trim(l.roadway || ' ' || l.road_dir) || ' ' || l.cross_mod || ' ' ||
		trim(l.cross_street || ' ' || l.cross_dir) AS "location",
	cab.style AS "type", d.name AS device, d.pin,
	d.cross_loc, d.corridor, c.notes
	FROM iris.controller c
	LEFT JOIN iris.cabinet cab ON c.cabinet = cab.name
	LEFT JOIN geo_loc_view l ON cab.geo_loc = l.name
	LEFT JOIN controller_device_view d ON d.controller = c.name;
GRANT SELECT ON controller_report TO PUBLIC;

INSERT INTO iris.capability (name, enabled) VALUES ('gate_arm_tab', true);
INSERT INTO iris.capability (name, enabled) VALUES ('gate_arm_control', true);

INSERT INTO iris.privilege (name, capability, pattern, priv_r, priv_w, priv_c,
	priv_d)
	VALUES ('prv_gtb0', 'gate_arm_tab', 'gate_arm(/.*)?', true, false,
	false, false);
INSERT INTO iris.privilege (name, capability, pattern, priv_r, priv_w, priv_c,
	priv_d)
	VALUES ('prv_da99', 'device_admin', 'gate_arm/.*', false, true,
	true, true);
INSERT INTO iris.privilege (name, capability, pattern, priv_r, priv_w, priv_c,
	priv_d)
	VALUES ('prv_gac0', 'gate_arm_control', 'gate_arm/.*/armState', false,
	true, false, false);
INSERT INTO iris.privilege (name, capability, pattern, priv_r, priv_w, priv_c,
	priv_d)
	VALUES ('prv_gac1', 'gate_arm_control', 'gate_arm/.*/ownerNext', false,
	true, false, false);

INSERT INTO iris.role_capability (role, capability)
	VALUES ('administrator', 'gate_arm_tab');
INSERT INTO iris.role_capability (role, capability)
	VALUES ('administrator', 'gate_arm_control');

INSERT INTO iris.comm_protocol (id, description) VALUES (28, 'HySecurity STC');