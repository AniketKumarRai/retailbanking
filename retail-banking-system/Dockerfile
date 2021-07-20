FROM adoptopenjdk/openjdk11
WORKDIR /
ADD target/Rules-0.0.1-SNAPSHOT.war rules-service.war 
EXPOSE 8090
CMD java -jar rules-service.war 
