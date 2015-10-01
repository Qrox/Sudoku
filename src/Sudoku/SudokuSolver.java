package Sudoku;

import java.util.LinkedList;

public class SudokuSolver {

    // <editor-fold defaultstate="collapsed" desc="DEBUG">
    private static boolean DEBUG = false;

    static {
        if (DEBUG) {
            System.out.println("Sudoku: DEBUG on");
        }
    }

    private static void print(String s) {
        if (DEBUG) {
            System.out.print(s);
        }
    }

    private static void println(String s) {
        if (DEBUG) {
            System.out.println(s);
        }
    }

    private static void println() {
        if (DEBUG) {
            System.out.println();
        }
    }

    private void show() {
        String separatorc = "|", separatorr = "---", separatorcross = "+";
        for (int j = 0; j < N; j++) {
            for (int k = 0; k < h; k++) {
                for (int i = 0; i < N; i++) {
                    if (i % w == w - 1) {
                        separatorc = "#";
                    } else {
                        separatorc = "|";
                    }
                    if (j % h == h - 1) {
                        separatorr = "###";
                        separatorcross = "#";
                    } else {
                        separatorr = "---";
                        separatorcross = "+";
                    }
                    if (bo[i][j] == null) {
                        if (k == h / 2) {
                            for (int a = 0; a < w >> 1; a++) {
                                print("   ");
                            }
                            print(" " + (by[i][j] + 1) + " ");
                            for (int a = 0; a < w - (w >> 1) - 1; a++) {
                                print("   ");
                            }
                            print(separatorc);
                        } else {
                            for (int a = 0; a < w; a++) {
                                print("   ");
                            }
                            print(separatorc);
                        }
                    } else {
                        for (int l = 0; l < w; l++) {
                            if (bo[i][j][k * w + l]) {
                                print(l == w - 1 ? " X " + separatorc : " X ");
                            } else {
                                print(l == w - 1 ? "   " + separatorc : "   ");
                            }
                        }
                    }
                }
                println();
            }
            for (int n = 0; n < N * (w + 1) - 1; n++) {
                if (n % (w + 1) == w) {
                    print(separatorcross);
                } else {
                    print(separatorr);
                }
            }
            print(separatorc);
            println();
        }
        println();
        /*separator("#", "#");
        println();
        String sepc = "|";
        for (int j = 0; j < N; j++) {
        for (int b = 0; b < h; b++) {
        print("#");
        for (int i = 0; i < N; i++) {
        if (i % w == w - 1) {
        sepc = "#";
        } else {
        sepc = "|";
        }
        if (bo[i][j] != null) {
        for (int a = 0; a < w; a++) {
        if (bo[i][j][b * w + a]) {
        print("X");
        } else {
        print(" ");
        }
        if (a != w - 1) {
        print(":");
        } else {
        print(sepc);
        }
        }
        } else {
        if (b == h / 2) {
        for (int x = 0; x < w / 2; x++) {
        print("  ");
        }
        print(by[i][j] + 1 + " ");
        for (int x = w / 2 + 1; x < w - 1; x++) {
        print("  ");
        }
        print(" " + sepc);
        } else {
        for (int a = 0; a < w - 1; a++) {
        print("  ");
        }
        print(" " + sepc);
        }
        }
        }
        println();
        if (b / h != h - 1) {
        print("#");
        for (int i = 0; i < N; i++) {
        if (i % w == w - 1) {
        sepc = "#";
        } else {
        sepc = "|";
        }
        if (bo[i][j] != null) {
        for (int a = 0; a < w * 2 - 1; a++) {
        if ((a & 1) == 0) {
        print("\"");
        } else {
        print("`");
        }
        }
        print(sepc);
        } else {
        for (int a = 0; a < w * 2 - 1; a++) {
        print(" ");
        }
        print(sepc);
        }
        }
        } else {
        separator("#", "#");
        }
        println();
        }
        }*/
    }

