# Запуск
Скомпилируйте проект:
```bash
./mvnw clean compile
```

Запуск сервера:
```bash
./mvnw -q exec:java -Dexec.mainClass="org.example.server.Main" -Dexec.args="порт_сервера concurrencyLevel"
```

Запуск клиента:
```bash
./mvnw -q exec:java -Dexec.mainClass="org.example.client.Main" -Dexec.args="хост_сервера порт_сервера имя_файла"
```

На Windows вместо `./mvnw` используйте `mvnw.cmd`
