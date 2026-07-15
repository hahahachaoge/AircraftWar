package cn.edu.scnu.application;

import cn.edu.scnu.rank.Difficulty;
import cn.edu.scnu.rank.PlayRecordDaoImpl;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * 排行榜难度选择页，布局与主菜单卡片完全一致
 */
public class RankingSelectPanel extends JPanel {
    private final CardLayout cardLayout;
    private final JPanel parent;

    public RankingSelectPanel(CardLayout cardLayout, JPanel parent) {
        this.cardLayout = cardLayout;
        this.parent = parent;
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // ========== 顶部：标题（完全对齐主页面标题位置） ==========
        JPanel titlePanel = new JPanel(null);
        titlePanel.setPreferredSize(new Dimension(1370, 60));
        titlePanel.setBackground(Color.WHITE);

        JLabel title = new JLabel("请选择要查看的难度", SwingConstants.CENTER);
        title.setFont(new Font("微软雅黑", Font.BOLD, 45));
        title.setBounds(0, 10, 1370, 50);
        titlePanel.add(title);
        add(titlePanel, BorderLayout.NORTH);

        // ========== 中央：五个难度图片 ==========
        JPanel difficultyPanel = new JPanel(new GridLayout(1, 5, 10, 10));
        difficultyPanel.setBorder(BorderFactory.createEmptyBorder(30, 10, 30, 10));
        difficultyPanel.setBackground(Color.WHITE);

        String[] imgPaths = {"/images/bg.jpg", "/images/bg2.jpg", "/images/bg3.jpg", "/images/bg4.jpg", "/images/bg5.jpg"};
        String[] diffNames = {"简单模式", "普通模式", "困难模式", "专家模式", "地狱模式"};
        Difficulty[] difficulties = Difficulty.values();

        for (int i = 0; i < 5; i++) {
            Difficulty diff = difficulties[i];
            ModePanel panel = new ModePanel(imgPaths[i], diffNames[i], () -> {
                PlayRecordDaoImpl dao = new PlayRecordDaoImpl(new ArrayList<>());
                dao.setGameMode(GameMode.SINGLE);
                dao.readFromFile(diff);
                dao.setGameMode(GameMode.DOUBLE);
                dao.readFromFile(diff);

                RankingShowPanel showPanel = new RankingShowPanel(cardLayout, parent, diff, dao);
                parent.add(showPanel, "rankingShow");
                cardLayout.show(parent, "rankingShow");
            });
            difficultyPanel.add(panel);
        }
        add(difficultyPanel, BorderLayout.CENTER);

        // ========== 底部：返回按钮 ==========
        JPanel functionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        functionPanel.setBackground(Color.WHITE);
        JButton backBtn = new JButton("返回主菜单");
        backBtn.setFont(new Font("微软雅黑", Font.BOLD, 18));
        backBtn.setPreferredSize(new Dimension(160, 50));
        backBtn.addActionListener(e -> cardLayout.show(parent, "main"));
        functionPanel.add(backBtn);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBackground(Color.WHITE);
        southPanel.add(functionPanel, BorderLayout.CENTER);
        southPanel.add(new JPanel(), BorderLayout.SOUTH);
        add(southPanel, BorderLayout.SOUTH);
    }
}
