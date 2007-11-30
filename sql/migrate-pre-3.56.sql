SET SESSION AUTHORIZATION 'tms';

DROP TABLE font;
DROP TABLE character_list;
DROP TABLE character;

CREATE TABLE graphic (
	name TEXT PRIMARY KEY,
	bpp INTEGER NOT NULL,
	height INTEGER NOT NULL,
	width INTEGER NOT NULL,
	pixels TEXT NOT NULL
);
CREATE TABLE font (
	name TEXT PRIMARY KEY,
	number INTEGER NOT NULL,
	height INTEGER NOT NULL,
	width INTEGER NOT NULL,
	line_spacing INTEGER NOT NULL,
	char_spacing INTEGER NOT NULL,
	version_id INTEGER NOT NULL
);
CREATE TABLE glyph (
	name TEXT PRIMARY KEY,
	font TEXT NOT NULL,
	code_point INTEGER NOT NULL,
	graphic TEXT NOT NULL
);
ALTER TABLE glyph
	ADD CONSTRAINT fk_glyph_font FOREIGN KEY (font) REFERENCES font(name);
ALTER TABLE glyph
	ADD CONSTRAINT fk_glyph_graphic FOREIGN KEY (graphic)
	REFERENCES graphic(name);

REVOKE ALL ON TABLE graphic FROM PUBLIC;
GRANT SELECT ON TABLE graphic TO PUBLIC;
REVOKE ALL ON TABLE font FROM PUBLIC;
GRANT SELECT ON TABLE font TO PUBLIC;
REVOKE ALL ON TABLE glyph FROM PUBLIC;
GRANT SELECT ON TABLE glyph TO PUBLIC;
