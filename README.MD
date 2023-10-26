### Prerequisites

* Java JDK 17 or later
* Maven 3
* NodeJs (16.9.1) and NPM (8.3.2) - [NodeJS Install](https://nodejs.org/en/download/package-manager/)
* Postgres (available for development via docker-compose scripts)
* using Keycloak is optional

### Installation Steps

:exclamation: Each step is executed from the project home directory.

1) go to the deployment folder and start the environment (database and keycloak) via docker-compose:

    ```bash
    cd deployment
    docker-compose -f localenv-docker-compose.yml up
    ```

2) go to `webclient/app` and install the frontend applications dependencies

    ```bash
    cd webclient/app
    npm install --legacy-peer-deps
    ```

3) build the project

    ```bash
    mvn clean install -P frontend
    ```

4) start project

    ```bash
    java -jar application/target/application-0.0.1-SNAPSHOT.jar
    ```
   You can also run the main-class via Visual Studio Code.

5) add Database to pgAdmin

    * open http://localhost:5050/ 
    * click on "Add new Server"
    * enter any name
    * click on Connection
    * type in the following:
    Host name: smartparkingconfig-db;
    Maintaince DB: smartparkingconfig;
    username: smartparkingconfig;
    passwort: smartparkingconfig:
    * now save the settings and you shoud be able to access the Database

* **The Application can be reached under http://localhost:8081/smartparkinkconfig/**
* **Project Builder can be reached under http://localhost:8081/starwit/**
* **If you are using keycloak:**
    * **default user/password is admin/admin**
    * **keycloak can be reached under http://localhost:8080/auth**

### Debugging

#### Frontend Debugging

For debugging, you can start the frontend separately.

```shell
cd webclient/app
npm run dev
```
Vite dev server starts under `http://localhost:5173/` by default.
Requests to `http://localhost:5173/api` are automatically proxied to `http://localhost:8081/smartparkingconfig/api` which is where the app will run by default, so you just need to start the app locally (preferable without auth) for the dev server version to be fully functional.

> **If you are using the installation with keycloak, make sure you are logged in before first usage - just go to localhost:8081/starwit in your browser.**

#### Backend Debugging

You can start the spring boot application in debug mode. See Spring Boot documentation for further details. The easiest way is, to use debug functionality integrated with your IDE like VS Code.

### Postgres Client

The database is available under localhost:3006

```
Username:starwit
Database:starwit
Password:starwit
```
PGAdmin is recommended to access database for development purpose. It can be deployed via docker-compose file.

### Starting without keycloak

If you want to start your application without keycloak, you need to change spring boot profile to dev in application\src\main\resources\application.properties.

```properties
spring.profiles.active=dev
```

or define env-variable

```bash
SPRING_PROFILES_ACTIVE=dev
```

Start the database without keycloak:

```bash
cd deployment
docker-compose -f postgreslocal-docker-compose.yml up
```
### Changelog

During development, you can add commits to change log by using this syntax: https://www.conventionalcommits.org/en/v1.0.0/#examples