    private void separator(String s1, String s2) {
        for (int a = 0; a < N; a++) {
            print(s1);
            for (int b = 0; b < (w << 1) - 1; b++) {
                print(s2);
            }
        }
        print(s1);
    }// </editor-fold>
    private TrialData trial;
    private final LinkedList<TrialData> Trial;
    private boolean bo[][][];
    private final int w, h, N;
    private byte by[][];

    public SudokuSolver(byte d[][]) {
        this(3, 3, d);
    }

    public SudokuSolver(SudokuData sd) {
        this(sd.w, sd.h, sd.d);
    }

    /*
     * w 和 h 表示次级大小的格子的长宽，默认为3*3。
     */
    public SudokuSolver(int w, int h, byte d[][]) {
        println("init():w=" + w + ";h=" + h);
        Trial = new LinkedList<TrialData>();
        if (w <= 0 || h <= 0) {
            throw new IllegalArgumentException("长和宽应该大于0");
        }
        if (w * h > Byte.MAX_VALUE) {
            throw new IllegalArgumentException("不支持w*h大于127的情况");
        }
        this.w = w;
        this.h = h;
        N = w * h;
        check(d);
        by = new byte[N][N];
        bo = new boolean[N][N][];
        trial = new TrialData();
        trial.bo = bo;
        trial.by = by;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (d[i][j] == 0) {
                    bo[i][j] = new boolean[N];
                    by[i][j] = (byte) N;
                } else {//I don't know why but if delete this code (seems useless) the program will not function correctly
                    bo[i][j] = null;
                    by[i][j] = (byte) (d[i][j] - 1);
                }
            }
        }
        try {
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    if (d[i][j] != 0) {
                        mark(i, j, d[i][j] - 1);
                    }
                }
            }
            test();
        } catch (RuntimeException e) {
            trial = null;
        }
    }

    public byte[][] nextSolution() {
        loopa:
        while (true) {
            if (trial == null) {
                return null;
            }
            int lu = trial.leastUnsettled;
            if (lu == N + 1) {
                byte ret[][] = transform();
                pop();
                return ret;
            } else {
                int x = trial.px, y = trial.py;
                for (int i = trial.tried; i < N; i++) {
                    if (!bo[x][y][i]) {
                        trial.tried = i + 1;
                        push();
                        try {
                            println("try to fill (" + (x + 1) + "," + (y + 1) + ") with number " + (i + 1));
                            mark(x, y, i);
                            test();
                            show();
                        } catch (RuntimeException e) {
                            println("trial failed");
                            pop();
                            continue;
                        }
                        continue loopa;
                    }
                }
                pop();
            }
        }
    }

    private byte[][] transform() {
        byte ret[][] = new byte[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                ret[i][j] = (byte) (by[i][j] + 1);
            }
        }
        return ret;
    }

    private void pop() {
        trial = Trial.poll();
        if (trial != null) {
            println("load previous data");
            bo = trial.bo;
            by = trial.by;
        } else {
            println("trials completed");
        }
    }

    private void push() {
        println("save current data");
        Trial.addFirst(trial);
        trial = trial.clone();
        bo = trial.bo;
        by = trial.by;
    }

    private void test() {
        trial.leastUnsettled = N + 1;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (bo[i][j] != null && by[i][j] < trial.leastUnsettled) {
                    trial.leastUnsettled = by[i][j];
                    trial.px = i;
                    trial.py = j;
                }
            }
        }
    }

    private void mark(int x, int y, int v) {
        bo[x][y] = null;
        by[x][y] = (byte) v;
        for (int x0 = 0; x0 < N; x0++) {
            if (x0 != x) {
                mark0(x0, y, v);
            }
        }
        for (int y0 = 0; y0 < N; y0++) {
            if (y0 != y) {
                mark0(x, y0, v);
            }
        }
        for (int x0 = x / w * w, x1 = x0 + w; x0 < x1; x0++) {
            if (x0 != x) {
                for (int y0 = y / h * h, y1 = y0 + h; y0 < y1; y0++) {
                    if (y0 != y) {
                        mark0(x0, y0, v);
                    }
                }
            }
        }
    }

    private void mark0(int x, int y, int v) {
        if (bo[x][y] == null) {
            if (by[x][y] == v) {
                throw new RuntimeException();
            }
        } else if (!bo[x][y][v]) {
            bo[x][y][v] = true;
            int t = --by[x][y];
            switch (t) {
                case 0:
                    throw new RuntimeException();
                case 1:
                    mark(x, y, value(x, y));
            }
        }
    }

    private int value(int x, int y) {
        for (int i = 0; i < N; i++) {
            if (!bo[x][y][i]) {
                return i;
            }
        }
        throw new RuntimeException("in value() no value is returned");
    }

    private void clear(boolean b[]) {
        for (int i = 0; i < N; i++) {
            b[i] = false;
        }
    }

    private void check(byte d[][]) {
        boolean b[] = new boolean[N];
        try {
            for (int i = 0; i < N; i++) {
                clear(b);
                for (int j = 0; j < N; j++) {
                    if (d[i][j] < 0 || d[i][j] > N) {
                        throw new IllegalArgumentException("The number at (" + i + "," + j + ") is out of range." + d[i][j]);
                    } else if (d[i][j] != 0) {
                        if (b[d[i][j] - 1]) {
                            throw new RuntimeException(i + "," + j + "(when checking rows)");
                        } else {
                            b[d[i][j] - 1] = true;
                        }
                    }
                }
            }
            for (int j = 0; j < N; j++) {
                clear(b);
                for (int i = 0; i < N; i++) {
                    if (d[i][j] != 0) {
                        if (b[d[i][j] - 1]) {
                            throw new RuntimeException(i + "," + j + "(when checking columns)");
                        } else {
                            b[d[i][j] - 1] = true;
                        }
                    }
                }
            }
            for (int i = 0; i < h; i++) {
                for (int j = 0; j < w; j++) {
                    clear(b);
                    for (int x = i * w, x0 = x + w; x < x0; x++) {
                        for (int y = j * h, y0 = y + h; y < y0; y++) {
                            if (d[x][y] != 0) {
                                if (b[d[x][y] - 1]) {
                                    throw new RuntimeException(x + "," + y + "(when checking blocks)");
                                } else {
                                    b[d[x][y] - 1] = true;
                                }
                            }
                        }
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("The number at (" + e.getMessage() + ") is duplicate with another number");
        }
    }

    /*public static void main(String args[]) {
    byte test[][] = {
    {0, 0, 0, 3, 9, 0, 0, 0, 5},
    {9, 0, 0, 0, 0, 0, 0, 4, 1},
    {0, 0, 8, 0, 4, 6, 9, 0, 3},
    {4, 7, 6, 0, 0, 0, 0, 5, 0},
    {0, 0, 2, 6, 0, 3, 4, 0, 0},
    {0, 8, 0, 0, 0, 0, 2, 1, 6},
    {8, 0, 4, 5, 1, 0, 7, 0, 0},
    {2, 1, 0, 0, 0, 0, 0, 0, 4},
    {6, 0, 0, 0, 3, 7, 0, 0, 0}
    };
    SudokuSolver su = new SudokuSolver(3, 3, test);
    int N = su.N;
    test = su.by;
    su.show();
    while ((test = su.nextSolution()) != null) {
    for (int i = 0; i < N; i++) {
    for (int j = 0; j < N; j++) {
    print(test[i][j] + ",");
    }
    println();
    }
    println();
    }
    println("finished");
    }*/
    class TrialData {

        boolean bo[][][];
        byte by[][];
        int leastUnsettled, tried = 0, px, py;

        public TrialData clone() {
            TrialData t = new TrialData();
            boolean bo1[][][] = new boolean[N][N][];
            byte by1[][] = new byte[N][N];
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    if (bo[i][j] == null) {
                        bo1[i][j] = null;
                    } else {
                        bo1[i][j] = new boolean[N];
                        System.arraycopy(bo[i][j], 0, bo1[i][j], 0, N);
                    }
                }
                System.arraycopy(by[i], 0, by1[i], 0, N);
            }
            t.bo = bo1;
            t.by = by1;
            return t;
        }
    }
}
