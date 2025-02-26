import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

public class TicTacToe {
    String PLAYER = "X";
    String COMPUTER = "O";
    String DRAW = "draw";

    int boardWidth = 600;
    int boardHeight = 700;

    JFrame frame = new JFrame("Tic Tac Toe");
    JLabel textLabel = new JLabel();
    JPanel textPanel = new JPanel();

    JPanel bordPanel = new JPanel();
    JButton[][] board = new JButton[3][3];

    Boolean gameOver = false;
    int played = 0;
    int turn =0;

    public TicTacToe() {
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        textLabel.setBackground(Color.PINK);
        textLabel.setForeground(Color.white);
        textLabel.setFont(new Font("Arial", Font.BOLD, 50));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Tic Tac Toe");
        textLabel.setOpaque(true);

        textPanel.setLayout(new BorderLayout());
        textPanel.add(textLabel);
        frame.add(textPanel, BorderLayout.NORTH);

        bordPanel.setLayout(new GridLayout(3, 3)); // Ensure GridLayout is set
        bordPanel.setBackground(Color.pink);
        bordPanel.setPreferredSize(new Dimension(boardWidth, boardHeight)); // Set preferred size
        int padding = 50; // Padding in pixels
        bordPanel.setBorder(new EmptyBorder(padding, padding, padding, padding));
        frame.add(bordPanel, BorderLayout.CENTER);

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                JButton tile = new JButton();
                board[row][col] = tile;

                tile.setBackground(Color.PINK);
                tile.setForeground(Color.pink);
                tile.setFont(new Font("Arial", Font.BOLD, 120));
                tile.setFocusable(false);
                bordPanel.add(tile);

                tile.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        JButton tile = (JButton) e.getSource();
                        if (Objects.equals(tile.getText(), "") && turn ==0 && !gameOver) {
                            tile.setText(PLAYER);
                            played++;
                            win("You ");
                            if (!gameOver) {
                                turn++;
                                // Schedule the computer's move after a delay
                                Timer timer = new Timer(1000, new ActionListener() {
                                    public void actionPerformed(ActionEvent e) {
                                        play();
                                    }
                                });
                                timer.setRepeats(false); // Ensure the timer only runs once
                                timer.start();
                            }
                        }
                    }
                });
            }
        }

        JButton restart = new JButton("New game");

        restart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                played = 0;
                turn= 0;
                gameOver=false;
                textLabel.setText("Tic Tac Toe");
                for (int row = 0; row < 3; row++) {
                    for (int col = 0; col < 3; col++) {
                        board[row][col].setText("");
                        board[row][col].setForeground(Color.pink);
                    }
                }
            }
        });
        JPanel restartPanel = new JPanel();
        restartPanel.setBackground(Color.pink);
        restartPanel.add(restart);
        restartPanel.setBorder(new EmptyBorder(0,0, 30, 0));
        frame.add(restartPanel,BorderLayout.SOUTH );

        frame.revalidate(); // Refresh the layout
        frame.repaint(); // Repaint the frame
        frame.setVisible(true); // Make the frame visible after all components are added
    }



    public void win(String winner) {
        String result = checkWinner();
        if (result != null) {
            gameOver = true;
            if (result.equals("draw")) {
                textLabel.setText("Tie !");
            } else {
                textLabel.setText(winner + "won !");
                highlightWinningCells(result);
            }
        }
    }

    public void play() {
        int[] bestMove = findBestMove();
        board[bestMove[0]][bestMove[1]].setText(COMPUTER);
        played++;
        win("The computer ");
        turn = 0;
    }

    private int[] findBestMove() {
        int bestScore = Integer.MIN_VALUE;
        int[] bestMove = new int[]{-1, -1};

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (board[row][col].getText().isEmpty()) {
                    board[row][col].setText(COMPUTER);
                    int score = minimax(0, false, Integer.MIN_VALUE, Integer.MAX_VALUE);
                    board[row][col].setText(""); // Undo the move
                    if (score > bestScore) {
                        bestScore = score;
                        bestMove[0] = row;
                        bestMove[1] = col;
                    }
                }
            }
        }
        return bestMove;
    }

    private int minimax(int depth, boolean isMaximizing, int alpha, int beta) {
        String result = checkWinner();
        if (result != null) {
            if (result.equals(COMPUTER)) return 10 - depth; // Computer wins
            if (result.equals(PLAYER)) return depth - 10; // Player wins
            return 0; // Draw
        }

        if (isMaximizing) {
            int bestScore = Integer.MIN_VALUE;
            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 3; col++) {
                    if (board[row][col].getText().isEmpty()) {
                        board[row][col].setText(COMPUTER);
                        int score = minimax(depth + 1, false, alpha, beta);
                        board[row][col].setText(""); // Undo the move
                        bestScore = Math.max(score, bestScore);
                        alpha = Math.max(alpha, bestScore);
                        if (beta <= alpha) break; // Alpha-beta pruning
                    }
                }
            }
            return bestScore;
        } else {
            int bestScore = Integer.MAX_VALUE;
            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 3; col++) {
                    if (board[row][col].getText().isEmpty()) {
                        board[row][col].setText(PLAYER);
                        int score = minimax(depth + 1, true, alpha, beta);
                        board[row][col].setText(""); // Undo the move
                        bestScore = Math.min(score, bestScore);
                        beta = Math.min(beta, bestScore);
                        if (beta <= alpha) break; // Alpha-beta pruning
                    }
                }
            }
            return bestScore;
        }
    }

    private String checkWinner() {
        // Check rows
        for (int row = 0; row < 3; row++) {
            if (!board[row][0].getText().isEmpty()
                    && Objects.equals(board[row][0].getText(), board[row][1].getText())
                    && Objects.equals(board[row][2].getText(), board[row][1].getText())) {
                return board[row][0].getText();
            }
        }

        // Check columns
        for (int col = 0; col < 3; col++) {
            if (!board[0][col].getText().isEmpty()
                    && Objects.equals(board[0][col].getText(), board[1][col].getText())
                    && Objects.equals(board[2][col].getText(), board[1][col].getText())) {
                return board[0][col].getText();
            }
        }

        // Check diagonally
        if (!board[0][0].getText().isEmpty()
                && Objects.equals(board[0][0].getText(), board[1][1].getText())
                && Objects.equals(board[2][2].getText(), board[1][1].getText())) {
            return board[0][0].getText();
        }

        // Check anti-diagonally
        if (!board[0][2].getText().isEmpty()
                && Objects.equals(board[0][2].getText(), board[1][1].getText())
                && Objects.equals(board[2][0].getText(), board[1][1].getText())) {
            return board[0][2].getText();
        }

        // Check for a draw
        if (played == 9) {
            return "draw";
        }

        return null; // No winner yet
    }


    private void highlightWinningCells(String winner) {
        // Check rows
        for (int row = 0; row < 3; row++) {
            if (!board[row][0].getText().isEmpty()
                    && Objects.equals(board[row][0].getText(), board[row][1].getText())
                    && Objects.equals(board[row][2].getText(), board[row][1].getText())) {
                for (int col = 0; col < 3; col++) {
                    board[row][col].setForeground(Color.green);
                }
                return;
            }
        }

        // Check columns
        for (int col = 0; col < 3; col++) {
            if (!board[0][col].getText().isEmpty()
                    && Objects.equals(board[0][col].getText(), board[1][col].getText())
                    && Objects.equals(board[2][col].getText(), board[1][col].getText())) {
                for (int row = 0; row < 3; row++) {
                    board[row][col].setForeground(Color.green);
                }
                return;
            }
        }

        // Check diagonally
        if (!board[0][0].getText().isEmpty()
                && Objects.equals(board[0][0].getText(), board[1][1].getText())
                && Objects.equals(board[2][2].getText(), board[1][1].getText())) {
            for (int i = 0; i < 3; i++) {
                board[i][i].setForeground(Color.green);
            }
            return;
        }

        // Check anti-diagonally
        if (!board[0][2].getText().isEmpty()
                && Objects.equals(board[0][2].getText(), board[1][1].getText())
                && Objects.equals(board[2][0].getText(), board[1][1].getText())) {
            board[0][2].setForeground(Color.green);
            board[1][1].setForeground(Color.green);
            board[2][0].setForeground(Color.green);
        }
    }


}