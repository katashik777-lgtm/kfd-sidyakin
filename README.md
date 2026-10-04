# База после занятия

Обычное имя и null обрабатываются правильно. CI выполняет Assemble и Test. Задания A и B находятся в STUDENT_ASSIGNMENT.md.

Откройте этот каталог в IntelliJ IDEA как Gradle-проект.
Основной код: `app/src/main/kotlin/kfd/`. Тесты: `app/src/test/kotlin/kfd/`.

В IDEA запустите тестовый класс стрелкой рядом с его объявлением. Все проверки в терминале из корня проекта:

```powershell
.\gradlew.bat assemble
.\gradlew.bat test --rerun-tasks
```

В Bash:

```bash
bash ./gradlew assemble
bash ./gradlew test --rerun-tasks
```

Ожидается 2 успешных теста.

`assemble` собирает основной код, `test` компилирует и выполняет тесты. Первый запуск скачивает Gradle и зависимости.

Workflow находится в `.github/workflows/ci.yml` и запускается после `push`. В GitHub откройте Actions → запуск для своего коммита → check → нужный шаг.

[Полный текст задания](STUDENT_ASSIGNMENT.md).
