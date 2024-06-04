[![Run Gradle Tests In Docker Container CI](https://github.com/yalosyash/qa-diploma/actions/workflows/gradle.yml/badge.svg)](https://github.com/yalosyash/qa-diploma/actions/workflows/gradle.yml)

# Процедура запуска автотестов

1. Установка приложений
2. Запуск программ
3. Запуск автотестов
    - с поддержкой MySQL
    - с поддержкой PostgreSQL
4. Генерация отчетов Allure

## 1. Установка приложений

Для запуска автотестов необходимо установить следующие приложения:

1. Среда и инструменты для разработки последних версий: JRE, JDK
2. Интегрированная среда разработки - IntelliJ IDEA Community Edition
3. Платформа контейнеризации - Docker Desktop
4. Браузер - Google Chrome

## 2. Запуск программ

1. Запустить Docker Desktop и свернуть
2. Склонировать [репозиторий](https://github.com/yalosyash/qa-diploma)
2. Открыть его в IntelliJ IDEA
3. Открыть локальный терминал IntelliJ IDEA
2. В первой вкладке терминала ввести команду `docker-compose up` - запустится докер-контейнер

## 3.1 Запуск автотестов с поддержкой MySQL

1. Во второй вкладке терминала ввести команду `java -jar ./artifacts/aqa-shop.jar` - запустится приложение с поддержкой
   СУБД MySQL
1. В третьей вкладке терминала ввести команду `.\gradlew clean test` - запустятся тесты
2. Для завершения работы приложения - во второй вкладке терминала нажать сочетание клавиш `CTRL + C`

## 3.2 Запуск автотестов с поддержкой PostgreSQL

1. Во второй вкладке терминала ввести
   команду `java "-Dspring.datasource.url=jdbc:postgresql://localhost:5432/app" -jar artifacts/aqa-shop.jar` -
   запустится приложение с поддержкой
   СУБД PostgreSQL
1. В третьей вкладке терминала ввести команду `.\gradlew clean test "-Ddb.url=jdbc:postgresql://localhost:5432/app"` - запустятся тесты
2. Для завершения работы приложения - во второй вкладке терминала нажать сочетание клавиш `CTRL + C`

## 4. Генерация отчетов Allure

- После прохождения тестов в третьей вкладке терминала ввести команду `.\gradlew allureServe`- сгенерируется Allure
  отчет
- Путь к отчету - `{склонированный репозиторий}/build/reports/allure-report/allureReport/index.html`