package cn.edu.scnu.application;

import cn.edu.scnu.application.game.*;
import cn.edu.scnu.rank.Difficulty;
import cn.edu.scnu.rank.PlayRecord;
import cn.edu.scnu.rank.PlayRecordDaoImpl;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * 主菜单窗口（支持常规进入和游戏结束进入）
 */
public class MainMenuFrame extends JFrame {
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JPanel contentCards;

    // 标题栏组件（仅主菜单卡片使用）
    private JPanel titlePanel;
    private JComboBox<String> modeComboBox;
    private JButton tutorialButton;

    private InstructionPanel instructionPanel;
    private RankingSelectPanel rankingSelectPanel;

    // ---------- 常规构造（主菜单入口） ----------
    public MainMenuFrame() {
        buildTitlePanel();
        initCardLayout();
        assembleFrame();
        setVisible(true);
    }

    // ---------- 游戏结束构造（不显示模式选择器和教学按钮） ----------
    public MainMenuFrame(Difficulty difficulty, int score, GameMode mode) {
        buildTitlePanel();
        // 隐藏不需要的控件
        modeComboBox.setVisible(false);
        tutorialButton.setVisible(false);

        initCardLayout();

        // 添加名字输入面板
        NameInputPanel namePanel = new NameInputPanel(difficulty, score, mode, this);
        contentCards.add(namePanel, "nameInput");

        assembleFrame();
        setVisible(true);                     // ★ 必须可见
        cardLayout.show(contentCards, "nameInput");
    }

    // ---------- 标题栏构建 ----------
    private void buildTitlePanel() {
        titlePanel = new JPanel(null);
        titlePanel.setPreferredSize(new Dimension(1370, 60));
        titlePanel.setBackground(Color.WHITE);

        JLabel modeLabel = new JLabel("当前模式：");
        modeLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        modeLabel.setBounds(35, 20, 100, 30);
        titlePanel.add(modeLabel);

        modeComboBox = new JComboBox<>(new String[]{"单人模式", "双人模式"});
        modeComboBox.setFont(new Font("微软雅黑", Font.BOLD, 16));
        modeComboBox.setBounds(115, 18, 110, 36);
        titlePanel.add(modeComboBox);

        JLabel gameTitle = new JLabel("飞机大战", SwingConstants.CENTER);
        gameTitle.setFont(new Font("微软雅黑", Font.BOLD, 45));
        gameTitle.setBounds(0, 10, 1370, 50);
        titlePanel.add(gameTitle);

        tutorialButton = new JButton("教学关");
        tutorialButton.setFont(new Font("微软雅黑", Font.BOLD, 16));
        tutorialButton.setBounds(1200, 14, 120, 36);
        tutorialButton.addActionListener(e -> startTutorial());
        titlePanel.add(tutorialButton);
    }

    // ---------- 卡片布局 ----------
    private void initCardLayout() {
        cardLayout = new CardLayout();
        contentCards = new JPanel(cardLayout);
        contentCards.setBackground(Color.WHITE);

        // 主菜单卡片（含标题 + 难度选择 + 底部按钮）
        JPanel mainMenuCard = buildMainMenuCard();
        contentCards.add(mainMenuCard, "main");

        // 游戏说明卡片
        instructionPanel = new InstructionPanel(cardLayout, contentCards);
        contentCards.add(instructionPanel, "instruction");

        // 排行榜难度选择卡片
        rankingSelectPanel = new RankingSelectPanel(cardLayout, contentCards);
        contentCards.add(rankingSelectPanel, "rankingSelect");
    }

    private JPanel buildMainMenuCard() {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.add(titlePanel, BorderLayout.NORTH);

        // 中央五个难度图片按钮
        JPanel modeSelectionPanel = new JPanel(new GridLayout(1, 5, 10, 10));
        modeSelectionPanel.setBorder(BorderFactory.createEmptyBorder(30, 10, 30, 10));
        modeSelectionPanel.setBackground(Color.WHITE);

        String[] imgNames = {"/images/bg.jpg", "/images/bg2.jpg", "/images/bg3.jpg",
                "/images/bg4.jpg", "/images/bg5.jpg"};
        String[] modeNames2 = {"简单模式", "普通模式", "困难模式", "专家模式", "地狱模式"};
        Difficulty[] difficulties = {
                Difficulty.BEGINNER,
                Difficulty.BASIC,
                Difficulty.INTERMEDIATE,
                Difficulty.ADVANCED,
                Difficulty.EXPERT
        };

        for (int i = 0; i < 5; i++) {
            int finalIndex = i;
            ModePanel panel = new ModePanel(
                    imgNames[i],
                    modeNames2[i],
                    () -> startGame(difficulties[finalIndex], getCurrentGameMode())
            );
            modeSelectionPanel.add(panel);
        }
        card.add(modeSelectionPanel, BorderLayout.CENTER);

        // 底部功能按钮
        JPanel functionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        functionPanel.setBackground(Color.WHITE);

        JButton instructionButton = new JButton("游戏说明");
        instructionButton.setFont(new Font("微软雅黑", Font.BOLD, 18));
        instructionButton.setPreferredSize(new Dimension(160, 50));
        instructionButton.addActionListener(e -> cardLayout.show(contentCards, "instruction"));

        JButton rankingButton = new JButton("排行榜");
        rankingButton.setFont(new Font("微软雅黑", Font.BOLD, 18));
        rankingButton.setPreferredSize(new Dimension(160, 50));
        rankingButton.addActionListener(e -> cardLayout.show(contentCards, "rankingSelect"));

        JButton exitButton = new JButton("退出游戏");
        exitButton.setFont(new Font("微软雅黑", Font.BOLD, 18));
        exitButton.setPreferredSize(new Dimension(160, 50));
        exitButton.addActionListener(e -> System.exit(0));

        functionPanel.add(instructionButton);
        functionPanel.add(rankingButton);
        functionPanel.add(exitButton);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBackground(Color.WHITE);
        southPanel.add(functionPanel, BorderLayout.CENTER);
        // 底部信息行（暂无内容）
        southPanel.add(new JPanel(), BorderLayout.SOUTH);
        card.add(southPanel, BorderLayout.SOUTH);

        return card;
    }

