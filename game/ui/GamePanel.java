package game.ui;

import command.*;
import core.CommandRegistry;
import domain.Farm;
import domain.item.Item;
import domain.Player;
import game.entity.Customer;
import game.entity.NormalCustomer;
import game.entity.PlayerRenderer;
import game.recipe.Recipe;
import game.recipe.RecipeManager;
import game.tile.FarmTile;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * Main game panel that handles rendering and user interaction for the farming game.
 */
public class GamePanel extends JPanel {
    private GameWindow gameWindow;
    private CommandRegistry registry;
    private PlayerRenderer playerRenderer;
    private Player player;
    private Farm farm;
    private List<Customer> customers;
    private Item selectedItem;
    private InventoryPanel inventoryPanel;

    private static final int TILE_SIZE = 40;
    private FarmTile[][] tiles;
    private Image backgroundImage;

    /**
     * Enum for defining different input types and their associated commands.
     */
    private enum InputType {
        MOUSE_LEFT(MouseEvent.BUTTON1, "till"),
        MOUSE_RIGHT(MouseEvent.BUTTON3, "plant"),
        KEY_H(KeyEvent.VK_H, "help"),
        KEY_Q(KeyEvent.VK_Q, "quit"),
        KEY_F(KeyEvent.VK_F, "farm"),
        KEY_SPACE(KeyEvent.VK_SPACE, "harvest");

        private final int inputCode; // Input key or mouse button code
        private final String commandName; // Name of the command associated with the input

        InputType(int inputCode, String commandName) {
            this.inputCode = inputCode;
            this.commandName = commandName;
        }

        public static InputType fromMouseButton(int button) {
            for (InputType type : values()) {
                if (type.inputCode == button && type.name().startsWith("MOUSE_")) {
                    return type;
                }
            }
            return null;
        }

        public static InputType fromKeyCode(int keyCode) {
            for (InputType type : values()) {
                if (type.inputCode == keyCode && type.name().startsWith("KEY_")) {
                    return type;
                }
            }
            return null;
        }

        public String getCommandName() {
            return commandName;
        }
    }

    /**
     * Helper class representing a position in the farm tile grid.
     */
    private static class Position {
        final int x;
        final int y;

        private Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public static Position fromMouseEvent(MouseEvent e, int tileSize) {
            return new Position(e.getX() / tileSize, e.getY() / tileSize);
        }

        public boolean isValidFarmPosition() {
            return x < 8 && y < 6; // Ensure position is within farm boundaries
        }
    }

    /**
     * Checks if a tile is interactable based on proximity to the player
     */
    private boolean isInteractable(int tileX, int tileY) {
        int playerCenterX = playerRenderer.getX() + (playerRenderer.getSize() / 2);
        int playerCenterY = playerRenderer.getY() + (playerRenderer.getSize() / 2);
        int playerTileX = playerCenterX / TILE_SIZE;
        int playerTileY = playerCenterY / TILE_SIZE;

        return Math.abs(tileX - playerTileX) <= 1 &&
               Math.abs(tileY - playerTileY) <= 1;
    }

    /**
     * Constructor initializes the game panel and sets event listeners.
     */
    public GamePanel(PlayerRenderer playerRenderer, Player player, Farm farm, CommandRegistry registry, List<Customer> customers) {
        this.playerRenderer = playerRenderer;
        this.player = player;
        this.farm = farm;
        this.registry = registry;
        this.customers = customers;
        this.inventoryPanel = new InventoryPanel(player, this);

        initializeTiles(); // Create the farm tile grid
        setupMouseListener(); // Handle mouse interactions
        setupKeyListener(); // Handle keyboard interaction
        setFocusable(true); // Ensure the panel can receive keyboard focus
        setBackground(Color.GREEN.darker());

        loadBackgroundImage();
    }

    private void loadBackgroundImage() {
        try {
            backgroundImage = ImageIO.read(new File("sprites/background/background2.png"));
        } catch (IOException e) {
            e.printStackTrace();
            backgroundImage = null;
        }

    }

    public void setSelectedItem(Item item) {
        this.selectedItem = item;
        repaint();
    }

    public InventoryPanel getInventoryPanel() {
        return inventoryPanel;
    }
    public void setGameWindow(GameWindow gameWindow) {
        this.gameWindow = gameWindow;
    }

    public void updateInventoryIfVisible() {
        if (gameWindow != null) {
            gameWindow.updateInventoryIfNeeded();
        }
    }

