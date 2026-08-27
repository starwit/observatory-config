-- Create link table for storing external links associated with ObservationArea
CREATE SEQUENCE IF NOT EXISTS "link_id_seq";

CREATE TABLE "link" (
    "id" BIGINT NOT NULL DEFAULT nextval('link_id_seq'),
    "name" VARCHAR(255) NOT NULL,
    "url" TEXT NOT NULL,
    "observation_area_id" BIGINT NOT NULL,
    FOREIGN KEY ("observation_area_id") REFERENCES "observationarea"("id") ON DELETE CASCADE,
    CONSTRAINT "link_pkey" PRIMARY KEY ("id")
);