    // ---------- 窗口组装 ----------
    private void assembleFrame() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.add(contentCards, BorderLayout.CENTER);

        setContentPane(mainPanel);
        setTitle("飞机大战");
        setSize(1400, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    // ---------- 工具方法 ----------
    private GameMode getCurrentGameMode() {
        int idx = modeComboBox.getSelectedIndex();
        return (idx == 0) ? GameMode.SINGLE : GameMode.DOUBLE;
    }

    private void startGame(Difficulty difficulty, GameMode mode) {
        this.dispose();
        SwingUtilities.invokeLater(() -> createGameWindow(difficulty, mode));
    }

    private void startTutorial() {
        GameMode mode = getCurrentGameMode();
        this.dispose();
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("飞机大战 - " + (mode == GameMode.SINGLE ? "单人" : "双人") + "教学");
            frame.setSize(Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            TutorialGame game = new TutorialGame(mode);
            frame.add(game);
            frame.setVisible(true);
            game.action();
        });
    }

    private void createGameWindow(Difficulty difficulty, GameMode mode) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        JFrame gameFrame = new JFrame("飞机大战 - " + difficulty);
        gameFrame.setSize(Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT);
        gameFrame.setResizable(false);
        gameFrame.setBounds(
                ((int) screenSize.getWidth() - Main.WINDOW_WIDTH) / 2, 0,
                Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT);
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        AbstractGame game = null;
        switch (difficulty) {
            case BEGINNER:      game = new BeginningGame(mode); break;
            case BASIC:         game = new BasicGame(mode); break;
            case INTERMEDIATE:  game = new IntermendtateGame(mode); break;
            case ADVANCED:      game = new AdvancedGame(mode); break;
            case EXPERT:        game = new ExpertGame(mode); break;
        }

        gameFrame.add(game);
        gameFrame.setVisible(true);
        game.action();
    }

    // ---------- 恢复标题栏（从游戏结束返回时调用） ----------
    public void restoreTitleBar() {
        modeComboBox.setVisible(true);
        tutorialButton.setVisible(true);
    }

    // ---------- 跳转到指定难度排行榜（游戏结束时调用） ----------
    public void showRankingForDifficulty(Difficulty difficulty) {
        PlayRecordDaoImpl recordDao = new PlayRecordDaoImpl(new ArrayList<>());
        recordDao.readFromFile(difficulty);   // 加载最新文件数据
        RankingShowPanel panel = new RankingShowPanel(cardLayout, contentCards,
                difficulty, recordDao, this);  // 传入 this 以支持标题栏恢复
        contentCards.add(panel, "ranking_" + difficulty.name());
        cardLayout.show(contentCards, "ranking_" + difficulty.name());
    }

    // ==================== 内部类：名字输入面板 ====================
    class NameInputPanel extends JPanel {
        public NameInputPanel(Difficulty difficulty, int score, GameMode mode, MainMenuFrame mainMenu) {
            setLayout(new GridBagLayout());
            setBackground(Color.WHITE);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(15, 0, 15, 0);
            gbc.gridwidth = GridBagConstraints.REMAINDER;

            JLabel gameOverLabel = new JLabel("游戏结束", SwingConstants.CENTER);
            gameOverLabel.setFont(new Font("微软雅黑", Font.BOLD, 36));
            add(gameOverLabel, gbc);

            JLabel scoreLabel = new JLabel("最终得分: " + score, SwingConstants.CENTER);
            scoreLabel.setFont(new Font("微软雅黑", Font.PLAIN, 24));
            add(scoreLabel, gbc);

            JLabel promptLabel = new JLabel("请输入你的名字：");
            promptLabel.setFont(new Font("微软雅黑", Font.PLAIN, 18));
            add(promptLabel, gbc);

            JTextField nameField = new JTextField(15);
            nameField.setFont(new Font("微软雅黑", Font.PLAIN, 18));
            add(nameField, gbc);

            JButton confirmBtn = new JButton("确定");
            confirmBtn.setFont(new Font("微软雅黑", Font.BOLD, 20));
            confirmBtn.addActionListener(e -> {
                String name = nameField.getText().trim();
                if (name.isEmpty()) name = "匿名玩家";

                // 保存记录
                PlayRecordDaoImpl dao = new PlayRecordDaoImpl(new ArrayList<>());
                dao.readFromFile(difficulty);
                dao.addRecord(new PlayRecord(score, name, LocalDateTime.now(), difficulty, mode));
                dao.writeToFile(difficulty);

                // 跳转到对应难度排行榜
                mainMenu.showRankingForDifficulty(difficulty);
            });
            add(confirmBtn, gbc);
        }
    }
}