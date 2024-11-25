package org.example.news;

import javax.swing.border.AbstractBorder;
import java.awt.*;

public class BottomBorder extends AbstractBorder {
    private final int thickness;

    public BottomBorder(int thickness) {
        this.thickness = thickness;
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(0, 0, thickness, 0);
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(Color.GRAY);
        g2d.fillRect(x, y + height - thickness, width, thickness);
        g2d.dispose();
    }
}