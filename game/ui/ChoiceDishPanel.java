package game.ui;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;
import game.recipe.Recipe;

public class ChoiceDishPanel extends JPanel {
    private List<Recipe> recipes;  // Recipe 객체 목록
    private Consumer<String> onDishSelected;

    public ChoiceDishPanel(List<Recipe> recipes, Consumer<String> onDishSelected) {
        this.recipes = recipes;
        this.onDishSelected = onDishSelected;

        setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20)); // 가로 배치와 간격 설정
        setBackground(Color.WHITE);

        // 각 요리에 대해 고정된 크기의 슬롯을 생성
        for (Recipe recipe : recipes) {
            add(createDishSlot(recipe));
        }
    }

    private JPanel createDishSlot(Recipe recipe) {
        JPanel dishPanel = new JPanel();
        dishPanel.setLayout(new BoxLayout(dishPanel, BoxLayout.Y_AXIS)); // 세로 정렬
        dishPanel.setPreferredSize(new Dimension(120, 150)); // 슬롯 크기 고정
        dishPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1)); // 테두리 추가
        dishPanel.setBackground(new Color(250, 250, 250)); // 배경 색상 설정

        // 아이콘 추가
        JLabel iconLabel = new JLabel();
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        Image image = recipe.getSprite(50, 50); // 아이콘 크기 조정
        if (image != null) {
            iconLabel.setIcon(new ImageIcon(image));
        } else {
            iconLabel.setText("No Image");
        }
        dishPanel.add(iconLabel);

        // 이름 추가
        JLabel dishNameLabel = new JLabel(recipe.getName());
        dishNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        dishNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        dishPanel.add(dishNameLabel);

        // 버튼 추가
        JButton dishButton = new JButton("Select");
        dishButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        dishButton.setBackground(new Color(240, 240, 240));
        dishButton.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        dishButton.setFocusPainted(false);
        dishButton.addActionListener(e -> onDishSelected.accept(recipe.getName()));
        dishPanel.add(Box.createVerticalStrut(5)); // 버튼과 이름 사이 여백
        dishPanel.add(dishButton);

        return dishPanel;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(450, 200); // 전체 패널 크기
    }
}
