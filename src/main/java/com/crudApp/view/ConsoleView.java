package com.crudApp.view;

import java.util.Scanner;

public class ConsoleView {
    private final LabelView labelView;
    private final PostView postView;
    private final WriterView writerView;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleView(LabelView labelView, PostView postView, WriterView writerView) {
        this.labelView = labelView;
        this.postView = postView;
        this.writerView = writerView;
    }

    public void start() {
        String input;
        do {
            System.out.println("\n=== Главное меню ===");
            System.out.println("1. Label");
            System.out.println("2. Post");
            System.out.println("3. Writer");
            System.out.println("0. Выход");
            System.out.print("Выберите опцию: ");
            input = scanner.nextLine();

            switch (input) {
                case "1" -> labelView.menu();
                case "2" -> postView.menu();
                case "3" -> writerView.menu();
                case "0" -> System.out.println("Выход");
                default -> System.out.println("Некорректный ввод");
            }
        } while (!input.equals(0));
    }
}
