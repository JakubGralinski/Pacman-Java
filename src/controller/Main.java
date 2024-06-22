import controller.PacmanGame;

import javax.swing.*;

public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        PacmanGame game = new PacmanGame("Medium");
        game.displayMainMenu();
    });
}