package cn.edu.scnu.application;

import javax.swing.*;
import java.awt.*;

public class InstructionPanel extends JPanel {
    private final CardLayout cardLayout;
    private final JPanel parent;

    public InstructionPanel(CardLayout cardLayout, JPanel parent) {
        this.cardLayout = cardLayout;
        this.parent = parent;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 说明内容
        JTextPane textPane = new JTextPane();
        textPane.setContentType("text/html");
        textPane.setText(getInstructionHtml());
        textPane.setEditable(false);
        textPane.setBackground(Color.WHITE);
        textPane.setMargin(new Insets(15, 25, 15, 25));  // 增加内边距，让阅读更舒适
        add(textPane, BorderLayout.CENTER);

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

    private String getInstructionHtml() {
        return "<html><body style='font-family: 微软雅黑; font-size:14px; margin:0;'>" +
                "<h2 style='text-align:center;font-size:18px;margin:5px 0;'>游玩说明</h2>" +
                "<b>游戏目标</b><br>操控英雄机，消灭不断出现的敌机，获得高分，冲击排行榜！<br><br>" +
                "<b>操作方式</b><br>【单人模式】鼠标拖拽移动飞机，自动射击<br>" +
                "【双人模式】玩家1键盘WASD控制（左侧飞机），玩家2鼠标控制（右侧飞机）<br>" +
                "暂停：游戏中按 ESC 键<br><br>" +
                "<b>游戏规则</b><br>1. 每消灭一架敌机获得对应分数<br>" +
                "2. 拾取道具可增强能力或恢复生命<br>3. 英雄机生命值归零时游戏结束<br>" +
                "4. 达到分数阈值会触发Boss战<br>5. 游戏过程中会定期提升难度<br><br>" +
                "<b>教学模式</b><br>主菜单右上角点击「教学关」，适合新手熟悉操作<br><br>" +
                "<b>难度说明</b><br>简单 → 普通 → 困难 → 专家 → 地狱<br>" +
                "难度越高，敌机越多越快越耐打<br><br>" +
                "<b>道具说明</b><br>❤️ 回血 — 恢复生命值<br>🔥 火力 — 散射子弹（持续2秒）<br>" +
                "🔥🔥 超级火力 — 环形弹幕（持续5秒）<br>💣 炸弹 — 消灭大部分敌机<br>" +
                "❄️ 冰冻 — 减速敌机和子弹<br><br>祝您游戏愉快！" +
                "</body></html>";
    }
}