package cn.edu.scnu.show;

import javax.swing.*;
import java.awt.*;

/**
 * 该面板继承自 {@link JPanel}，用于在游戏中展示详细的操作说明、游戏规则以及
 * 道具功能介绍。面板内嵌一个支持 HTML 渲染的 {@link JTextPane}，并配有滚动条
 * 以便浏览较长内容；底部提供一个"返回主菜单"按钮，供用户切换回主界面。
 * </p>
 * <p>
 * 使用方式：在父容器（例如 {@link JFrame} 或另一个 {@link JPanel}）中通过
 * {@link CardLayout} 管理该面板，构造时传入相同的 {@link CardLayout} 实例
 * 及父容器引用即可自动完成按钮的事件绑定。
 * </p>
 *
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public class InstructionPanel extends JPanel {

    /**
     * 构造游戏说明面板。
     * <p>
     * 初始化面板布局为 {@link BorderLayout}，在中央区域放置一个只读的
     * {@link JTextPane}（内容为 HTML 格式的游戏说明），并包裹在
     * {@link JScrollPane} 中以支持滚动。底部放置"返回主菜单"按钮，
     * 点击后通过指定的 {@link CardLayout} 将父容器切换到名为 {@code "main"}
     * 的卡片。
     * </p>
     *
     * @param cl     用于管理父容器卡片切换的 {@link CardLayout} 实例，
     *               点击返回按钮时将调用 {@code cl.show(parent, "main")}
     *               跳转到主菜单卡片。
     * @param parent 持有该面板的父容器（通常是一个 {@link JPanel} 或
     *               {@link JFrame}），必须已与 {@code cl} 关联，否则
     *               {@code cl.show(...)} 调用将抛出异常。
     */
    public InstructionPanel(CardLayout cl, JPanel parent) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JTextPane tp = new JTextPane();
        tp.setContentType("text/html");
        tp.setText("<html><body style='font-family:微软雅黑;font-size:14px'>"
                + "<h2 style='text-align:center'>游戏说明</h2>"
                + "<b>操作方式</b><br>单人鼠标移动，双人WASD+鼠标<br><br>"
                + "<b>游戏规则</b><br>消灭敌机得分，漏敌扣血扣分<br><br>"
                + "<b>道具说明</b><br>❤️回血 🔥火力 💣炸弹 ❄️冰冻<br><br>祝您游戏愉快！</body></html>");
        tp.setEditable(false);
        tp.setBackground(Color.WHITE);

        JScrollPane sp = new JScrollPane(tp);
        sp.setBorder(null);
        add(sp, BorderLayout.CENTER);

        JButton btn = new JButton("返回主菜单");
        btn.setFont(new Font("微软雅黑", Font.BOLD, 18));
        btn.setPreferredSize(new Dimension(160, 50));
        btn.addActionListener(e -> cl.show(parent, "main"));

        JPanel bp = new JPanel();
        bp.setBackground(Color.WHITE);
        bp.add(btn);
        add(bp, BorderLayout.SOUTH);
    }
}
