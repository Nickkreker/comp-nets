# Запуск
Скомпилируйте проект:
```bash
./mvnw clean compile
```

## RIP
Запуск симуляции:
```bash
./mvnw -q exec:java -Dexec.mainClass="org.example.rip.Main"
```

Конфигурация читается из файла `rip.json`, расположенного в resources. Результат симуляции и промежуточные шаги пишутся в файл `log.txt`.


## Скорость передачи
__В UI управление происходит стрелками и нажатием клавиши Enter__

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

Конфигурация читается из файла `forwardes.csv`, который передается аргументом в программу. После обновления конфигурации необходимо выбрать `<Обновить конфигурацию>`
