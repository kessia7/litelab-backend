FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY *.java /app/
COPY postgresql-42.7.13.jar /app/

RUN javac -cp ".:postgresql-42.7.13.jar" *.java

CMD ["java", "-cp", ".:postgresql-42.7.13.jar", "Main"]