# Observatory Config Helm chart

This Helm chart deploys Observatory Config to a Kubernetes cluster. 

## Values

| Parameter | Default | Description |
|---|---|---|
| **Image** | | |
| `image.repository` | `starwitorg/observatory-config` | Container image repository |
| `image.pullPolicy` | `IfNotPresent` | Image pull policy |
| `image.tag` | `${project.version}` | Image tag, defaults to chart appVersion |
| **Naming** | | |
| `nameOverride` | `""` | Overrides the chart name |
| `fullnameOverride` | `observatory-config` | Overrides the full resource name |
| **Application** | | |
| `app.contextPath` | `""` | Application context path |
| `app.observatoryUrl` | `""` | URL of the observatory backend |
| **Redis / ValKey** | | |
| `redis.enabled` | `false` | Enable Redis/ValKey message bus for live tracking |
| `redis.host` | `valkey` | Redis host |
| `redis.port` | `6379` | Redis port |
| **Authentication** | | |
| `auth.enabled` | `true` | Enable Keycloak authentication |
| `auth.keycloakRealmUrlInternal` | `http://internal-hostname/realms/observatory-config` | Internal Keycloak realm URL |
| `auth.keycloakRealmUrlExternal` | `https://external-hostname/realms/observatory-config` | External Keycloak realm URL |
| `auth.clientId` | `observatory-config` | Keycloak client ID |
| `auth.clientSecret` | `observatory-config` | Keycloak client secret |
| **Database** | | |
| `database.internal` | `true` | Use bundled internal PostgreSQL |
| `database.name` | `observatory-config` | Database name |
| `database.username` | `observatory-config` | Database username |
| `database.password` | `observatory-config` | Database password |
| `database.retention.days` | `2` | Number of days to retain recordings |
| **Ingress** | | |
| `ingress.enabled` | `true` | Enable ingress |
| `ingress.annotations` | `cert-manager.io/cluster-issuer: letsencrypt-prod` | Ingress annotations |
| `ingress.hosts[0].host` | `spc.data-wolfsburg.de` | Ingress hostname |
| `ingress.hosts[0].paths[0].path` | `/` | Ingress path |
| `ingress.hosts[0].paths[0].pathType` | `ImplementationSpecific` | Ingress path type |
| `ingress.tls[0].secretName` | `spc.data-wolfsburg.de` | TLS secret name |
| `ingress.tls[0].hosts[0]` | `spc.data-wolfsburg.de` | TLS hostname |
| **Pod** | | |
| `podAnnotations` | `{}` | Annotations to add to the pod |
| `podSecurityContext` | `{}` | Pod-level security context |
| `securityContext` | `{}` | Container-level security context |
| **Resources** | | |
| `resources.limits.memory` | `1000Mi` | Memory limit |
