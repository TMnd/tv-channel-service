CREATE TABLE cat_config (
    name text PRIMARY KEY,
    configuration text
);

CREATE TABLE cat_shows (
    name text PRIMARY KEY,
    duration bigint,
    episode text,
	season text,
	type text,
	path text
);