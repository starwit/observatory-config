Example customValues.yaml

```yaml

app:
  context_path: "/"

##see bitwarden for docker registry
pullSecret:
  user: "docker"
  password: "super secret"

auth:
  enabled: true
  keycloak_url: http://auth.localhost/realms/smartparkingconfig # will be used for readyness probe
  client_id: smartparkingconfig
  client_secret: smartparkingconfig # TODO get from secret

postgresql:
  auth:
    database: smartparkingconfig
    username: smartparkingconfig
    password: smartparkingconfig

ingress:
  enabled: true
  hosts:
    - host: localhost
      paths:
        - path: /smartparkingconfig
          pathType: ImplementationSpecific

```
