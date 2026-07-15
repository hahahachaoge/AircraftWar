package cn.edu.scnu.application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 自定义面板类，用于在游戏模式选择界面中展示带背景图片和文字说明的可点击面板。
 * 每个 ModePanel 实例加载一张背景图片，在图片底部绘制半透明遮罩和模式名称，
 * 并支持通过 {@link Runnable} 回调响应鼠标点击事件，从而实现不同游戏模式（如街机模式、闯关模式等）的入口。
 *
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public class ModePanel extends JPanel {
    private Image image;
    private String text;
    private Runnable action;

    /**
     * 构造一个模式选择面板。
     *
     * @param imagePath 背景图片的资源路径（相对于 classpath，如 "/images/arcade.png"）
     * @param text      面板底部显示的模式名称
     * @param action    鼠标点击该面板时要执行的回调操作（如切换到对应游戏模式）
     */
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

    /**
     * 重写组件的绘制方法，完成以下渲染步骤：<br>
     * 1. 如果背景图片加载成功，按比例缩放并居中绘制图片，使其完全覆盖面板区域；<br>
     * 2. 如果背景图片加载失败，绘制灰色背景并显示错误提示文字；<br>
     * 3. 在面板底部绘制半透明黑色遮罩条；<br>
     * 4. 在遮罩条上居中绘制白色模式名称文字（微软雅黑，粗体，24磅）。
     *
     * @param g 用于绘制的 {@link Graphics} 上下文对象
     */
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