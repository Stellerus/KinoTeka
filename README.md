# KinoTeKa — Кинотека

Приложение для учёта фильмов и сериалов с постоянным хранением в локальном JSON-файле.

## Требования
- Android Studio Hedgehog (2023.1.1) или новее
- Kotlin 1.9+
- Gradle 8.x
- Минимальная версия Android: API 26 (Android 8.0)
- Целевая версия: API 34

## Структура проекта
- `app/src/main/java/com/example/kinoteka/` — исходный код
  - `MainActivity.kt` — главный экран со списком
  - `DetailActivity.kt` — экран деталей (edge‑to‑edge)
  - `model/` — модели данных (Movie, Series)
  - `repository/` — репозиторий для работы с JSON
  - `adapter/` — адаптер RecyclerView
- `app/src/main/res/layout/` — макеты экранов

## Сборка и запуск
1. Откройте проект в Android Studio
2. Синхронизируйте Gradle
3. Запустите на устройстве или эмуляторе с API ≥ 26

## Примечание
Все данные сохраняются в файл `kinoteka.json` во внутренней памяти приложения.
При удалении приложения файл удаляется.
