FROM adoptopenjdk/openjdk11
WORKDIR /
ADD target/Authentication-MS-0.0.1-SNAPSHOT.war auth-app.war 
EXPOSE 8084
CMD java -jar auth-app.war 
