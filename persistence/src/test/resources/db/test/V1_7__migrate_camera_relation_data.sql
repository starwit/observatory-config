-- Data migration: map observation areas to shared camera rows by SAE stream key
WITH canonical_camera AS (
    SELECT "saestreamkey", MIN("id") AS "camera_id"
    FROM "camera"
    GROUP BY "saestreamkey"
)
UPDATE "observationarea" oa
SET "camera_id" = cc."camera_id"
FROM "camera" c
JOIN canonical_camera cc ON cc."saestreamkey" = c."saestreamkey"
WHERE c."observationarea_id" = oa."id";

-- Remove duplicate camera rows now represented by one canonical row per stream key
DELETE FROM "camera" c
USING (
    SELECT "saestreamkey", MIN("id") AS "keep_id"
    FROM "camera"
    GROUP BY "saestreamkey"
) canon
WHERE c."saestreamkey" = canon."saestreamkey"
  AND c."id" <> canon."keep_id";

-- Enforce one camera row per SAE stream key
ALTER TABLE "camera"
    ADD CONSTRAINT "uq_camera_saestreamkey" UNIQUE ("saestreamkey");

-- Drop old relation column from camera table
ALTER TABLE "camera"
    DROP CONSTRAINT IF EXISTS "fk_camera_observationarea";

ALTER TABLE "camera"
    DROP COLUMN IF EXISTS "observationarea_id";
