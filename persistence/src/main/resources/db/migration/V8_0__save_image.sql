ALTER TABLE "image"
DROP "src";

ALTER TABLE "image"
ADD "data" VARBINARY(MAX);

ALTER TABLE "image"
ADD "type" VARCHAR(255);