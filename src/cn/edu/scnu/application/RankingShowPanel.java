package cn.edu.scnu.application;

import cn.edu.scnu.rank.Difficulty;
import cn.edu.scnu.rank.PlayRecord;
import cn.edu.scnu.rank.PlayRecordDaoImpl;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 排行榜显示面板（难度独立界面）
 * 支持从主菜单和游戏结束两种场景进入。
 */
public class RankingShowPanel extends JPanel {
    private final CardLayout cardLayout;
    private final JPanel parent;
    private final Difficulty difficulty;
    private final PlayRecordDaoImpl recordDao;
    private DefaultTableModel tableModel;
    private MainMenuFrame mainMenuFrame;   // 用于恢复标题栏（游戏结束场景）

    /**
     * 构造方法（不带 MainMenuFrame 引用，用于普通菜单进入）
     */
    public RankingShowPanel(CardLayout cardLayout, JPanel parent,
                            Difficulty difficulty, PlayRecordDaoImpl recordDao) {
        this(cardLayout, parent, difficulty, recordDao, null);
    }

    /**
     * 构造方法（带 MainMenuFrame 引用，用于游戏结束场景）
     */
    public RankingShowPanel(CardLayout cardLayout, JPanel parent,
                            Difficulty difficulty, PlayRecordDaoImpl recordDao,
                            MainMenuFrame mainMenuFrame) {
        this.cardLayout = cardLayout;
        this.parent = parent;
        this.difficulty = difficulty;
        this.recordDao = recordDao;
        this.mainMenuFrame = mainMenuFrame;

        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // 顶部标题
        String difficultyChinese = getDifficultyChinese(difficulty);
        JLabel titleLabel = new JLabel(difficultyChinese + "模式难度排行榜", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 28));
        add(titleLabel, BorderLayout.NORTH);

        // 中央表格
        String[] columns = {"排名", "玩家名", "分数", "模式", "记录时间"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 14));
        table.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setGridColor(Color.GRAY);

        // 列宽调整
        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(80);
        table.getColumnModel().getColumn(3).setPreferredWidth(80);
        table.getColumnModel().getColumn(4).setPreferredWidth(180);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("排行榜数据"));
        add(scrollPane, BorderLayout.CENTER);

        // 底部按钮面板
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        btnPanel.setBackground(Color.WHITE);

        JButton backBtn = new JButton("返回难度选择");
        backBtn.setFont(new Font("微软雅黑", Font.BOLD, 14));
        backBtn.addActionListener(e -> {
            // 如果是游戏结束场景，恢复标题栏控件
            if (mainMenuFrame != null) {
                mainMenuFrame.restoreTitleBar();
            }
            cardLayout.show(parent, "rankingSelect");
        });

        JButton deleteBtn = new JButton("删除选中记录");
        deleteBtn.setFont(new Font("微软雅黑", Font.BOLD, 14));
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "请先选中一行");
                return;
            }
            int rank = (int) tableModel.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "确定删除排名第" + rank + "的记录？", "删除确认",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                recordDao.deleteByRank(difficulty, rank);
                recordDao.writeToFile(difficulty);
                loadData();   // 刷新表格
            }
        });

        JButton mainBtn = new JButton("返回主菜单");
        mainBtn.setFont(new Font("微软雅黑", Font.BOLD, 14));
        mainBtn.addActionListener(e -> {
            if (mainMenuFrame != null) {
                mainMenuFrame.restoreTitleBar();
            }
            cardLayout.show(parent, "main");
        });

        btnPanel.add(backBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(mainBtn);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void loadData() {
        tableModel.setRowCount(0);

        List<PlayRecord> records = recordDao.getAllPlayRecords(difficulty);
        if (records.isEmpty()) {
            tableModel.addRow(new Object[]{"-", "暂无记录", "-", "-", "-"});
            return;
        }

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (int i = 0; i < records.size(); i++) {
            PlayRecord r = records.get(i);
            tableModel.addRow(new Object[]{
                    i + 1,
                    r.getName(),
                    r.getScore(),
                    r.getGameMode() == GameMode.SINGLE ? "单人" : "双人",
                    r.getDateTime().format(fmt)
            });
        }
    }

    private String getDifficultyChinese(Difficulty diff) {
        switch (diff) {
            case BEGINNER:    return "简单";
            case BASIC:       return "普通";
            case INTERMEDIATE: return "困难";
            case ADVANCED:    return "专家";
            case EXPERT:      return "地狱";
            default:          return diff.toString();
        }
    }
}