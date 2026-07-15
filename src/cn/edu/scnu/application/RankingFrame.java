package cn.edu.scnu.application;

import javax.swing.*;

import cn.edu.scnu.rank.Difficulty;
import cn.edu.scnu.rank.PlayRecord;

import java.awt.*;
import java.time.format.DateTimeFormatter;

import javax.swing.table.DefaultTableModel;
import java.util.List;
import cn.edu.scnu.rank.PlayRecordDao;

/**
 * 排行榜窗口类，继承自 JFrame。
 * 用于显示、管理和删除指定难度下的玩家游戏记录。
 * 提供包含排名、玩家名、分数、模式和记录时间的表格视图，
 * 支持删除记录以及返回主菜单的功能。
 *
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public class RankingFrame extends JFrame {

    private Difficulty difficulty;
    private PlayRecordDao recordDao;
    private JTable rankingTable;
    private DefaultTableModel tableModel;

    /**
     * 构造一个指定难度和持久化数据源的排行榜窗口。
     * 初始化界面、加载排行榜数据，并居中显示窗口。
     *
     * @param difficulty 要显示的难度等级
     * @param recordDao  游戏记录的数据访问对象，用于读取和删除记录
     */
    public RankingFrame(Difficulty difficulty, PlayRecordDao recordDao) {
        this.difficulty = difficulty;
        this.recordDao = recordDao;

        initUI();
        loadRankingData();

        setTitle("排行榜 - " + difficulty + "难度");
        setSize(600, 500);
        setLocationRelativeTo(null); // 窗口居中显示
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 只关闭此窗口，不退出程序
        setResizable(false);

        setVisible(true);
    }

    /**
     * 初始化窗口界面。
     * 使用 BorderLayout 布局，依次添加标题面板、表格面板和按钮面板。
     */
    private void initUI() {
        // 设置主容器的布局管理器为BorderLayout
        setLayout(new BorderLayout(10, 10));

        // 1. 创建标题面板
        JPanel titlePanel = createTitlePanel();

        // 2. 创建表格面板
        JPanel tablePanel = createTablePanel();

        // 3. 创建按钮面板
        JPanel buttonPanel = createButtonPanel();

        // 4. 将各个面板添加到主窗口
        add(titlePanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * 创建标题面板。
     * 显示居中的难度标题文字，带有蓝色加粗字体。
     *
     * @return 包含标题标签的面板
     */
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JLabel titleLabel = new JLabel(difficulty + "难度排行榜");
        titleLabel.setFont(new Font("宋体", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 100, 200));

        panel.add(titleLabel);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        return panel;
    }

    /**
     * 创建表格面板。
     * 包含一个不可编辑的 JTable，列分别为：排名、玩家名、分数、模式、记录时间。
     * 设置列宽、行高、字体和选择模式，并将表格置于带标题边框的滚动面板中。
     *
     * @return 包含排行榜表格的面板
     */
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 定义表格的列名
        String[] columnNames = { "排名", "玩家名", "分数", "模式", "记录时间" };

        // 创建表格模型
        tableModel = new DefaultTableModel(columnNames, 2) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 所有单元格不可编辑
            }
        };

        // 创建表格
        rankingTable = new JTable(tableModel);
        rankingTable.setRowHeight(20);
        rankingTable.getTableHeader().setFont(new Font("宋体", Font.BOLD, 14));
        rankingTable.setFont(new Font("宋体", Font.PLAIN, 12));
        rankingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        rankingTable.setGridColor(Color.GRAY);

        // 设置列宽
        rankingTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        rankingTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        rankingTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        rankingTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        rankingTable.getColumnModel().getColumn(4).setPreferredWidth(180);

        // 将表格放入滚动面板
        JScrollPane scrollPane = new JScrollPane(rankingTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("排行榜数据"));

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    /**
     * 创建按钮面板。
     * 包含"关闭窗口"按钮（关闭排行榜并返回主菜单）和
     * "删除记录"按钮（删除选中行对应的记录并刷新数据）。
     *
     * @return 包含操作按钮的面板
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton closeButton = new JButton("关闭窗口");
        closeButton.setFont(new Font("宋体", Font.PLAIN, 14));
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dispose();
                SwingUtilities.invokeLater(()->{
                    new MainMenuFrame();
                });
            }
        });

        JButton deleteButton = new JButton("删除记录");
        deleteButton.setFont(new Font("宋体", Font.PLAIN, 14));
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                int selectedRow = rankingTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(null, "请先选中一行");
                } else {
                    int rank = (int) tableModel.getValueAt(selectedRow, 0);
                    if(confirmDelete()){
                        recordDao.deleteByRank(difficulty, rank);
                        recordDao.writeToFile(difficulty);
                        loadRankingData();
                    }
                }
            }
        });

        panel.add(closeButton);
        panel.add(deleteButton);
        return panel;
    }

    /**
     * 弹出删除确认对话框，询问用户是否确认删除选中的记录。
     *
     * @return true 如果用户点击"是"，否则返回 false
     */
    private boolean confirmDelete() {
        int option = JOptionPane.showConfirmDialog(
                this, // 父组件
                "确定要删除选中的记录吗？", // 消息内容
                "删除确认", // 标题
                JOptionPane.YES_NO_OPTION, // 选项类型（是/否）
                JOptionPane.WARNING_MESSAGE // 消息类型（警告图标）
        );
        return option == JOptionPane.YES_OPTION;
    }


    /**
     * 加载排行榜数据并刷新表格显示。
     * 从记录数据源获取指定难度的所有记录，逐行填充到表格模型中。
     * 若数据访问对象为空则打印提示信息并返回；
     * 若没有记录则显示"暂无记录"占位行。
     */
    private void loadRankingData() {
        tableModel.setRowCount(0);

        if (recordDao == null) {
            System.out.println("数据层为null 无法获取数据");
            return;
        }

        List<PlayRecord> records = recordDao.getAllPlayRecords(difficulty);

        if (records.isEmpty()) {
            Object[] row = { "-", "暂无记录", "-", "-", "-" };
            tableModel.addRow(row);
            return;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (int i = 0; i < records.size(); i++) {
            PlayRecord record = records.get(i);
            String modeStr = (record.getGameMode() == GameMode.SINGLE) ? "单人" : "双人";
            Object[] rowData = {
                    i + 1,
                    record.getName(),
                    record.getScore(),
                    modeStr,
                    record.getDateTime().format(formatter)
            };
            tableModel.addRow(rowData);
        }
    }
}
