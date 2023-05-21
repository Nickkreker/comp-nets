# Запуск
Скомпилируйте проект:
```bash
./mvnw clean compile
```

## Почта и SMTP
Для тестирования использовал локально развернутый SMTP-сервер mailcatcher. Для его запуска использовал команды:
```bash
docker pull schickling/mailcatcher
docker run -d -p 1080:1080 -p 1025:1025 --name mailcatcher schickling/mailcatcher
```

Его админка находится на 1080 порту.

### Решение с помощью библиотек
```bash
./mvnw -q exec:java -Dexec.mainClass="org.example.mail.smtp.Main" -Dexec.args="receiver@mail.ru"
```


### Решение на сокетах
```bash
./mvnw -q exec:java -Dexec.mainClass="org.example.mail.smtpsocket.smtpsocket.Main"
```

## Удаленный запуск команд
Запуск клиента:
```bash
./mvnw -q exec:java -Dexec.mainClass="org.example.rpc.client.Client" -Dexec.args="127.0.0.1 8080 'ping yandex.ru'"
```

Запуск сервера:
```bash
./mvnw -q exec:java -Dexec.mainClass="org.example.rpc.server.Main" -Dexec.args="8080"
```