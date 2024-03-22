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
    "image_id" BIGINT,
    "name" VARCHAR(255) NOT NULL,
    "classification_id" BIGINT,
    "id" BIGINT NOT NULL DEFAULT nextval('polygon_id_seq'),
    CONSTRAINT "polygon_pkey" PRIMARY KEY ("id"),
    CONSTRAINT "polygon_name_image" UNIQUE ("name", "image_id")
);

CREATE SEQUENCE IF NOT EXISTS "image_id_seq";

CREATE TABLE "image"
(
    "name" VARCHAR(255) NOT NULL,
    "data" BYTEA,
    "type" VARCHAR(255),
    "parkingconfig_id" BIGINT,
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

CREATE SEQUENCE IF NOT EXISTS "parkingconfig_id_seq";

CREATE TABLE "parkingconfig"
(
    "name" VARCHAR(255) NOT NULL,
    "observationarea_id" BIGINT,
    "id" BIGINT NOT NULL DEFAULT nextval('parkingconfig_id_seq'),
    CONSTRAINT "parkingconfig_pkey" PRIMARY KEY ("id")
);

CREATE SEQUENCE IF NOT EXISTS "observationarea_id_seq";

CREATE TABLE "observationarea"
(
    "name" VARCHAR(255) NOT NULL,
    "prodconfig_id" BIGINT UNIQUE,
    "testconfig_id" BIGINT UNIQUE,
    "id" BIGINT NOT NULL DEFAULT nextval('observationarea_id_seq'),
    CONSTRAINT "observationarea_pkey" PRIMARY KEY ("id")
);

ALTER TABLE "point"
    ADD CONSTRAINT "fk_point_polygon"
    FOREIGN KEY ("polygon_id")
    REFERENCES "polygon" ("id");

ALTER TABLE "polygon"
    ADD CONSTRAINT "fk_polygon_image"
    FOREIGN KEY ("image_id")
    REFERENCES "image" ("id");

ALTER TABLE "polygon"
    ADD CONSTRAINT "fk_polygon_classification"
    FOREIGN KEY ("classification_id")
    REFERENCES "classification" ("id");

ALTER TABLE "image"
    ADD CONSTRAINT "fk_image_parkingconfig"
    FOREIGN KEY ("parkingconfig_id")
    REFERENCES "parkingconfig" ("id");

ALTER TABLE "parkingconfig"
    ADD CONSTRAINT "fk_parkingconfig_observationarea"
    FOREIGN KEY ("observationarea_id")
    REFERENCES "observationarea" ("id");

ALTER TABLE "observationarea"
    ADD CONSTRAINT "fk_observationarea_testconfig"
    FOREIGN KEY ("testconfig_id")
    REFERENCES "parkingconfig" ("id");

ALTER TABLE "observationarea"
    ADD CONSTRAINT "fk_observationarea_prodconfig"
    FOREIGN KEY ("prodconfig_id")
    REFERENCES "parkingconfig" ("id");