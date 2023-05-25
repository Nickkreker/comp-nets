# Запуск
Скомпилируйте проект:
```bash
./mvnw clean compile
```

## Реализация протокола Stop and Wait
**Запуск клиента:**
```bash
./mvnw -q exec:java -Dexec.mainClass="org.example.rdt.client.ClientMain"
```
Менять таймаут можно в коде


**Запуск сервера:**
```bash
./mvnw -q exec:java -Dexec.mainClass="org.example.rdt.server.ServerMain"
```
