ALTER TABLE "parkingconfig" DROP COLUMN "version";

ALTER TABLE "parkingarea" DROP COLUMN "activeconfigversion";
ALTER TABLE "parkingarea" DROP COLUMN "testconfigversion";

ALTER TABLE "parkingarea" ADD COLUMN "testconfig_id" BIGINT UNIQUE;
ALTER TABLE "parkingarea" ADD COLUMN "prodconfig_id" BIGINT UNIQUE;

ALTER TABLE "parkingarea"
    ADD CONSTRAINT "fk_parkingarea_testconfig"
    FOREIGN KEY ("testconfig_id")
    REFERENCES "parkingconfig" ("id");

ALTER TABLE "parkingarea"
    ADD CONSTRAINT "fk_parkingarea_prodconfig"
    FOREIGN KEY ("prodconfig_id")
    REFERENCES "parkingconfig" ("id");
