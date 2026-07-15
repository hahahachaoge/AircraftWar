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
 * 专家难度游戏类，继承自 {@link AbstractGame}。
 * <p>
 * 该类实现了专家难度模式下的所有游戏逻辑，包括：
 * <ul>
 *   <li>初始化专家难度的游戏参数（血量、生成速度、敌机属性等）</li>
 *   <li>定义专家模式特有的敌机类型生成概率（以王牌和精锐敌机为主）</li>
 *   <li>控制 BOSS 敌机的生成条件和血量递增</li>
 *   <li>处理敌机被击毁后的道具掉落逻辑（BOSS 必掉三个道具）</li>
 *   <li>实现随时间递增的难度升级机制</li>
 * </ul>
 * </p>
 *
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public class ExpertGame extends AbstractGame{

    /**
     * 构造一个专家难度游戏实例。
     *
     * @param mode 游戏模式（用于区分单人/双人等模式）
     */
    public ExpertGame(GameMode mode) {
        super(mode);
        this.difficulty = Difficulty.EXPERT;
    }

    /**
     * 初始化专家难度的游戏设置。
     * <p>
     * 包括加载专属背景图片，设置英雄机初始血量、BOSS 血量、
     * 敌机最大数量、敌机生成周期、射击周期、血量/速度倍率、
     * BOSS 出现分数阈值、道具掉落概率以及难度升级间隔。
     * </p>
     */
    @Override
    protected void initGameSettings() {
        try {
            ImageManager.BACKGROUND_IMAGE = ImageIO.read(new FileInputStream("src/images/bg5.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.initHeroHp = 1000;
        this.bossEnemyHp = 500;

        this.enemyMaxNumber = 15;
        this.enemySpawnCycle = 10;
        this.heroShootCycle = 20;
        this.enemyShootCycle = 10;
        this.enemyHpFactor = 1.8;
        this.enemySpeedFactor = 1.8;

        this.scoreThreshold = 400;

        this.propRand = 0.25;
        this.difficultyLevelUpInterval = 500;
    }

    /**
     * 根据专家难度的概率分布随机生成敌机类型。
     * <p>
     * 专家模式下敌机类型分布为：
     * <ul>
     *   <li>普通敌机（MOB）：20%</li>
     *   <li>精英敌机（ELITE）：20%</li>
     *   <li>精锐敌机（VETERAN）：25%</li>
     *   <li>王牌敌机（ACE）：35%</li>
     * </ul>
     * 与简单/普通模式相比，专家模式大幅提高了精锐和王牌敌机的出现概率。
     * </p>
     *
     * @return 随机生成的敌机类型 {@link EnemyType}
     */
    @Override
    protected EnemyType getRandomEnemyType() {
        double rand = Math.random();
        if (rand < 0.2)
            return EnemyType.MOB; // 20% 普通
        if (rand < 0.4)
            return EnemyType.ELITE; // 20% 精英
        if (rand < 0.65)
            return EnemyType.VETERAN; // 25% 精锐
        return EnemyType.ACE; // 35% 王牌
    }

    /**
     * 判断当前是否应该生成 BOSS 敌机。
     * <p>
     * 生成条件为：BOSS 尚未生成过、当前分数达到分数阈值、
     * 且场上不存在有效的 BOSS 敌机。
     * </p>
     *
     * @return 如果满足生成条件返回 {@code true}，否则返回 {@code false}
     */
    @Override
    protected boolean shouldSpawnBoss() {
        return !bossSpawned && score >= scoreThreshold && (bossEnemy == null || bossEnemy.notValid());
    }

    /**
     * 设置并返回当前 BOSS 敌机的血量。
     * <p>
     * 每次调用时 BOSS 血量在上一次的基础上增加 200，
     * 实现随着游戏进行 BOSS 越来越难被击败的效果。
     * </p>
     *
     * @return 本次 BOSS 敌机的血量值
     */
    @Override
    protected int setBossEnemyHp() {
        this.bossEnemyHp += 200;
        return this.bossEnemyHp;
    }

    /**
     * 处理敌机被击毁时的道具掉落逻辑。
     * <p>
     * 对于非 BOSS 敌机，按照当前的道具掉落概率（{@code propRand}）决定是否掉落道具；
     * 对于 BOSS 敌机，固定掉落三个道具（掉落概率强制为 100%），
     * 同时更新下一次 BOSS 出现的分数阈值并重置 BOSS 生成状态。
     * </p>
     *
     * @param enemyAircraft 被击毁的敌机对象，用于生成道具
     */
    @Override
    protected void triggerProp(AbstractAircraft enemyAircraft) {

        if (!(enemyAircraft instanceof BossEnemy)) {
            AbstractProp newProp = enemyAircraft.obtainProp(enemyAircraft,propRand);
            if (newProp != null) {
                props.add(newProp);
            }
        } else {
            for (int i = 0; i < 3; i++) {
                AbstractProp newProp = enemyAircraft.obtainProp(enemyAircraft,1.0);
                if (newProp != null) {
                    props.add(newProp);
                }
            }

            scoreThreshold = (int) (score * 1.5);
            bossSpawned = false;
            System.out.println("BOSS敌机被击毁 下次出现的分数阈值为：" + scoreThreshold);
        }

    }


    /**
     * 执行专家模式的难度升级逻辑。
     * <p>
     * 每次调用时逐步提升游戏难度，具体表现为：
     * <ul>
     *   <li>增加敌机最大数量（上限 20 个）</li>
     *   <li>缩短敌机生成周期（下限 8 帧）</li>
     *   <li>提高敌机血量和速度倍率（上限 2.5 倍）</li>
     *   <li>增加英雄机射击周期（上限 35 帧），降低射击频率</li>
     * </ul>
     * 同时将当前各项参数打印到控制台以便调试。
     * </p>
     */
    @Override
    protected void difficultyLevelUp() {
        this.enemyMaxNumber = Math.min(20, this.enemyMaxNumber + 1);
        this.enemySpawnCycle = Math.max(8, this.enemySpawnCycle * 0.9);
        this.enemyHpFactor = Math.min(2.5, this.enemyHpFactor * 1.1);
        this.enemySpeedFactor = Math.min(2.5, this.enemySpeedFactor * 1.1);
        this.heroShootCycle = Math.min(35,this.heroShootCycle + 2);
        System.out.println("==========提高难度===========");
        System.out.println(String.format("敌机最大数量 = %d 个",enemyMaxNumber));
        System.out.println(String.format("敌机产生周期 = %.2f 秒",(double) enemySpawnCycle * 50 / 1000));
        System.out.println(String.format("敌机血量增值 = %.2f 倍",enemyHpFactor));
        System.out.println(String.format("敌机速度增值 = %.2f 倍",enemySpeedFactor));
        System.out.println(String.format("英雄机子弹产生周期 = %.2f 秒",(double) enemyShootCycle * 50 / 1000));
        System.out.println("============================");
    }

}
