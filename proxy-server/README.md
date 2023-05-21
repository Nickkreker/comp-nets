# Запуск
Скомпилируйте проект:
```bash
./mvnw clean compile
```

В `config.properties` укажите порт, путь до логов и черный список доменов

Запуск прокси сервера:
```bash
./mvnw -q exec:java -Dexec.mainClass="org.example.Main"
```

На Windows вместо `./mvnw` используйте `mvnw.cmd`
