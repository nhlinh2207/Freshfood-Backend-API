FROM openjdk:8
ARG JAR_fILE=target/*.jar
COPY ${JAR_fILE} freshfoodBackend.jar
ENTRYPOINT ["java", "-jar", "/freshfoodBackend.jar"]
EXPOSE 8080

# Build jar : mvn clean package -DskipTests=true
# docker build -t nhlinh2207/freshfoodbackend:1 .
# docker run -p 8080:8080 -e DB_HOST=192.168.25.129 -e DB_USER=root -e DB_PASS=root nhlinh2207/freshfoodbackend:1