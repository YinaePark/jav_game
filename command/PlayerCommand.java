package command;

import domain.player.Player;

public class PlayerCommand implements Command {
    private Player player;

    public PlayerCommand(Player player) {
        this.player = player;
    }

    public PlayerCommand() {

    }

    @Override
    public void execute(String[] args) {
        // 플레이어의 현재 돈 출력
        System.out.println("Current money: " + player.getMoney());

        // 플레이어의 인벤토리 출력
        player.printInventory();  // Player 클래스에서 인벤토리 출력 메서드를 사용
    }
}
