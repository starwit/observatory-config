ALTER TABLE "image"
DROP "src";

ALTER TABLE "image"
ADD "data" BYTEA;

ALTER TABLE "image"
ADD "type" VARCHAR(255);