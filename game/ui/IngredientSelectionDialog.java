package game.ui;
import game.entity.NormalCustomer;
import domain.item.Item;
import domain.player.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class IngredientSelectionDialog extends JDialog {
    public static final int MAX_INGREDIENTS = 5;  // 최대 재료 개수
    private InventoryPanel inventoryPanel;
    private Player player;
    private Item[] selectedIngredients;
    private JPanel ingredientSlotsPanel;
    private ActionListener submitListener;
    private NormalCustomer normalCustomer;

    public IngredientSelectionDialog(Frame owner, Player player,NormalCustomer normalCustomer) {
        super(owner, "Select Ingredients", true);  // Modal로 띄운다.
        this.player = player;
        this.normalCustomer = normalCustomer;
        this.selectedIngredients = new Item[MAX_INGREDIENTS];
        initialize();
    }

    private void onSubmitButtonClicked() {
        // NormalCustomer에서 사용될 플레이어 객체와 선택된 재료들을 전달
        if (player != null && selectedIngredients != null) {
            // Customer 객체를 생성하고, 만족도를 계산
            List<String> selectedIngredientNames = new ArrayList<>();
            for (Item ingredient : selectedIngredients) {
                if (ingredient != null) {
                    selectedIngredientNames.add(ingredient.getName());
                }
            }

            // 만족도 업데이트
            normalCustomer.updateSatisfaction(selectedIngredientNames);

            // 보상 계산
            int reward = normalCustomer.calculateReward();

            // 보상 지급
            player.earnMoney(reward);  // player.addMoney는 플레이어 클래스에서 보상을 추가하는 메서드

            // 결과 메시지 표시
            JOptionPane.showMessageDialog(this, "You have earned " + reward + " money!");
        }

        // 대화 상자 닫기
        dispose();
    }

    private void initialize() {
        setLayout(new BorderLayout());

        // 인벤토리 패널 생성 (아이템 선택을 위한 패널)
        inventoryPanel = new InventoryPanel(player, null); // GamePanel은 필요없어서 null로 설정
        add(inventoryPanel, BorderLayout.CENTER);
        inventoryPanel.updateInventory(player.getInventory());  // 플레이어의 아이템을 인벤토리에 표시

        // 재료 선택 슬롯 패널
        ingredientSlotsPanel = new JPanel(new GridLayout(1, MAX_INGREDIENTS));
        for (int i = 0; i < MAX_INGREDIENTS; i++) {
            final int index = i;
            JLabel slotLabel = new JLabel("Slot " + (i + 1));
            slotLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));  // 슬롯에 테두리 추가
            slotLabel.setPreferredSize(new Dimension(80, 80));  // 슬롯 크기 설정
            slotLabel.setHorizontalAlignment(SwingConstants.CENTER);  // 텍스트 및 아이콘 중앙 정렬
            slotLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    openIngredientSelectionDialog(index);
                }
            });
            ingredientSlotsPanel.add(slotLabel);
        }
        add(ingredientSlotsPanel, BorderLayout.SOUTH);

        // 제출 버튼
        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> onSubmitButtonClicked());  // onSubmitButtonClicked를 직접 호출
        add(submitButton, BorderLayout.NORTH);

        setSize(400, 500);  // 창 크기 조정
        setLocationRelativeTo(null);  // 창 가운데에 띄우기
    }

    private void updateIngredientSlot(int slotIndex, Item selectedItem) {
        // 기존 JLabel에 아이템의 아이콘과 이름을 표시
        JLabel slotLabel = (JLabel) ingredientSlotsPanel.getComponent(slotIndex);
        slotLabel.setIcon(new ImageIcon(selectedItem.getSprite(50, 50)));  // 50x50 크기의 아이템 이미지
        slotLabel.setText(selectedItem.getName());  // 아이템 이름
        slotLabel.setHorizontalTextPosition(SwingConstants.CENTER);  // 텍스트 중앙 정렬
        slotLabel.setVerticalTextPosition(SwingConstants.BOTTOM);  // 텍스트 아래쪽에 표시
        slotLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));  // 테두리 추가
    }

    private void openIngredientSelectionDialog(int slotIndex) {
        Item selectedItem = inventoryPanel.getSelectedItem();
        if (selectedItem != null) {
            // 재료 슬롯에 아이템 채우기
            selectedIngredients[slotIndex] = selectedItem;
            // 아이템을 인벤토리에서 삭제
            player.removeItem(selectedItem.getName());

            // 재료 슬롯에 아이템 아이콘과 이름을 표시
            updateIngredientSlot(slotIndex, selectedItem);

            // 인벤토리 업데이트
            inventoryPanel.updateInventory(player.getInventory());

            JOptionPane.showMessageDialog(this, "Selected " + selectedItem.getName() + " for Slot " + (slotIndex + 1));
        } else {
            JOptionPane.showMessageDialog(this, "No item selected.");
        }
    }




    public void addSubmitListener(ActionListener listener) {
        this.submitListener = listener;
    }

    public Item getSelectedIngredient(int index) {
        return selectedIngredients[index];
    }
}
