# Introduction
This repository contains code for SmartParkingConfig. 

## What does it do?
This software provides graphical tools to manage parking spaces. Users can define various area types that creates a configuration for a parking space which will then converted into AI based counting and monitoring jobs.

# Installation
This application runs on Kubernetes and standard installation tool is Helm. See section [Development](#development-setup) for other ways to run software in an dev environment.

## Deployment with Helm
Helm is a templating engine, that generates Kubernetes config files. It can be used to install, upgrade or delete apps on Kubernetes.

## Prerequisites
All artifacts for this app are stored in custom registries. For instructions how to use those please refer to [Wiki](https://dev.azure.com/starwit/Smart%20Parking/_wiki/wikis/Smart-Parking.wiki/97/Build-Infrastructure).

    # create a namespace for your application
    kubectl create ns spc
    
    # install app into created namespace
    helm -n spc install spc internal/smartparkingconfig -f customvalues.yaml

    # upgrade app
    helm -n spc upgrade spc internal/smartparkingconfig -f customvalues.yaml

    # delete app
    helm -n spc uninstall spc

## Custom Values File
For target environment some configuration is necessary. In Helm you can provide config data via a custom value file. See the template [value file](deployment/helm/smartparkingconfig/values-local-tpl.yaml) for an example. More detailed explanation for individual fields are provided in the [Readme](deployment/helm/smartparkingconfig/Readme.md) file.

# Build

## Build with Github
* Docker image & Helm chart are released to custom registry
* Custom runner necessary to reach custom registry

### Custom runner installation
Please refer official documentation on how to install Github runners at:
https://docs.github.com/en/actions/hosting-your-own-runners/managing-self-hosted-runners/adding-self-hosted-runners

# Development Setup
## Prerequisites

* Java JDK 17 or later
* Maven 3
* NodeJs (16.9.1) and NPM (8.3.2) - [NodeJS Install](https://nodejs.org/en/download/package-manager/)
* Postgres (available for development via docker-compose scripts)
* using Keycloak is optional

## Installation Steps

Each step is executed from the project home directory.

1) go to the deployment folder and start the environment (database and keycloak) via docker-compose:

    ```bash
    cd deployment
    docker-compose -f localenv-docker-compose.yml up
    ```

2) go to `webclient/app` and install the frontend applications dependencies

    ```bash
    cd webclient/app
    npm install
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

* **The Application can be reached under http://localhost:8081/smartparkinkconfig/**
* **If you are using keycloak:**
    * **default user/password is admin/admin**
    * **keycloak can be reached under http://localhost:8080/auth**

## Debugging

### Frontend Debugging

For debugging, you can start the frontend separately.

```shell
cd webclient/app
npm run dev
```
Vite dev server starts under `http://localhost:5173/` by default.
Requests to `http://localhost:5173/api` are automatically proxied to `http://localhost:8081/smartparkingconfig/api` which is where the app will run by default, so you just need to start the app locally (preferable without auth) for the dev server version to be fully functional.

> **If you are using the installation with keycloak, make sure you are logged in before first usage - just go to localhost:8081/starwit in your browser.**

### Backend Debugging

You can start the spring boot application in debug mode. See Spring Boot documentation for further details. The easiest way is, to use debug functionality integrated with your IDE like VS Code.

## Postgres Client

The database is available under smartparkingconfig-db:5432 (using pgadmin of docker compose script)

```
Username:smartparkingconfig
Database:smartparkingconfig
Password:smartparkingconfig
```
PGAdmin is recommended to access database for development purpose. It can be deployed via docker-compose file.

## Starting without keycloak

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
## Changelog

During development, you can add commits to change log by using this syntax: https://www.conventionalcommits.org/en/v1.0.0/#examples
