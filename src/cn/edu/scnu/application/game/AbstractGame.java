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
 * @author 黄彪骐
 */
@SuppressWarnings("all")
public abstract class AbstractGame extends JPanel {
    /**
     * ! 定义所有游戏子类都会用到的公共属性
     */
    private int backGroundTop = 0;

    // 调度器, 用于定时任务调度
    protected Timer timer;
    // 时间间隔(ms)，控制刷新频率
    private final int timeInterval = 50;
    protected long gameTime = 0;
    protected int difficultyLevelUpInterval = 0;

    protected List<HeroAircraft> heroes;
    protected int initHeroHp = 200;

    protected int bossEnemyHp = 100;

    protected final List<AbstractAircraft> enemyAircrafts; // 多态数组
    protected final List<BaseBullet> heroBullets;
    protected final List<BaseBullet> enemyBullets;
    protected final List<AbstractProp> props;

    protected final Map<EnemyType, EnemyFactory> enemyFactories = new EnumMap<>(EnemyType.class);

    // 屏幕中出现的敌机最大数量
    protected int enemyMaxNumber = 5;

    // 敌机生成周期
    protected double enemySpawnCycle = 10;
    private int enemySpawnCounter = 0;

    // 道具生成概率
    protected double propRand;

    // 英雄机和敌机射击周期
    protected int heroShootCycle = 10;
    protected int enemyShootCycle = 10;
    private int enemyShootCounter = 0;
    private int heroShootCounter = 0;

    protected double enemyHpFactor = 1.0;
    protected double enemySpeedFactor = 1.0;

    // 当前玩家信息
    protected String playName;
    protected int score = 0;
    protected Difficulty difficulty;

    private PropEffectTimer fireTimer;

    // 游戏结束标志
    private boolean gameOverFlag = false;

    // Boss机产生标志
    protected boolean bossSpawned = false;
    // Boss机产生的分数阈值器
    protected int scoreThreshold = 500;
    // Boss机
    protected AbstractAircraft bossEnemy = null;

    // 排行榜功能类
    PlayRecordDaoImpl playRecordDao = new PlayRecordDaoImpl(new ArrayList<>());
    RankingBoard rankingBoard = new RankingBoard(playRecordDao);

    protected MusicManager musicManager = MusicManager.getInstance();

    protected GameMode gameMode;
    protected List<KeyboardController> keyboardControllers = new ArrayList<>();

    // 暂停相关
    private boolean paused = false;
    private Rectangle continueBtnRect, exitBtnRect;
    private static final int BTN_WIDTH = 120, BTN_HEIGHT = 40;

    // 爆炸动画列表：每个元素为 {x, y, frame(0~7), speed(1=正常, 2=慢放)}
    private final List<int[]> explosions = new ArrayList<>();

    // 屏幕震动
    private int screenShakeTime = 0;
    private int screenShakeIntensity = 0;
    private int shakeCounter = 0;

    public void addExplosion(int x, int y) {
        explosions.add(new int[]{x, y, 0, 1});
    }

    /** Boss 死亡大爆炸：慢放 + 多炸点 + 屏幕震动 */
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

    /** 开始屏幕震动 */
    public void startScreenShake(int intensity, int duration) {
        screenShakeIntensity = intensity;
        screenShakeTime = duration;
    }

    public boolean isPaused() {
        return paused;
    }

    /**
     * ! 设置参数部分
     */

    public void addScore(int points) {
        this.score += points;
    }

    public int getScore() {
        return score;
    }

    public synchronized void setFireTimer(PropEffectTimer fireTimer) {
        if (this.fireTimer != null) {
            this.fireTimer.cancel();
        }
        this.fireTimer = fireTimer;
    }

    /**
     * ! 构造器部分
     */
    // 通用构造函数（具体方法）
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

    // 具体方法：初始化工厂映射
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

    // 抽象方法：初始化游戏设置（由子类实现）
    protected abstract void initGameSettings();

    /**
     * ! 游戏启动入口，执行游戏逻辑，流程骨架
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

                if (shouldSpawnBoss()) {
                    spawnBossEnemy();
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

    private boolean shouldLevelUp() {
        return difficultyLevelUpInterval != 0 && gameTime % difficultyLevelUpInterval == 0;
    }

    protected void difficultyLevelUp() {
    }

    protected abstract EnemyType getRandomEnemyType();

    /**
     * ! 获取图片宽度和高度的方法 公用的
     * 因为这个图片资源没有改变
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
            case ACE:
                imageWidth = ImageManager.ACE_ENEMY_IMAGE.getWidth();
            case BOSS:
                imageWidth = ImageManager.BOSS_ENEMY_IMAGE.getWidth();
            default:
                break;
        }
        return (int) (Math.random() * (Main.WINDOW_WIDTH - imageWidth));
    }

    private int getRandomHeight() {
        return (int) (Math.random() * Main.WINDOW_HEIGHT * 0.05);
    }

    /**
     * ! BOSS敌机部分
     */

