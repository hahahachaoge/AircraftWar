package cn.edu.scnu.show;

import cn.edu.scnu.manager.Difficulty;
import cn.edu.scnu.rank.PlayRecordDaoImpl;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * 排行榜难度选择面板。
 * <p>
 * 该面板作为主界面的一个子页面，向用户展示五种游戏难度（简单、普通、困难、专家、地狱）的排行榜入口。
 * 用户点击对应难度的面板后，程序会读取该难度下的单人和双人游戏记录，
 * 并跳转到 {@link RankingShowPanel} 以排行榜表格的形式展示记录。
 * </p>
 *
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public class RankingSelectPanel extends JPanel {

    /**
     * 构造排行榜难度选择面板。
     * <p>
     * 页面采用 BorderLayout 布局：
     * <ul>
     *   <li>北部：标题标签 "请选择要查看的难度"；</li>
     *   <li>中部：五个 {@link ModePanel}，每个代表一种难度，点击后读取对应难度的
     *       游戏记录并跳转到排行榜详情页；</li>
     *   <li>南部：返回主菜单按钮。</li>
     * </ul>
     * </p>
     *
     * @param cl     主界面的卡片布局管理器，用于面板切换
     * @param parent 卡片布局的父容器（通常为主面板），新页面将被添加到此容器中
     */
    public RankingSelectPanel(CardLayout cl, JPanel parent) {
        setLayout(new BorderLayout(10,10)); setBackground(Color.WHITE);
        JPanel tp = new JPanel(null);
        tp.setPreferredSize(new Dimension(1370,60)); tp.setBackground(Color.WHITE);
        JLabel title = new JLabel("请选择要查看的难度", SwingConstants.CENTER);
        title.setFont(new Font("微软雅黑", Font.BOLD, 45));
        title.setBounds(0,10,1370,50); tp.add(title);
        add(tp, BorderLayout.NORTH);
        JPanel dp = new JPanel(new GridLayout(1,5,10,10));
        dp.setBorder(BorderFactory.createEmptyBorder(30,10,30,10)); dp.setBackground(Color.WHITE);
        String[] imgs = {"/images/bg.jpg","/images/bg2.jpg","/images/bg3.jpg","/images/bg4.jpg","/images/bg5.jpg"};
        String[] names = {"简单模式","普通模式","困难模式","专家模式","地狱模式"};
        Difficulty[] diffs = Difficulty.values();
        for (int i = 0; i < 5; i++) {
            Difficulty d = diffs[i];
            ModePanel mp = new ModePanel(imgs[i], names[i], () -> {
                PlayRecordDaoImpl dao = new PlayRecordDaoImpl(new ArrayList<>());
                dao.setGameMode(cn.edu.scnu.application.GameMode.SINGLE);
                dao.readFromFile(cn.edu.scnu.rank.Difficulty.values()[d.ordinal()]);
                dao.setGameMode(cn.edu.scnu.application.GameMode.DOUBLE);
                dao.readFromFile(cn.edu.scnu.rank.Difficulty.values()[d.ordinal()]);
                RankingShowPanel rsp = new RankingShowPanel(cl, parent, d, dao);
                parent.add(rsp, "rankingShow");
                cl.show(parent, "rankingShow");
            });
            dp.add(mp);
        }
        add(dp, BorderLayout.CENTER);
        JPanel bp = new JPanel(new FlowLayout(FlowLayout.CENTER,30,10));
        bp.setBackground(Color.WHITE);
        JButton bb = new JButton("返回主菜单");
        bb.setFont(new Font("微软雅黑", Font.BOLD, 18));
        bb.setPreferredSize(new Dimension(160,50));
        bb.addActionListener(e -> cl.show(parent, "main"));
        bp.add(bb);
        JPanel sp = new JPanel(new BorderLayout());
        sp.setBackground(Color.WHITE); sp.add(bp, BorderLayout.CENTER);
        sp.add(new JPanel(), BorderLayout.SOUTH);
        add(sp, BorderLayout.SOUTH);
    }
}
