FROM adoptopenjdk/openjdk11
WORKDIR /
ADD target/account-service.war account-service.war
EXPOSE 8086
CMD java -jar account-service.war