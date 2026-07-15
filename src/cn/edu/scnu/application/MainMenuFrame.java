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
 * 主菜单窗口，是整个游戏的入口界面。
 * <p>
 * 支持两种进入方式：一种是常规启动进入主菜单，玩家可自由切换单/双人模式、选择难度开始游戏、
 * 查看游戏说明、查看排行榜或退出程序；另一种是游戏结束后进入，此时不显示模式选择器和教学按钮，
 * 而是弹出名字输入面板供玩家录入得分记录，随后自动跳转到对应难度的排行榜。
 * </p>
 * <p>
 * 窗口采用 {@link CardLayout} 管理多张卡片，包括主菜单卡片、游戏说明卡片、排行榜选择卡片、
 * 名字输入卡片以及各难度的排行榜展示卡片。
 * </p>
 *
 * @see CardLayout
 * @see InstructionPanel
 * @see RankingSelectPanel
 * @see NameInputPanel
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

    /**
     * 常规构造方法，用于从启动入口进入主菜单。
     * <p>
     * 构建标题栏（含模式选择器、教学按钮），初始化卡片布局，
     * 组装窗口并显示。
     * </p>
     */
    public MainMenuFrame() {
        buildTitlePanel();
        initCardLayout();
        assembleFrame();
        setVisible(true);
    }

    /**
     * 游戏结束后的构造方法，用于从游戏窗口返回主菜单并录入成绩。
     * <p>
     * 隐藏模式选择器和教学按钮，添加名字输入面板 {@link NameInputPanel}，
     * 初始化卡片布局后直接切换到名字输入卡片。
     * </p>
     *
     * @param difficulty 游戏难度，用于保存和查询排行榜记录
     * @param score      玩家本局得分
     * @param mode       游戏模式（单人/双人），用于保存记录
     */
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

    /**
     * 构建标题栏，包含模式选择标签、模式下拉框、游戏标题以及教学按钮。
     * <p>
     * 标题栏使用绝对定位（null布局），固定在窗口上方。
     * </p>
     */
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

    /**
     * 初始化卡片布局，创建内容卡片容器并添加所有初始卡片。
     * <p>
     * 初始包含三张卡片：
     * <ul>
     *   <li>"main" —— 主菜单卡片（含标题栏、难度选择、底部功能按钮）</li>
     *   <li>"instruction" —— 游戏说明卡片</li>
     *   <li>"rankingSelect" —— 排行榜难度选择卡片</li>
     * </ul>
     * </p>
     */
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

    /**
     * 构建主菜单卡片，包含标题栏、五个难度选择按钮以及底部功能按钮区域。
     * <p>
     * 难度选择区域使用 {@link GridLayout} 展示五个带背景图片的 {@link ModePanel}，
     * 分别对应简单、普通、困难、专家、地狱五个难度。
     * 底部提供"游戏说明"、"排行榜"、"退出游戏"三个功能按钮。
     * </p>
     *
     * @return 组装完成的主菜单卡片面板
     */
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

    /**
     * 组装窗口，将内容卡片放入主面板，设置窗口标题、尺寸、关闭行为、居中位置及不可调整大小。
     */
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

    /**
     * 获取当前选中的游戏模式。
     *
     * @return {@link GameMode#SINGLE} 表示单人模式，{@link GameMode#DOUBLE} 表示双人模式
     */
    private GameMode getCurrentGameMode() {
        int idx = modeComboBox.getSelectedIndex();
        return (idx == 0) ? GameMode.SINGLE : GameMode.DOUBLE;
    }

    /**
     * 开始游戏：关闭当前菜单窗口，在事件调度线程中创建并启动游戏窗口。
     *
     * @param difficulty 游戏难度
     * @param mode       游戏模式（单人/双人）
     */
    private void startGame(Difficulty difficulty, GameMode mode) {
        this.dispose();
        SwingUtilities.invokeLater(() -> createGameWindow(difficulty, mode));
    }

    /**
     * 启动教学关：关闭当前菜单窗口，在事件调度线程中创建教学游戏窗口并开始游戏循环。
     * <p>
     * 教学关使用 {@link TutorialGame}，根据当前选中的模式显示单人或双人教学。
     * </p>
     */
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

    /**
     * 创建游戏窗口并根据难度实例化对应的游戏对象。
     * <p>
     * 根据 {@code difficulty} 参数创建对应难度的游戏子类实例：
     * <ul>
     *   <li>{@link BeginningGame} —— 简单模式</li>
     *   <li>{@link BasicGame} —— 普通模式</li>
     *   <li>{@link IntermendtateGame} —— 困难模式</li>
     *   <li>{@link AdvancedGame} —— 专家模式</li>
     *   <li>{@link ExpertGame} —— 地狱模式</li>
     * </ul>
     * 窗口宽度取 {@link Main#WINDOW_WIDTH}，垂直居中靠上放置。
     * </p>
     *
     * @param difficulty 游戏难度，决定实例化的游戏子类
     * @param mode       游戏模式（单人/双人）
     */
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

    /**
     * 恢复标题栏中模式选择器和教学按钮的可见性。
     * <p>
     * 当从游戏结束状态返回主菜单时，调用此方法将之前隐藏的控件重新显示。
     * </p>
     */
    public void restoreTitleBar() {
        modeComboBox.setVisible(true);
        tutorialButton.setVisible(true);
    }

    /**
     * 跳转到指定难度的排行榜展示卡片。
     * <p>
     * 从文件加载该难度的历史记录数据，创建 {@link RankingShowPanel} 并动态添加到卡片容器中，
     * 然后切换显示。
     * </p>
     *
     * @param difficulty 要展示的排行榜对应的游戏难度
     */
    public void showRankingForDifficulty(Difficulty difficulty) {
        PlayRecordDaoImpl recordDao = new PlayRecordDaoImpl(new ArrayList<>());
        recordDao.readFromFile(difficulty);   // 加载最新文件数据
        RankingShowPanel panel = new RankingShowPanel(cardLayout, contentCards,
                difficulty, recordDao, this);  // 传入 this 以支持标题栏恢复
        contentCards.add(panel, "ranking_" + difficulty.name());
        cardLayout.show(contentCards, "ranking_" + difficulty.name());
    }

    // ==================== 内部类：名字输入面板 ====================

    /**
     * 游戏结束后的名字输入面板。
     * <p>
     * 显示"游戏结束"提示、最终得分，并提供一个文本输入框让玩家输入名字。
     * 点击"确定"按钮后将玩家记录保存到文件，然后自动跳转到对应难度的排行榜。
     * 若输入为空，则默认保存为"匿名玩家"。
     * </p>
     */
    class NameInputPanel extends JPanel {

        /**
         * 构造名字输入面板。
         *
         * @param difficulty 游戏难度，用于保存和查询排行榜
         * @param score      玩家最终得分
         * @param mode       游戏模式（单人/双人）
         * @param mainMenu   主菜单窗口引用，用于完成后的页面跳转和标题栏恢复
         */
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
