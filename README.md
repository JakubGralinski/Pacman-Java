# Pacman Game

A simple Pacman game implemented in Java using Swing.

## Features

- Pacman with opening and closing mouth animation
- Multiple maps to choose from
- Enemy characters with different movement patterns
- Various upgrades (speed boost, extra life, double points, pass-through walls, etc.)
- High score management

## Getting Started

### Prerequisites

- Java JDK 8 or higher
- An IDE or text editor (Eclipse, IntelliJ, VSCode, etc.)

### Running the Game

1. Clone the repository:


2. Open the project in your IDE.

3. Run the `PacmanGame.java` file located in the `src/controller` directory.

## Project Structure

- `model` - Contains the core game logic classes (`Board`, `Character`, `Enemy`, `Player`, `Upgrade`)
- `view` - Contains the graphical representation (`BoardPanel`)
- `controller` - Contains the main game class (`PacmanGame`) and input handling (`PlayerMovementListener`)
- `util` - Contains utility classes for managing high scores (`HighScore`, `HighScoreManager`)
- `sprites` - Contains image assets used in the game
- `res` - Contains resource files, such as `highscores.txt`

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
