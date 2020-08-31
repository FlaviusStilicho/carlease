FROM openjdk:11
RUN addgroup regularusers
RUN useradd -ms /bin/bash filthypeasant
USER filthypeasant:regularusers
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} carlease.jar
COPY ./wait-for-it.sh /
EXPOSE 8090
#ENTRYPOINT ["java","-jar","/carlease.jar"]