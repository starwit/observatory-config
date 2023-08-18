INSERT INTO "parkingarea"(
	"name", "activeconfigversion", "testconfigversion")
	VALUES ('Hühnerrudi', 1, 1);
COMMIT;
INSERT INTO "parkingconfig"(
	"name", "version", "parkingarea_id")
	VALUES ('Hühnerrudi', 1, 1);
COMMIT;
INSERT INTO "image"(
	"src", "name", "parkingconfig_id")
	VALUES ('parking_south.jpg', 'Kamera 1', 1);