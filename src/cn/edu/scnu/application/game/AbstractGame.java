package cn.edu.scnu.application.game;

import cn.edu.scnu.aircraft.*;
import cn.edu.scnu.aircraft.factory.AceEnemyFactory;
import cn.edu.scnu.aircraft.factory.BossEnemyFactory;
import cn.edu.scnu.aircraft.factory.EliteEnemyFactory;
import cn.edu.scnu.aircraft.factory.EnemyFactory;
import cn.edu.scnu.aircraft.factory.MobEnemyFactory;
import cn.edu.scnu.aircraft.VeteranEnemyFactory;
import cn.edu.scnu.application.GameMode;
import cn.edu.scnu.application.HeroController;
import cn.edu.scnu.application.ImageManager;
import cn.edu.scnu.application.KeyboardController;
import cn.edu.scnu.application.Main;
import cn.edu.scnu.application.MainMenuFrame;
import cn.edu.scnu.application.RankingBoard;
import cn.edu.scnu.bullet.BaseBullet;
import cn.edu.scnu.music.MusicManager;
import cn.edu.scnu.music.MusicManager.MusicType;
import cn.edu.scnu.prop.AbstractProp;
import cn.edu.scnu.prop.PropEffectTimer;
import cn.edu.scnu.rank.Difficulty;
import cn.edu.scnu.rank.PlayRecordDaoImpl;
import cn.edu.scnu.basic.AbstractFlyingObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.Timer;

