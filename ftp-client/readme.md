# Запуск
Скомпилируйте проект:
```bash
./mvnw clean compile
```

Запустите проект:
```
./mvnw -q exec:java -Dexec.args="host port user password"
```

На Windows вместо `./mvnw` используйте `mvnw.cmd`
