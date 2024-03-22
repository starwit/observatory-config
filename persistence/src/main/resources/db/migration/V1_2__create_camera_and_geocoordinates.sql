ALTER TABLE "image" ADD "top_left_latitude" DECIMAL(22,19);
ALTER TABLE "image" ADD "top_left_longitude" DECIMAL(22,19);
ALTER TABLE "image" ADD "degree_per_pixel_x" DECIMAL(22,19);
ALTER TABLE "image" ADD "degree_per_pixel_y" DECIMAL(22,19);
ALTER TABLE "image" ADD "georeferenced" BOOLEAN;
ALTER TABLE "image" ADD COLUMN "image_height" INTEGER;
ALTER TABLE "image" ADD COLUMN "image_width" INTEGER;

