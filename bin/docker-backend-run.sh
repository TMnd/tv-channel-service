source ./files/configurator.properties

docker run -i --rm \
  -p 8080:8080 \
  -e DB_HOST_URL=${DB_USER_NAME} \
  -e DB_TYPE=${DB_TYPE} \
  -e DB_USER_NAME=${DB_USER_NAME} \
  -e DB_USER_PASSWORD=${DB_USER_PASSWORD} \
  -e DB_HOST_URL=${DB_HOST_URL} \
  -e DB_DATABASE=${DB_DATABASE} \
  -e DB_SCHEMA=${DB_SCHEMA} \
  -e GELF_ENABLE=${GELF_ENABLE} \
  -e GELF_HOST=${GELF_HOST} \
  -e GELF_PORT=${GELF_PORT} \
  -e GELF_FACILITY=${GELF_FACILITY} \
  quarkus/tv-channel-service