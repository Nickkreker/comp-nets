# Запуск
Скомпилируйте проект:
```bash
./mvnw clean compile
```

## SMTP
### Решение на сокетах
```bash
./mvnw -q exec:java -Dexec.mainClass="org.example.smtpsocket.Main"
```

Для тестирования использовал локально развернутый SMTP-сервер mailcatcher. Для его запуска использовал команды: 
```bash
docker pull schickling/mailcatcher
docker run -d -p 1080:1080 -p 1025:1025 --name mailcatcher schickling/mailcatcher
```

Его админка находится на 1080 порту