CREATE SEQUENCE IF NOT EXISTS "camera_id_seq";

CREATE TABLE "camera"
(
    "saeid" VARCHAR(255) NOT NULL UNIQUE,
    "image_id" BIGINT,
    "id" BIGINT NOT NULL DEFAULT nextval('camera_id_seq'),
    CONSTRAINT "camera_pkey" PRIMARY KEY ("id")
);

ALTER TABLE "camera"
    ADD CONSTRAINT "fk_camera_image"
    FOREIGN KEY ("image_id")
    REFERENCES "image" ("id");

ALTER TABLE "parkingarea" ADD "center_latitude" DECIMAL(22,19);
ALTER TABLE "parkingarea" ADD "center_longitude" DECIMAL(22,19);

ALTER TABLE "image" ADD "top_left_latitude" DECIMAL(22,19);
ALTER TABLE "image" ADD "top_left_longitude" DECIMAL(22,19);
ALTER TABLE "image" ADD "degree_per_pixel_x" DECIMAL(19,2);
ALTER TABLE "image" ADD "degree_per_pixel_y" DECIMAL(19,2);
ALTER TABLE "image" ADD "georeferenced" BOOLEAN;
