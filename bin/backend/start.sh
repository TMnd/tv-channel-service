#!/bin/bash
#/opt/amaral-software/tv-channel-service/bin/configurator.sh /opt/amaral-software/tv-channel-service/config/application.properties.SAMPLE /opt/amaral-software/tv-channel-service/bin/configurator.properties

echo ${DB_USER_NAME}
echo ${DB_USER_PASSWORD}
echo ${DB_HOST_URL}
echo ${DB_DATABASE}
echo ${DB_SCHEMA}
echo ${GELF_ENABLE}
echo ${GELF_HOST}
echo ${GELF_PORT}
echo ${GELF_FACILITY}

/opt/jboss/container/java/run/run-java.sh