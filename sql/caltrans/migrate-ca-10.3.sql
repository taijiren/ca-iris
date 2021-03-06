-- current as of MnDOT 4.35.4


-- updates required before rest of updates
\set ON_ERROR_STOP

SET SESSION AUTHORIZATION 'tms';

-- updates CA-only protocols (that haven't been given to MnDOT)

-- ADDITIONS for CA-IRIS v10.3 go at the bottom




-- ============================================================================
-- BEGIN: MnDOT updates
-- ============================================================================



-- ============================================================================
-- BEGIN 10.3 changes
-- ============================================================================

-- feature 587 travel time route max link in miles
INSERT INTO iris.system_attribute(name, value) VALUES ('route_max_link_miles', 0.6);

-- feature 587 dms action duration (travel time auto-blanking)
INSERT INTO iris.system_attribute(name, value) VALUES ('dms_action_duration_minutes', 0);
ALTER TABLE iris.dms_action
ADD COLUMN duration_minutes INTEGER NOT NULL DEFAULT 0;

INSERT INTO iris.system_attribute (name, value) VALUES ('system_min_password_length', 8);

INSERT INTO iris.system_attribute (name, value) VALUES ('travel_time_max_mph', 65);

INSERT INTO iris.system_attribute (name, value) VALUES ('travel_time_unit_text', '');

-- to accommodate larger tags and their attributes, e.g. tt
ALTER TABLE iris.quick_message
  ALTER COLUMN multi TYPE VARCHAR(2048);
