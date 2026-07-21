-- Schema change: move relation from camera.observationarea_id to observationarea.camera_id
ALTER TABLE "observationarea"
    ADD COLUMN "camera_id" BIGINT;

ALTER TABLE "observationarea"
    ADD CONSTRAINT "fk_observationarea_camera"
    FOREIGN KEY ("camera_id")
    REFERENCES "camera" ("id");

CREATE INDEX "idx_observationarea_camera_id"
    ON "observationarea" ("camera_id");

UPDATE "observationarea" oa
SET "camera_id" = (
    SELECT MIN(c2."id")
    FROM "camera" c1
    JOIN "camera" c2 ON c2."saestreamkey" = c1."saestreamkey"
    WHERE c1."observationarea_id" = oa."id"
)
WHERE EXISTS (
    SELECT 1
    FROM "camera" c
    WHERE c."observationarea_id" = oa."id"
);

-- Remove duplicate camera rows now represented by one canonical row per stream key
DELETE FROM "camera" c
WHERE EXISTS (
        SELECT 1
        FROM "camera" c2
        WHERE c2."saestreamkey" = c."saestreamkey"
            AND c2."id" < c."id"
);

-- Enforce one camera row per SAE stream key
ALTER TABLE "camera"
    ADD CONSTRAINT "uq_camera_saestreamkey" UNIQUE ("saestreamkey");

-- Drop old relation column from camera table
ALTER TABLE "camera"
    DROP CONSTRAINT IF EXISTS "fk_camera_observationarea";

ALTER TABLE "camera"
    DROP COLUMN IF EXISTS "observationarea_id";
