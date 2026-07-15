package cn.edu.scnu.application.game;

import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import cn.edu.scnu.aircraft.AbstractAircraft;
import cn.edu.scnu.aircraft.BossEnemy;
import cn.edu.scnu.aircraft.EnemyType;
import cn.edu.scnu.application.GameMode;
import cn.edu.scnu.application.ImageManager;
import cn.edu.scnu.prop.AbstractProp;
import cn.edu.scnu.rank.Difficulty;

/**
 * 高级模式游戏类，继承自 {@link AbstractGame}。
 * <p>
 * 本类实现了「高级模式」下特有的游戏逻辑，包括：
 * <ul>
 *   <li>初始化高级模式的游戏参数（血量、敌机数量、生成周期、道具概率等）</li>
 *   <li>随机生成多种类型的敌机（普通/精英/精锐/王牌），高级模式敌机构成更复杂</li>
 *   <li>Boss 敌机的生成判定与血量递增机制</li>
 *   <li>击败普通敌机有概率掉落道具，击败 Boss 敌机必定掉落多个道具并提升下次 Boss 出现分数阈值</li>
 *   <li>游戏过程中动态提升难度（增加敌机数量、缩短生成周期、提升属性倍率等）</li>
 * </ul>
 * </p>
 *
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public class AdvancedGame extends AbstractGame {

    /**
     * 构造一个高级模式游戏实例。
     *
     * @param mode 游戏模式枚举值，用于传递给父类进行初始化
     */
    public AdvancedGame(GameMode mode) {
        super(mode);
        this.difficulty = Difficulty.ADVANCED;
    }

    /**
     * 初始化高级模式的游戏设置。
     * <p>
     * 设置以下内容：
     * <ul>
     *   <li>加载专属背景图片 {@code bg4.jpg}</li>
     *   <li>英雄机初始生命值、Boss 敌机初始血量</li>
     *   <li>敌机最大数量、敌机及英雄机子弹生成周期</li>
     *   <li>敌机血量和速度的倍率因子</li>
     *   <li>Boss 出现分数阈值、道具掉落概率、难度升级间隔</li>
     * </ul>
     * </p>
     */
    @Override
    protected void initGameSettings() {
        try {
            ImageManager.BACKGROUND_IMAGE = ImageIO.read(new FileInputStream("src/images/bg4.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.initHeroHp = 1000;
        this.bossEnemyHp = 500;

        this.enemyMaxNumber = 12;
        this.enemySpawnCycle = 12;
        this.heroShootCycle = 20;
        this.enemyShootCycle = 12;
        this.enemyHpFactor = 1.5;
        this.enemySpeedFactor = 1.5;

        this.scoreThreshold = 500;

        this.propRand = 0.4;
        this.difficultyLevelUpInterval = 800;
    }

    /**
     * 根据概率随机返回一种敌机类型。
     * <p>
     * 高级模式中敌机类型分布如下：
     * <ul>
     *   <li>30% 概率返回 {@link EnemyType#MOB 普通}</li>
     *   <li>25% 概率返回 {@link EnemyType#ELITE 精英}</li>
     *   <li>25% 概率返回 {@link EnemyType#VETERAN 精锐}</li>
     *   <li>20% 概率返回 {@link EnemyType#ACE 王牌}</li>
     * </ul>
     * </p>
     *
     * @return 随机选取的敌机类型，不会为 {@code null}
     */
    @Override
    protected EnemyType getRandomEnemyType() {
        double rand = Math.random();
        if (rand < 0.3)
            return EnemyType.MOB; // 30% 普通
        if (rand < 0.55)
            return EnemyType.ELITE; // 25% 精英
        if (rand < 0.80)
            return EnemyType.VETERAN; // 25% 精锐
        return EnemyType.ACE; // 20% 王牌
    }

    /**
     * 判断当前是否应该生成 Boss 敌机。
     * <p>
     * 满足以下所有条件时返回 {@code true}：
     * <ul>
     *   <li>当前 Boss 尚未生成过（或上次 Boss 已被击败）</li>
     *   <li>当前得分已达到或超过分数阈值</li>
     *   <li>场上不存在有效的 Boss 敌机实例</li>
     * </ul>
     * </p>
     *
     * @return 是否应生成 Boss 敌机
     */
    @Override
    protected boolean shouldSpawnBoss() {
        return !bossSpawned && score >= scoreThreshold && (bossEnemy == null || bossEnemy.notValid());
    }

    /**
     * 设置 Boss 敌机的血量，并在每次调用时递增。
     * <p>
     * 每次生成 Boss 时血量会在上次基础上增加 100，使后续 Boss 更具挑战性。
     * </p>
     *
     * @return 当前 Boss 敌机应持有的生命值
     */
    @Override
    protected int setBossEnemyHp() {
        this.bossEnemyHp += 100;
        return this.bossEnemyHp;
    }

    /**
     * 触发指定敌机被击毁时的道具掉落逻辑。
     * <p>
     * 根据敌机类型有不同的处理方式：
     * <ul>
     *   <li>普通敌机（非 Boss）：按概率 {@code propRand} 尝试掉落一个道具</li>
     *   <li>Boss 敌机：必定掉落三个道具，同时更新下次 Boss 出现的分数阈值（当前得分的 1.2 倍），
     *       并重置 Boss 生成标志以便下一轮 Boss 出现</li>
     * </ul>
     * </p>
     *
     * @param enemyAircraft 被击毁的敌机实例，用于生成对应的道具
     */
    @Override
    protected void triggerProp(AbstractAircraft enemyAircraft) {

        if (!(enemyAircraft instanceof BossEnemy)) {
            AbstractProp newProp = enemyAircraft.obtainProp(enemyAircraft, propRand);
            if (newProp != null) {
                props.add(newProp);
            }
        } else {
            for (int i = 0; i < 3; i++) {
                AbstractProp newProp = enemyAircraft.obtainProp(enemyAircraft, 1.0);
                if (newProp != null) {
                    props.add(newProp);
                }
            }

            scoreThreshold = (int) (score * 1.2);
            bossSpawned = false;
            System.out.println("BOSS敌机被击毁 下次出现的分数阈值为：" + scoreThreshold);
        }

    }

    /**
     * 执行难度升级逻辑。
     * <p>
     * 每次调用时对游戏难度参数进行以下调整：
     * <ul>
     *   <li>敌机最大数量 +1（上限 20）</li>
     *   <li>敌机生成周期 * 0.9（下限 8）</li>
     *   <li>敌机血量倍率 * 1.1（上限 3.0）</li>
     *   <li>敌机速度倍率 * 1.1（上限 3.0）</li>
     *   <li>英雄机子弹生成周期 +1（上限 30）</li>
     * </ul>
     * 同时在控制台输出当前难度参数信息，便于调试和观察。
     * </p>
     */
    @Override
    protected void difficultyLevelUp() {
        this.enemyMaxNumber = Math.min(20, this.enemyMaxNumber + 1);
        this.enemySpawnCycle = Math.max(8, this.enemySpawnCycle * 0.9);
        this.enemyHpFactor = Math.min(3, this.enemyHpFactor * 1.1);
        this.enemySpeedFactor = Math.min(3, this.enemySpeedFactor * 1.1);
        this.heroShootCycle = Math.min(30, this.heroShootCycle + 1);
        System.out.println("==========提高难度===========");
        System.out.println(String.format("敌机最大数量 = %d 个", enemyMaxNumber));
        System.out.println(String.format("敌机产生周期 = %.2f 秒", (double) enemySpawnCycle * 50 / 1000));
        System.out.println(String.format("敌机血量增值 = %.2f 倍", enemyHpFactor));
        System.out.println(String.format("敌机速度增值 = %.2f 倍", enemySpeedFactor));
        System.out.println(String.format("英雄机子弹产生周期 = %.2f 秒", (double) enemyShootCycle * 50 / 1000));
        System.out.println("============================");
    }

}