/**
 * 游戏抽象基类，定义所有游戏模式（单人、双人、教程）的通用逻辑骨架。
 * <p>
 * 本类继承自 {@link JPanel}，作为游戏的主面板，负责：
 * <ul>
 *   <li>游戏主循环调度（定时任务驱动）</li>
 *   <li>英雄机、敌机、子弹、道具等游戏对象的创建与管理</li>
 *   <li>碰撞检测与游戏状态更新</li>
 *   <li>画面绘制（背景滚动、爆炸动画、暂停菜单等）</li>
 *   <li>音效播放与暂停/恢复控制</li>
 * </ul>
 * 子类需实现抽象方法以提供具体的游戏设置（难度参数、敌机生成策略等），
 * 同时可通过重写钩子方法（如 {@link #setBossEnemyHp()}、{@link #difficultyLevelUp()}）定制特定行为。
 *
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
@SuppressWarnings("all")
public abstract class AbstractGame extends JPanel {
    /**
     * 背景图片纵向滚动的起始 Y 坐标
     */
    private int backGroundTop = 0;

    /**
     * 定时调度器，用于驱动游戏主循环
     */
    protected Timer timer;
    /**
     * 时间间隔（毫秒），控制游戏刷新频率
     */
    private final int timeInterval = 50;
    /**
     * 游戏运行总帧数，每帧递增
     */
    protected long gameTime = 0;
    /**
     * 难度提升的时间间隔（帧数），0 表示不自动升级
     */
    protected int difficultyLevelUpInterval = 0;

    /**
     * 当前存活的所有英雄机列表
     */
    protected List<HeroAircraft> heroes;
    /**
     * 英雄机的初始生命值
     */
    protected int initHeroHp = 200;

    /**
     * Boss 敌机的生命值
     */
    protected int bossEnemyHp = 100;

    /**
     * 当前存活的敌机列表（多态存储）
     */
    protected final List<AbstractAircraft> enemyAircrafts;
    /**
     * 英雄机发射的子弹列表
     */
    protected final List<BaseBullet> heroBullets;
    /**
     * 敌机发射的子弹列表
     */
    protected final List<BaseBullet> enemyBullets;
    /**
     * 掉落道具列表
     */
    protected final List<AbstractProp> props;

    /**
     * 敌机类型到工厂对象的映射
     */
    protected final Map<EnemyType, EnemyFactory> enemyFactories = new EnumMap<>(EnemyType.class);

    /**
     * 屏幕中同时出现的敌机最大数量
     */
    protected int enemyMaxNumber = 5;

    /**
     * 敌机生成周期（帧数）
     */
    protected double enemySpawnCycle = 10;
    /**
     * 敌机生成计数器
     */
    private int enemySpawnCounter = 0;

    /**
     * 道具掉落概率
     */
    protected double propRand;

    /**
     * 英雄机射击周期（帧数）
     */
    protected int heroShootCycle = 10;
    /**
     * 敌机射击周期（帧数）
     */
    protected int enemyShootCycle = 10;
    /**
     * 敌机射击计数器
     */
    private int enemyShootCounter = 0;
    /**
     * 英雄机射击计数器
     */
    private int heroShootCounter = 0;

    /**
     * 敌机生命值倍率
     */
    protected double enemyHpFactor = 1.0;
    /**
     * 敌机速度倍率
     */
    protected double enemySpeedFactor = 1.0;

    /**
     * 当前玩家名称
     */
    protected String playName;
    /**
     * 当前得分
     */
    protected int score = 0;
    /**
     * 当前难度
     */
    protected Difficulty difficulty;

    /**
     * 道具效果定时器（如火力的持续时间）
     */
    private PropEffectTimer fireTimer;

    /**
     * 游戏是否已结束
     */
    private boolean gameOverFlag = false;

    /**
     * Boss 敌机是否已生成
     */
    protected boolean bossSpawned = false;
    /**
     * Boss 敌机生成的分数阈值
     */
    protected int scoreThreshold = 500;
    /**
     * Boss 敌机实例
     */
    protected AbstractAircraft bossEnemy = null;

    /**
     * 游戏记录持久化对象
     */
    PlayRecordDaoImpl playRecordDao = new PlayRecordDaoImpl(new ArrayList<>());
    /**
     * 排行榜面板
     */
    RankingBoard rankingBoard = new RankingBoard(playRecordDao);

    /**
     * 音乐管理器实例
     */
    protected MusicManager musicManager = MusicManager.getInstance();

    /**
     * 当前游戏模式
     */
    protected GameMode gameMode;
    /**
     * 键盘控制器列表（用于双人模式中玩家1的键盘操控）
     */
    protected List<KeyboardController> keyboardControllers = new ArrayList<>();

    /**
     * 暂停状态标志
     */
    private boolean paused = false;
    /**
     * 暂停菜单中"继续"按钮的矩形区域
     */
    private Rectangle continueBtnRect;
    /**
     * 暂停菜单中"退出"按钮的矩形区域
     */
    private Rectangle exitBtnRect;
    /**
     * 暂停菜单按钮的宽度和高度
     */
    private static final int BTN_WIDTH = 120, BTN_HEIGHT = 40;

    /**
     * 爆炸动画列表，每个元素为 {x, y, frame(0~7), speed(1=正常, 2=慢放)}
     */
    private final List<int[]> explosions = new ArrayList<>();

    /**
     * 屏幕震动剩余帧数
     */
    private int screenShakeTime = 0;
    /**
     * 屏幕震动强度
     */
    private int screenShakeIntensity = 0;
    /**
     * 震动计数器
     */
    private int shakeCounter = 0;

    /** Boss 登场警告是否激活 */
    protected boolean bossWarningActive = false;
    /** Boss 警告倒计时（帧数） */
    protected int bossWarningTimer = 0;
    /** Boss 警告持续帧数（60帧 × 50ms = 3秒） */
    protected static final int BOSS_WARNING_DELAY = 60;

    /**
     * 添加一个爆炸动画效果。
     *
     * @param x 爆炸中心的 X 坐标
     * @param y 爆炸中心的 Y 坐标
     */
    public void addExplosion(int x, int y) {
        explosions.add(new int[]{x, y, 0, 1});
    }

    /**
     * Boss 死亡时触发的大爆炸效果：包含多个炸点、慢放动画以及屏幕震动。
     *
     * @param x Boss 中心的 X 坐标
     * @param y Boss 中心的 Y 坐标
     */
    public void addBossExplosion(int x, int y) {
        // 一圈炸点（形成范围爆炸）
        for (int dx = -50; dx <= 50; dx += 25) {
            for (int dy = -50; dy <= 50; dy += 25) {
                explosions.add(new int[]{x + dx + (int)(Math.random() * 15 - 7),
                        y + dy + (int)(Math.random() * 15 - 7), 0, 2});
            }
        }
        // 中心额外炸点
        for (int i = 0; i < 5; i++) {
            explosions.add(new int[]{x + (int)(Math.random() * 40 - 20),
                    y + (int)(Math.random() * 40 - 20), 0, 2});
        }
        // 触发屏幕震动
        startScreenShake(8, 30);
    }

    /**
     * 开始屏幕震动效果。
     *
     * @param intensity 震动强度（像素偏移幅度）
     * @param duration  震动持续帧数
     */
    public void startScreenShake(int intensity, int duration) {
        screenShakeIntensity = intensity;
        screenShakeTime = duration;
    }

    /**
     * 判断游戏当前是否处于暂停状态。
     *
     * @return 暂停返回 true，否则返回 false
     */
    public boolean isPaused() {
        return paused;
    }

    /**
     * 增加游戏分数。
     *
     * @param points 要增加的分值
     */
    public void addScore(int points) {
        this.score += points;
    }

    /**
     * 获取当前游戏得分。
     *
     * @return 当前得分
     */
    public int getScore() {
        return score;
    }

    /**
     * 设置道具效果定时器。如果已有定时器则先取消。
     *
     * @param fireTimer 新的道具效果定时器
     */
    public synchronized void setFireTimer(PropEffectTimer fireTimer) {
        if (this.fireTimer != null) {
            this.fireTimer.cancel();
        }
        this.fireTimer = fireTimer;
    }

    /**
     * 构造一个游戏实例。
     * <p>
     * 根据指定的游戏模式初始化英雄机（单人/教程模式生成一台，双人模式生成两台）、
     * 敌机工厂映射、游戏对象列表，并启动定时调度器。
     * 同时注册键盘监听（ESC 暂停切换）和鼠标监听（暂停菜单按钮点击）。
     *
     * @param mode 游戏模式（{@link GameMode#SINGLE}、{@link GameMode#DOUBLE} 或 {@link GameMode#TUTORIAL}）
     */
    public AbstractGame(GameMode mode) {
        this.gameMode = mode;
        rankingBoard.setGameMode(mode);
        musicManager.playBgmMusic(MusicType.BGM, true);

        // 调用抽象方法进行难度相关初始化
        initGameSettings();

        // 1. 初始化英雄机（根据模式）
        heroes = new ArrayList<>();
        if (mode == GameMode.SINGLE || mode == GameMode.TUTORIAL) {
            HeroAircraft hero = new HeroAircraft(
                    Main.WINDOW_WIDTH / 2,
                    Main.WINDOW_HEIGHT - ImageManager.HERO_IMAGE.getHeight(),
                    0, 0, initHeroHp);
            heroes.add(hero);
            new HeroController(this, hero);
        } else if (mode == GameMode.DOUBLE) {
            // 左边英雄机（键盘控制 - 玩家1）
            HeroAircraft leftHero = new HeroAircraft(
                    Main.WINDOW_WIDTH / 3,
                    Main.WINDOW_HEIGHT - ImageManager.HERO_IMAGE.getHeight(),
                    0, 0, initHeroHp);
            // 右边英雄机（鼠标控制 - 玩家2）
            HeroAircraft rightHero = new HeroAircraft(
                    Main.WINDOW_WIDTH * 2 / 3,
                    Main.WINDOW_HEIGHT - ImageManager.HERO_IMAGE.getHeight(),
                    0, 0, initHeroHp);
            heroes.add(leftHero);
            heroes.add(rightHero);
            new HeroController(this, rightHero);
            KeyboardController kb = new KeyboardController(this, leftHero,
                    KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D);
            keyboardControllers.add(kb);
        }

        // 2. 初始化列表（通用）
        enemyAircrafts = new LinkedList<>();
        heroBullets = new LinkedList<>();
        enemyBullets = new LinkedList<>();
        props = new LinkedList<>();

        // 3. 初始化工厂映射（通用）
        initEnemyFactories();

        // 4. 初始化定时器（通用）
        this.timer = new Timer("game-action-timer", true);
        this.setFocusable(true);
        this.requestFocusInWindow();

        // 添加键盘监听（ESC 切换暂停）
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE && !gameOverFlag) {
                    togglePause();
                }
            }
        });

        // 鼠标监听：暂停菜单按钮点击
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!paused || gameOverFlag) return;
                Point p = e.getPoint();
                if (continueBtnRect != null && continueBtnRect.contains(p)) {
                    togglePause();
                } else if (exitBtnRect != null && exitBtnRect.contains(p)) {
                    exitGame();
                }
            }
        });

    }

    /**
     * 初始化敌机工厂映射表。
     * <p>
     * 如果映射表为空则新建所有工厂实例；否则只更新已有工厂的倍率参数。
     */
    private void initEnemyFactories() {
        if (enemyFactories.size() == 0) {
            enemyFactories.put(EnemyType.MOB, new MobEnemyFactory(enemyHpFactor, enemySpeedFactor));
            enemyFactories.put(EnemyType.ELITE, new EliteEnemyFactory(enemyHpFactor, enemySpeedFactor));
            enemyFactories.put(EnemyType.VETERAN, new VeteranEnemyFactory(enemyHpFactor, enemySpeedFactor));
            enemyFactories.put(EnemyType.ACE, new AceEnemyFactory(enemyHpFactor, enemySpeedFactor));
            enemyFactories.put(EnemyType.BOSS, new BossEnemyFactory(enemyHpFactor,
                    enemySpeedFactor));
        } else {
            enemyFactories.get(EnemyType.MOB).setParams(enemyHpFactor, enemySpeedFactor);
            enemyFactories.get(EnemyType.ELITE).setParams(enemyHpFactor, enemySpeedFactor);
            enemyFactories.get(EnemyType.VETERAN).setParams(enemyHpFactor, enemySpeedFactor);
            enemyFactories.get(EnemyType.ACE).setParams(enemyHpFactor, enemySpeedFactor);
            enemyFactories.get(EnemyType.BOSS).setParams(enemyHpFactor, enemySpeedFactor);
        }
    }

    /**
     * 初始化游戏设置（难度相关参数）。
     * <p>
     * 由各子类根据自身难度等级实现具体逻辑。
     */
    protected abstract void initGameSettings();

    /**
     * 游戏主循环入口，启动定时任务驱动整个游戏逻辑。
     * <p>
     * 每个刷新周期依次执行：难度升级检查 → 控制台信息输出 → 敌机生成 →
     * Boss 生成判断 → 射击 → 键盘控制更新 → 子弹/飞机/道具移动 →
     * 敌机逃离惩罚 → 碰撞检测 → 爆炸动画 → 后处理 → 界面重绘 → 游戏结束检查。
     */
    public final void action() {

        // 定时任务：绘制、对象产生、碰撞判定、及结束判定
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                // 如果暂停，只重绘（显示暂停界面），不更新逻辑
                if (paused) {
                    repaint();
                    return;
                }

                gameTime++;

                if (shouldLevelUp()) {
                    difficultyLevelUp();
                    initEnemyFactories();
                }

                printInfo();

                createRandomEnemy();

                // Boss 警告 & 生成控制
                if (shouldSpawnBoss()) {
                    if (!bossWarningActive) {
                        bossWarningActive = true;
                        bossWarningTimer = 0;
                    }
                }
                if (bossWarningActive) {
                    bossWarningTimer++;
                    if (bossWarningTimer >= BOSS_WARNING_DELAY) {
                        spawnBossEnemy();
                        bossWarningActive = false;
                    }
                }

                // 飞机发射子弹
                shootAction();
                // 键盘控制更新
                updateKeyboardHeroes();
                // 子弹移动
                bulletsMoveAction();
                // 飞机移动
                aircraftsMoveAction();
                // 敌机逃离惩罚
                checkEscapedEnemies();
                // 道具移动
                propMoveAction();
                // 撞击检测
                crashCheckAction();
                // 爆炸动画帧推进
                advanceExplosions();
                // 后处理
                postProcessAction();
                // 重绘界面
                repaint();
                // 游戏结束检查
                checkResultAction();
            }
        };
        // 以固定延迟时间进行执行：本次任务执行完成后，延迟 timeInterval 再执行下一次
        timer.schedule(task, 0, timeInterval);
    }

    /**
     * 在控制台输出游戏状态信息，每 25 帧输出一次。
     * <p>
     * 包括 Boss 状态（血量/有效性）或距 Boss 登场所需分数，
     * 以及距下次难度提升的剩余时间（仅在升级间隔非零且小于 5 秒时输出）。
     */
    private void printInfo() {
        if (gameTime % 25 == 0) {
            if (bossEnemy != null && !bossEnemy.notValid()) {
                System.out.println("BOSS状态 - 血量: " + bossEnemy.getHp() +
                        ", 有效: " + !bossEnemy.notValid());
            } else {
                System.out.println(String.format("距离BOSS敌机登场 还差 %d 分 !!!", scoreThreshold - score));
            }

            if (difficultyLevelUpInterval != 0) {
                double levelUpTime = (double) (difficultyLevelUpInterval - gameTime % difficultyLevelUpInterval)
                        * timeInterval / 1000;
                if (levelUpTime < 5) {
                    System.out.println(String.format("距离游戏难度提升 还有 %.2f 秒", levelUpTime));
                }
            }
        }
    }

    /**
     * 判断当前帧是否应触发难度升级。
     *
     * @return 如果 {@code difficultyLevelUpInterval} 非零且时间到则返回 true
     */
    private boolean shouldLevelUp() {
        return difficultyLevelUpInterval != 0 && gameTime % difficultyLevelUpInterval == 0;
    }

    /**
     * 难度提升时的回调方法。
     * <p>
     * 默认为空实现，子类可重写以添加具体的难度提升逻辑。
     */
    protected void difficultyLevelUp() {
    }

    /**
     * 根据当前游戏状态随机获取一种敌机类型。
     * <p>
     * 各子类实现不同的随机策略以控制敌机出现概率分布。
     *
     * @return 随机选取的敌机类型
     */
    protected abstract EnemyType getRandomEnemyType();

    /**
     * 根据敌机类型计算随机的初始 X 坐标，确保敌机不会超出屏幕边界。
     *
     * @param enemyType 敌机类型，用于获取对应图片宽度
     * @return 随机 X 坐标（范围：[0, 窗口宽度 - 图片宽度)）
     */
    private int getRandomWidth(EnemyType enemyType) {
        int imageWidth = 0;
        switch (enemyType) {
            case MOB:
                imageWidth = ImageManager.MOB_ENEMY_IMAGE.getWidth();
                break;
            case ELITE:
                imageWidth = ImageManager.ELITE_ENEMY_IMAGE.getWidth();
                break;
            case VETERAN:
                imageWidth = ImageManager.VETERAN_ENEMY_IMAGE.getWidth();
                break;
            case ACE:
                imageWidth = ImageManager.ACE_ENEMY_IMAGE.getWidth();
                break;
            case BOSS:
                imageWidth = ImageManager.BOSS_ENEMY_IMAGE.getWidth();
                break;
            default:
                break;
        }
        return (int) (Math.random() * (Main.WINDOW_WIDTH - imageWidth));
    }

    /**
     * 获取敌机生成的随机初始 Y 坐标。
     * <p>
     * 范围限制在屏幕顶部 5% 高度内，使敌机从上方进入。
     *
     * @return 随机 Y 坐标
     */
    private int getRandomHeight() {
        return (int) (Math.random() * Main.WINDOW_HEIGHT * 0.05);
    }

    /**
     * 判断当前是否应生成 Boss 敌机。
     * <p>
     * 具体判断条件（如分数阈值）由各子类实现。
     *
     * @return 应生成 Boss 返回 true，否则返回 false
     */
    protected abstract boolean shouldSpawnBoss();

    /**
     * 生成 Boss 敌机。
     * <p>
     * 切换背景音乐为 Boss 战音乐，创建 Boss 实例并设置血量，添加到敌机列表中。
     * Boss 的血量通过钩子方法 {@link #setBossEnemyHp()} 确定。
     */
    protected void spawnBossEnemy() {
        musicManager.stopBgmMusic(MusicType.BGM);
        musicManager.playBgmMusic(MusicType.BGM_BOSS, true);
        bossEnemy = new BossEnemyFactory(enemyHpFactor, enemySpeedFactor).createEnemy(getRandomWidth(EnemyType.BOSS),
                0);
        bossEnemy.setHp(setBossEnemyHp());
        enemyAircrafts.add(bossEnemy);
        bossSpawned = true;
        System.out.println(String.format("BOSS敌机登场 血量为：%d", bossEnemyHp));
    }

    /**
     * 设置 Boss 敌机的血量。
     * <p>
     * 钩子方法，由子类覆盖以提供不同难度下的 Boss 血量。
     *
     * @return Boss 血量值
     */
    protected int setBossEnemyHp() {
        return bossEnemyHp;
    }

    /**
     * 按周期随机生成普通敌机。
     * <p>
     * 使用敌机工厂根据 {@link #getRandomEnemyType()} 返回的类型创建敌机，
     * 并添加到屏幕中，数量不超过 {@link #enemyMaxNumber}。
     */
    protected void createRandomEnemy() {
        enemySpawnCounter++;
        if (enemySpawnCounter >= enemySpawnCycle) {
            enemySpawnCounter = 0;
            if (enemyAircrafts.size() < enemyMaxNumber) {
                EnemyType type = getRandomEnemyType();
                AbstractAircraft enemyAircraft = enemyFactories.get(type).createEnemy(
                        getRandomWidth(type),
                        getRandomHeight());
                enemyAircrafts.add(enemyAircraft);
            }
        }
    }

    /**
     * 控制英雄机和敌机的射击行为。
     * <p>
     * 基于各自的射击周期计数器触发 {@link #heroShoot()} 和 {@link #enemyShoot()}。
     */
    protected void shootAction() {
        heroShootCounter++;
        enemyShootCounter++;
        if (heroShootCounter >= heroShootCycle) {
            heroShootCounter = 0;
            heroShoot();
        }
        if (enemyShootCounter >= enemyShootCycle) {
            enemyShootCounter = 0;
            enemyShoot();
        }
    }

    /**
     * 所有英雄机执行射击，将生成的子弹加入英雄机子弹列表。
     */
    protected void heroShoot() {
        for (HeroAircraft hero : heroes) {
            heroBullets.addAll(hero.shoot());
        }
    }

    /**
     * 所有敌机执行射击，将生成的子弹加入敌机子弹列表。
     */
    protected void enemyShoot() {
        for (AbstractAircraft enemyAircraft : enemyAircrafts) {
            enemyBullets.addAll(enemyAircraft.shoot());
        }
    }

    /**
     * 移动所有子弹（英雄机子弹和敌机子弹）。
     */
    protected void bulletsMoveAction() {
        for (BaseBullet bullet : heroBullets) {
            bullet.forward();
        }
        for (BaseBullet bullet : enemyBullets) {
            bullet.forward();
        }
    }

    /**
     * 移动所有敌机。
     */
    protected void aircraftsMoveAction() {
        for (AbstractAircraft enemyAircraft : enemyAircrafts) {
            enemyAircraft.forward();
        }
    }

    /**
     * 检测并惩罚逃离出屏幕底部的敌机。
     * <p>
     * 对逃离敌机根据类型扣除英雄机的生命值并扣分，生成爆炸效果后移除该敌机。
     * 各类型敌机惩罚值不同：普通10血5分、精英20血10分、老兵30血15分、王牌40血20分。
     */
    private void checkEscapedEnemies() {
        java.util.Iterator<AbstractAircraft> it = enemyAircrafts.iterator();
        while (it.hasNext()) {
            AbstractAircraft enemy = it.next();
            if (enemy.getLocationY() >= Main.WINDOW_HEIGHT && enemy.notValid()) {
                int hpPenalty = 10;
                int scorePenalty = 5;
                if (enemy instanceof EliteEnemy) {
                    hpPenalty = 20;
                    scorePenalty = 10;
                } else if (enemy instanceof VeteranEnemy) {
                    hpPenalty = 30;
                    scorePenalty = 15;
                } else if (enemy instanceof AceEnemy) {
                    hpPenalty = 40;
                    scorePenalty = 20;
                }
                for (HeroAircraft hero : heroes) {
                    hero.decreaseHp(hpPenalty);
                }
                addExplosion(enemy.getLocationX(), enemy.getLocationY());
                score = Math.max(0, score - scorePenalty);
                System.out.println(enemy.getClass().getSimpleName() + "逃离！扣血" + hpPenalty + "点，扣分" + scorePenalty + "分");
                it.remove();
            }
        }
    }

    /**
     * 切换游戏的暂停/继续状态。
     * <p>
     * 暂停时停止背景音乐，继续时恢复播放，并触发界面重绘以显示或隐藏暂停菜单。
     */
    private void togglePause() {
        paused = !paused;
        if (paused) {
            musicManager.stopBgmMusic(MusicType.BGM);
            musicManager.stopBgmMusic(MusicType.BGM_BOSS);
        } else {
            musicManager.playBgmMusic(MusicType.BGM, true);
        }
        repaint();
    }

    /**
     * 退出游戏，返回主菜单。
     * <p>
     * 取消定时器、停止所有音乐，关闭当前游戏窗口并打开主菜单界面。
     */
    private void exitGame() {
        timer.cancel();
        musicManager.stopAllMusic();
        SwingUtilities.invokeLater(() -> {
            closeGameWindow();
            new MainMenuFrame();
        });
    }

    /**
     * 更新所有键盘控制英雄机的位置。
     * <p>
     * 用于双人模式中由键盘（WASD）控制的玩家1。
     */
    protected void updateKeyboardHeroes() {
        for (KeyboardController kb : keyboardControllers) {
            kb.updatePosition();
        }
    }

    /**
     * 移动所有掉落道具。
     */
    protected void propMoveAction() {
        for (AbstractProp prop : props) {
            prop.forward();
        }
    }

    /**
     * 碰撞检测主逻辑。
     * <p>
     * 依次检测以下碰撞：
     * <ol>
     *   <li>敌机子弹 vs 英雄机</li>
     *   <li>英雄机子弹 vs 敌机（含得分、道具掉落、爆炸效果）</li>
     *   <li>英雄机 vs 敌机（直接碰撞导致游戏结束）</li>
     *   <li>道具 vs 英雄机（触发道具效果）</li>
     * </ol>
     */
    protected void crashCheckAction() {
        // 敌机子弹攻击所有英雄机
        for (BaseBullet bullet : enemyBullets) {
            if (bullet.notValid()) {
                continue;
            }
            for (HeroAircraft hero : heroes) {
                if (hero.crash(bullet)) {
                    hero.decreaseHp(bullet.getPower());
                    bullet.vanish();
                    break;
                }
            }
        }

        // 英雄机子弹攻击敌机
        for (BaseBullet bullet : heroBullets) {
            if (bullet.notValid()) {
                continue;
            }
            for (AbstractAircraft enemyAircraft : enemyAircrafts) {
                if (enemyAircraft.notValid()) {
                    continue;
                }
                if (enemyAircraft.crash(bullet)) {
                    enemyAircraft.decreaseHp(bullet.getPower());
                    bullet.vanish();
                    musicManager.playEffectMusic(MusicType.BULLET_HIT);
                    if (enemyAircraft.notValid()) {
                        if (enemyAircraft instanceof BossEnemy) {
                            addBossExplosion(enemyAircraft.getLocationX(), enemyAircraft.getLocationY());
                            musicManager.stopBgmMusic(MusicType.BGM_BOSS);
                            musicManager.playBgmMusic(MusicType.BGM, true);
                        } else {
                            addExplosion(enemyAircraft.getLocationX(), enemyAircraft.getLocationY());
                        }
                        addGameScore(enemyAircraft);
                        triggerProp(enemyAircraft);
                        musicManager.playEffectMusic(MusicType.BOMB_EXPLOSION);
                    }
                }
            }
        }

        // 英雄机 与 敌机 相撞（任意英雄碰撞立即结束游戏）
        for (AbstractAircraft enemyAircraft : enemyAircrafts) {
            if (enemyAircraft.notValid()) continue;
            for (HeroAircraft hero : heroes) {
                if (enemyAircraft.crash(hero) || hero.crash(enemyAircraft)) {
                    addExplosion(enemyAircraft.getLocationX(), enemyAircraft.getLocationY());
                    enemyAircraft.vanish();
                    musicManager.playEffectMusic(MusicType.BOMB_EXPLOSION);
                    // 所有英雄机立即死亡，游戏结束
                    for (HeroAircraft h : heroes) {
                        h.decreaseHp(Integer.MAX_VALUE);
                    }
                    break;
                }
            }
        }

        // 我方获得道具，道具生效
        for (AbstractProp prop : props) {
            if (prop.notValid()) {
                continue;
            }
            for (HeroAircraft hero : heroes) {
                if (prop.crash(hero)) {
                    musicManager.playEffectMusic(MusicType.GET_SUPPLY);
                    prop.activate(hero, this);
                    prop.vanish();
                    break;
                }
            }
        }
    }

    /**
     * 根据敌机类型增加对应分数。
     *
     * @param enemyAircraft 被击毁的敌机对象
     */
    private void addGameScore(AbstractAircraft enemyAircraft) {
        if (enemyAircraft instanceof MobEnemy) {
            addScore(EnemyType.MOB.getScore());
        } else if (enemyAircraft instanceof EliteEnemy) {
            addScore(EnemyType.ELITE.getScore());
        } else if (enemyAircraft instanceof VeteranEnemy) {
            addScore(EnemyType.VETERAN.getScore());
        } else if (enemyAircraft instanceof AceEnemy) {
            addScore(EnemyType.ACE.getScore());
        } else {
            addScore(EnemyType.BOSS.getScore());
        }
    }

    /**
     * 触发敌机被击毁后的道具掉落逻辑。
     * <p>
     * 由子类实现具体的随机掉落策略。
     *
     * @param enemyAircraft 被击毁的敌机对象
     */
    protected abstract void triggerProp(AbstractAircraft enemyAircraft);

    /**
     * 推进爆炸动画帧并衰减屏幕震动效果。
     * <p>
     * 每个动画帧按各自速度前进（1=正常，2=慢放），达到最大帧数（8）后移除。
     * 屏幕震动的持续时间逐帧递减。
     */
    private void advanceExplosions() {
        Iterator<int[]> it = explosions.iterator();
        while (it.hasNext()) {
            int[] e = it.next();
            e[2] += e[3];
            if (e[2] >= 8) {
                it.remove();
            }
        }
        // 屏幕震动衰减
        if (screenShakeTime > 0) {
            screenShakeTime--;
            shakeCounter++;
        }
    }

    /**
     * 后处理清理操作。
     * <p>
     * 从各对象列表中移除所有已标记为无效（notValid）的子弹、敌机和道具。
     */
    private void postProcessAction() {
        enemyBullets.removeIf(AbstractFlyingObject::notValid);
        heroBullets.removeIf(AbstractFlyingObject::notValid);
        enemyAircrafts.removeIf(AbstractFlyingObject::notValid);
        props.removeIf(AbstractFlyingObject::notValid);
    }

    /**
     * 检查游戏是否结束。
     * <p>
     * 若所有英雄机均阵亡，则触发爆炸动画、停止定时器、
     * 播放游戏结束音效并返回主菜单（携带最终得分等信息）。
     */
    protected void checkResultAction() {
        boolean allDead = true;
        for (HeroAircraft hero : heroes) {
            if (hero.getHp() > 0) {
                allDead = false;
                break;
            }
        }
        if (allDead) {
            for (HeroAircraft h : heroes) {
                addExplosion(h.getLocationX(), h.getLocationY());
            }
            timer.cancel();
            gameOverFlag = true;
            musicManager.playEffectMusic(MusicType.GAME_OVER);
            SwingUtilities.invokeLater(() -> {
                closeGameWindow();
                musicManager.stopAllMusic();
                // 启动带游戏结束面板的主菜单
                new MainMenuFrame(difficulty, score, gameMode);
            });
        }
    }

    /**
     * 安全关闭当前游戏窗口并释放资源。
     * <p>
     * 通过 {@link SwingUtilities#invokeLater} 在事件分派线程中执行窗口关闭。
     */
    protected void closeGameWindow() {
        SwingUtilities.invokeLater(() -> {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null) {
                window.dispose();
            }
        });
    }

    /**
     * 重写组件的绘制方法，实现游戏画面的完整渲染。
     * <p>
     * 每帧依次绘制：滚动背景 → 敌机子弹 → 英雄机子弹 → 敌机 → 道具 →
     * 英雄机 → 爆炸动画 → 得分生命值信息 → 暂停菜单（如暂停）。
     * 同时支持屏幕震动效果。
     *
     * @param g 绘图上下文
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);

        // 屏幕震动——创建副本避免 translate 累积
        Graphics2D shakeG = (Graphics2D) g.create();
        if (screenShakeTime > 0) {
            int offsetX = (int)(Math.random() * screenShakeIntensity * 2 - screenShakeIntensity);
            int offsetY = (int)(Math.random() * screenShakeIntensity * 2 - screenShakeIntensity);
            shakeG.translate(offsetX, offsetY);
        }

        // 绘制背景,图片滚动
        shakeG.drawImage(ImageManager.BACKGROUND_IMAGE, 0, this.backGroundTop - Main.WINDOW_HEIGHT, null);
        shakeG.drawImage(ImageManager.BACKGROUND_IMAGE, 0, this.backGroundTop, null);
        this.backGroundTop += 1;
        if (this.backGroundTop == Main.WINDOW_HEIGHT) {
            this.backGroundTop = 0;
        }

        // 先绘制子弹，后绘制飞机
        // 这样子弹显示在飞机的下层
        paintImageWithPositionRevised(shakeG, enemyBullets);
        paintImageWithPositionRevised(shakeG, heroBullets);
        paintImageWithPositionRevised(shakeG, enemyAircrafts);
        paintImageWithPositionRevised(shakeG, props);

        for (int i = 0; i < heroes.size(); i++) {
            HeroAircraft hero = heroes.get(i);
            BufferedImage img = (gameMode == GameMode.DOUBLE && i == 1)
                    ? ImageManager.HERO_PURPLE_IMAGE : ImageManager.HERO_IMAGE;
            shakeG.drawImage(img, hero.getLocationX() - img.getWidth() / 2,
                    hero.getLocationY() - img.getHeight() / 2, null);
        }

        // 绘制爆炸动画
        for (int[] e : explosions) {
            BufferedImage frame = ImageManager.BANG_FRAMES[Math.min(e[2], 7)];
            shakeG.drawImage(frame, e[0] - frame.getWidth() / 2, e[1] - frame.getHeight() / 2, null);
        }

        // 绘制得分和生命值
        paintScoreAndLife(shakeG);

        // 绘制 BOSS 警告（暂停菜单之前）
        if (bossWarningActive) {
            String warningText = "WARNING !!";
            shakeG.setFont(new Font("SansSerif", Font.BOLD, 60));
            FontMetrics fm = shakeG.getFontMetrics();
            int x = (Main.WINDOW_WIDTH - fm.stringWidth(warningText)) / 2;
            int y = Main.WINDOW_HEIGHT / 2 - 30;
            if (gameTime % 20 < 10) {
                shakeG.setColor(Color.RED);
            } else {
                shakeG.setColor(Color.ORANGE);
            }
            shakeG.drawString(warningText, x, y);
            int remain = (BOSS_WARNING_DELAY - bossWarningTimer) * 50 / 1000;
            String countDown = String.valueOf(remain + 1);
            shakeG.setFont(new Font("SansSerif", Font.BOLD, 40));
            shakeG.setColor(Color.WHITE);
            fm = shakeG.getFontMetrics();
            x = (Main.WINDOW_WIDTH - fm.stringWidth(countDown)) / 2;
            shakeG.drawString(countDown, x, y + 70);
        }

        // 暂停时绘制半透明遮罩和菜单
        if (paused && !gameOverFlag) {
            drawPauseMenu(shakeG);
        }

        shakeG.dispose();
    }

    /**
     * 绘制列表中所有飞行对象在屏幕上的正确位置。
     * <p>
     * 以各对象的中心坐标为准，向左上方偏移半个图片尺寸进行绘制。
     *
     * @param g       绘图上下文
     * @param objects 飞行对象列表（飞机、子弹、道具等 AbstractFlyingObject 子类）
     */
    private void paintImageWithPositionRevised(Graphics g, List<? extends AbstractFlyingObject> objects) {
        if (objects.isEmpty()) {
            return;
        }

        for (AbstractFlyingObject object : objects) {
            BufferedImage image = object.getImage();
            assert image != null : objects.getClass().getName() + " has no image! ";
            g.drawImage(image, object.getLocationX() - image.getWidth() / 2,
                    object.getLocationY() - image.getHeight() / 2, null);
        }
    }

    /**
     * 在屏幕左上角绘制当前得分和生命值信息。
     * <p>
     * 单人模式显示 "SCORE" 和 "LIFE"；双人模式分别显示 "P1 LIFE" 和 "P2 LIFE"。
     * 同时显示"按 ESC 暂停"的提示文字。
     *
     * @param g 绘图上下文
     */
    private void paintScoreAndLife(Graphics g) {
        int x = 10;
        int y = 25;
        g.setColor(Color.RED);
        g.setFont(new Font("SansSerif", Font.BOLD, 22));
        g.drawString("SCORE: " + this.score, x, y);
        y = y + 20;
        if (heroes.size() == 1) {
            g.drawString("LIFE: " + heroes.get(0).getHp(), x, y);
        } else {
            for (int i = 0; i < heroes.size(); i++) {
                g.drawString("P" + (i + 1) + " LIFE: " + heroes.get(i).getHp(), x, y);
                y += 20;
            }
        }
        // 显示暂停提示
        if (!paused && !gameOverFlag) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("SansSerif", Font.PLAIN, 14));
            g.drawString("按 ESC 暂停", Main.WINDOW_WIDTH - 100, 30);
        }
    }

    /**
     * 绘制暂停菜单。
     * <p>
     * 在半透明遮罩上绘制"游戏暂停"标题、"继续"按钮和"退出"按钮，
     * 以及"按 ESC 键继续游戏"的提示信息。
     *
     * @param g 绘图上下文
     */
    private void drawPauseMenu(Graphics g) {
        // 半透明遮罩
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT);

        // 标题
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 36));
        String title = "游戏暂停";
        FontMetrics fm = g.getFontMetrics();
        int titleX = (Main.WINDOW_WIDTH - fm.stringWidth(title)) / 2;
        g.drawString(title, titleX, 200);

        // 按钮位置
        int centerX = Main.WINDOW_WIDTH / 2;
        int btnY = 300;

        // 继续按钮
        continueBtnRect = new Rectangle(centerX - BTN_WIDTH - 20, btnY, BTN_WIDTH, BTN_HEIGHT);
        g.setColor(new Color(0, 200, 0));
        g.fillRoundRect(continueBtnRect.x, continueBtnRect.y, continueBtnRect.width, continueBtnRect.height, 10, 10);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 20));
        fm = g.getFontMetrics();
        String continueText = "继续";
        g.drawString(continueText,
                continueBtnRect.x + (BTN_WIDTH - fm.stringWidth(continueText)) / 2,
                continueBtnRect.y + BTN_HEIGHT / 2 + fm.getHeight() / 3);

        // 退出按钮
        exitBtnRect = new Rectangle(centerX + 20, btnY, BTN_WIDTH, BTN_HEIGHT);
        g.setColor(new Color(200, 0, 0));
        g.fillRoundRect(exitBtnRect.x, exitBtnRect.y, exitBtnRect.width, exitBtnRect.height, 10, 10);
        g.setColor(Color.WHITE);
        String exitText = "退出";
        g.drawString(exitText,
                exitBtnRect.x + (BTN_WIDTH - fm.stringWidth(exitText)) / 2,
                exitBtnRect.y + BTN_HEIGHT / 2 + fm.getHeight() / 3);

        // 提示信息
        g.setFont(new Font("SansSerif", Font.PLAIN, 16));
        g.setColor(Color.LIGHT_GRAY);
        String hint = "按 ESC 键继续游戏";
        fm = g.getFontMetrics();
        g.drawString(hint, (Main.WINDOW_WIDTH - fm.stringWidth(hint)) / 2, btnY + BTN_HEIGHT + 50);
    }

}
