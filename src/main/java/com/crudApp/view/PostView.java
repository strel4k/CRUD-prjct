package com.crudApp.view;

import com.crudApp.controller.PostController;
import com.crudApp.model.Post;
import com.crudApp.model.PostStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class PostView {
    private final PostController controller;
    private final Scanner scanner = new Scanner(System.in);

    public PostView(PostController controller) {
        this.controller = controller;
    }

    public void menu() {
        String input;
        do {
            System.out.println("\n--- Post меню ---");
            System.out.println("1. Создать");
            System.out.println("2. Обновить");
            System.out.println("3. Удалить");
            System.out.println("4. Найти по id");
            System.out.println("5. Показать все");
            System.out.println("6. Изменить статус");
            System.out.println("0. Назад");
            System.out.print("Выберите опцию: ");

            if (!scanner.hasNextLine()) {
                System.out.println("Ввод недоступен. ББ");
                return;
            }

            input = scanner.nextLine();

            switch (input) {
                case "1" -> create();
                case "2" -> update();
                case "3" -> delete();
                case "4" -> getById();
                case "5" -> getAll();
                case "6" -> changeStatus();
                case "0" -> {}
                default -> System.out.println("Неверный ввод.");
            }
        } while (!input.equals("0"));
    }
    public void create() {
        System.out.print("Введите контент поста: ");
        String content = scanner.nextLine();
        System.out.print("Введите ID лейблов через запятую (например: 1,2,3): ");
        List<Long> labelIds = parseIdList(scanner.nextLine());
        Post post = controller.create(content, labelIds);
        System.out.println("Создано: " + post);
    }

    public void update() {
        System.out.print("Введите ID поста: ");
        Long id = Long.parseLong(scanner.nextLine());
        System.out.print("Новый контент: ");
        String content = scanner.nextLine();
        System.out.print("ведите ID лейблов через запятую: ");
        List<Long> labelIds = parseIdList(scanner.nextLine());
        Post post = controller.update(id, content, labelIds);
        System.out.println("Обновлено: " + post);
    }

    public void delete() {
        System.out.print("ID для удаления: ");
        Long id = Long.parseLong(scanner.nextLine());
        controller.delete(id);
        System.out.println("Удалено.");
    }

    public void getById() {
        System.out.print("ID поста: ");
        Long id = Long.parseLong(scanner.nextLine());
        Post post = controller.getById(id);
        System.out.println(post);
    }

    public void getAll() {
        List<Post> all = controller.getAll();
        all.forEach(System.out::println);
    }

    public void changeStatus() {
        System.out.print("ID поста: ");
        Long id = Long.parseLong(scanner.nextLine());
        System.out.print("Новый статус (ACTIVE, UNDER_REVIEW, DELETED): ");
        String statusStr = scanner.nextLine();
        try {
            PostStatus status = PostStatus.valueOf(statusStr.toUpperCase());
            Post post = controller.changeStatus(id, status);
            System.out.println("Статус обновлён: " + post);
        } catch (IllegalArgumentException e) {
            System.out.println("Неверный статус.");
        }
    }

    private List<Long> parseIdList(String input) {
        if (input == null || input.isBlank()) return new ArrayList<>();
        return Arrays.stream(input.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Long::parseLong)
                .toList();
    }
}
