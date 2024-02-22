CREATE SEQUENCE IF NOT EXISTS "camera_id_seq";

CREATE TABLE "camera"
(
    "uuid" VARCHAR(255) UNIQUE,
    "image_id" BIGINT UNIQUE,
    "id" BIGINT NOT NULL DEFAULT nextval('camera_id_seq'),
    CONSTRAINT "camera_pkey" PRIMARY KEY ("id")
);

ALTER TABLE "camera"
    ADD CONSTRAINT "fk_camera_image"
    FOREIGN KEY ("image_id")
    REFERENCES "image" ("id");

ALTER TABLE "parkingarea" ADD "centerlatitude" DECIMAL(22,19);
ALTER TABLE "parkingarea" ADD "centerlongitude" DECIMAL(22,19);

ALTER TABLE "image" ADD "topleftlatitude" DECIMAL(22,19);
ALTER TABLE "image" ADD "topleftlongitude" DECIMAL(22,19);
ALTER TABLE "image" ADD "degreeperpixelx" DECIMAL(19,2);
ALTER TABLE "image" ADD "degreeperpixely" DECIMAL(19,2);
ALTER TABLE "image" ADD "georeferenced" BOOLEAN;
