//package core;
//
//import domain.player.Player;
//import domain.Shop;
//import domain.Farm;
//import java.util.Scanner;
//
//public class GameManager {
//    private Player player;
//    private Shop shop;
//    private Farm farm;
//    private CommandRegistry registry;
//    private CommandProcessor processor;
//    private boolean isRunning;
//
//    public GameManager() {
//        this.player = new Player("Player1"); // 기본 플레이어 설정
//        this.shop = new Shop();
//        this.farm = new Farm();
//        this.registry = new CommandRegistry();
//        this.processor = new CommandProcessor(registry);
//        this.isRunning = true;
//        registry.registerDefaults(); // 기본 명령어 등록
//    }
//
//    // 게임을 시작하는 메서드
//    public void start() {
//        Scanner scanner = new Scanner(System.in);
//        System.out.println("Welcome to the Game!");
//        System.out.println("Type 'help' for a list of commands or 'quit' to exit.");
//
//        while (isRunning) {
//            System.out.print("> ");
//            String input = scanner.nextLine();
//            if ("quit".equalsIgnoreCase(input)) {
//                isRunning = false;
//            } else {
//                processor.process(input);  // 명령어 처리
//            }
//        }
//
//        System.out.println("Exiting the game... Goodbye!");
//    }
//
//    // 게임 상태를 반환 (디버깅 또는 상태 확인 용도)
//    public boolean isRunning() {
//        return isRunning;
//    }
//
//    // 플레이어 객체를 반환
//    public Player getPlayer() {
//        return player;
//    }
//
//    // 상점 객체를 반환
//    public Shop getShop() {
//        return shop;
//    }
//
//    // 농장 객체를 반환
//    public Farm getFarm() {
//        return farm;
//    }
//
//    // 종료 메서드
//    public void stop() {
//        this.isRunning = false;
//    }
//}
