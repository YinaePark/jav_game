package core;


import command.Command;

public class CommandProcessor {
    private final CommandRegistry registry;

    public CommandProcessor(CommandRegistry registry) {
        this.registry = registry;
    }

    public void process(String input) {
        String[] parts = input.split(" ", 2);
        String commandKey = parts[0].toLowerCase();
        String[] args = parts.length > 1 ? parts[1].split(" ") : new String[0];

        Command command = registry.getCommand(commandKey);
        if (command != null) {
            try {
                command.execute(args);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        } else {
            System.out.println("Unknown command. Type 'help' for a list of commands.");
        }
    }
}
