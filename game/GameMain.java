package game;

import game.ui.GameWindow;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameWindow game = new GameWindow();
            game.setVisible(true);
        });
    }
}
