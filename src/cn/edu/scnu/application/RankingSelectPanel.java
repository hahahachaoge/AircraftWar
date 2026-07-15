package cn.edu.scnu.application;

import cn.edu.scnu.rank.Difficulty;
import cn.edu.scnu.rank.PlayRecordDaoImpl;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class RankingSelectPanel extends JPanel {
    private final CardLayout cardLayout;
    private final JPanel parent;

    public RankingSelectPanel(CardLayout cardLayout, JPanel parent) {
        this.cardLayout = cardLayout;
        this.parent = parent;
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // 标题
        JLabel title = new JLabel("请选择要查看的难度", SwingConstants.CENTER);
        title.setFont(new Font("微软雅黑", Font.BOLD, 40));
        add(title, BorderLayout.NORTH);

        // 五个难度卡片
        JPanel difficultyPanel = new JPanel(new GridLayout(1, 5, 20, 0));
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

        // 返回按钮（风格与主菜单按钮一致）
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(Color.WHITE);
        JButton backBtn = new JButton("返回主菜单");
        backBtn.setFont(new Font("微软雅黑", Font.BOLD, 18));
        backBtn.setPreferredSize(new Dimension(160, 50));
        backBtn.addActionListener(e -> cardLayout.show(parent, "main"));
        bottomPanel.add(backBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }
}