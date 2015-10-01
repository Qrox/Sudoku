package Sudoku;

public abstract class AbstractSudokuData {

    protected int w, h, N;
    protected byte d[][];
    protected boolean b[][][];

    public AbstractSudokuData() {
        this(3, 3, null);
    }

    public AbstractSudokuData(int w, int h) {
        this(w, h, null);
    }

    public AbstractSudokuData(byte d[][]) {
        this(3, 3, d);
    }

    public AbstractSudokuData(int w, int h, byte d0[][]) {
        this.w = w;
        this.h = h;
        N = w * h;
        d = new byte[N][N];
        b = new boolean[N][N][N];
        if (d0 != null) {
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    if (d0[i][j] != 0) {
                        mark(i, j, d0[i][j]);
                    }
                }
            }
        }
    }

    public abstract void mark(int x, int y, byte v);

    public abstract void unmark(int x, int y);

    public abstract byte get(int x,int y);

    public abstract boolean getChangeable(int x,int y,int v);
    
    public abstract boolean getFillable(int x,int y,int v);
    
    public abstract boolean isOccupied(int x,int y);

    public abstract void clear();
}
