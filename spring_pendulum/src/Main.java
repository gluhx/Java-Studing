import physics.Point;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayDeque;
import java.util.Deque;

/** Наглядная анимация пружинного маятника для модели Point. */
public class Main {
    public static void main(String[] args) { SwingUtilities.invokeLater(Window::new); }
}

class Window extends JFrame {
    private final JTextField mass = new JTextField("1.0", 5);
    private final JTextField stiffness = new JTextField("10.0", 5);
    private final JTextField position = new JTextField("1.0", 5);
    private final JLabel status = new JLabel();
    private final View view = new View();
    private Point point;
    private double time;
    private boolean running = true;

    Window() {
        super("Пружинный маятник");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.add(new JLabel("Масса m:")); controls.add(mass);
        controls.add(new JLabel("Жёсткость k:")); controls.add(stiffness);
        controls.add(new JLabel("Начальное x:")); controls.add(position);
        JButton reset = new JButton("Применить / сбросить");
        reset.addActionListener(e -> reset()); controls.add(reset);
        JButton pause = new JButton("Пауза");
        pause.addActionListener(e -> { running = !running; pause.setText(running ? "Пауза" : "Продолжить"); }); controls.add(pause);
        add(controls, BorderLayout.NORTH); add(view); add(status, BorderLayout.SOUTH);
        status.setBorder(BorderFactory.createEmptyBorder(0, 10, 8, 10));
        pack(); setLocationByPlatform(true); setVisible(true); reset();
        new Timer(16, e -> {
            if (running && point != null) {
                for (int i = 0; i < 10; i++) { point.calculate_point(0.001f); time += .001; }
                view.add(point.get_x()); showValues();
            }
            view.repaint();
        }).start();
    }

    private void reset() {
        try {
            float m = Float.parseFloat(mass.getText().replace(',', '.'));
            float k = Float.parseFloat(stiffness.getText().replace(',', '.'));
            float x = Float.parseFloat(position.getText().replace(',', '.'));
            if (m <= 0 || k <= 0) throw new NumberFormatException();
            point = new Point(k, x, m); time = 0; view.clear(x); showValues();
        } catch (NumberFormatException e) { status.setText("Введите положительные числа для массы и жёсткости."); }
    }
    private void showValues() {
        status.setText(String.format("t = %.3f с     x = %.4f м     v = %.4f м/с     a = %.4f м/с²", time, point.get_x(), point.get_V(), point.get_a()));
    }
}

class View extends JPanel {
    private static final int MAX = 320;
    private final Deque<Float> values = new ArrayDeque<>();
    View() { setPreferredSize(new Dimension(900, 500)); setBackground(Color.WHITE); }
    void clear(float x) { values.clear(); values.add(x); }
    void add(float x) { values.addLast(x); if (values.size() > MAX) values.removeFirst(); }

    @Override protected void paintComponent(Graphics source) {
        super.paintComponent(source); Graphics2D g = (Graphics2D) source.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth(), h = getHeight(), split = w * 3 / 5, middle = h / 2;
        float value = values.isEmpty() ? 0 : values.getLast();
        int massY = Math.max(80, Math.min(h - 50, middle + Math.round(value * 85))), cx = split / 2;
        g.setColor(Color.DARK_GRAY); g.setStroke(new BasicStroke(6)); g.drawLine(cx - 120, 45, cx + 120, 45);
        int[] xs = new int[18], ys = new int[18]; xs[0] = cx; ys[0] = 45;
        for (int i = 1; i < 17; i++) { xs[i] = cx + (i % 2 == 0 ? -18 : 18); ys[i] = 45 + i * (massY - 70) / 17; }
        xs[17] = cx; ys[17] = massY - 25; g.setColor(new Color(40, 110, 190)); g.setStroke(new BasicStroke(2)); g.drawPolyline(xs, ys, xs.length);
        g.setColor(new Color(230, 120, 45)); g.fillRoundRect(cx - 30, massY - 25, 60, 50, 12, 12);
        g.setColor(Color.DARK_GRAY); g.drawString("пружина и груз", 15, 25); g.drawString("равновесие", 15, middle - 5); g.drawLine(10, middle, split - 15, middle);
        int x0 = split + 45, x1 = w - 20; g.setColor(new Color(230, 230, 230)); g.drawLine(x0, middle, x1, middle); g.setColor(Color.DARK_GRAY); g.drawString("x(t)", x0, 25);
        if (values.size() > 1) {
            float max = .2f; for (float x : values) max = Math.max(max, Math.abs(x)); max *= 1.15f;
            g.setColor(new Color(190, 50, 65)); int px = x0, py = middle - Math.round(values.getFirst() / max * (h / 2 - 45)), i = 0;
            for (float x : values) { int nx = x0 + i * (x1 - x0) / (MAX - 1), ny = middle - Math.round(x / max * (h / 2 - 45)); if (i++ > 0) g.drawLine(px, py, nx, ny); px = nx; py = ny; }
            g.setColor(Color.DARK_GRAY); g.drawString(String.format("+%.2f м", max), x0 + 3, 45); g.drawString(String.format("−%.2f м", max), x0 + 3, h - 25);
        }
        g.dispose();
    }
}
