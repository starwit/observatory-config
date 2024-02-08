ALTER TABLE "polygon"
    ADD CONSTRAINT "polygon_name_image" UNIQUE ("name", "image_id");
ALTER TABLE "polygon"
    ALTER COLUMN "name" SET NOT NULL;