    private void initializeTiles() {
        tiles = new FarmTile[8][6];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 6; j++) {
                tiles[i][j] = new FarmTile();
            }
        }
    }

    private void setupMouseListener() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e); // Handle farm-related clicks
                handleCustomerClick(e); // Handle customer interactions
            }
        });
    }

    private void setupKeyListener() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                InputType inputType = InputType.fromKeyCode(e.getKeyCode());
                if (inputType != null) {
                    Command command;
                    if (inputType == InputType.KEY_SPACE) {
                        // Create a special command for harvesting
                        command = new HarvestCommand(player, farm, tiles, playerRenderer, GamePanel.this);
                    } else {
                        // Retrieve a command from the registry
                        command = registry.getCommand(inputType.getCommandName());
                    }

                    if (command != null) {
                        command.execute(new String[]{});
                        repaint(); // Update the panel
                    }
                }
            }
        });
    }

    /**
     * Handles mouse clicks, (e.g.) tilling, planting
     */
    private void handleMouseClick(MouseEvent e) {
        Position clickPosition = Position.fromMouseEvent(e, TILE_SIZE);

        if (!clickPosition.isValidFarmPosition()) {
            return; // Ignore clicks outside the farm boundaries
        }

        InputType inputType = InputType.fromMouseButton(e.getButton());
        if (inputType != null) {
            FarmTile tile = tiles[clickPosition.x][clickPosition.y];

            if (!isInteractable(clickPosition.x, clickPosition.y)) {
                System.out.println("Too far away!");
                return; // Prevent interaction if the tile is out of range
            }

            Command command = createMouseCommand(inputType, tile);
            if (command != null) {
                command.execute(new String[]{});
                repaint(); // Update the farm display
            }
        }
    }

    private Command createMouseCommand(InputType inputType, FarmTile tile) {
        switch (inputType) {
            case MOUSE_LEFT:
                return new TillCommand(tile); // Command to till the tile
            case MOUSE_RIGHT:
                if (selectedItem != null) {
                    return new PlantCommand(player, farm, tile, selectedItem.getName(), this);
                } else {
                    JOptionPane.showMessageDialog(this, "Please select a valid crop to plant.", "No Crop Selected", JOptionPane.WARNING_MESSAGE);
                    return null; // No valid item selected
                }
            default:
                return null; // No command for the given input
        }
    }

    /**
     * Paints the game panel, including tiles, player, customers, and HUD elements.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the background
        if (backgroundImage != null){
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), (ImageObserver) null);
        } else {
            g.setColor(Color.GREEN.darker());
            g.fillRect(0,0, getWidth(), getHeight());
        }

        // Draw the farm tiles and their states
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 6; j++) {
                int x = i * TILE_SIZE;
                int y = j * TILE_SIZE;
                FarmTile tile = tiles[i][j];

                // Highlight interactable tiles
                if (isInteractable(i, j)) {
                    g.setColor(new Color(120,120, 100, 150));
                } else {
                    g.setColor(new Color(80, 80, 80, 150));
                }
                g.fillRect(x, y, TILE_SIZE, TILE_SIZE);

                // Draw tile borders
                g.setColor(new Color(101, 67, 33, 150));
                g.drawRect(x, y, TILE_SIZE, TILE_SIZE);

                // Draw tilled soil
                if (tile.isTilled()) {
                    g.setColor(new Color(139, 69, 19));
                    g.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                }

                // Draw crops based on their growth progress
                if (tile.hasCrop()) {
                    int growthProgress = Math.min(100, Math.max(0, tile.getGrowthProgress()));
                    int size = calculateCropSize(growthProgress);
                    int alpha = Math.min(255, Math.max(0, calculateCropAlpha(growthProgress)));

                    Image cropSprite = tile.getCrop().getSprite(size, size);

                    int cropX = x + (TILE_SIZE - size) / 2;
                    int cropY = y + (TILE_SIZE - size) / 2;

                    Graphics2D g2d = (Graphics2D) g;
                    AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha/255.0f);
                    g2d.setComposite(alphaComposite);

                    g2d.drawImage(cropSprite, cropX, cropY, null);

                    g2d.setComposite(AlphaComposite.SrcOver);

                    drawProgressBar(g, x, y, TILE_SIZE, growthProgress);
                }
            }
        }

        // Draw customers
        if (customers != null) {
            for (Customer customer : customers) {
                customer.draw(g);
            }
        }

        // Draw the player
        playerRenderer.draw(g);

        // Draw the selected item
        if (selectedItem != null) {
            int x = 10;  // 왼쪽 여백
            int y = getHeight() - 60; // 아래 여백
            int size = 40;

            Image itemSprite = selectedItem.getSprite(size, size);
            g.drawImage(itemSprite, x, y, null);
            g.setColor(Color.BLACK);
            g.drawString(selectedItem.getName(), x, y + size + 15);
        }

        // Draw money display
        g.setColor(new Color(0, 0, 0, 180));
        g.setFont(new Font("Arial", Font.BOLD, 16));
        String moneyText = String.format("€ %.2f", player.getMoney());
        FontMetrics metrics = g.getFontMetrics();
        int moneyWidth = metrics.stringWidth(moneyText);
        int moneyX = getWidth() - moneyWidth - 20;
        int moneyY = 30;

        g.setColor(new Color(255, 255, 255, 180));
        g.fillRect(moneyX - 5, moneyY - metrics.getAscent(), moneyWidth + 10, metrics.getHeight());

        g.setColor(new Color(0, 100, 0));
        g.drawString(moneyText, moneyX, moneyY);

        for (Customer customer : customers) {
            customer.draw(g);
        }
    }

    private int calculateCropSize(int growthProgress) {
        int maxCropSize = TILE_SIZE - 10; // Crop size at full growth
        return maxCropSize * growthProgress / 100;
    }

    private int calculateCropAlpha(int growthProgress) {
        return 255 * growthProgress / 100; // Full opacity at full growth
    }

    /**
     * Draws a progress bar to indicate the growth progress of a crop.
     */
    private void drawProgressBar(Graphics g, int x, int y, int tileSize, int progress) {
        int barHeight = 4;
        int barWidth = tileSize - 6;
        int barX = x + 3;
        int barY = y + tileSize - barHeight - 3;

        g.setColor(new Color(100, 100, 100, 180));
        g.fillRect(barX, barY, barWidth, barHeight);

        int progressWidth = (int)((barWidth * progress) / 100.0);

        // Green for the progress bar
        g.setColor(new Color(50, 205, 50, 230));
        g.fillRect(barX, barY, progressWidth, barHeight);

        // Border of the progress bar
        g.setColor(new Color(0, 0, 0, 180));
        g.drawRect(barX, barY, barWidth, barHeight);
    }

    private void handleCustomerClick(MouseEvent e) {
        // 1. Find the customer at the clicked position
        Customer customer = findCustomerAtPosition(e.getX(), e.getY());
        if (customer == null) {
            return; // Exit if no customer is found at the click position
        }
        // 2. Show the dish selection dialog for the found customer
        showDishSelectionDialog(customer);
    }

    private Customer findCustomerAtPosition(int x, int y) {
        for (Customer customer : customers) {
            if (customer.contains(x, y)) {
                return customer; // Return the customer if the position is inside their area
            }
        }
        return null; // if no customer is found at the clicked position
    }

    /**
     * Displays a dialog for selecting a dish for the customer.
     * @param customer the customer who will receive the dish
     */
    private void showDishSelectionDialog(Customer customer) {
        List<Recipe> customerRecipes = customer.getAssignedRecipes(); // Get the list of available recipes for the customer

        // Create and configure the ChoiceDishPanel
        JPanel panel = new JPanel(new BorderLayout());
        ChoiceDishPanel choiceDishPanel = new ChoiceDishPanel(customerRecipes, selectedDish -> {
            serveDishToCustomer(selectedDish, customer);
            ((JDialog) SwingUtilities.getWindowAncestor(panel)).dispose(); // 다이얼로그 닫기
        });

        // Set up the dialog window
        panel.add(choiceDishPanel, BorderLayout.CENTER);
        JDialog dialog = new JDialog();
        dialog.setTitle("Select a Dish for Customer");
        dialog.setModal(true); // Make the dialog modal (blocks interaction with other windows)
        dialog.add(panel);
        dialog.pack(); // Adjust dialog size to fit its content
        dialog.setLocationRelativeTo(this); // Position dialog relative to this window
        dialog.setVisible(true); // Show the dialog
    }

    /**
     * Serves the selected dish to the customer and shows the ingredient selection dialog.
     * @param selectedDish the name of the selected dish
     * @param customer the customer who will receive the dish
     */
    private void serveDishToCustomer(String selectedDish, Customer customer) {
        RecipeManager recipeManager = RecipeManager.getInstance(); // Get the recipe manager instance
        Recipe selectedRecipe = recipeManager.getRecipe(selectedDish); // Retrieve the recipe for the selected dish
        if (selectedRecipe == null) {
            // Handle error if recipe is not found
            System.out.println("Error: Recipe not found for " + selectedDish);
            return;
        }
        showIngredientSelectionDialog(selectedRecipe, customer);
    }

    /**
     * Displays the ingredient selection dialog for the selected recipe.
     * @param selectedDish the selected recipe object
     * @param customer the customer who is receiving the dish
     */
    private void showIngredientSelectionDialog(Recipe selectedDish, Customer customer) {
        // Create and configure the IngredientSelectionPanel
        IngredientSelectionPanel ingredientSelectionPanel = new IngredientSelectionPanel(
                gameWindow, player, (NormalCustomer) customer, this, gameWindow
        );

        // Show the ingredient selection panel as a dialog
        ingredientSelectionPanel.setVisible(true);
    }
}