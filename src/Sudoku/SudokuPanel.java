package Sudoku;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class SudokuPanel extends JPanel implements MouseListener {

    public static void main(String args[]) {
        JFrame fr = new JFrame();
        fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        fr.setContentPane(new SudokuPanel(fr));
        fr.pack();
        fr.setVisible(true);
    }
    private static final AlphaComposite composite1 = AlphaComposite.getInstance(AlphaComposite.SRC, 1.0F),
            composite2 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.95F);
    private static final int n = 20, PA = 10, BW = 50, BH = 20, MAX_ALLOWED = 16;
    private SudokuData sd;
    private boolean left, right;
    private int pressedButton = -1;
    private int cx, cy, s = -1, w, h, W, H, N, nw, nh, boxw, boxh, BMH;
    private Button buttons[], solve, prev, next;
    private ArrayList<byte[][]> sol;
    private SudokuSolver su;
    private JFrame fr;

    public SudokuPanel(JFrame fr) {
        this.fr = fr;
        buttons = new Button[5];
        buttons[0] = new Button(0, PA, PA, BW, BH, "new", true);
        buttons[1] = new Button(1, PA, BH + (PA << 1), BW, BH, "clear", true);
        solve = buttons[2] = new Button(2, PA, (BH << 1) + ((PA << 1) + PA), BW, BH, "solve", true);
        prev = buttons[3] = new Button(3, PA, (BH << 1) + ((PA << 1) + PA), BW, BH, "prev", false);
        next = buttons[4] = new Button(4, PA, ((BH << 1) + BH) + (PA << 2), BW, BH, "next", false);
        BMH = (BH << 2) + (PA << 2);
        set(3, 3);
        addMouseListener(this);
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        int x = e.getX(), y = e.getY();
        switch (e.getButton()) {
            case MouseEvent.BUTTON1:
                for (int i = 0; i < buttons.length; i++) {
                    if (buttons[i].action(x, y)) {
                        buttons[i].pressed = true;
                        pressedButton = i;
                        repaint();
                        break;
                    }
                }
                if (!right) {
                    left = true;
                    trans(x - (BW + (PA << 1)), y);
                    repaint();
                }
                break;
            case MouseEvent.BUTTON3:
                if (left) {
                    left = false;
                    repaint();
                } else {
                    right = true;
                    trans(x - (BW + (PA << 1)), y);
                    //don't need to repaint cuz not change is to be done to the gui.
                }
                break;
        }
    }

    public void mouseReleased(MouseEvent e) {
        int x = e.getX(), y = e.getY();
        switch (e.getButton()) {
            case MouseEvent.BUTTON1:
                if (pressedButton != -1) {
                    buttons[pressedButton].pressed = false;
                    if (buttons[pressedButton].action(x, y)) {
                        actionPerformed(buttons[pressedButton].id);
                    }
                    pressedButton = -1;
                    repaint();
                }
                if (left) {
                    left = false;
                    trans0(x - (BW + (PA << 1)), y);
                    repaint();
                }
                break;
            case MouseEvent.BUTTON3:
                if (right) {
                    right = false;
                    clear(x - (BW + (PA << 1)), y);
                    repaint();
                }
                break;
        }
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    private void showSolve() {
        s = -1;
        su = null;
        sol = null;
        solve.visible = true;
        solve.enabled = true;
        prev.visible = false;
        next.visible = false;
    }

    private void hideSolve(boolean b) {
        solve.visible = false;
        prev.visible = true;
        prev.enabled = false;
        next.visible = true;
        next.enabled = b;
    }

    private void trans(int x, int y) {
        if (x < 0 || y < 0 || x > N * W || y > N * H) {
            left = false;
            right = false;
            return;
        }
        cx = x / W;
        cy = y / H;
    }

    private void trans0(int x, int y) {
        if (x < 0 || y < 0 || x > N * W || y > N * H) {
            return;
        }
        int cx0 = x / W, cy0 = y / H, px = x % W / nw, py = y % H / nh;
        byte ind = (byte) (py * boxw + px);
        if (ind >= N) {
            unmark(cx0, cy0);
            return;
        }
        if (cx0 == cx && cy0 == cy && !getFillable(cx0, cy0, ind + 1) && get(cx0, cy0) != ind + 1) {
            showSolve();
            if (get(cx0, cy0) != 0) {
                unmark(cx0, cy0);
            }
            mark(cx0, cy0, (byte) (ind + 1));
        }
    }

    private void clear(int x, int y) {
        if (x < 0 || y < 0 || x > N * W || y > N * H) {
            return;
        }
        int cx0, cy0;
        if ((cx0 = x / W) == cx && (cy0 = y / H) == cy && get(cx0, cy0) != 0) {
            unmark(cx0, cy0);
            showSolve();
        }
    }

    public void clear() {
        sd.clear();
    }

    public byte get(int x, int y) {
        return sd.get(x, y);
    }

    public boolean getFillable(int x, int y, int v) {
        return sd.getChangeable(x, y, v);
    }

    public void mark(int x, int y, byte v) {
        sd.mark(x, y, v);
    }

    public void unmark(int x, int y) {
        sd.unmark(x, y);
    }

    private void actionPerformed(int id) {
        switch (id) {
            case 0://new
                String st = JOptionPane.showInputDialog("Input width and height (syntax: width*height)");
                if (st != null) {
                    String[] v = st.split("\\*");
                    try {
                        if (v.length != 2) {
                            throw new NumberFormatException();
                        }
                        set(Integer.parseInt(v[0].trim()), Integer.parseInt(v[1].trim()));
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Wrong syntax!");
                        return;
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(this, ex.getMessage());
                        return;
                    }
                    repaint();
                }
                break;
            case 1://clear
                clear();
                showSolve();
                repaint();
                break;
            case 2://solve
                try {
                    su = new SudokuSolver(sd);
                    byte b[][] = su.nextSolution();
                    if (b == null) {
                        su = null;
                        throw new Exception();
                    }
                    sol = new ArrayList<byte[][]>();
                    s = 0;
                    sol.add(b);
                    b = su.nextSolution();
                    if (b == null) {
                        hideSolve(false);
                    } else {
                        hideSolve(true);
                        sol.add(b);
                    }
                } catch (Exception e) {
                    //todo:提示无解
                    solve.enabled = false;
                }
                break;
            case 3://prev
                next.enabled = true;
                s--;
                if (s == 0) {
                    prev.enabled = false;
                }
                break;
            case 4://next
                prev.enabled = true;
                s++;
                if (s == sol.size() - 1) {
                    byte b[][] = su.nextSolution();
                    if (b != null) {
                        sol.add(b);
                    } else {
                        next.enabled = false;
                    }
                }
                break;
        }
    }

    private void set(int w, int h) throws IllegalArgumentException {
        if (w <= 0 || h <= 0) {
            throw new IllegalArgumentException("Width and height should not be zero or lower!");
        } else if (w * h > MAX_ALLOWED) {
            throw new IllegalArgumentException("Width * height has exceeded the max limit! (" + MAX_ALLOWED + ")");
        }
        this.w = w;
        this.h = h;
        N = w * h;
        boxh = (int) (Math.round(Math.sqrt(N)));
        boxw = N / boxh + (N % boxh != 0 ? 1 : 0);
        W = boxw * n;
        nw = n;
        nh = W / boxh;
        H = boxh * nh;//to avoid the remainder
        sd = new SudokuData(w, h);
        setPreferredSize(new Dimension((PA << 1) + BW + W * N, Math.max(BMH, H * N)));
        fr.pack();
        showSolve();
    }

    public void paintComponent(Graphics gr) {
        super.paintComponent(gr);
        Graphics2D g = (Graphics2D) gr;
        Shape sh = g.getClip();
        g.setFont(f);
        FontMetrics m = g.getFontMetrics();
        for (int i = 0; i < buttons.length; i++) {
            Button b = buttons[i];
            if (b.visible) {
                g.translate(b.x, b.y);
                g.setClip(0, 0, b.w, b.h);
                b.paint(g, m);
                g.translate(-b.x, -b.y);
            }
        }
        g.setClip(sh);
        g.translate((PA << 1) + BW, 0);
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {//paint in columns
                g.setComposite(composite1);
                g.setFont(f);
                g.setColor(Color.lightGray);
                for (int a = 1; a < boxw; a++) {
                    g.drawLine(a * nw, 0, a * nw, H);
                }
                for (int b = 1; b < boxh; b++) {
                    g.drawLine(0, b * nh, W, b * nh);
                }
                for (int b = 0, cnt = 0; b < boxh; b++) {
                    for (int a = 0; a < boxw; a++, cnt++) {//paint in rows
                        if (cnt >= N) {
                            break;
                        } else if (getFillable(i, j, cnt + 1)) {
                            g.setColor(Color.red);
                            g.fillRect(a * nw, b * nh, nw, nh);
                        } else {
                            String text = Integer.toString(cnt + 1);
                            if (cnt + 1 == get(i, j)) {
                                g.setColor(Color.black);
                            } else {
                                g.setColor(Color.gray);
                            }
                            g.drawString(text, a * nw + ((nw - m.stringWidth(text)) >> 1), b * nh + ((nh + m.getAscent()) >> 1));
                        }
                    }
                }
                if (!left || cx != i || cy != j) {
                    g.setComposite(composite2);
                    g.setColor(getBackground());
                    g.fillRect(0, 0, W + 1, H + 1);
                    g.setFont(f2);
                    if (s < 0 || get(i, j) != 0) {
                        g.setColor(Color.black);
                        String text = Integer.toString(get(i, j));
                        g.drawString(text, (W - m.stringWidth(text)) >> 1, (H + m.getHeight()) >> 1);
                    } else {
                        g.setColor(Color.gray);
                        String text = Integer.toString(sol.get(s)[i][j]);
                        g.drawString(text, (W - m.stringWidth(text)) >> 1, (H + m.getHeight()) >> 1);
                    }
                }
                g.translate(0, H);
            }
            g.translate(W, -N * H);
        }
        g.setComposite(composite1);
        g.translate(-N * W, 0);
        g.setColor(Color.black);
        for (int i = 1; i < N; i++) {
            g.fillRect(i * W - 1, 0, (i % w == 0) ? 3 : 1, H * N);
        }
        for (int j = 1; j < N; j++) {
            g.fillRect(0, j * H - 1, W * N, (j % h == 0) ? 3 : 1);
        }
        g.translate(-(PA << 1) - BW, 0);
    }
    static Font f, f2;

    static {
        /*
         * try { f = Font.createFont(Font.TRUETYPE_FONT,
         * SudokuPanel.class.getResourceAsStream("rc\\font.ttf")); if
         * (!GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(f))
         * { System.err.println("Fail to register font instance");
         * System.exit(1); } } catch (Exception e) { System.err.println("Fail to
         * create font instance"); System.exit(1); }
         */
        f = new Font("微软雅黑", Font.PLAIN, 20);
        f2 = new Font("微软雅黑 ", Font.BOLD, 30);
    }
}

class Button {

    Button(int id0, int x0, int y0, int w0, int h0, String t, boolean v) {
        id = id0;
        x = x0;
        y = y0;
        w = w0;
        h = h0;
        text = t;
        visible = v;
    }

    boolean action(int x0, int y0) {
        if (visible && enabled && x0 > x && x0 < x + w && y0 > y && y0 < y + h) {
            return true;
        }
        return false;
    }

    void paint(Graphics g, FontMetrics m) {
        g.setFont(SudokuPanel.f);
        g.setColor(enabled ? (pressed ? Pressed : Plain) : Disabled);
        g.fillRect(0, 0, w, h);
        g.setColor(enabled ? (pressed ? textPressed : textPlain) : textDisabled);
        g.drawString(text, (w - m.stringWidth(text)) >> 1, (h + m.getAscent() - m.getDescent()) >> 1);
    }
    private static final Color Pressed = Color.lightGray, Plain = Color.white, Disabled = Color.lightGray,
            textPressed = Color.black, textPlain = Color.black, textDisabled = Color.gray;
    int id, x, y, w, h;
    private String text;
    boolean visible, enabled = true, pressed = false;
}
