CREATE SEQUENCE IF NOT EXISTS "detection_id_seq";

CREATE TABLE "detection" (
    "id"                  BIGINT NOT NULL DEFAULT nextval('detection_id_seq'),
    "stream_id"           VARCHAR(255) NOT NULL,
    "object_id"           VARCHAR(255) NOT NULL,
    "class_id"            INTEGER      NOT NULL,
    "detection_timestamp"   TIMESTAMP WITH TIME ZONE    NOT NULL,
    "has_geo_coordinates" BOOLEAN      NOT NULL,
    "latitude"            DOUBLE PRECISION,
    "longitude"           DOUBLE PRECISION,
    "x"                   DOUBLE PRECISION,
    "y"                   DOUBLE PRECISION
) PARTITION BY RANGE (detection_timestamp);


