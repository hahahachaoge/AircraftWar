package cn.edu.scnu.show;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 继承自 {@link JPanel}，用于在游戏选单界面中以卡片形式展示不同的游戏模式。
 * 每个面板包含一张背景图片和一段说明文字，用户点击后触发对应的 {@link Runnable} 动作。
 * 面板会自动缩放图片以自适应面板尺寸，并在底部绘制半透明遮罩和居中的模式名称。
 * </p>
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
     * @param imgPath 背景图片的资源路径（相对于当前类所在包，例如 {@code "/images/mode_easy.png"}）。
     *                若资源不存在则静默失败，面板仅显示文字而不显示图片。
     * @param text    在面板底部显示的模式名称（中文或英文，推荐不超过 8 个字符）。
     * @param action  点击面板时要执行的动作（例如切换到该模式的 {@link Runnable} 逻辑）；
     *                允许为 {@code null}，此时点击无响应。
     */
    public ModePanel(String imgPath, String text, Runnable action) {
        this.text = text;
        this.action = action;
        try {
            java.net.URL url = getClass().getResource(imgPath);
            if (url != null) image = new ImageIcon(url).getImage();
        } catch (Exception e) {
            System.err.println("图片加载失败: " + imgPath);
        }
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (action != null) action.run();
            }
        });
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    /**
     * 自定义绘制组件内容。
     * <ul>
     *   <li>先绘制父类默认外观；</li>
     *   <li>若有背景图片，则按面板宽高比进行等比例缩放并居中绘制（裁剪超出部分）；</li>
     *   <li>在面板底部绘制一条半透明黑色遮罩；</li>
     *   <li>在遮罩层上居中绘制模式名称（白色、微软雅黑、加粗 24 号）。</li>
     * </ul>
     *
     * @param g 用于绘制的 {@link Graphics} 上下文，会被转换为 {@link Graphics2D} 使用。
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // 绘制缩放后的背景图片（居中裁剪，保持比例）
        if (image != null) {
            int iw = image.getWidth(null), ih = image.getHeight(null);
            double pr = (double) getWidth() / getHeight(), ir = (double) iw / ih;
            int dw, dh, dx, dy;
            if (ir > pr) {
                dh = getHeight();
                dw = (int) (dh * ir);
                dx = (getWidth() - dw) / 2;
                dy = 0;
            } else {
                dw = getWidth();
                dh = (int) (dw / ir);
                dx = 0;
                dy = (getHeight() - dh) / 2;
            }
            g2d.drawImage(image, dx, dy, dw, dh, null);
        }

        // 底部半透明遮罩
        g2d.setColor(new Color(0, 0, 0, 160));
        g2d.fillRect(0, getHeight() - 60, getWidth(), 60);

        // 模式名称文字（居中）
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("微软雅黑", Font.BOLD, 24));
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(text, (getWidth() - fm.stringWidth(text)) / 2, getHeight() - 20);
    }
}
