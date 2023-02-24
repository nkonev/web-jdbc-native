```
./gradlew clean bootBuildImage
docker-compose up -d
docker run --network=host --rm docker.io/library/web-jdbc-native:0.0.1-SNAPSHOT
```
