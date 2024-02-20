FROM maven:3-eclipse-temurin-17-alpine AS autodone
COPY / /tmp/autodone/
RUN mvn -f /tmp/autodone package spring-boot:repackage

FROM eclipse-temurin:17-jre-alpine
COPY --from=autodone /tmp/autodone/target/autodone-*.jar /opt
CMD java -jar /opt/autodone-*.jar
