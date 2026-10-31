# PROYECTO MODULO CONTENERIZACION Y ORQUESTACION DE CONTENEDORES

El presente proyecto consta de una API la cual permite registrar tareas pendientes o completadas,
el repositorio consta de una APIREST sin interfaz grafica que permite crear, listar y marcar como completadas
las tareas, tiene persistencia en MYSQL, este proyecto esta preparado para reproducirse
usando docker o docker-compose asi mismo permite el despliegue en un cluster local kubernetes (minikube).

## 1. Contenido del repositorio
```
├─ src/                    Código Spring Boot (paquete com.luzam.contenerizacion)
├─ build.gradle, gradlew   Build con Gradle wrapper
├─ Dockerfile              Imagen multietapa
├─ .dockerignore
├─ docker-compose.yaml            API + MySQL, red interna, volumen, healthcheck
├─ .env.example            Variables de ejemplo (copiar a .env)
├─ k8s/                    Manifiestos de Kubernetes
│  ├─ 00-namespace.yaml
│  ├─ 01-configmap.yaml
│  ├─ 02-secret.yaml       Credenciales DE PRÁCTICA
│  ├─ 10-mysql.yaml        PVC + Service headless + StatefulSet (1 réplica)
│  └─ 20-api.yaml          Deployment (2 réplicas) + Service NodePort
├─ helm/tareas-api/        Chart propio (Deployment, Service, ConfigMap)
│  ├─templates
   ├─ Chart.yaml
   ├─ values.yaml
   ├─ values-dev.yaml
   └─ values-prod.yaml
```
## 2. Requisitos
- Ubuntu Server 26.04.1 LTS
- Docker Engine Compose | 29.9.2
- minikube kubectl (Kubernetes) v1.39.0
- Helm v4.3.1
- java 21 (compilacion fuera de docker)

## 3. Ejecucion Docker Compose
bash
- git clone https://github.com/sergioale1990/examencontenerizacion.git
- cd examencontenerizacion
- cp .env.example .env (completar las variables necesarias)
- docker compose up -d --build
- docker compose ps (verificar que los contenedores estan UP)

## 4. Pruebas de funcionamiento
bash
- curl -s localhost:8080/info
- curl -s -X POST localhost:8080/tareas -H 'Content-Type: application/json' -d '{"titulo":"Revisar inventario"}'
- curl -s localhost:8080/tareas
- curl -s -X PATCH localhost:8080/tareas/1/completar
- curl -s "localhost:8080/tareas?completada=true"

## 5. Despliegue de Kubernetes
bash
- docker compose stop (liberar RAM del sistema)
- minikube start --driver=docker --container-runtime=containerd --cpus=2 --memory=4096
- docker build -t contenerizacion:1.0.3 .
- minikube image load contenerizacion:1.0.3
- kubectl apply -f k8s/
- kubectl get pods -n tareas -w

## 6. Verificacion funcionamiento Kubernetes
bash
- API=http://$(minikube ip):30080
- curl -s $API/info
- curl -s -X POST $API/tareas -H 'Content-Type: application/json' -d '{"titulo":"Desde Kubernetes"}'
- curl -s $API/tareas
- for i in 1 2 3 4; do curl -s $API/info | jq -r .host; done (balanceo entre 2 pods)

## Habilitar acceso en caso de usar una VM
bash
- kubectl port-forward --address 0.0.0.0 -n tareas svc/tareas-api 9080:80
- Ingresar a `http://devops-lab.mshome.net:9080/swagger-ui.html`
- En caso de usar docker-compose ingresar mediante Con Compose, Swagger está en `http://<host>:8080/swagger-ui.html`.

## 7. Recuperacion automatica de Pod
bash
- kubectl get pods -n tareas -l app=tareas-api -w          # (terminal 1)
- kubectl delete pod -n tareas $(kubectl get pods -n tareas -l app=tareas-api -o jsonpath='{.items[0].metadata.name}')
- kubectl get events -n tareas --sort-by=.lastTimestamp | tail -8   # SuccessfulCreate del ReplicaSet
- curl -s $API/tareas 

## 8. Persistencia de datos con PVC
- kubectl delete pod mysql-0 -n tareas
- kubectl get pods -n tareas -w
- curl -s $API/tareas

## 9. Operacion
bash
- kubectl rollout history deployment/tareas-api -n tareas
- kubectl rollout undo deployment/tareas-api -n tareas
- kubectl scale deployment tareas-api -n tareas --replicas=3
- kubectl logs -n tareas deploy/tareas-api
- kubectl describe pod <pod> -n tareas

## 10. Helm
bash
- helm lint helm/tareas-api -f helm/tareas-api/values-dev.yaml
- helm template tareas-helm helm/tareas-api -n tareas -f helm/tareas-api/values-dev.yaml
- helm install tareas-helm helm/tareas-api -n tareas -f helm/tareas-api/values-dev.yaml (1 réplica, dev)
- helm upgrade tareas-helm helm/tareas-api -n tareas -f helm/tareas-api/values-prod.yaml (2 réplicas, prod)
- helm history tareas-helm -n tareas
- helm rollback tareas-helm 1 -n tareas
- curl -s http://$(minikube ip):30081/info