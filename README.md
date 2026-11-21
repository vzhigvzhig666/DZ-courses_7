# UI-тесты приложения Wikipedia

Black-box тесты двух экранов: списка языков Wikipedia и добавления языка

## Запуск

Понадобятся JDK 21, Appium 3 с UiAutomator2 и Android-эмулятор на английском

1. Установить [Wikipedia из Google Play](https://play.google.com/store/apps/details?id=org.wikipedia) или [официальный APK Wikimedia](https://releases.wikimedia.org/mobile/android/wikipedia/stable/)
2. В списке языков приложения должен быть только English
3. Запустить Appium с настроенным `ANDROID_HOME` и выполнить:

```sh
./gradlew androidTest
```

По умолчанию используется `emulator-5554`. Для другого эмулятора указать его ID в `ANDROID_SERIAL`

[Отчёт и сценарии](reports/test-report.md)
