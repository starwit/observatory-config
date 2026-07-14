-- Data migration: map observation areas to shared camera rows by SAE stream key
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
