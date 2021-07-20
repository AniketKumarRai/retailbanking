FROM adoptopenjdk/openjdk11
WORKDIR /
ADD target/CustomerService-0.0.1-SNAPSHOT.war customer-service.war 
EXPOSE 8085
CMD java -jar customer-service.war 
