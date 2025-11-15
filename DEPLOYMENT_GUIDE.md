
# QuickServe - Deployment Guide for 200K RPS

## Quick Start (Local Development)

### 1. Start with Docker Compose

```bash
# Build and start all services
docker-compose up -d

# Scale application instances
docker-compose up -d --scale quickserve=5

# View logs
docker-compose logs -f quickserve

# Stop all services
docker-compose down
```

### 2. Access Services

- **Application**: http://localhost:8080
- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000 (admin/admin)
- **Zipkin**: http://localhost:9411
- **Kafka UI**: http://localhost:8081

## Production Deployment (Kubernetes)

### Prerequisites

- Kubernetes cluster (1.25+)
- kubectl configured
- Helm 3.x installed
- Docker registry access

### Step 1: Build and Push Docker Image

```bash
# Build image
docker build -t quickserve/delivery-platform:latest .

# Tag for registry
docker tag quickserve/delivery-platform:latest registry.example.com/quickserve:v1.0.0

# Push to registry
docker push registry.example.com/quickserve:v1.0.0
```

### Step 2: Deploy Infrastructure

```bash
# Create namespace
kubectl create namespace quickserve

# Deploy PostgreSQL (using Helm)
helm install postgres bitnami/postgresql \
  --namespace quickserve \
  --set auth.username=quickserve \
  --set auth.password=quickserve123 \
  --set auth.database=quickserve_db \
  --set primary.persistence.size=100Gi \
  --set readReplicas.replicaCount=5 \
  --set readReplicas.persistence.size=100Gi

# Deploy Redis Cluster
helm install redis bitnami/redis-cluster \
  --namespace quickserve \
  --set cluster.nodes=6 \
  --set cluster.replicas=1 \
  --set persistence.size=50Gi

# Deploy Kafka
helm install kafka bitnami/kafka \
  --namespace quickserve \
  --set replicaCount=3 \
  --set zookeeper.replicaCount=3 \
  --set persistence.size=100Gi \
  --set defaultReplicationFactor=3 \
  --set numPartitions=32
```

### Step 3: Deploy Application

```bash
# Apply configuration
kubectl apply -f k8s-deployment.yml

# Verify deployment
kubectl get pods -n quickserve
kubectl get svc -n quickserve
kubectl get hpa -n quickserve

# Check pod status
kubectl describe pod <pod-name> -n quickserve

# View logs
kubectl logs -f deployment/quickserve-deployment -n quickserve
```

### Step 4: Deploy Monitoring Stack

```bash
# Add Prometheus Helm repo
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo update

# Install Prometheus
helm install prometheus prometheus-community/kube-prometheus-stack \
  --namespace monitoring --create-namespace

# Access Grafana
kubectl port-forward -n monitoring svc/prometheus-grafana 3000:80
```

### Step 5: Configure Ingress and SSL

```bash
# Install NGINX Ingress Controller
helm install ingress-nginx ingress-nginx/ingress-nginx \
  --namespace ingress-nginx --create-namespace \
  --set controller.replicaCount=3 \
  --set controller.resources.requests.cpu=500m \
  --set controller.resources.requests.memory=512Mi

# Install cert-manager for SSL
kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.13.0/cert-manager.yaml

# Apply ingress configuration
kubectl apply -f k8s-deployment.yml
```

## Scaling Configuration

### Horizontal Scaling

```bash
# Manual scaling
kubectl scale deployment quickserve-deployment --replicas=50 -n quickserve

# Check HPA status
kubectl get hpa -n quickserve -w

# View HPA metrics
kubectl describe hpa quickserve-hpa -n quickserve
```

### Vertical Scaling

Update resource limits in `k8s-deployment.yml`:

```yaml
resources:
  requests:
    memory: "2Gi"
    cpu: "2000m"
  limits:
    memory: "4Gi"
    cpu: "4000m"
```

### Database Scaling

```bash
# Add more read replicas
helm upgrade postgres bitnami/postgresql \
  --set readReplicas.replicaCount=10 \
  --namespace quickserve

# Verify replicas
kubectl get pods -l app.kubernetes.io/component=read -n quickserve
```

### Redis Scaling

```bash
# Scale Redis cluster
helm upgrade redis bitnami/redis-cluster \
  --set cluster.nodes=12 \
  --namespace quickserve
```

### Kafka Scaling

```bash
# Add more Kafka brokers
helm upgrade kafka bitnami/kafka \
  --set replicaCount=9 \
  --namespace quickserve
```

## Performance Tuning

### JVM Tuning

Update Dockerfile or deployment YAML:

```yaml
env:
  - name: JAVA_OPTS
    value: "-Xms2g -Xmx4g -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
```

### Database Connection Pool

Update `application-prod.yml`:

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 100  # Increase for high load
      minimum-idle: 20
```

### Tomcat Thread Pool

```yaml
server:
  tomcat:
    threads:
      max: 800  # Increase for higher concurrency
      min-spare: 100
```

## Monitoring and Alerting

### View Metrics

```bash
# Application metrics
kubectl port-forward svc/quickserve-service 8080:80 -n quickserve
curl http://localhost:8080/actuator/metrics

# Prometheus metrics
kubectl port-forward -n monitoring svc/prometheus-kube-prometheus-prometheus 9090:9090
# Access: http://localhost:9090
```

### Key Metrics to Monitor

```
http_server_requests_seconds_count  - Request count
http_server_requests_seconds_sum    - Total request time
jvm_memory_used_bytes                - Memory usage
process_cpu_usage                    - CPU usage
hikaricp_connections_active          - DB connections
cache_get_hit_total                  - Cache hits
kafka_consumer_lag                   - Consumer lag
```

### Grafana Dashboards

Import these dashboard IDs:
- **JVM (Micrometer)**: 4701
- **Spring Boot**: 12900
- **PostgreSQL**: 9628
- **Redis**: 11835
- **Kafka**: 7589

## Troubleshooting

### High Latency

```bash
# Check pod resources
kubectl top pods -n quickserve

