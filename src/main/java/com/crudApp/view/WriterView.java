package com.crudApp.view;

import com.crudApp.controller.WriterController;
import com.crudApp.model.Writer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class WriterView {

    private final WriterController controller;
    private final Scanner scanner = new Scanner(System.in);

    public WriterView(WriterController controller) {
        this.controller = controller;
    }

    public void menu() {
        String input;
        do {
            System.out.println("\n--- Writer меню ---");
            System.out.println("1. Создать");
            System.out.println("2. Обновить");
            System.out.println("3. Удалить");
            System.out.println("4. Найти по id");
            System.out.println("5. Показать всех");
            System.out.println("0. Назад");
            System.out.print("Выберите опцию: ");
            input = scanner.nextLine();

            switch (input) {
                case "1" -> create();
                case "2" -> update();
                case "3" -> delete();
                case "4" -> getById();
                case "5" -> getAll();
                case "0" -> {}
                default -> System.out.println("Неверный ввод.");
            }

        } while (!input.equals("0"));
    }

    private void create() {
        System.out.print("Имя: ");
        String firstName = scanner.nextLine();
        System.out.print("Фамилия: ");
        String lastName = scanner.nextLine();
        System.out.print("ID постов через запятую (или пусто): ");
        List<Long> postIds = parseIdList(scanner.nextLine());

        Writer writer = controller.create(firstName, lastName, postIds);
        System.out.println("Создан: " + writer);
    }

    private void update() {
        System.out.print("ID автора: ");
        Long id = Long.parseLong(scanner.nextLine());
        System.out.print("Новое имя: ");
        String firstName = scanner.nextLine();
        System.out.print("Новая фамилия: ");
        String lastName = scanner.nextLine();
        System.out.print("Новые ID постов через запятую (или пусто): ");
        List<Long> postIds = parseIdList(scanner.nextLine());

        Writer writer = controller.update(id, firstName, lastName);
        System.out.println("Обновлено: " + writer);
    }

    private void delete() {
        System.out.print("ID для удаления: ");
        Long id = Long.parseLong(scanner.nextLine());
        controller.delete(id);
        System.out.println("Удалено.");
    }

    private void getById() {
        System.out.print("ID автора: ");
        Long id = Long.parseLong(scanner.nextLine());
        Writer writer = controller.getById(id);
        System.out.println(writer);
    }

    private void getAll() {
        List<Writer> writers = controller.getAll();
        writers.forEach(System.out::println);
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
