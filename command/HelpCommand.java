package command;

public class HelpCommand implements Command {
    @Override
    public void execute(String[] args) {
        System.out.println("Available commands:");
        System.out.println("plant [crop]      - Plant a crop.");
        System.out.println("harvest [plot]    - Harvest a specific plot or all plots.");
        System.out.println("quit              - Exit the game.");
        // 추가 명령어 설명
    }
}

