package cn.edu.scnu.application.game;

import cn.edu.scnu.aircraft.*;
import cn.edu.scnu.aircraft.factory.AceEnemyFactory;
import cn.edu.scnu.aircraft.factory.BossEnemyFactory;
import cn.edu.scnu.aircraft.factory.EliteEnemyFactory;
import cn.edu.scnu.aircraft.factory.EnemyFactory;
import cn.edu.scnu.aircraft.factory.MobEnemyFactory;
import cn.edu.scnu.aircraft.VeteranEnemyFactory;
import cn.edu.scnu.application.HeroController;
import cn.edu.scnu.application.ImageManager;
import cn.edu.scnu.application.Main;
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
    private final Timer timer;
    // 时间间隔(ms)，控制刷新频率
    private final int timeInterval = 50;
    protected long gameTime = 0;
    protected int difficultyLevelUpInterval = 0;

    protected final HeroAircraft heroAircraft;
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

    private MusicManager musicManager = MusicManager.getInstance();

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
    public AbstractGame() {
        musicManager.playBgmMusic(MusicType.BGM, true);

        // 调用抽象方法进行难度相关初始化
        initGameSettings();

        // 1. 初始化英雄机（通用）
        HeroAircraft.reset();
        heroAircraft = HeroAircraft.getInstance(
                Main.WINDOW_WIDTH / 2,
                Main.WINDOW_HEIGHT - ImageManager.HERO_IMAGE.getHeight(),
                0, 0, initHeroHp);

        // 2. 初始化列表（通用）
        enemyAircrafts = new LinkedList<>();
        heroBullets = new LinkedList<>();
        enemyBullets = new LinkedList<>();
        props = new LinkedList<>();

        // 3. 初始化工厂映射（通用）
        initEnemyFactories();

        // 4. 初始化鼠标监听（通用）
        new HeroController(this, heroAircraft);

        // 5. 初始化定时器（通用）
        this.timer = new Timer("game-action-timer", true);

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
                // 子弹移动
                bulletsMoveAction();
                // 飞机移动
                aircraftsMoveAction();
                // 道具移动
                propMoveAction();
                // 撞击检测
                crashCheckAction();
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
        // 英雄机射击
        heroBullets.addAll(heroAircraft.shoot());
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
        // 敌机子弹攻击英雄机
        for (BaseBullet bullet : enemyBullets) {
            if (bullet.notValid()) {
                continue;
            }
            if (heroAircraft.crash(bullet)) {
                heroAircraft.decreaseHp(bullet.getPower());
                bullet.vanish();
            }
        }

        // 英雄机子弹攻击敌机
        for (BaseBullet bullet : heroBullets) {
            if (bullet.notValid()) {
                continue;
            }
            for (AbstractAircraft enemyAircraft : enemyAircrafts) {
                if (enemyAircraft.notValid()) {
                    // 已被其他子弹击毁的敌机，不再检测
                    // 避免多个子弹重复击毁同一敌机的判定
                    continue;
                }
                if (enemyAircraft.crash(bullet)) {
                    // 敌机撞击到英雄机子弹
                    // 敌机损失一定生命值
                    enemyAircraft.decreaseHp(bullet.getPower());
                    bullet.vanish();
                    musicManager.playEffectMusic(MusicType.BULLET_HIT);
                    if (enemyAircraft.notValid()) {
                        // 添加分数
                        addGameScore(enemyAircraft);
                        // 触发道具
                        triggerProp(enemyAircraft);
                        if (enemyAircraft instanceof BossEnemy) {
                            musicManager.stopBgmMusic(MusicType.BGM_BOSS);
                            musicManager.playBgmMusic(MusicType.BGM, true);
                        }
                        musicManager.playEffectMusic(MusicType.BOMB_EXPLOSION);
                    }
                }
                // 英雄机 与 敌机 相撞，均损毁
                if (enemyAircraft.crash(heroAircraft) || heroAircraft.crash(enemyAircraft)) {
                    enemyAircraft.vanish();
                    heroAircraft.decreaseHp(Integer.MAX_VALUE);
                }
            }
        }

        // 我方获得道具，道具生效
        for (AbstractProp prop : props) {
            if (prop.notValid()) {
                continue;
            }
            if (prop.crash(heroAircraft)) {
                musicManager.playEffectMusic(MusicType.GET_SUPPLY);
                prop.activate(heroAircraft, this);
                prop.vanish();
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
    private void checkResultAction() {
        // 游戏结束检查英雄机是否存活
        if (heroAircraft.getHp() <= 0) {
            timer.cancel(); // 取消定时器并终止所有调度任务
            gameOverFlag = true;
            System.out.println("Game Over!");
            musicManager.playEffectMusic(MusicType.GAME_OVER);
            SwingUtilities.invokeLater(() -> {
                // 弹出对话框 让用户输入玩家名
                playName = showNameInputDialog();
                // 关闭游戏窗口
                closeGameWindow();
                // 关闭所有游戏
                musicManager.stopAllMusic();
                // 更新数据库
                rankingBoard.addCurRecord(playName, score, difficulty);
                // 显示排行榜表格
                rankingBoard.showRankInfo(difficulty);
            });
        }
    };

    private String showNameInputDialog() {
        // 创建自定义的输入对话框
        JTextField nameField = new JTextField(15);

        Object[] message = {
                "游戏结束！您的得分: " + score,
                "请输入玩家姓名:",
                nameField
        };

        int option = JOptionPane.showConfirmDialog(
                this, // 父组件，如果是JPanel，可以用this
                message,
                "游戏结束 - 记录得分",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                // 如果用户没有输入，使用默认名称
                return "匿名玩家";
            }
            return name;
        }

        return "匿名玩家"; // 用户取消了输入
    }

    /**
     * 安全关闭游戏窗口并返回主菜单
     */
    private void closeGameWindow() {
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

        // 绘制背景,图片滚动
        g.drawImage(ImageManager.BACKGROUND_IMAGE, 0, this.backGroundTop - Main.WINDOW_HEIGHT, null);
        g.drawImage(ImageManager.BACKGROUND_IMAGE, 0, this.backGroundTop, null);
        this.backGroundTop += 1;
        if (this.backGroundTop == Main.WINDOW_HEIGHT) {
            this.backGroundTop = 0;
        }

        // 先绘制子弹，后绘制飞机
        // 这样子弹显示在飞机的下层
        paintImageWithPositionRevised(g, enemyBullets);
        paintImageWithPositionRevised(g, heroBullets);
        paintImageWithPositionRevised(g, enemyAircrafts);
        paintImageWithPositionRevised(g, props);

        g.drawImage(ImageManager.HERO_IMAGE, heroAircraft.getLocationX() - ImageManager.HERO_IMAGE.getWidth() / 2,
                heroAircraft.getLocationY() - ImageManager.HERO_IMAGE.getHeight() / 2, null);

        // 绘制得分和生命值
        paintScoreAndLife(g);

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
        g.drawString("LIFE: " + this.heroAircraft.getHp(), x, y);
    }

}
