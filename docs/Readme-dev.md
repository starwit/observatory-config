# Development Documentation

## Prerequisites

* Java JDK 25 or later
* Maven 3
* NodeJs (24.11.1) and NPM (11.6.2) - [NodeJS Install](https://nodejs.org/en/download/package-manager/)
* Postgres (available for development via docker-compose scripts)
* using Keycloak is optional

## Setup / Running

Each step is executed from the project home directory.

1) Go to `webclient/app` and install the frontend applications dependencies

    ```bash
    cd webclient/app
    npm install
    ```

2) Go to Main Folder and build the project

    ```bash
    mvn clean install -P frontend
    ```

3) Go to the deployment folder and start the local dev environment docker compose:

    A. Without authentication

    ```bash
    cd deployment
    docker compose -f noauth-docker-compose.yml up
    ```

    B. With authentication (through Keycloak)

    ```bash
    cd deployment
    docker compose -f auth-docker-compose.yml up
    ```

4) Start application

    A. Without authentication: `java -jar aplication/target/application-0.0.1-SNAPSHOT.jar`\
    B. With authentication: `SPRING_PROFILES_ACTIVE=auth-dev java -jar aplication/target/application-0.0.1-SNAPSHOT.jar`

    (you can also use the spring boot maven plugin `mvn spring-boot:run` or run the main class `Application.java` through VSCode)

**The Application can be reached under <http://localhost:8081/observatory-config/>**

**If you are using keycloak:**

* **default user/password is admin/admin**
* **keycloak can be reached under <http://localhost:8080/auth>**


### Frontend Debugging

For debugging, you can start the frontend separately.

```shell
cd webclient/app
npm run dev
```

Vite dev server starts under `http://localhost:5173/` by default.
Requests to `http://localhost:5173/api` are automatically proxied to `http://localhost:8081/observatory-config/api` which is where the app will run by default, so you just need to start the app locally (preferable without auth) for the dev server version to be fully functional.

> **If you are using the installation with keycloak, make sure you are logged in before first usage - just go to localhost:8081/starwit in your browser.**

### Backend Debugging

You can start the spring boot application in debug mode. See Spring Boot documentation for further details. The easiest way is, to use debug functionality integrated with your IDE like VS Code.

### Postgres Client

PGadmin for GUI-based database access is available at `http://localhost:5050` (database connection is preconfigured)

## API

All backend functions are available via Swagger UI: http://localhost:8081/observatory-config/swagger-ui/index.html

Load trajectories from backend with the following curl command. Note timezone appendix in timestamp
```bash
curl -X POST "localhost:8081/observatory-config/api/detection/window"   -H "Content-Type: application/json"   -d '{"timestamp": "2026-07-07T11:46:00+02:00","windowSize": 10,"streamId": "geomapper:dzs-d37-rothenfelderstr"}'
```

## Changelog

During development, you can add commits to change log by using this syntax: <https://www.conventionalcommits.org/en/v1.0.0/#examples>