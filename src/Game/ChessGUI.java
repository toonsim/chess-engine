package Game;

import Game.GameParts.Pieces.*;
import Game.GameParts.Position;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChessGUI extends JFrame {
    private Game game;
    private JButton[][] boardButtons;
    private JLabel statusLabel;
    private JButton newGameButton;
    private JButton undoButton;
    private JButton resignButton;
    
    private String selectedSquare = null;
    
    public ChessGUI() {
        initializeGUI();
        startNewGame(true, false, 0, 3); // Default: Human vs AI
    }
    
    private void initializeGUI() {
        setTitle("Chess Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Chess board panel
        JPanel boardPanel = new JPanel(new GridLayout(8, 8));
        boardButtons = new JButton[8][8];
        
        // Create board buttons
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(60, 60));
                button.setFont(new Font("Serif", Font.PLAIN, 36));
                
                // Set square colors (alternating pattern)
                if ((row + col) % 2 == 0) {
                    button.setBackground(new Color(240, 217, 181)); // Light squares
                } else {
                    button.setBackground(new Color(181, 136, 99));  // Dark squares
                }
                
                final int finalRow = row;
                final int finalCol = col;
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        handleSquareClick(finalRow, finalCol);
                    }
                });
                
                boardButtons[row][col] = button;
                boardPanel.add(button);
            }
        }
        
        // Control panel
        JPanel controlPanel = new JPanel(new FlowLayout());
        
        newGameButton = new JButton("New Game");
        undoButton = new JButton("Undo");
        resignButton = new JButton("Resign");
        
        newGameButton.addActionListener(e -> showGameSetupDialog());
        undoButton.addActionListener(e -> handleUndo());
        resignButton.addActionListener(e -> handleResign());
        
        controlPanel.add(newGameButton);
        controlPanel.add(undoButton);
        controlPanel.add(resignButton);
        
        // Status panel
        statusLabel = new JLabel("White to move", JLabel.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Add components to main panel
        mainPanel.add(boardPanel, BorderLayout.CENTER);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);
        mainPanel.add(statusLabel, BorderLayout.NORTH);
        
        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
    }
    
    private void startNewGame(boolean whiteControlled, boolean blackControlled, int whiteDifficulty, int blackDifficulty) {
        game = new Game(whiteControlled, blackControlled, whiteDifficulty, blackDifficulty);
        selectedSquare = null;
        updateBoard();
        updateStatus();
        
        // If current player is AI, make AI move
        if (!game.getPlayerAtPlay().isControlled()) {
            makeAIMove();
        }
    }
    
    private void updateBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                JButton button = boardButtons[row][col];
                Position position = game.getPosition(col, 7 - row); // Convert GUI coordinates to game coordinates
                Piece piece = position.getOccupyingPiece();
                
                if (piece != null) {
                    button.setText(getPieceSymbol(piece));
                } else {
                    button.setText("");
                }
                
                // Reset button colors
                if ((row + col) % 2 == 0) {
                    button.setBackground(new Color(240, 217, 181)); // Light squares
                } else {
                    button.setBackground(new Color(181, 136, 99));  // Dark squares
                }
                
                // Highlight selected square
                if (selectedSquare != null) {
                    String squareName = getSquareName(col, 7 - row);
                    if (squareName.equals(selectedSquare)) {
                        button.setBackground(Color.YELLOW);
                    }
                }
            }
        }
    }
    
    private String getPieceSymbol(Piece piece) {
        boolean isWhite = piece.getPlayer().isWhite();
        
        if (piece instanceof King) {
            return isWhite ? "♔" : "♚";
        } else if (piece instanceof Queen) {
            return isWhite ? "♕" : "♛";
        } else if (piece instanceof Rook) {
            return isWhite ? "♖" : "♜";
        } else if (piece instanceof Bishop) {
            return isWhite ? "♗" : "♝";
        } else if (piece instanceof Knight) {
            return isWhite ? "♘" : "♞";
        } else if (piece instanceof Pawn) {
            return isWhite ? "♙" : "♟";
        }
        
        return "";
    }
    
    private void handleSquareClick(int row, int col) {
        if (game.isGameOver()) {
            return;
        }
        
        // Only allow human players to click
        if (!game.getPlayerAtPlay().isControlled()) {
            return;
        }
        
        String squareName = getSquareName(col, 7 - row); // Convert GUI coordinates to chess notation
        
        if (selectedSquare == null) {
            // First click - select piece
            Position position = game.getPosition(col, 7 - row);
            if (position.getOccupyingPiece() != null && 
                position.getOccupyingPiece().getPlayer() == game.getPlayerAtPlay()) {
                selectedSquare = squareName;
                updateBoard();
            }
        } else {
            // Second click - attempt move
            if (squareName.equals(selectedSquare)) {
                // Clicked same square - deselect
                selectedSquare = null;
                updateBoard();
            } else {
                // Attempt move
                boolean success = game.makeMove(selectedSquare, squareName);
                selectedSquare = null;
                updateBoard();
                updateStatus();
                
                if (success && !game.isGameOver() && !game.getPlayerAtPlay().isControlled()) {
                    // AI's turn
                    Timer timer = new Timer(500, e -> makeAIMove());
                    timer.setRepeats(false);
                    timer.start();
                }
            }
        }
    }
    
    private void makeAIMove() {
        if (!game.isGameOver() && !game.getPlayerAtPlay().isControlled()) {
            game.playATurn();
            updateBoard();
            updateStatus();
            
            // Check if next player is also AI
            if (!game.isGameOver() && !game.getPlayerAtPlay().isControlled()) {
                Timer timer = new Timer(500, e -> makeAIMove());
                timer.setRepeats(false);
                timer.start();
            }
        }
    }
    
    private String getSquareName(int x, int y) {
        char file = (char) ('A' + x);
        int rank = y + 1;
        return file + String.valueOf(rank);
    }
    
    private void updateStatus() {
        if (game.isGameOver()) {
            statusLabel.setText(game.getGameEndMessage());
        } else {
            String currentPlayer = game.getPlayerAtPlay().isWhite() ? "White" : "Black";
            String playerType = game.getPlayerAtPlay().isControlled() ? " (Human)" : " (AI)";
            statusLabel.setText(currentPlayer + playerType + " to move");
        }
    }
    
    private void handleUndo() {
        if (game.undoMove()) {
            selectedSquare = null;
            updateBoard();
            updateStatus();
        }
    }
    
    private void handleResign() {
        if (game.resignPlayer()) {
            updateBoard();
            updateStatus();
        }
    }
    
    private void showGameSetupDialog() {
        String[] options = {"Human vs Human", "Human vs AI (Easy)", "Human vs AI (Medium)", "Human vs AI (Hard)", "AI vs AI"};
        int choice = JOptionPane.showOptionDialog(this, 
            "Choose game type:", 
            "New Game", 
            JOptionPane.DEFAULT_OPTION, 
            JOptionPane.QUESTION_MESSAGE, 
            null, 
            options, 
            options[1]);
        
        switch (choice) {
            case 0: // Human vs Human
                startNewGame(true, true, 0, 0);
                break;
            case 1: // Human vs AI (Easy)
                startNewGame(true, false, 0, 1);
                break;
            case 2: // Human vs AI (Medium)
                startNewGame(true, false, 0, 3);
                break;
            case 3: // Human vs AI (Hard)
                startNewGame(true, false, 0, 7);
                break;
            case 4: // AI vs AI
                startNewGame(false, false, 3, 7);
                break;
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ChessGUI().setVisible(true);
            }
        });
    }
}