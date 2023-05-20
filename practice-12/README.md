# Запуск
Скомпилируйте проект:
```bash
./mvnw clean compile
```

## Скорость передачи
### TCP
Запуск сервера:
```bash
./mvnw -q exec:java -Dexec.mainClass="org.example.throughput.tcp.Server"
```

Запуск клиента:
```bash
./mvnw -q exec:java -Dexec.mainClass="org.example.throughput.tcp.Client"
```

### UDP
Запуск сервера:
```bash
./mvnw -q exec:java -Dexec.mainClass="org.example.throughput.udp.Server"
```

Запуск клиента:
```bash
./mvnw -q exec:java -Dexec.mainClass="org.example.throughput.udp.Client"
```

## Транслятор портов
Запуск транслятора:
```bash
./mvnw -q exec:java -Dexec.mainClass="org.example.forwarder.Main" -Dexec.args="forwardes.csv"
```