# Introduction
This repository contains an application that allows you to define areas to observe present objects and thresholds to count objects moving across. It is called SmartParkingConfig because its first use case is the management of parking spaces. It is open source under the AGPL license and other usages are recommended. For more details on licensing see section [License](#license).

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

**The Application can be reached under http://localhost:8081/smartparkingconfig/**

**If you are using keycloak:**
- **default user/password is admin/admin**
- **keycloak can be reached under http://localhost:8080/auth**

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

PGadmin for GUI-based database access is available at `http://localhost:5050` (database connection is preconfigured)

## Changelog

During development, you can add commits to change log by using this syntax: https://www.conventionalcommits.org/en/v1.0.0/#examples

# License

Code in this repository is property of [Starwit Technologies GmbH](https://starwit-technologies.de/) and is published under AGPL. So if you want to adapt it, any change needs to be published under AGPL as well. Please let us know, which changes you made. Same goes for errors and bugs. License can be found at [here](License).