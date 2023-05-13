# Запуск
Скомпилируйте проект:
```bash
./mvnw clean compile
```

Запуск сервера:
```bash
./mvnw -q exec:java -Dexec.mainClass="org.example.server.Main" -Dexec.args="порт_сервера waitPeriod"
```

Запуск клиента:
```bash
./mvnw -q exec:java -Dexec.mainClass="org.example.client.Main" -Dexec.args="ip_сервера порт_сервера"
```

На Windows вместо `./mvnw` используйте `mvnw.cmd`
