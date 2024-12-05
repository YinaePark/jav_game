//package command;
//
//import domain.item.ShopItem;
//import domain.player.Player;
//import domain.item.Item;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//public class BuyCommand implements Command {
//    private Player player;
//    private List<ShopItem> shopItems;
//
//    public BuyCommand(Player player) {
//        this.player = player;
////        this.shopItems = shopItems;
//
//        // 더미 데이터를 하드코딩하여 shopItems 리스트 초기화
//        shopItems = new ArrayList<>();
//        shopItems.add(new ShopItem("Apple", 10));     // 예시 아이템: Apple, 가격 10
//        shopItems.add(new ShopItem("Banana", 15));    // 예시 아이템: Banana, 가격 15
//        shopItems.add(new ShopItem("Carrot", 5));     // 예시 아이템: Carrot, 가격 5
//    }
//
//    @Override
//    public void execute(String[] args) {
////        System.out.println("Command arguments: " + Arrays.toString(args));  // args 내용 출력
//
//        // 커맨드 파싱
//        if (args.length != 2) {
//            System.out.println("Invalid command format. Use: buy [itemName] [itemCount]");
//            return;
//        }
//
//        String itemName = args[0];
//        int itemCount;
//        try {
//            itemCount = Integer.parseInt(args[1]);
//        } catch (NumberFormatException e) {
//            System.out.println("Invalid item count. Please provide a valid number.");
//
//            return;
//        }
//
//
//        // 상점에서 해당 아이템 찾기
//        ShopItem itemToBuy = findItemByName(itemName);
//        if (itemToBuy == null) {
//            System.out.println("Item " + itemName + " not found in the shop.");
//            return;
//        }
//
//        // 상품 가격과 총 비용 계산
//        double totalPrice = itemToBuy.getPrice() * itemCount;
//
//        // 돈이 충분한지, 인벤토리 공간이 충분한지 확인
//        if (player.getMoney() < totalPrice) {
//            System.out.println("Not enough money to buy " + itemCount + " " + itemName + "(s).");
//            return;
//        }
//
//        if (player.getInventorySize() + itemCount > player.getMaxInventorySize()) {
//            System.out.println("Not enough inventory space to buy " + itemCount + " " + itemName + "(s).");
//            return;
//        }
//
//        // 구매 가능
//        if (player.spendMoney(totalPrice)) {
//            for (int i = 0; i < itemCount; i++) {
//                player.addItem(itemToBuy);  // 인벤토리에 아이템 추가
//            }
//            System.out.println("Successfully bought " + itemCount + " " + itemName + "(s) for " + totalPrice + " money.");
//        }
//    }
//
//    // 상점에서 아이템을 이름으로 찾는 메소드
//    private ShopItem findItemByName(String itemName) {
//        for (ShopItem item : shopItems) {
//            if (item.getName().equalsIgnoreCase(itemName)) {
//                return item;
//            }
//        }
//        return null;  // 아이템을 찾을 수 없으면 null 반환
//    }
//}