    /**
     * 判断是否需要生成BOSS敌机
     *
     * @see 抽象方法
     */
    protected abstract boolean shouldSpawnBoss();

    /**
     * 生成BOSS敌机
     *
     * @see 具体方法中应用了钩子方法
     *      - 整体逻辑通用 对应 具体方法
     *      - BOSS敌机的血量设置 对应 钩子方法
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
     * BOSS敌机的血量设置
     *
     * @see 钩子方法 需要具体设置
     *
     */
    protected int setBossEnemyHp() {
        return bossEnemyHp;
    }

    /**
     * ! 非BOSS敌机的生成部分
     */

    /**
     * 随机生成敌机
     *
     * @see 具体方法中应用抽象方法
     *      - 敌机生成的周期 和 最大敌机数量 不确定
     */
    protected void createRandomEnemy() {
        enemySpawnCounter++;
        if (enemySpawnCounter >= enemySpawnCycle) {
            enemySpawnCounter = 0;
            if (enemyAircrafts.size() < enemyMaxNumber) {
                AbstractAircraft enemyAircraft = enemyFactories.get(getRandomEnemyType()).createEnemy(
                        getRandomWidth(getRandomEnemyType()),
                        getRandomHeight());
                enemyAircrafts.add(enemyAircraft);
            }
        }
    }

    /**
     * ! Action部分
     */

    /**
     * 射击部分
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

    protected void heroShoot() {
        // 所有英雄机射击
        for (HeroAircraft hero : heroes) {
            heroBullets.addAll(hero.shoot());
        }
    }

    protected void enemyShoot() {
        // 敌机射击
        for (AbstractAircraft enemyAircraft : enemyAircrafts) {
            enemyBullets.addAll(enemyAircraft.shoot());
        }
    }

    /**
     * 子弹移动部分
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
     * 飞机移动部分
     */
    protected void aircraftsMoveAction() {
        for (AbstractAircraft enemyAircraft : enemyAircrafts) {
            enemyAircraft.forward();
        }
    }

    /**
     * 检测并惩罚逃离出屏的敌机：扣血 + 扣分，然后立即移除（避免重复惩罚）
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

    // ★ 暂停切换
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

    // ★ 退出游戏返回主菜单
    private void exitGame() {
        timer.cancel();
        musicManager.stopAllMusic();
        SwingUtilities.invokeLater(() -> {
            closeGameWindow();
            new MainMenuFrame();
        });
    }

    protected void updateKeyboardHeroes() {
        for (KeyboardController kb : keyboardControllers) {
            kb.updatePosition();
        }
    }

    /**
     * 道具移动部分
     */
    protected void propMoveAction() {
        for (AbstractProp prop : props) {
            prop.forward();
        }
    }

    /**
     * ! 碰撞检测部分
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

    protected abstract void triggerProp(AbstractAircraft enemyAircraft);

    // 爆炸动画帧推进 + 屏幕震动衰减
    private void advanceExplosions() {
        // 爆炸帧推进
        Iterator<int[]> it = explosions.iterator();
        while (it.hasNext()) {
            int[] e = it.next();
            e[2] += e[3]; // 按速度推进（1=正常，2=慢放，实际帧增快）
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
     * ! 后处理部分
     */

    /**
     * 后处理：
     * 1. 删除无效的子弹
     * 2. 删除无效的敌机
     * 3. 删除无效的道具
     */
    private void postProcessAction() {
        enemyBullets.removeIf(AbstractFlyingObject::notValid);
        heroBullets.removeIf(AbstractFlyingObject::notValid);
        enemyAircrafts.removeIf(AbstractFlyingObject::notValid);
        props.removeIf(AbstractFlyingObject::notValid);
    }

    /**
     * 检查游戏是否结束，若结束：关闭线程池
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
            // 所有英雄机位置触发爆炸动画
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
     * 安全关闭游戏窗口并返回主菜单
     */
    protected void closeGameWindow() {
        SwingUtilities.invokeLater(() -> {
            // 获取顶层窗口
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null) {
                window.dispose();
            }
        });
    }

    /**
     * ! paint部分 公用的
     * 因为画图的逻辑不变 只是要画的东西变了
     * 但是paint部分只管画图逻辑
     */

    /**
     * 重写 paint方法
     * 通过重复调用paint方法，实现游戏动画
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

        // 暂停时绘制半透明遮罩和菜单
        if (paused && !gameOverFlag) {
            drawPauseMenu(shakeG);
        }

        shakeG.dispose();
    }

    /**
     *
     * @param g       画笔
     * @param objects 列表（元素是AbstractFlyingObject以及它的子类） 所以包括：飞机类，子弹类，道具类
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

    // 绘制暂停菜单
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