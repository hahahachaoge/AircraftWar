package cn.edu.scnu.show;

import cn.edu.scnu.manager.Difficulty;
import cn.edu.scnu.rank.PlayRecord;
import cn.edu.scnu.rank.PlayRecordDaoImpl;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 根据传入的难度枚举值，从数据库读取对应的排行榜数据并以表格形式展示，
 * 同时提供"返回难度选择"和"返回主菜单"两个导航按钮。
 *
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public class RankingShowPanel extends JPanel {

    /**
     * 构造一个排行榜展示面板。
     * <p>面板布局采用 BorderLayout，顶部显示难度标题，中央为排行榜表格，
     * 底部包含返回难度选择与返回主菜单两个按钮。</p>
     *
     * @param cl      卡片布局管理器，用于面板切换
     * @param parent  承载本面板的父容器（CardLayout 的宿主）
     * @param diff    当前展示的难度枚举，决定排行榜数据的筛选范围
     * @param dao     排行榜数据访问对象，提供按难度查询记录的能力
     */
    public RankingShowPanel(CardLayout cl, JPanel parent, Difficulty diff, PlayRecordDaoImpl dao) {
        setLayout(new BorderLayout(10,10)); setBackground(Color.WHITE);
        String diffCn = getChinese(diff);
        JLabel title = new JLabel(diffCn + "模式难度排行榜", SwingConstants.CENTER);
        title.setFont(new Font("微软雅黑", Font.BOLD, 28)); add(title, BorderLayout.NORTH);
        String[] cols = {"排名","玩家名","分数","模式","记录时间"};
        DefaultTableModel tm = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; }};
        JTable table = new JTable(tm);
        table.setRowHeight(35);
        table.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 18));
        table.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(80);
        table.getColumnModel().getColumn(3).setPreferredWidth(80);
        table.getColumnModel().getColumn(4).setPreferredWidth(180);
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY),
                "排行榜数据", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                new Font("微软雅黑", Font.BOLD, 18)));
        add(sp, BorderLayout.CENTER);
        cn.edu.scnu.rank.Difficulty oldDiff = cn.edu.scnu.rank.Difficulty.values()[diff.ordinal()];
        List<PlayRecord> records = dao.getAllPlayRecords(oldDiff);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (int i = 0; i < records.size(); i++) {
            PlayRecord r = records.get(i);
            tm.addRow(new Object[]{i+1, r.getName(), r.getScore(),
                    r.getGameMode() == cn.edu.scnu.application.GameMode.SINGLE ? "单人" : "双人",
                    r.getDateTime().format(fmt)});
        }
        JPanel bp = new JPanel(new FlowLayout(FlowLayout.CENTER,30,10));
        bp.setBackground(Color.WHITE);
        JButton back = new JButton("返回难度选择");
        back.setFont(new Font("微软雅黑", Font.BOLD, 18));
        back.setPreferredSize(new Dimension(160,50));
        back.addActionListener(e -> cl.show(parent, "rankingSelect"));
        JButton main = new JButton("返回主菜单");
        main.setFont(new Font("微软雅黑", Font.BOLD, 18));
        main.setPreferredSize(new Dimension(160,50));
        main.addActionListener(e -> cl.show(parent, "main"));
        bp.add(back); bp.add(main);
        JPanel sp2 = new JPanel(new BorderLayout());
        sp2.setBackground(Color.WHITE); sp2.add(bp, BorderLayout.CENTER);
        sp2.add(new JPanel(), BorderLayout.SOUTH);
        add(sp2, BorderLayout.SOUTH);
    }

    /**
     * 将内部难度枚举 {@code Difficulty} 转换为对应的中文字符串。
     *
     * @param d 难度枚举值
     * @return 对应的中文描述："简单"、"普通"、"困难"、"专家" 或 "地狱"；
     *         若无法匹配则返回枚举的 {@code toString()}
     */
    private String getChinese(Difficulty d) {
        switch(d) {
            case BEGINNER: return "简单"; case BASIC: return "普通";
            case INTERMEDIATE: return "困难"; case ADVANCED: return "专家";
            case EXPERT: return "地狱"; default: return d.toString();
        }
    }
}
