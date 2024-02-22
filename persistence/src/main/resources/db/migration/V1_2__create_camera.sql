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
