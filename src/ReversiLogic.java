public class ReversiLogic {
    public static final int SIZE = 8;
    public static final char EMPTY = '.';
    public static final char BLACK = 'B';
    public static final char WHITE = 'W';

    public char[][] board = new char[SIZE][SIZE];
    public char currentPlayer = BLACK;

    public ReversiLogic() {
        initBoard();
    }

    public int getScore(char player) {
        int score = 0;
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                if (board[i][j] == player) score++;
        return score;
    }

    public boolean isGameOver() {
        return !hasValidMove(BLACK) && !hasValidMove(WHITE);
    }

    public void initBoard() {
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                board[i][j] = EMPTY;

        board[3][3] = WHITE;
        board[3][4] = BLACK;
        board[4][3] = BLACK;
        board[4][4] = WHITE;
    }

    public boolean isOnBoard(int row, int col) {
        return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
    }

    public boolean isValidMove(int row, int col) {
        if (!isOnBoard(row, col) || board[row][col] != EMPTY)
            return false;

        char opponent = (currentPlayer == BLACK) ? WHITE : BLACK;

        int[] dx = {-1, -1, -1, 0, 1, 1, 1, 0};
        int[] dy = {-1, 0, 1, 1, 1, 0, -1, -1};

        for (int d = 0; d < 8; d++) {
            int x = row + dx[d], y = col + dy[d];
            boolean foundOpponent = false;

            while (isOnBoard(x, y) && board[x][y] == opponent) {
                x += dx[d];
                y += dy[d];
                foundOpponent = true;
            }

            if (foundOpponent && isOnBoard(x, y) && board[x][y] == currentPlayer)
                return true;
        }

        return false;
    }

    public boolean placePiece(int row, int col) {
        if (!isValidMove(row, col))
            return false;

        board[row][col] = currentPlayer;
        flipPieces(row, col);
        currentPlayer = (currentPlayer == BLACK) ? WHITE : BLACK;
        return true;
    }

    public void flipPieces(int row, int col) {
        char opponent = (currentPlayer == BLACK) ? WHITE : BLACK;
        int[] dx = {-1, -1, -1, 0, 1, 1, 1, 0};
        int[] dy = {-1, 0, 1, 1, 1, 0, -1, -1};

        for (int d = 0; d < 8; d++) {
            int x = row + dx[d], y = col + dy[d];
            if (!isOnBoard(x, y) || board[x][y] != opponent)
                continue;

            int nx = x, ny = y;
            while (isOnBoard(nx, ny) && board[nx][ny] == opponent) {
                nx += dx[d];
                ny += dy[d];
            }

            if (isOnBoard(nx, ny) && board[nx][ny] == currentPlayer) {
                while (nx != row || ny != col) {
                    nx -= dx[d];
                    ny -= dy[d];
                    board[nx][ny] = currentPlayer;
                }
            }
        }
    }
    public boolean hasValidMove(char player) {
        char oldPlayer = currentPlayer;
        currentPlayer = player;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (isValidMove(i, j)) {
                    currentPlayer = oldPlayer;
                    return true;
                }
            }
        }
        currentPlayer = oldPlayer;
        return false;
    }

    public boolean aiMove(String difficulty) {
        char oldPlayer = currentPlayer;
        currentPlayer = WHITE;

        int bestRow = -1, bestCol = -1;
        int bestScore = (difficulty.equals("Easy")) ? Integer.MIN_VALUE : Integer.MIN_VALUE;

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (isValidMove(i, j)) {
                    int score = simulateFlipScore(i, j);
                    if (score > bestScore) {
                        bestScore = score;
                        bestRow = i;
                        bestCol = j;
                    }
                    if (difficulty.equals("Easy")) break;  // take first valid
                }
            }
            if (difficulty.equals("Easy") && bestRow != -1) break;
        }

        if (bestRow != -1) {
            placePiece(bestRow, bestCol);
            currentPlayer = oldPlayer;
            return true;
        }

        currentPlayer = oldPlayer;
        return false;
    }

    // Estimate how many pieces this move would flip
    private int simulateFlipScore(int row, int col) {
        int count = 0;
        char opponent = (currentPlayer == BLACK) ? WHITE : BLACK;
        int[] dx = {-1, -1, -1, 0, 1, 1, 1, 0};
        int[] dy = {-1, 0, 1, 1, 1, 0, -1, -1};

        for (int d = 0; d < 8; d++) {
            int x = row + dx[d], y = col + dy[d];
            int temp = 0;
            while (isOnBoard(x, y) && board[x][y] == opponent) {
                x += dx[d];
                y += dy[d];
                temp++;
            }
            if (isOnBoard(x, y) && board[x][y] == currentPlayer)
                count += temp;
        }
        return count;
    }

}
