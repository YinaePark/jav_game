package core;

import command.Command;
import command.HelpCommand;
import command.PlantCommand;
import command.TillCommand;

import java.util.HashMap;
import java.util.Map;

public class CommandRegistry {
    private final Map<String, Command> commands = new HashMap<>();

    public void register(String name, Command command) {
        commands.put(name, command);
    }

    public Command getCommand(String name) {
        return commands.get(name);
    }

    public void registerDefaults() {
        register("plant", new PlantCommand(null, null));
        register("till", new TillCommand(null));  
        register("help", new HelpCommand());
        register("quit", args -> System.out.println("Exiting the game... Goodbye!"));
    }
}
