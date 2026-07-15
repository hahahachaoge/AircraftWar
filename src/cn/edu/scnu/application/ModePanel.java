package cn.edu.scnu.application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 自定义的图片面板类，用于加载背景图、绘制文字并实现鼠标点击
 */
public class ModePanel extends JPanel {
    private Image image;
    private String text;
    private Runnable action;

    public ModePanel(String imagePath, String text, Runnable action) {
        this.text = text;
        this.action = action;

        // 加载图片
        try {
            java.net.URL imgUrl = getClass().getResource(imagePath);
            if (imgUrl != null) {
                image = new ImageIcon(imgUrl).getImage();
            } else {
                System.err.println("图片加载失败，请检查路径: " + imagePath);
            }
        } catch (Exception e) {
            System.err.println("背景图 " + imagePath + " 加载异常！");
            e.printStackTrace();
        }

        // 添加鼠标点击事件
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (action != null) {
                    action.run();
                }
            }
        });
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (image != null) {
            int imgW = image.getWidth(null);
            int imgH = image.getHeight(null);
            double panelRatio = (double) getWidth() / getHeight();
            double imgRatio = (double) imgW / imgH;
            int drawW, drawH, drawX, drawY;
            if (imgRatio > panelRatio) {
                drawH = getHeight();
                drawW = (int) (drawH * imgRatio);
                drawX = (getWidth() - drawW) / 2;
                drawY = 0;
            } else {
                drawW = getWidth();
                drawH = (int) (drawW / imgRatio);
                drawX = 0;
                drawY = (getHeight() - drawH) / 2;
            }
            g2d.drawImage(image, drawX, drawY, drawW, drawH, this);
        } else {
            g2d.setColor(new Color(200, 200, 200));
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.setColor(Color.RED);
            g2d.setFont(new Font("微软雅黑", Font.BOLD, 12));
            g2d.drawString("图片加载失败: " + text, getWidth()/2 - 50, getHeight()/2);
        }

        g2d.setColor(new Color(0, 0, 0, 160));
        g2d.fillRect(0, getHeight() - 60, getWidth(), 60);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("微软雅黑", Font.BOLD, 24));
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int x = (getWidth() - textWidth) / 2;
        int y = getHeight() - 20;
        g2d.drawString(text, x, y);
    }
}