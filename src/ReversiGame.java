import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class ReversiGame extends JFrame {
    JLabel scoreLabel = new JLabel();
    JButton restartButton = new JButton("Restart");
    String difficulty = "Easy";  // default difficulty
    JComboBox<String> difficultyBox; // Moved to class level for restart
    ReversiLogic logic = new ReversiLogic();
    JButton[][] buttons = new JButton[ReversiLogic.SIZE][ReversiLogic.SIZE];

    public ReversiGame() {
        setTitle("Reversi Game");
        setSize(600, 600);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // === Top panel with score and controls ===
        JPanel topPanel = new JPanel(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);

        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        updateScoreLabel();

        String[] levels = {"Easy", "Hard"};
        difficultyBox = new JComboBox<>(levels);
        difficultyBox.addActionListener(e -> difficulty = (String) difficultyBox.getSelectedItem());

        topPanel.add(scoreLabel, BorderLayout.CENTER);
        topPanel.add(difficultyBox, BorderLayout.WEST);
        topPanel.add(restartButton, BorderLayout.EAST);

        restartButton.addActionListener(e -> {
            logic = new ReversiLogic();              // Reset logic
            difficulty = "Easy";                     // Reset difficulty
            difficultyBox.setSelectedItem("Easy");   // Reset dropdown
            updateBoard();                           // Refresh display
        });

        // === Game board ===
        JPanel boardPanel = new JPanel(new GridLayout(ReversiLogic.SIZE, ReversiLogic.SIZE));
        add(boardPanel, BorderLayout.CENTER);

        for (int i = 0; i < ReversiLogic.SIZE; i++) {
            for (int j = 0; j < ReversiLogic.SIZE; j++) {
                JButton btn = new JButton();
                btn.setBackground(new Color(0, 128, 0)); // green background
                btn.setOpaque(true);
                btn.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                int row = i, col = j;

                btn.addActionListener(e -> {
                    if (logic.placePiece(row, col)) {
                        updateBoard();
                        // Let AI play if valid moves exist
                        if (logic.hasValidMove(ReversiLogic.WHITE)) {
                            logic.aiMove(difficulty);
                        }
                        updateBoard();
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid move!");
                    }
                });

                buttons[i][j] = btn;
                boardPanel.add(btn);
            }
        }

        updateBoard();
        setVisible(true);
    }

    void updateBoard() {
        for (int i = 0; i < ReversiLogic.SIZE; i++) {
            for (int j = 0; j < ReversiLogic.SIZE; j++) {
                JButton btn = buttons[i][j];
                char cell = logic.board[i][j];

                if (cell == ReversiLogic.BLACK) {
                    btn.setIcon(createDisc(Color.BLACK));
                } else if (cell == ReversiLogic.WHITE) {
                    btn.setIcon(createDisc(Color.WHITE));
                } else {
                    btn.setIcon(null);
                    if (logic.isValidMove(i, j)) {
                        btn.setBackground(Color.LIGHT_GRAY);
                    } else {
                        btn.setBackground(new Color(0, 128, 0));
                    }
                }
            }
        }

        updateScoreLabel();

        if (logic.isGameOver()) {
            int blackScore = logic.getScore(ReversiLogic.BLACK);
            int whiteScore = logic.getScore(ReversiLogic.WHITE);
            String winner = (blackScore > whiteScore) ? "Black wins!" :
                    (whiteScore > blackScore) ? "White wins!" : "It's a tie!";
            JOptionPane.showMessageDialog(this, "Game Over!\n" + winner);
        }
    }

    void updateScoreLabel() {
        int black = logic.getScore(ReversiLogic.BLACK);
        int white = logic.getScore(ReversiLogic.WHITE);
        scoreLabel.setText("Black: " + black + "  |  White: " + white + "  |  Turn: " + logic.currentPlayer);
    }

    Icon createDisc(Color color) {
        BufferedImage image = new BufferedImage(60, 60, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setColor(color);
        g.fillOval(5, 5, 50, 50);
        g.dispose();
        return new ImageIcon(image);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ReversiGame::new);
    }
}
