FROM openjdk:17

WORKDIR /app

COPY . /app

COPY C:/Users/huawei/javaProjects/javafx-sdk-17.0.1/lib /app/lib

EXPOSE 8080

CMD ["java", "--module-path", "/app/lib", "--add-modules", "javafx.controls,javafx.graphics,javafx.fxml", "-jar", "Gestion-Dune_Filliere.jar"]
