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
    "id" BIGINT NOT NULL DEFAULT nextval('polygon_id_seq'),
    CONSTRAINT "polygon_pkey" PRIMARY KEY ("id")
);

CREATE SEQUENCE IF NOT EXISTS "image_id_seq";

CREATE TABLE "image"
(
    "src" VARCHAR(255),
    "name" VARCHAR(255),
    "parkingconfig_id" BIGINT,
    "id" BIGINT NOT NULL DEFAULT nextval('image_id_seq'),
    CONSTRAINT "image_pkey" PRIMARY KEY ("id")
);

CREATE SEQUENCE IF NOT EXISTS "classification_id_seq";

CREATE TABLE "classification"
(
    "name" VARCHAR(255) NOT NULL ,
    "id" BIGINT NOT NULL DEFAULT nextval('classification_id_seq'),
    CONSTRAINT "classification_pkey" PRIMARY KEY ("id")
);

CREATE SEQUENCE IF NOT EXISTS "parkingconfig_id_seq";

CREATE TABLE "parkingconfig"
(
    "name" VARCHAR(255) NOT NULL ,
    "version" INTEGER NOT NULL ,
    "parkingarea_id" BIGINT,
    "id" BIGINT NOT NULL DEFAULT nextval('parkingconfig_id_seq'),
    CONSTRAINT "parkingconfig_pkey" PRIMARY KEY ("id")
);

CREATE SEQUENCE IF NOT EXISTS "parkingarea_id_seq";

CREATE TABLE "parkingarea"
(
    "name" VARCHAR(255) NOT NULL ,
    "activeconfigversion" INTEGER,
    "testconfigversion" INTEGER,
    "id" BIGINT NOT NULL DEFAULT nextval('parkingarea_id_seq'),
    CONSTRAINT "parkingarea_pkey" PRIMARY KEY ("id")
);

ALTER TABLE "point"
    ADD CONSTRAINT "fk_point_polygon"
    FOREIGN KEY ("polygon_id")
    REFERENCES "polygon" ("id");

ALTER TABLE "polygon"
    ADD CONSTRAINT "fk_polygon_image"
    FOREIGN KEY ("image_id")
    REFERENCES "image" ("id");

CREATE TABLE "polygon_classification" (
    "polygon_id" BIGINT NOT NULL,
    "classification_id" BIGINT NOT NULL,
    PRIMARY KEY ("polygon_id", "classification_id")
);

ALTER TABLE "polygon_classification"
    ADD CONSTRAINT "fk_polygon_classification"
    FOREIGN KEY ("polygon_id")
    REFERENCES "polygon" ("id");

ALTER TABLE "polygon_classification"
    ADD CONSTRAINT "fk_classification_classification"
    FOREIGN KEY ("classification_id")
    REFERENCES "classification" ("id");

ALTER TABLE "image"
    ADD CONSTRAINT "fk_image_parkingconfig"
    FOREIGN KEY ("parkingconfig_id")
    REFERENCES "parkingconfig" ("id");

ALTER TABLE "parkingconfig"
    ADD CONSTRAINT "fk_parkingconfig_parkingarea"
    FOREIGN KEY ("parkingarea_id")
    REFERENCES "parkingarea" ("id");

