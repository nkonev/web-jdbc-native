```
./gradlew clean bootBuildImage
docker-compose up -d
docker run --network=host --rm -p 8080:8080 docker.io/library/web-jdbc-native:0.0.1-SNAPSHOT
```