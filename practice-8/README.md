# Запуск
Скомпилируйте проект:
```bash
./mvnw clean compile
```

## Реализация протокола Stop and Wait
Сначала запускается сервер

**Запуск сервера:**
```bash
./mvnw -q exec:java -Dexec.mainClass="org.example.rdt.server.ServerMain"
```

**Запуск клиента:**
```bash
./mvnw -q exec:java -Dexec.mainClass="org.example.rdt.client.ClientMain"
```
Менять таймаут можно в коде

## Контрольные суммы
**Запуск примеров:**
```bash
./mvnw -q exec:java -Dexec.mainClass="org.example.csum.Main"
```
