package cn.edu.scnu.application;

import javax.swing.*;
import java.awt.*;

/**
 * 游戏说明面板。
 * <p>
 * 此类继承自 {@link JPanel}，负责显示游戏的详细说明页面。
 * 面板中包含一个滚动显示的 HTML 格式说明内容（包括游戏目标、操作方式、规则、
 * 敌机类型、道具说明等），以及一个返回主菜单的按钮。
 * </p>
 *
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public class InstructionPanel extends JPanel {
    private final CardLayout cardLayout;
    private final JPanel parent;

    /**
     * 构造一个游戏说明面板。
     * <p>
     * 初始化面板布局为 {@link BorderLayout}，背景设为白色。
     * 创建并添加一个包含 HTML 说明内容的只读文本编辑区（带滚动面板），
     * 并在底部添加一个"返回主菜单"按钮。
     * </p>
     *
     * @param cardLayout 用于切换页面的卡片布局管理器
     * @param parent     父面板，点击返回按钮时通过卡片布局切换回主菜单
     */
    public InstructionPanel(CardLayout cardLayout, JPanel parent) {
        this.cardLayout = cardLayout;
        this.parent = parent;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 说明内容（包进滚动面板）
        JTextPane textPane = new JTextPane();
        textPane.setContentType("text/html");
        textPane.setText(getInstructionHtml());
        textPane.setEditable(false);
        textPane.setBackground(Color.WHITE);
        textPane.setMargin(new Insets(15, 25, 15, 25));
        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setBorder(null);
        // 每次打开时滚动到顶部
        SwingUtilities.invokeLater(() -> scrollPane.getViewport().setViewPosition(new Point(0, 0)));
        add(scrollPane, BorderLayout.CENTER);

        // 返回按钮
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.WHITE);
        JButton backBtn = new JButton("返回主菜单");
        backBtn.setFont(new Font("微软雅黑", Font.BOLD, 18));
        backBtn.setPreferredSize(new Dimension(160, 50));
        backBtn.addActionListener(e -> cardLayout.show(parent, "main"));
        bottomPanel.add(backBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * 获取游戏说明的 HTML 字符串。
     * <p>
     * 返回一个格式化的 HTML 页面字符串，包含游戏目标、操作方式、游戏规则、
     * 漏敌惩罚、敌机类型、教学模式、难度说明以及道具说明等内容。
     * </p>
     *
     * @return 包含完整游戏说明内容的 HTML 字符串
     */
    private String getInstructionHtml() {
        return "<html><body style='font-family: 微软雅黑; font-size:14px; margin:0;'>" +
                "<h2 style='text-align:center;font-size:18px;margin:5px 0;'>游戏说明</h2>" +
                "<b>🎯 游戏目标</b><br>操控英雄机，消灭不断出现的敌机，获得高分，冲击排行榜！<br><br>" +
                "<b>🎮 操作方式</b><br>【单人模式】鼠标拖拽移动飞机，自动射击<br>" +
                "【双人模式】玩家1键盘WASD控制（左侧飞机），玩家2鼠标控制（右侧飞机）<br>" +
                "暂停：游戏中按 ESC 键<br><br>" +
                "<b>📋 游戏规则</b><br>" +
                "1. 每消灭一架敌机获得对应分数<br>" +
                "2. 拾取道具可增强能力或恢复生命<br>" +
                "3. 英雄机生命值归零时游戏结束<br>" +
                "4. 达到分数阈值会触发Boss战<br>" +
                "5. 游戏过程中会定期提升难度<br><br>" +
                "<b>⚠️ 漏敌惩罚</b><br>" +
                "1. 普通（Mob）飞出 → 扣 <b>10 HP</b> + 扣 <b>5 分</b><br>" +
                "2. 精英（Elite）飞出 → 扣 <b>20 HP</b> + 扣 <b>10 分</b><br>" +
                "3. 精锐（Veteran）飞出 → 扣 <b>30 HP</b> + 扣 <b>15 分</b><br>" +
                "4. 王牌（Ace）飞出 → 扣 <b>40 HP</b> + 扣 <b>20 分</b><br>" +
                "尽量在敌机飞出屏幕前将其消灭！<br><br>" +
                "<b>🛩️ 敌机类型</b><br>" +
                "1. 普通（Mob）— 1 发击毁，不掉道具<br>" +
                "2. 精英（Elite）+ 精锐（Veteran）+ 王牌（Ace）— 均需 <b>2 发</b>击毁<br>" +
                "3. Boss — 血量极高，环形射击，掉落3个道具<br><br>" +
                "<b>教学模式</b><br>主菜单右上角点击「教学关」，适合新手熟悉操作<br><br>" +
                "<b>难度说明</b><br>简单 → 普通 → 困难 → 专家 → 地狱<br>" +
                "难度越高，敌机越多越快越耐打<br><br>" +
                "<b>道具说明</b><br>❤️ 回血 — 恢复生命值<br>🔥 火力 — 散射子弹（持续2秒）<br>" +
                "🔥🔥 超级火力 — 环形弹幕（持续5秒）<br>💣 炸弹 — 消灭大部分敌机<br>" +
                "❄️ 冰冻 — 减速敌机和子弹<br><br>祝您游戏愉快！" +
                "</body></html>";
    }
}
