package command;

import domain.player.Player;
import javax.swing.*;
import java.awt.*;

public class PlayerCommand implements Command {
    private Player player;

    public PlayerCommand(Player player) {
        this.player = player;
    }

    @Override
    public void execute(String[] args) {
        JDialog playerDialog = new JDialog();
        playerDialog.setTitle("Player Status");
        playerDialog.setSize(300, 200);
        playerDialog.setLocationRelativeTo(null);
        playerDialog.setModal(true);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // 플레이어 정보 패널
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(0, 1, 5, 5));
        
        // 돈 표시
        JLabel moneyLabel = new JLabel("Money: $" + player.getMoney());
        moneyLabel.setFont(new Font("Arial", Font.BOLD, 16));
        infoPanel.add(moneyLabel);
        
        // 인벤토리 표시
        JLabel inventoryLabel = new JLabel("Inventory:");
        inventoryLabel.setFont(new Font("Arial", Font.BOLD, 14));
        infoPanel.add(inventoryLabel);
        
        // 인벤토리 아이템들을 리스트로 표시
        DefaultListModel<String> inventoryModel = new DefaultListModel<>();
        // 여기에 인벤토리 아이템들을 추가
        // player.getInventory().forEach(item -> inventoryModel.addElement(item.toString()));
        
        JList<String> inventoryList = new JList<>(inventoryModel);
        JScrollPane scrollPane = new JScrollPane(inventoryList);
        
        mainPanel.add(infoPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // 닫기 버튼
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> playerDialog.dispose());
        mainPanel.add(closeButton, BorderLayout.SOUTH);
        
        playerDialog.add(mainPanel);
        playerDialog.setVisible(true);
    }
}