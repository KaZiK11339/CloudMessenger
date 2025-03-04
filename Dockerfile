FROM maven:3.8.4-openjdk-8-slim as builder

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем файлы pom.xml и src
COPY pom.xml .
COPY src ./src

# Собираем проект
RUN mvn clean package -DskipTests

FROM tomcat:9.0-jdk8-openjdk-slim

# Имя приложения
ENV APP_NAME=cloudmessenger

# Удаляем стандартные приложения Tomcat из webapps
RUN rm -rf /usr/local/tomcat/webapps/*

# Копируем скомпилированное приложение из предыдущего этапа
COPY --from=builder /app/target/${APP_NAME}.war /usr/local/tomcat/webapps/ROOT.war

# Создаем директории для данных
RUN mkdir -p /usr/local/tomcat/cloudmessenger/uploads

# Копирование Firebase конфигурации
COPY firebase-service-account.json /usr/local/tomcat/

# Открываем порт
EXPOSE 8080

# Запускаем Tomcat
CMD ["catalina.sh", "run"] 