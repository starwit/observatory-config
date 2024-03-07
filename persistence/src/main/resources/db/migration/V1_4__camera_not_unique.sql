DROP TABLE "camera";

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