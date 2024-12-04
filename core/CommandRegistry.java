package core;

import command.Command;
import command.HelpCommand;
import command.PlantCommand;
import command.TillCommand;
import command.*;
import domain.Farm;
import domain.player.Player;
import domain.item.Item;

import java.util.HashMap;
import java.util.Map;

public class CommandRegistry {
    private final Map<String, Command> commands = new HashMap<>();
    private Player player;
    private Farm farm;

    // Player 객체를 전달받는 생성자
    public CommandRegistry(Player player, Farm farm) {
        this.player = player;
        this.farm = farm;
    }

    // 명령어 등록
    public void register(String name, Command command) {
        commands.put(name, command);
    }

    // 명령어 가져오기
    public Command getCommand(String name) {
        return commands.get(name);
    }

    // 기본 명령어 등록
    public void registerDefaults() {
        // PlayerCommand 생성 시 player 객체를 전달
        register("till", new TillCommand(null));
        register("plant", new PlantCommand(player, farm, null, null));
        register("help", new HelpCommand());
        register("player", new PlayerCommand(player)); // PlayerCommand에 player 객체 전달
        register("farm", new FarmCommand(farm));
        register("harvest", new HarvestCommand(player, farm));
        register("quit", args -> System.out.println("Exiting the game... Goodbye!"));
    }
}
