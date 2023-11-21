#source $PWD/../buildFiles/files/configurator.properties

docker build \
  --build-arg DB_USER_NAME=${DB_USER_NAME} \
  --build-arg DB_USER_PASSWORD=${DB_USER_PASSWORD} \
  --build-arg DB_HOST_URL=${DB_HOST_URL} \
  --build-arg DB_DATABASE=${DB_DATABASE} \
  --build-arg DB_SCHEMA=${DB_SCHEMA} \
  --build-arg GELF_ENABLE=${GELF_ENABLE} \
  --build-arg GELF_HOST=${GELF_HOST} \
  --build-arg GELF_PORT=${GELF_PORT} \
  --build-arg GELF_FACILITY=${GELF_FACILITY} \
  -f $PWD/docker/Dockerfile-backend \
  -t quarkus/tv-channel-service \
  ..

ARG DB_TYPE