# Запуск
Скомпилируйте проект:
```bash
./mvnw clean compile
```

## IP адрес и маска сети
Запуск программы:
```bash
./mvnw -q exec:java -Dexec.mainClass="org.example.ipchecker.IpChecker"
```

В результате будут выведены адреса всех сетевых интерфейсов вместе с масками подсети
