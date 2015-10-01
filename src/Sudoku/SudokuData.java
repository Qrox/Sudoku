package Sudoku;

public class SudokuData extends AbstractSudokuData {

    public SudokuData() {
        super(3, 3, null);
    }

    public SudokuData(int w, int h) {
        super(w, h, null);
    }

    public SudokuData(byte d[][]) {
        super(3, 3, d);
    }

    public SudokuData(int w, int h, byte d[][]) {
        super(w, h, d);
    }

    public byte get(int x, int y) {
        return d[x][y];
    }

    public boolean getChangeable(int x, int y, int v) {
        return b[x][y][v - 1];
    }

    public boolean getFillable(int x, int y, int v) {
        return d[x][y] == 0 && b[x][y][v - 1];
    }

    public boolean isOccupied(int x, int y) {
        return d[x][y] != 0;
    }

    public synchronized void clear() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                d[i][j] = 0;
                for (int k = 0; k < N; k++) {
                    b[i][j][k] = false;
                }
            }
        }
    }

    public synchronized void mark(int x, int y, byte v) {
        if (v == 0) {
            unmark(x, y);
        }
        if (getChangeable(x, y, v)) {
            throw new SudokuCellDuplicate();
        }
        d[x][y] = v;
        v--;
        for (int x0 = 0; x0 < N; x0++) {
            if (x0 != x) {
                b[x0][y][v] = true;
            }
        }
        for (int y0 = 0; y0 < N; y0++) {
            if (y0 != y) {
                b[x][y0][v] = true;
            }
        }
        for (int x0 = x / w * w, x1 = x0 + w; x0 < x1; x0++) {
            if (x0 != x) {
                for (int y0 = y / h * h, y1 = y0 + h; y0 < y1; y0++) {
                    if (y0 != y) {
                        b[x0][y0][v] = true;
                    }
                }
            }
        }
    }

    public synchronized void unmark(int x, int y) {
        if (d[x][y] == 0) {
            return;
        }
        int v = d[x][y] - 1;
        d[x][y] = 0;
        for (int x0 = 0; x0 < N; x0++) {
            if (b[x0][y][v]) {
                unmark0(x0, y, v);
            }
        }
        for (int y0 = 0; y0 < N; y0++) {
            if (b[x][y0][v]) {
                unmark0(x, y0, v);
            }
        }
        for (int x0 = x / w * w, x1 = x0 + w; x0 < x1; x0++) {
            for (int y0 = y / h * h, y1 = y0 + h; y0 < y1; y0++) {
                if (b[x0][y0][v]) {
                    unmark0(x0, y0, v);
                }
            }
        }
    }

    private void unmark0(int x, int y, int v) {
        v++;
        for (int x0 = 0; x0 < N; x0++) {
            if (d[x0][y] == v) {
                return;
            }
        }
        for (int y0 = 0; y0 < N; y0++) {
            if (d[x][y0] == v) {
                return;
            }
        }
        for (int x0 = x / w * w, x1 = x0 + w; x0 < x1; x0++) {
            for (int y0 = y / h * h, y1 = y0 + h; y0 < y1; y0++) {
                if (d[x0][y0] == v) {
                    return;
                }
            }
        }
        b[x][y][v - 1] = false;
    }
}
