package core;
import command.*;
import domain.Farm;
import domain.item.ShopItem;
import domain.player.Player;
import game.ui.GamePanel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandRegistry {
    private final Map<String, Command> commands = new HashMap<>();
    private Player player;
    private Farm farm;
    private GamePanel gamePanel;

    public CommandRegistry(Player player, Farm farm) {
        this.player = player;
        this.farm = farm;
    }

    public void register(String name, Command command) {
        commands.put(name, command);
    }

    public Command getCommand(String name) {
        return commands.get(name);
    }

    public void registerDefaults() {
        register("till", new TillCommand(null));
        register("plant", new PlantCommand(player, farm, null, null, gamePanel));
        register("help", new HelpCommand());
        register("player", new PlayerCommand(player));
        register("farm", new FarmCommand(farm));
        register("harvest", new HarvestCommand(player, farm, null, null, null));
        register("quit", exit -> {
            System.out.println("Exiting game... Goodbye!");
            System.exit(0);
        });
    }
}
