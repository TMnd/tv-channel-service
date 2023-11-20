docker run -i --rm \
  -p 8080:8080 \
  -e DB_TYPE="ola" \
  quarkus/tv-channel-service