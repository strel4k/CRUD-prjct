# CRUD App (Console Version)

📌 Простое CRUD-приложение на Java для управления сущностями `Writer`, `Post` и `Label` с использованием JDBC и реляционной базы данных MySQL.

## 🪄 Функциональность

- 🗿 **Writer**: создание, обновление, удаление, просмотр
- 📝 **Post**: привязка к Writer, список Label, редактирование, статус
- 🏷️ **Label**: базовое управление тегами

## 🪜 Технологии

- **Java 17**
- **JDBC** (через `PreparedStatement`)
- **MySQL 8+**
- **Gradle** (Kotlin DSL)
- **Maven-style структура проекта**
- **Паттерн Repository**
- **Singleton JdbcUtil**
- **ResultSetMapper** для преобразования данных из БД в объекты

## 🗂️ Структура проекта
src
└── main
└── java
└── com.crudApp
├── model # Writer, Post, Label, PostStatus
├── repository # Интерфейсы
├── repository.impl # JDBC-реализации
└── util # JdbcUtil, ResultSetMapper

## 🪃 Как запустить

1. Установи MySQL и создай базу данных `prjct_app`
2. Пропиши свои креды в `JdbcUtil.java`:
   ```java
   private static final String URL = "jdbc:mysql://localhost:3306/prjct_app";
   private static final String USER = "root";
   private static final String PASSWORD = "your_password";

▶️ Запуск через IDE или консоль:
   ./gradlew run
