-- Schema change: move relation from camera.observationarea_id to observationarea.camera_id
ALTER TABLE "observationarea"
    ADD COLUMN "camera_id" BIGINT;

ALTER TABLE "observationarea"
    ADD CONSTRAINT "fk_observationarea_camera"
    FOREIGN KEY ("camera_id")
    REFERENCES "camera" ("id");

CREATE INDEX "idx_observationarea_camera_id"
    ON "observationarea" ("camera_id");
