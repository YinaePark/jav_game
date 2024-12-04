package main;

import java.util.Scanner;
import core.CommandProcessor;
import core.CommandRegistry;


public class Main {
    private static boolean isRunning = true;

    public static void main(String[] args) {
        CommandRegistry registry = new CommandRegistry();
        registry.registerDefaults();
        CommandProcessor processor = new CommandProcessor(registry);
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to Game!");
        System.out.println("Type 'help' for a list of commands or 'quit' to exit.");

        // The main game loop
        while (isRunning) {
            System.out.print("> ");
            String input = scanner.nextLine();
            if ("quit".equalsIgnoreCase(input)) {
                isRunning = false;
            } else {
                processor.process(input);
            }
        }

        System.out.println("Exiting the game... \nGoodbye!");
    }

}
