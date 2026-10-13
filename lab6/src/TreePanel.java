import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.util.HashMap;
import java.util.Map;

public class TreePanel extends JPanel {

    private static final int GAP_X = 44;
    private static final int GAP_Y = 66;
    private static final int RADIUS = 19;
    private static final int MARGIN = 40;
    private static final Color RED_NODE = new Color(0xD32F2F);
    private static final Color BLACK_NODE = new Color(0x212121);
    private static final Color MARK = new Color(0xFFB300);

    private RedBlackTree.NodeView root;
    private Integer marked;
    private final Map<Integer, Point> positions = new HashMap<>();
    private int columns;
    private int deepest;

    public TreePanel() {
        setBackground(Color.WHITE);
    }

    public void setTree(RedBlackTree.NodeView root, Integer marked) {
        this.root = root;
        this.marked = marked;
        positions.clear();
        columns = 0;
        deepest = 0;
        place(root, 0);
        revalidate();
        repaint();
    }

    private void place(RedBlackTree.NodeView node, int depth) {
        if (node == null) {
            return;
        }
        place(node.left(), depth + 1);
        positions.put(node.value(), new Point(MARGIN + columns * GAP_X, MARGIN + depth * GAP_Y));
        columns++;
        deepest = Math.max(deepest, depth);
        place(node.right(), depth + 1);
    }

    private int treeWidth() {
        return 2 * MARGIN + Math.max(columns - 1, 0) * GAP_X;
    }

    @Override
    public Dimension getPreferredSize() {
        if (root == null) {
            return new Dimension(400, 200);
        }
        return new Dimension(treeWidth(), 2 * MARGIN + deepest * GAP_Y);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (root == null) {
            g2.setColor(Color.GRAY);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 16));
            g2.drawString("The tree is empty. Add a number.", 30, 40);
            g2.dispose();
            return;
        }

        g2.translate(Math.max(0, (getWidth() - treeWidth()) / 2), 0);
        g2.setStroke(new BasicStroke(2f));
        drawLines(g2, root);
        g2.setFont(new Font("SansSerif", Font.BOLD, 14));
        drawNodes(g2, root);
        g2.dispose();
    }

    private void drawLines(Graphics2D g2, RedBlackTree.NodeView node) {
        if (node == null) {
            return;
        }
        Point from = positions.get(node.value());
        g2.setColor(Color.GRAY);
        if (node.left() != null) {
            Point to = positions.get(node.left().value());
            g2.drawLine(from.x, from.y, to.x, to.y);
        }
        if (node.right() != null) {
            Point to = positions.get(node.right().value());
            g2.drawLine(from.x, from.y, to.x, to.y);
        }
        drawLines(g2, node.left());
        drawLines(g2, node.right());
    }

    private void drawNodes(Graphics2D g2, RedBlackTree.NodeView node) {
        if (node == null) {
            return;
        }
        Point p = positions.get(node.value());

        if (marked != null && marked == node.value()) {
            g2.setColor(MARK);
            g2.fillOval(p.x - RADIUS - 5, p.y - RADIUS - 5, 2 * RADIUS + 10, 2 * RADIUS + 10);
        }
        g2.setColor(node.red() ? RED_NODE : BLACK_NODE);
        g2.fillOval(p.x - RADIUS, p.y - RADIUS, 2 * RADIUS, 2 * RADIUS);

        g2.setColor(Color.WHITE);
        String text = String.valueOf(node.value());
        FontMetrics metrics = g2.getFontMetrics();
        g2.drawString(text, p.x - metrics.stringWidth(text) / 2, p.y + metrics.getAscent() / 2 - 1);

        drawNodes(g2, node.left());
        drawNodes(g2, node.right());
    }
}
