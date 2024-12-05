package game.ui;

import domain.item.Item;
import domain.player.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class IngredientSelectionDialog extends JDialog {
    public static final int MAX_INGREDIENTS = 5;  // 최대 재료 개수
    private InventoryPanel inventoryPanel;
    private Player player;
    private Item[] selectedIngredients;
    private JPanel ingredientSlotsPanel;
    private ActionListener submitListener;

    public IngredientSelectionDialog(Frame owner, Player player) {
        super(owner, "Select Ingredients", true);  // Modal로 띄운다.
        this.player = player;
        this.selectedIngredients = new Item[MAX_INGREDIENTS];
        initialize();
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
            JButton slotButton = new JButton("Slot " + (i + 1));
            slotButton.addActionListener(e -> openIngredientSelectionDialog(index));
            ingredientSlotsPanel.add(slotButton);
        }
        add(ingredientSlotsPanel, BorderLayout.SOUTH);

        // 제출 버튼
        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> {
            if (submitListener != null) {
                submitListener.actionPerformed(e);
            }
        });
        add(submitButton, BorderLayout.NORTH);

        setSize(400, 500);  // 창 크기 조정
        setLocationRelativeTo(null);  // 창 가운데에 띄우기
    }

    private void openIngredientSelectionDialog(int slotIndex) {
        Item selectedItem = inventoryPanel.getSelectedItem();
        if (selectedItem != null) {
            selectedIngredients[slotIndex] = selectedItem; // 재료 슬롯에 아이템 채우기
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
