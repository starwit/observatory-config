ALTER TABLE "image" ALTER COLUMN "src" VARCHAR(255) NOT NULL DEFAULT 'parking_south.jpg';
ALTER TABLE "image" ALTER COLUMN "name" VARCHAR(255) NOT NULL DEFAULT 'parking_south.jpg';

ALTER TABLE "classification" ADD COLUMN "color" VARCHAR(7) DEFAULT '#000' NOT NULL;