# Check HPA status
kubectl get hpa -n quickserve

# View slow queries (PostgreSQL)
kubectl exec -it postgres-0 -n quickserve -- psql -U quickserve -d quickserve_db
SELECT * FROM pg_stat_statements ORDER BY mean_exec_time DESC LIMIT 10;
```

### Memory Leaks

```bash
# Get heap dump
kubectl exec -it <pod-name> -n quickserve -- jmap -dump:format=b,file=/tmp/heap.bin 1

# Copy heap dump locally
kubectl cp quickserve/<pod-name>:/tmp/heap.bin ./heap.bin

# Analyze with VisualVM or Eclipse MAT
```

### Database Connection Issues

```bash
# Check connection pool metrics
curl http://localhost:8080/actuator/metrics/hikaricp.connections.active

# Check PostgreSQL connections
kubectl exec -it postgres-0 -n quickserve -- psql -U quickserve -c "SELECT count(*) FROM pg_stat_activity;"
```

### Kafka Consumer Lag

```bash
# Check consumer lag
kubectl exec -it kafka-0 -n quickserve -- kafka-consumer-groups.sh \
  --bootstrap-server localhost:9092 \
  --describe --group quickserve-consumer-group

# Reset consumer offset (if needed)
kubectl exec -it kafka-0 -n quickserve -- kafka-consumer-groups.sh \
  --bootstrap-server localhost:9092 \
  --group quickserve-consumer-group \
  --reset-offsets --to-latest --execute --all-topics
```

### Redis Cache Issues

```bash
# Check Redis info
kubectl exec -it redis-0 -n quickserve -- redis-cli INFO

# Monitor Redis in real-time
kubectl exec -it redis-0 -n quickserve -- redis-cli MONITOR

# Clear cache (use with caution)
kubectl exec -it redis-0 -n quickserve -- redis-cli FLUSHALL
```

## Load Testing

### Using Apache JMeter

```bash
# Create thread group with 10,000 users
# Ramp-up: 60 seconds
# Loop count: 1000

# Sample HTTP requests:
GET  /api/restaurants/active
POST /api/orders
GET  /api/orders/{id}
```

### Using k6

```javascript
import http from 'k6/http';
import { check } from 'k6';

export let options = {
  stages: [
    { duration: '2m', target: 10000 },  // Ramp up to 10K users
    { duration: '5m', target: 10000 },  // Stay at 10K
    { duration: '2m', target: 20000 },  // Ramp to 20K
    { duration: '5m', target: 20000 },  // Stay at 20K
    { duration: '2m', target: 0 },      // Ramp down
  ],
};

export default function () {
  let response = http.get('http://api.quickserve.com/api/restaurants/active');
  check(response, {
    'status is 200': (r) => r.status === 200,
    'response time < 200ms': (r) => r.timings.duration < 200,
  });
}
```

Run test:
```bash
k6 run --vus 20000 --duration 10m load-test.js
```

## Disaster Recovery

### Database Backup

```bash
# Create backup
kubectl exec -it postgres-0 -n quickserve -- \
  pg_dump -U quickserve quickserve_db > backup_$(date +%Y%m%d).sql

# Restore from backup
kubectl exec -i postgres-0 -n quickserve -- \
  psql -U quickserve quickserve_db < backup_20240101.sql
```

### Multi-Region Failover

```bash
# Switch traffic to backup region
kubectl patch ingress quickserve-ingress -n quickserve \
  --type='json' -p='[{"op": "replace", "path": "/spec/rules/0/host", "value":"backup.quickserve.com"}]'
```

## Cost Optimization

### Right-Sizing

```bash
# Analyze resource usage
kubectl top pods -n quickserve --containers
kubectl top nodes

# Adjust resources based on actual usage
```

### Use Spot Instances

```yaml
# Add node selector for spot instances
nodeSelector:
  kubernetes.io/lifecycle: spot
```

### Auto-scaling Schedule

```yaml
# Scale down during off-peak hours (e.g., 2 AM - 6 AM)
apiVersion: autoscaling.k8s.io/v1
kind: ScheduledPodAutoscaler
```

## Security Checklist

- [ ] Enable RBAC in Kubernetes
- [ ] Use network policies
- [ ] Enable pod security policies
- [ ] Use secrets for sensitive data
- [ ] Enable SSL/TLS encryption
- [ ] Implement API authentication (JWT)
- [ ] Enable database encryption at rest
- [ ] Set up VPC/subnet isolation
- [ ] Enable audit logging
- [ ] Implement rate limiting
- [ ] Use Web Application Firewall (WAF)
- [ ] Regular security scanning (Trivy, Snyk)

## Production Checklist

- [ ] Load tested to 200K RPS
- [ ] Database properly indexed
- [ ] Caching strategy implemented
- [ ] Monitoring and alerting configured
- [ ] Backup and disaster recovery tested
- [ ] Auto-scaling configured
- [ ] Circuit breakers enabled
- [ ] Rate limiting configured
- [ ] SSL certificates installed
- [ ] DNS configured
- [ ] CDN configured (for static content)
- [ ] Log aggregation setup
- [ ] Security audit completed
- [ ] Performance benchmarks documented
- [ ] Runbooks created for incidents

## Support

For issues or questions:
- GitHub Issues: https://github.com/quickserve/delivery-platform/issues
- Documentation: https://docs.quickserve.com
- Slack: #quickserve-support

---

**Built to Scale. Ready for Production. 200K RPS and Beyond!**
