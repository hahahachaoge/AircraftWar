package cn.edu.scnu.application.game;

import cn.edu.scnu.aircraft.AbstractAircraft;
import cn.edu.scnu.aircraft.EnemyType;
import cn.edu.scnu.aircraft.HeroAircraft;
import cn.edu.scnu.application.GameMode;
import cn.edu.scnu.application.ImageManager;
import cn.edu.scnu.application.Main;
import cn.edu.scnu.application.MainMenuFrame;
import cn.edu.scnu.prop.AbstractProp;
import cn.edu.scnu.prop.PropFactory;
import cn.edu.scnu.prop.PropType;
import cn.edu.scnu.rank.Difficulty;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * 教学关游戏类，继承自 {@link AbstractGame}，提供新手引导和双人教学模式。
 * <p>
 * 本类通过分步提示（tips）引导玩家逐步了解游戏操作：
 * <ul>
 *   <li>单机模式：鼠标移动飞机、自动射击、消灭敌机、拾取道具</li>
 *   <li>双人模式：玩家1（键盘WASD）控制左机、玩家2（鼠标）控制右机，共同消灭敌机</li>
 * </ul>
 * 当击杀敌机数量达到 {@link #requiredKills}（5架）时，教学完成并弹窗提示，
 * 随后返回主菜单。教学关不生成Boss、不升级难度、英雄拥有极高血量（9999），
 * 确保新手能轻松完成。
 * </p>
 *
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public class TutorialGame extends AbstractGame {

    /** 教学提示文本数组，每一条对应一个教学步骤 */
    private String[] tips;
    /** 当前教学步骤索引 */
    private int tipIndex = 0;
    /** 教学是否已完成 */
    private boolean tutorialComplete = false;
    /** 玩家1（单机/键盘）是否已移动 */
    private boolean hasMoved = false;
    /** 玩家2（鼠标）是否已操作 */
    private boolean player2Action = false;
    /** 是否已拾取道具 */
    private boolean hasPicked = false;
    /** 已击杀敌机数量 */
    private int enemyKillCount = 0;
    /** 完成教学所需击杀数 */
    private final int requiredKills = 5;
    /** 首架敌机是否已被击杀（用于强制掉落第一个道具） */
    private boolean firstEnemyKilled = false;
    /** 当前步骤开始的时间戳（毫秒），用于控制步骤切换的最小停留时间 */
    private long stepStartTime = 0;
    /** 每个教学步骤的最短持续时间（毫秒） */
    private final long MIN_STEP_DURATION = 2000;
    /** 玩家2的初始X坐标（用于检测鼠标是否移动） */
    private int player2InitX;
    /** 玩家2的初始Y坐标（用于检测鼠标是否移动） */
    private int player2InitY;

    /**
     * 构造教学关游戏实例。
     * <p>
     * 初始化教学提示文本，设置难度为初学者（BEGINNER），
     * 若是双人模式则记录玩家2的初始坐标以便后续检测移动。
     * </p>
     *
     * @param mode 游戏模式（{@link GameMode#SINGLE} 或 {@link GameMode#DOUBLE}）
     */
    public TutorialGame(GameMode mode) {
        super(mode);
        this.difficulty = Difficulty.BEGINNER;
        initTutorialTips();
        if (gameMode == GameMode.DOUBLE && heroes.size() > 1) {
            player2InitX = heroes.get(1).getLocationX();
            player2InitY = heroes.get(1).getLocationY();
        }
        stepStartTime = System.currentTimeMillis();
    }

    /**
     * 初始化教学提示文本。
     * <p>
     * 根据游戏模式（单机/双人）设置不同的提示数组：
     * <ul>
     *   <li>单机模式：引导鼠标移动、自动射击、拾取道具、完成击杀</li>
     *   <li>双人模式：引导玩家1键盘WASD控制左机、玩家2鼠标控制右机、自动射击、拾取道具、共同完成击杀</li>
     * </ul>
     * </p>
     */
    private void initTutorialTips() {
        if (gameMode == GameMode.SINGLE) {
            tips = new String[]{
                    "欢迎来到教学关！\n鼠标拖动移动你的飞机",
                    "飞机将自动射击，消灭敌机",
                    "拾取道具",
                    "消灭 " + requiredKills + " 架敌机完成教学！"
            };
        } else {
            tips = new String[]{
                    "欢迎来到双人教学！\n玩家1（键盘）WASD控制左机\n玩家2（鼠标）控制右机",
                    "飞机将自动射击，消灭敌机",
                    "拾取道具",
                    "共同消灭 " + requiredKills + " 架敌机完成教学！"
            };
        }
    }

    /**
     * 初始化教学关的游戏设置。
     * <p>
     * 加载背景图片（若失败则使用深色纯色背景），并设置友好的游戏参数：
     * 英雄血量 9999、敌机上限和生成周期、射击周期、敌机属性系数、道具概率等，
     * 同时禁用难度升级和分数阈值限制。
     * </p>
     */
    @Override
    protected void initGameSettings() {
        try {
            ImageManager.BACKGROUND_IMAGE = ImageIO.read(new FileInputStream("src/images/bg.jpg"));
        } catch (IOException e) {
            BufferedImage defaultBg = new BufferedImage(Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = defaultBg.createGraphics();
            g2d.setColor(new Color(30, 30, 60));
            g2d.fillRect(0, 0, Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT);
            g2d.dispose();
            ImageManager.BACKGROUND_IMAGE = defaultBg;
        }

        this.initHeroHp = 9999;
        this.enemyMaxNumber = (gameMode == GameMode.DOUBLE) ? 4 : 2;
        this.enemySpawnCycle = 100;
        this.heroShootCycle = 10;
        this.enemyShootCycle = 100;
        this.enemyHpFactor = 0.5;
        this.enemySpeedFactor = 0.5;
        this.propRand = 0.6;
        this.difficultyLevelUpInterval = 0;
        this.scoreThreshold = Integer.MAX_VALUE;
    }

    /**
     * 获取随机敌机类型。
     * <p>
     * 教学关仅生成普通小兵（{@link EnemyType#MOB}），不生成精英或Boss。
     * </p>
     *
     * @return 始终返回 {@link EnemyType#MOB}
     */
    @Override
    protected EnemyType getRandomEnemyType() {
        return EnemyType.MOB;
    }

    /**
     * 判断是否应生成Boss敌机。
     * <p>
     * 教学关不生成Boss，始终返回 false。
     * </p>
     *
     * @return 始终返回 false
     */
    @Override
    protected boolean shouldSpawnBoss() {
        return false;
    }

    /**
     * 触发道具掉落逻辑。
     * <p>
     * 若首架敌机被击杀，强制在敌机位置生成一个HP回复道具（仅一次），
     * 用于引导玩家拾取道具。之后按正常概率掉落。
     * </p>
     *
     * @param enemyAircraft 被击杀的敌机实例
     */
    @Override
    protected void triggerProp(AbstractAircraft enemyAircraft) {
        if (!firstEnemyKilled) {
            firstEnemyKilled = true;
            int propX = Math.max(0, Math.min(Main.WINDOW_WIDTH, enemyAircraft.getLocationX()));
            int propY = Math.max(0, Math.min(Main.WINDOW_HEIGHT, enemyAircraft.getLocationY()));
            AbstractProp forcedProp = PropFactory.createProp(PropType.HP, propX, propY);
            if (forcedProp != null) {
                props.add(forcedProp);
            }
            return;
        }
        AbstractProp newProp = enemyAircraft.obtainProp(enemyAircraft, propRand);
        if (newProp != null) {
            props.add(newProp);
        }
    }

    /**
     * 难度升级（空实现）。
     * <p>
     * 教学关不进行难度升级，因此该方法不执行任何操作。
     * </p>
     */
    @Override
    protected void difficultyLevelUp() {
        // 教学关不升级
    }

    /**
     * 绘制教学关的游戏画面。
     * <p>
     * 调用父类绘制方法后，额外在画面上绘制：
     * <ul>
     *   <li>当前教学步骤的提示文本（步骤完成前显示）</li>
     *   <li>教学完成提示（绿色文字）</li>
     *   <li>击杀进度条（青色文字，格式：敌机击杀: N / 5）</li>
     * </ul>
     * 双人模式下，包含"键盘"关键字的提示行以黄色渲染以区分玩家。
     * </p>
     *
     * @param g 图形上下文对象
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 20));
        if (!tutorialComplete && tipIndex < tips.length) {
            String[] lines = tips[tipIndex].split("\n");
            int y = 100;
            for (String line : lines) {
                if (gameMode == GameMode.DOUBLE && line.contains("键盘")) {
                    g.setColor(Color.YELLOW);
                } else {
                    g.setColor(Color.WHITE);
                }
                g.drawString(line, 50, y);
                y += 30;
            }
        } else if (tutorialComplete) {
            g.setColor(Color.GREEN);
            g.drawString("教学完成！", 150, 350);
            g.drawString("即将返回主菜单...", 130, 390);
        }

        g.setColor(Color.CYAN);
        g.setFont(new Font("SansSerif", Font.BOLD, 18));
        g.drawString("敌机击杀: " + enemyKillCount + " / " + requiredKills, 50, 500);
    }

    /**
     * 碰撞检测后的教学步骤推进逻辑。
     * <p>
     * 在调用父类碰撞检测后，根据当前教学步骤索引和条件判断是否进入下一步：
     * <ul>
     *   <li>步骤0 → 步骤1：检测玩家移动（单机移动或双人中任一玩家操作）</li>
     *   <li>步骤1 → 步骤2：检测是否已拾取道具或已有击杀</li>
     *   <li>步骤2 → 步骤3：检测击杀数是否达到要求</li>
     * </ul>
     * 每个步骤切换至少需要 {@link #MIN_STEP_DURATION}（2秒）的间隔。
     * </p>
     */
    @Override
    protected void crashCheckAction() {
        super.crashCheckAction();

        long now = System.currentTimeMillis();
        boolean enoughTime = (now - stepStartTime) >= MIN_STEP_DURATION;

        // 步骤0 → 步骤1：键盘玩家（左侧，index 0）移动 或 鼠标玩家（右侧，index 1）操作
        if (tipIndex == 0 && enoughTime) {
            boolean p1Moved = false;
            if (heroes.size() > 0) {
                HeroAircraft keyboardHero = heroes.get(0);
                // 单人模式下初始 X = WINDOW_WIDTH/2，双人模式下键盘初始 X = WINDOW_WIDTH/3
                int initX = (gameMode == GameMode.DOUBLE) ? Main.WINDOW_WIDTH / 3 : Main.WINDOW_WIDTH / 2;
                if (!hasMoved && keyboardHero.getLocationX() != initX) {
                    hasMoved = true;
                    p1Moved = true;
                }
            }

            boolean p2Acted = false;
            if (gameMode == GameMode.DOUBLE && heroes.size() > 1) {
                HeroAircraft mouseHero = heroes.get(1);
                if (!player2Action && (mouseHero.getLocationX() != player2InitX || mouseHero.getLocationY() != player2InitY)) {
                    player2Action = true;
                }
                p2Acted = player2Action;
            }

            if (p1Moved || p2Acted) {
                tipIndex++;
                stepStartTime = now;
            }
        }

        // 步骤1 → 步骤2：击杀>0 或 拾取道具
        if (tipIndex == 1 && enoughTime) {
            if (!hasPicked && !props.isEmpty()) {
                hasPicked = true;
            }
            if (hasPicked || enemyKillCount > 0) {
                tipIndex++;
                stepStartTime = now;
            }
        }

        // 步骤2 → 步骤3：击杀达到要求
        if (tipIndex == 2 && enoughTime && enemyKillCount >= requiredKills) {
            tipIndex++;
            stepStartTime = now;
        }

        if (enemyKillCount >= requiredKills && tipIndex < tips.length - 1 && enoughTime) {
            tipIndex = tips.length - 1;
            stepStartTime = now;
        }
    }

    /**
     * 增加得分并更新击杀计数。
     * <p>
     * 调用父类增加分数方法，同时递增 {@link #enemyKillCount}。
     * 当累计击杀数达到 {@link #requiredKills} 时，标记教学完成（{@link #tutorialComplete} = true）。
     * </p>
     *
     * @param points 本次击杀获得的分数
     */
    @Override
    public void addScore(int points) {
        super.addScore(points);
        enemyKillCount++;
        if (enemyKillCount >= requiredKills) {
            tutorialComplete = true;
        }
    }

    /**
     * 检查游戏结束条件并执行相应操作。
     * <p>
     * 当教学完成（{@link #tutorialComplete} 为 true）时：
     * 取消游戏定时器、停止所有背景音乐、弹出完成提示对话框、
     * 关闭游戏窗口并返回主菜单界面（{@link MainMenuFrame}）。
     * </p>
     */
    @Override
    protected void checkResultAction() {
        if (tutorialComplete) {
            timer.cancel();
            musicManager.stopAllMusic();
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this, "恭喜完成教学！");
                closeGameWindow();
                new MainMenuFrame();
            });
        }
    }
}
