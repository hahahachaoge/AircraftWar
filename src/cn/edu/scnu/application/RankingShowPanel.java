package cn.edu.scnu.application;

import cn.edu.scnu.rank.Difficulty;
import cn.edu.scnu.rank.PlayRecord;
import cn.edu.scnu.rank.PlayRecordDaoImpl;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 排行榜显示面板（难度独立界面）。
 * <p>
 * 该面板用于展示指定难度下的玩家排行榜数据，支持从主菜单和游戏结束两种场景进入。
 * 主要功能包括：以表格形式展示排名、玩家名、分数、游戏模式和记录时间；
 * 支持删除选中的排行榜记录；支持将排行榜导出为 XLS 文件（HTML 表格格式）；
 * 提供"返回难度选择"、"返回主菜单"等导航按钮。
 * </p>
 */
public class RankingShowPanel extends JPanel {
    private final CardLayout cardLayout;
    private final JPanel parent;
    private final Difficulty difficulty;
    private final PlayRecordDaoImpl recordDao;
    private DefaultTableModel tableModel;
    private MainMenuFrame mainMenuFrame;   // 用于恢复标题栏（游戏结束场景）

    /**
     * 构造方法（不带 MainMenuFrame 引用，用于普通菜单进入）。
     *
     * @param cardLayout 父容器的卡片布局管理器
     * @param parent     父容器面板
     * @param difficulty 要显示排行榜的难度
     * @param recordDao  排行榜数据访问对象
     */
    public RankingShowPanel(CardLayout cardLayout, JPanel parent,
                            Difficulty difficulty, PlayRecordDaoImpl recordDao) {
        this(cardLayout, parent, difficulty, recordDao, null);
    }

    /**
     * 构造方法（带 MainMenuFrame 引用，用于游戏结束场景）。
     * <p>
     * 在游戏结束后显示排行榜时，需要持有 MainMenuFrame 引用来恢复标题栏控件的显示状态。
     * </p>
     *
     * @param cardLayout     父容器的卡片布局管理器
     * @param parent         父容器面板
     * @param difficulty     要显示排行榜的难度
     * @param recordDao      排行榜数据访问对象
     * @param mainMenuFrame  主菜单窗口引用，用于恢复标题栏（可为 null）
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

    /**
     * 初始化用户界面。
     * <p>
     * 创建并布局排行榜面板的所有 UI 组件，包括：
     * <ul>
     *   <li>顶部标题标签，显示当前难度名称</li>
     *   <li>中央排行榜表格，包含排名、玩家名、分数、模式、记录时间五列</li>
     *   <li>底部按钮面板，包含"返回难度选择"、"删除选中记录"、"返回主菜单"和"导出排行"四个按钮</li>
     * </ul>
     * 并为各个按钮注册事件监听器。
     * </p>
     */
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
        table.setRowHeight(35);
        table.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 18));
        table.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setGridColor(Color.GRAY);

        // 列宽调整
        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(80);
        table.getColumnModel().getColumn(3).setPreferredWidth(80);
        table.getColumnModel().getColumn(4).setPreferredWidth(180);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "排行榜数据",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("微软雅黑", Font.PLAIN, 16)));
        add(scrollPane, BorderLayout.CENTER);

        // 底部按钮面板（与主页面底部按钮样式一致）
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        btnPanel.setBackground(Color.WHITE);

        JButton backBtn = new JButton("返回难度选择");
        backBtn.setFont(new Font("微软雅黑", Font.BOLD, 18));
        backBtn.setPreferredSize(new Dimension(160, 50));
        backBtn.addActionListener(e -> {
            // 如果是游戏结束场景，恢复标题栏控件
            if (mainMenuFrame != null) {
                mainMenuFrame.restoreTitleBar();
            }
            cardLayout.show(parent, "rankingSelect");
        });

        JButton deleteBtn = new JButton("删除选中记录");
        deleteBtn.setFont(new Font("微软雅黑", Font.BOLD, 18));
        deleteBtn.setPreferredSize(new Dimension(160, 50));
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
        mainBtn.setFont(new Font("微软雅黑", Font.BOLD, 18));
        mainBtn.setPreferredSize(new Dimension(160, 50));
        mainBtn.addActionListener(e -> {
            if (mainMenuFrame != null) {
                mainMenuFrame.restoreTitleBar();
            }
            cardLayout.show(parent, "main");
        });

        JButton exportBtn = new JButton("导出排行");
        exportBtn.setFont(new Font("微软雅黑", Font.BOLD, 18));
        exportBtn.setPreferredSize(new Dimension(160, 50));
        exportBtn.addActionListener(e -> exportToExcel());

        btnPanel.add(backBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(exportBtn);
        btnPanel.add(mainBtn);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBackground(Color.WHITE);
        southPanel.add(btnPanel, BorderLayout.CENTER);
        southPanel.add(new JPanel(), BorderLayout.SOUTH);
        add(southPanel, BorderLayout.SOUTH);
    }

    /**
     * 加载排行榜数据并填充表格。
     * <p>
     * 从数据库（通过 recordDao）获取指定难度的所有游戏记录，
     * 依次添加到表格模型中。如果当前难度下没有任何记录，
     * 则在表格中显示一行"暂无记录"的提示信息。
     * </p>
     */
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

    /**
     * 导出当前难度的排行榜为 XLS 文件。
     * <p>
     * 使用 HTML 表格格式生成文件，Excel 可直接打开，无需第三方依赖库。
     * 文件编码为 GBK，文件名为"{难度中文名}模式排行榜.xls"。
     * 导出成功或失败时均会弹出提示对话框告知用户。
     * </p>
     */
    private void exportToExcel() {
        String diffCn = getDifficultyChinese(difficulty);
        String fileName = diffCn + "模式排行榜.xls";

        try {
            List<PlayRecord> records = recordDao.getAllPlayRecords(difficulty);
            java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            StringBuilder html = new StringBuilder();
            html.append("<html><head><meta charset=\"GBK\"></head><body><table border='1'>");
            html.append("<tr><th>排名</th><th>玩家名</th><th>分数</th><th>模式</th><th>记录时间</th></tr>");
            for (int i = 0; i < records.size(); i++) {
                PlayRecord r = records.get(i);
                String modeStr = r.getGameMode() == GameMode.SINGLE ? "单人" : "双人";
                html.append(String.format("<tr><td>%d</td><td>%s</td><td>%d</td><td>%s</td><td>%s</td></tr>",
                        i + 1, r.getName(), r.getScore(), modeStr, r.getDateTime().format(fmt)));
            }
            html.append("</table></body></html>");

            java.nio.file.Files.write(new java.io.File(fileName).toPath(), html.toString().getBytes("GBK"));

            JOptionPane.showMessageDialog(this,
                    "排行榜已导出到: " + new java.io.File(fileName).getAbsolutePath(),
                    "导出成功", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "导出失败: " + e.getMessage(),
                    "导出错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 将难度枚举值转换为对应的中文名称。
     *
     * @param diff 难度枚举值
     * @return 对应的中文名称字符串（简单 / 普通 / 困难 / 专家 / 地狱）
     */
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
