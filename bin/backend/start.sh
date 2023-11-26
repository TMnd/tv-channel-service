#!/bin/bash

#Db-kind is one of the properties that can't be changed at runTime, so is is hardcoded in the application.properties file.
JAVA_OPTS_APPEND+=" -Dquarkus.datasource.username=${DB_USER_NAME} "
JAVA_OPTS_APPEND+="-Dquarkus.datasource.password=${DB_USER_PASSWORD} "
JAVA_OPTS_APPEND+="-Dquarkus.datasource.jdbc.url=jdbc:postgresql://${DB_HOST_URL}/${DB_DATABASE}?currentSchema=${DB_SCHEMA} "
JAVA_OPTS_APPEND+="-Dquarkus.flyway.schemas=${DB_SCHEMA} "
JAVA_OPTS_APPEND+="-Dquarkus.flyway.table=flyway_${DB_SCHEMA}_history "
JAVA_OPTS_APPEND+="-Dquarkus.log.handler.gelf.enabled=${GELF_ENABLE} "
JAVA_OPTS_APPEND+="-Dquarkus.log.handler.gelf.host=${GELF_HOST} "
JAVA_OPTS_APPEND+="-Dquarkus.log.handler.gelf.port=${GELF_PORT} "
JAVA_OPTS_APPEND+="-Dquarkus.log.handler.gelf.facility=${GELF_FACILITY}"

/opt/jboss/container/java/run/run-java.sh