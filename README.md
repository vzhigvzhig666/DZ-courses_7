# Тестирование интерфейса Wikipedia

Автоматические проверки главной страницы и перехода в раздел About Wikipedia.

Для запуска нужны JDK 21, Appium и Android-эмулятор или iOS-симулятор.

Запустить Appium, затем выполнить нужную команду:

```shell
./gradlew androidTest
./gradlew iosTest
```

Отчёт находится в `reports/test-report.md`.
