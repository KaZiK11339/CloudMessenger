# Cloud Messenger

Приложение для обмена сообщениями с поддержкой реального времени и push-уведомлений.

## Особенности

- 🚀 Обмен сообщениями в реальном времени с WebSocket
- 📱 Push-уведомления с Firebase Cloud Messaging
- 🔄 Синхронизация статусов сообщений (отправлено, доставлено, прочитано)
- 👥 Групповые чаты
- 📁 Обмен файлами и медиа
- 🔒 Безопасность с Spring Security

## Требования

- JDK 8+
- Maven 3.6+
- Docker и Docker Compose (для запуска в контейнере)
- Аккаунт Firebase (для push-уведомлений и realtime DB)

## Настройка Firebase

1. Создайте проект в [Firebase Console](https://console.firebase.google.com/)
2. Активируйте Firebase Realtime Database и Firebase Cloud Messaging
3. В настройках проекта, во вкладке "Service accounts", нажмите "Generate new private key"
4. Скачанный JSON файл переименуйте в `firebase-service-account.json` и поместите в корень проекта
5. В Firebase Console, на странице настроек проекта, найдите:
   - `apiKey`
   - `authDomain`
   - `databaseURL`
   - `projectId` 
   - `storageBucket`
   - `messagingSenderId`
   - `appId`
6. Обновите эти значения в следующих файлах:
   - `src/main/resources/application.properties`
   - `src/main/webapp/WEB-INF/resources/js/firebase-client.js`
   - `src/main/webapp/firebase-messaging-sw.js`

## Запуск в Docker

1. Убедитесь, что Docker и Docker Compose установлены
2. Поместите `firebase-service-account.json` в корень проекта
3. Запустите приложение:

```bash
docker-compose up -d
```

Приложение будет доступно по адресу: http://localhost:8080

Adminer (для работы с базой данных) будет доступен по адресу: http://localhost:8081

## Запуск без Docker

1. Соберите проект:

```bash
mvn clean package
```

2. Скопируйте полученный WAR файл (`target/cloudmessenger.war`) в директорию `webapps` вашего Tomcat
3. Скопируйте `firebase-service-account.json` в корневую директорию Tomcat
4. Запустите Tomcat

## Настройка приложения

1. Перейдите по адресу http://localhost:8080 (или соответствующему URL вашего сервера)
2. Зарегистрируйте пользователя с email `admin@cloudmessenger.com` - он автоматически получит права администратора
3. Войдите под созданным пользователем

## Структура проекта

- `src/main/java/com/cloudmessenger` - Исходный код приложения
  - `config` - Конфигурационные классы
  - `controller` - Контроллеры для обработки HTTP и WebSocket запросов
  - `model` - Модели данных (Hibernate сущности)
  - `service` - Сервисные классы
  - `utils` - Вспомогательные утилиты
- `src/main/resources` - Ресурсы приложения
- `src/main/webapp` - Web-ресурсы (JSP, статические файлы)

## Развитие проекта

### Добавление новых функций

1. Модификация моделей: `src/main/java/com/cloudmessenger/model/`
2. Обновление сервисов: `src/main/java/com/cloudmessenger/service/`
3. Добавление контроллеров: `src/main/java/com/cloudmessenger/controller/`
4. Обновление представлений: `src/main/webapp/WEB-INF/views/`

### Миграция на новые версии

Проект использует:
- Spring MVC 5.3.x
- Spring Security 5.8.x
- Hibernate 5.6.x
- Firebase Admin SDK 9.2.0

Для миграции на новые версии обновите зависимости в `pom.xml`.