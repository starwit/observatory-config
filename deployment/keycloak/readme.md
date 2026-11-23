# info keycloak

run local keycloak with docker command:

```bash
docker run -d --name keycloak -p 8080:8080 -e KEYCLOAK_USER=admin -e KEYCLOAK_PASSWORD=admin jboss/keycloak
```
Click add realm and import mapcollector-realm.json

start/stop container with
```bash
docker stop keycloak
docker start keycloak
```
In keycloak:

* add user 
* set password under tab credentials
* assign roles to user (Role Mappings)

## Test users (imports/realm.json)

The realm `observatory-config` is imported automatically by `deployment/auth-docker-compose.yml`.
It contains one user per role. Realm role names are mapped to the internal roles by
`security.rolemapping.*` (see `RoleMapper`).

| Username | Password | Realm role                 | Internal role | Permissions          |
|----------|----------|----------------------------|---------------|----------------------|
| admin    | admin    | `observatoryconfig_admin`  | admin         | everything           |
| user     | user     | `observatoryconfig_user`   | user          | read and modify data |
| reader   | reader   | `observatoryconfig_reader` | reader        | read only            |
