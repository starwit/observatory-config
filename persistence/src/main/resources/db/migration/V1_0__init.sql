CREATE SEQUENCE IF NOT EXISTS "point_id_seq";

CREATE TABLE "point"
(
    "xvalue" DECIMAL(19,2) NOT NULL ,
    "yvalue" DECIMAL(19,2) NOT NULL ,
    "polygon_id" BIGINT,
    "id" BIGINT NOT NULL DEFAULT nextval('point_id_seq'),
    CONSTRAINT "point_pkey" PRIMARY KEY ("id")
);

CREATE SEQUENCE IF NOT EXISTS "polygon_id_seq";

CREATE TABLE "polygon"
(
    "open" BOOLEAN,
    "observationarea_id" BIGINT,
    "name" VARCHAR(255) NOT NULL,
    "classification_id" BIGINT,
    "id" BIGINT NOT NULL DEFAULT nextval('polygon_id_seq'),
    CONSTRAINT "polygon_pkey" PRIMARY KEY ("id"),
    CONSTRAINT "polygon_name_observationarea" UNIQUE ("name", "observationarea_id")
);

CREATE SEQUENCE IF NOT EXISTS "image_id_seq";

CREATE TABLE "image"
(
    "name" VARCHAR(255) NOT NULL,
    "data" BYTEA,
    "type" VARCHAR(255),
    "observationarea_id" BIGINT,
    "id" BIGINT NOT NULL DEFAULT nextval('image_id_seq'),
    CONSTRAINT "image_pkey" PRIMARY KEY ("id")
);

CREATE SEQUENCE IF NOT EXISTS "classification_id_seq";

CREATE TABLE "classification"
(
    "name" VARCHAR(255) NOT NULL,
    "color" VARCHAR(7) NOT NULL DEFAULT '#000',
    "id" BIGINT NOT NULL DEFAULT nextval('classification_id_seq'),
    CONSTRAINT "classification_pkey" PRIMARY KEY ("id")
);


CREATE SEQUENCE IF NOT EXISTS "camera_id_seq";

CREATE TABLE "camera"
(
    "saeid" VARCHAR(255) NOT NULL,
    "observationarea_id" BIGINT,
    "id" BIGINT NOT NULL DEFAULT nextval('camera_id_seq'),
    CONSTRAINT "camera_pkey" PRIMARY KEY ("id")
);

CREATE SEQUENCE IF NOT EXISTS "observationarea_id_seq";

CREATE TABLE "observationarea"
(
    "name" VARCHAR(255) NOT NULL,
    "center_latitude" DECIMAL(22,19),
    "center_longitude" DECIMAL(22,19),
    "top_left_latitude" DECIMAL(22,19),
    "top_left_longitude" DECIMAL(22,19),
    "degree_per_pixel_x" DECIMAL(22,19),
    "degree_per_pixel_y" DECIMAL(22,19),
    "georeferenced" BOOLEAN,
    "processing_enabled" BOOLEAN,
    "image_height" INTEGER,
    "image_width" INTEGER,
    "id" BIGINT NOT NULL DEFAULT nextval('observationarea_id_seq'),
    CONSTRAINT "observationarea_pkey" PRIMARY KEY ("id")
);

ALTER TABLE "point"
    ADD CONSTRAINT "fk_point_polygon"
    FOREIGN KEY ("polygon_id")
    REFERENCES "polygon" ("id");

ALTER TABLE "polygon"
    ADD CONSTRAINT "fk_polygon_observationarea"
    FOREIGN KEY ("observationarea_id")
    REFERENCES "observationarea" ("id");

ALTER TABLE "polygon"
    ADD CONSTRAINT "fk_polygon_classification"
    FOREIGN KEY ("classification_id")
    REFERENCES "classification" ("id");

ALTER TABLE "image"
    ADD CONSTRAINT "fk_image_observationarea"
    FOREIGN KEY ("observationarea_id")
    REFERENCES "observationarea" ("id");

ALTER TABLE "camera"
    ADD CONSTRAINT "fk_camera_observationarea"
    FOREIGN KEY ("observationarea_id")
    REFERENCES "observationarea" ("id");