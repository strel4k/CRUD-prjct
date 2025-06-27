package com.crudApp.view;

import com.crudApp.controller.LabelController;
import com.crudApp.model.Label;

import java.util.List;
import java.util.Scanner;

public class LabelView {
    private final LabelController controller;
    private final Scanner scanner = new Scanner(System.in);

    public LabelView(LabelController controller) {
        this.controller = controller;
    }

    public void menu() {
        String input;
        do {
            System.out.println("\n--- Label меню ---");
            System.out.println("1. Создать");
            System.out.println("2. Обновить");
            System.out.println("3. Удалить");
            System.out.println("4. Найти по id");
            System.out.println("5. Показать все");
            System.out.println("0. Назад");
            System.out.print("Выберите опцию: ");

            if (!scanner.hasNextLine()) {
                System.out.println("Ввод недоступен. Завершение работы.");
                return;
            }

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
        System.out.print("Введите имя Label: ");
        String name = scanner.nextLine();
        Label label = controller.create(name);
        System.out.println("Создано: " + label);
    }

    private void update() {
        System.out.print("ID: ");
        Long id = Long.parseLong(scanner.nextLine());
        System.out.print("Новое имя: ");
        String name = scanner.nextLine();
        Label label = controller.update(id, name);
        System.out.println("Обновлено: " + label);
    }

    private void delete() {
        System.out.print("ID для удаления: ");
        Long id = Long.parseLong(scanner.nextLine());
        controller.delete(id);
        System.out.println("Удалено.");
    }

    private void getById() {
        System.out.print("ID для получения");
        Long id = Long.parseLong(scanner.nextLine());
        Label label = controller.getById(id);
        System.out.println(label);
    }

    private void getAll() {
        List<Label> all = controller.getAll();
        all.forEach(System.out::println);
    }
}
