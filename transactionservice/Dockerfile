FROM adoptopenjdk/openjdk11
WORKDIR /
ADD target/transactionservice-0.0.1-SNAPSHOT.war transaction-service.war
EXPOSE 8087
CMD java -jar transaction-service.war
