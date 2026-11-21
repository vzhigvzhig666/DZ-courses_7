# UI-тесты приложения Wikipedia

Black-box тесты списка языков и экрана добавления языка в Wikipedia на Android и iOS

## Android

Понадобятся JDK 21, Appium 3 с UiAutomator2 и Android-эмулятор на английском

1. Установить [Wikipedia из Google Play](https://play.google.com/store/apps/details?id=org.wikipedia) или [официальный APK Wikimedia](https://releases.wikimedia.org/mobile/android/wikipedia/stable/)
2. В списке языков приложения должен быть только English
3. Запустить Appium с настроенным `ANDROID_HOME` и выполнить:

```sh
./gradlew androidTest
```

По умолчанию используется `emulator-5554`. Для другого эмулятора указать его ID в `ANDROID_SERIAL`

## iOS

Понадобятся JDK 21, Xcode с iPhone Simulator на английском и Appium 3 с XCUITest

1. Собрать [релиз Wikipedia 2026.10.13](https://github.com/wikimedia/wikipedia-ios/tree/releases/2026.10.13#building-and-running) для симулятора и установить его
2. Оставить в списке языков приложения только English
3. Запустить Appium и выполнить команду

```sh
./gradlew iosTest
```

По умолчанию используется iPhone 17 Pro с iOS 26.5. Для другого симулятора указать его ID в `IOS_UDID`

[Отчёт и сценарии](reports/test-report.md)
