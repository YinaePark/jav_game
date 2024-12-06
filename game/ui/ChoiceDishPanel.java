package game.ui;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;
import game.recipe.Recipe;

/**
 * A panel that displays a list of recipes as selectable dish slots.
 * Each dish is represented by an icon, name, and a "Select" button.
 */
public class ChoiceDishPanel extends JPanel {
    private List<Recipe> recipes;
    private Consumer<String> onDishSelected;

    public ChoiceDishPanel(List<Recipe> recipes, Consumer<String> onDishSelected) {
        this.recipes = recipes;
        this.onDishSelected = onDishSelected;

        // Set layout for the panel with centered flow and spacing between components
        setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        setBackground(Color.WHITE);

        // Create and add dish slots for each recipe in the list
        for (Recipe recipe : recipes) {
            add(createDishSlot(recipe));
        }
    }
    /**
     * Creates a panel representing a single dish with an icon, name, and selection button.
     */
    private JPanel createDishSlot(Recipe recipe) {
        JPanel dishPanel = new JPanel();
        dishPanel.setLayout(new BoxLayout(dishPanel, BoxLayout.Y_AXIS));
        dishPanel.setPreferredSize(new Dimension(120, 150));
        dishPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        dishPanel.setBackground(new Color(250, 250, 250));
        // add icon
        JLabel iconLabel = new JLabel();
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        Image image = recipe.getSprite(50, 50);
        if (image != null) {
            iconLabel.setIcon(new ImageIcon(image));
        } else {
            iconLabel.setText("No Image");
        }
        dishPanel.add(Box.createVerticalStrut(10));
        dishPanel.add(iconLabel);

        // name label
        JLabel dishNameLabel = new JLabel(recipe.getName());
        dishNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        dishNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        dishPanel.add(Box.createVerticalStrut(5));
        dishPanel.add(dishNameLabel);

        // select button
        JButton dishButton = new JButton("Select");
        dishButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        dishButton.setBackground(new Color(240, 240, 240));
        dishButton.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        dishButton.setFocusPainted(false);
        dishButton.addActionListener(e -> onDishSelected.accept(recipe.getName()));
        dishPanel.add(Box.createVerticalStrut(10)); // space between button and name
        dishPanel.add(dishButton);
        return dishPanel;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(450, 200);
    }
}
