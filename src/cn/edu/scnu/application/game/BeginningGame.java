package cn.edu.scnu.application.game;

import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import cn.edu.scnu.aircraft.AbstractAircraft;
import cn.edu.scnu.aircraft.EnemyType;
import cn.edu.scnu.application.GameMode;
import cn.edu.scnu.application.ImageManager;
import cn.edu.scnu.prop.AbstractProp;
import cn.edu.scnu.rank.Difficulty;

/**
 * 新手难度游戏模式。
 * <p>
 * 继承自 {@link AbstractGame}，提供面向初学者/低难度的游戏体验。
 * 与普通/困难模式相比，此模式具有以下特点：
 * <ul>
 *   <li>敌机生成数量较少（最多 5 架同时存在）</li>
 *   <li>敌机生成周期较长（每 20 帧生成一次）</li>
 *   <li>英雄机初始生命值较高（300）</li>
 *   <li>敌机生命值和速度均有折扣因子（0.7 和 0.8）</li>
 *   <li>敌机类型分布偏向低难度单位（70% 普通敌机，2% 王牌敌机）</li>
 *   <li>不会生成 Boss 敌机</li>
 * </ul>
 *
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public class BeginningGame extends AbstractGame {

    /**
     * 创建一个新手难度游戏实例。
     *
     * @param mode 游戏模式枚举值，用于区分不同的游戏模式入口
     */
    public BeginningGame(GameMode mode) {
        super(mode);
        this.difficulty = Difficulty.BEGINNER;
    }

    /**
     * 初始化新手模式的游戏参数。
     * <p>
     * 加载背景图片，并设置英雄机初始生命值、敌机数量上限、敌机生成周期、
     * 英雄机射击周期、敌机射击周期以及敌机生命值和速度的折扣因子。
     */
    @Override
    protected void initGameSettings() {
        try {
            ImageManager.BACKGROUND_IMAGE = ImageIO.read(new FileInputStream("src/images/bg.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.initHeroHp = 300;
        this.enemyMaxNumber = 5;
        this.enemySpawnCycle = 20;
        this.heroShootCycle = 15;
        this.enemyShootCycle = 20;
        this.enemyHpFactor = 0.7;
        this.enemySpeedFactor = 0.8;
    }

    /**
     * 根据概率分布随机生成敌机类型。
     * <p>
     * 敌机类型分布如下：
     * <ul>
     *   <li>70% 概率生成普通敌机（{@link EnemyType#MOB}）</li>
     *   <li>20% 概率生成精英敌机（{@link EnemyType#ELITE}）</li>
     *   <li>8% 概率生成精锐敌机（{@link EnemyType#VETERAN}）</li>
     *   <li>2% 概率生成王牌敌机（{@link EnemyType#ACE}）</li>
     * </ul>
     *
     * @return 根据概率分布选中的敌机类型
     */
    @Override
    protected EnemyType getRandomEnemyType() {
        double rand = Math.random();
        if (rand < 0.7)
            return EnemyType.MOB; // 70% 普通
        if (rand < 0.9)
            return EnemyType.ELITE; // 20% 精英
        if (rand < 0.98)
            return EnemyType.VETERAN; // 8% 精锐
        return EnemyType.ACE; // 2% 王牌
    }

    /**
     * 触发敌机掉落道具的逻辑。
     * <p>
     * 当敌机被击毁时调用，根据概率判断该敌机是否掉落道具，若掉落则将道具
     * 添加到游戏的道具列表中。
     *
     * @param enemyAircraft 被击毁的敌机实例，用于生成对应的道具
     */
    @Override
    protected void triggerProp(AbstractAircraft enemyAircraft) {
        AbstractProp newProp = enemyAircraft.obtainProp(enemyAircraft, Math.random());
        if (newProp != null) {
            props.add(newProp);
        }

    }

    /**
     * 判断是否应该生成 Boss 敌机。
     * <p>
     * 新手模式禁用 Boss 机制，始终返回 {@code false}。
     *
     * @return 始终返回 {@code false}，表示不生成 Boss
     */
    @Override
    protected boolean shouldSpawnBoss() {
        return false;
    }

}
