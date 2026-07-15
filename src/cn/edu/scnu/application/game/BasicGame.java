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
 * 基础模式游戏类，继承自 {@link AbstractGame}。
 * <p>
 * 该类实现了游戏的基础难度模式（{@link Difficulty#BASIC}），负责初始化基础游戏参数、
 * 随机生成不同类型敌机（普通、精英、精锐、王牌）、控制 Boss 敌机的生成逻辑、
 * 处理敌机被击毁后道具的掉落，以及定期提升游戏难度。
 * </p>
 *
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public class BasicGame extends AbstractGame {

    /**
     * 构造一个基础模式游戏实例。
     *
     * @param mode 游戏模式，用于传递给父类初始化通用游戏设置
     */
    public BasicGame(GameMode mode) {
        super(mode);
        this.difficulty = Difficulty.BASIC;
    }

    /**
     * 初始化基础模式的游戏参数。
     * <p>
     * 加载背景图片，设置英雄机初始生命值、Boss 敌机生命值、敌机最大数量、
     * 敌机生成周期、射击周期、敌机血量和速度倍率、过关分数阈值、道具掉落概率
     * 以及难度提升间隔时间。
     * </p>
     */
    @Override
    protected void initGameSettings() {
        try {
            ImageManager.BACKGROUND_IMAGE = ImageIO.read(new FileInputStream("src/images/bg2.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.initHeroHp = 500;
        this.bossEnemyHp = 500;

        this.enemyMaxNumber = 8;
        this.enemySpawnCycle = 20;
        this.heroShootCycle = 20;
        this.enemyShootCycle = 20;
        this.enemyHpFactor = 1.0;
        this.enemySpeedFactor = 1.0;

        this.scoreThreshold = 100;

        this.propRand = 0.8;
        this.difficultyLevelUpInterval = 3000; // 每2分钟提高游戏难度
    }

    /**
     * 根据概率随机生成敌机类型。
     * <ul>
     *   <li>50% 概率生成普通敌机（{@link EnemyType#MOB}）</li>
     *   <li>25% 概率生成精英敌机（{@link EnemyType#ELITE}）</li>
     *   <li>15% 概率生成精锐敌机（{@link EnemyType#VETERAN}）</li>
     *   <li>10% 概率生成王牌敌机（{@link EnemyType#ACE}）</li>
     * </ul>
     *
     * @return 随机生成的敌机类型
     */
    @Override
    protected EnemyType getRandomEnemyType() {
        double rand = Math.random();
        if (rand < 0.5)
            return EnemyType.MOB; // 50% 普通
        if (rand < 0.75)
            return EnemyType.ELITE; // 25% 精英
        if (rand < 0.90)
            return EnemyType.VETERAN; // 15% 精锐
        return EnemyType.ACE; // 10% 王牌
    }

    /**
     * 判断是否应当生成 Boss 敌机。
     * <p>
     * 条件为：Boss 尚未生成过、当前分数达到或超过分数阈值、且当前没有存活的 Boss 敌机。
     * </p>
     *
     * @return 如果满足生成条件则返回 {@code true}，否则返回 {@code false}
     */
    @Override
    protected boolean shouldSpawnBoss() {
        return !bossSpawned && score >= scoreThreshold && (bossEnemy == null || bossEnemy.notValid());
    }

    /**
     * 处理敌机被击毁时的道具掉落逻辑。
     * <p>
     * 如果被击毁的不是 Boss 敌机，则以预设概率 {@code propRand} 尝试掉落一个道具；
     * 如果是 Boss 敌机被击毁，则必定掉落 3 个道具，并更新分数阈值及重置 Boss 生成状态。
     * </p>
     *
     * @param enemyAircraft 被击毁的敌机实例
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
            // System.out.println("BOSS敌机被击毁 下次出现的分数阈值为：" + scoreThreshold);
        }

    }

    /**
     * 提高游戏难度。
     * <p>
     * 每经过 {@code difficultyLevelUpInterval} 帧后调用一次。效果包括：
     * 增加敌机最大数量、缩短敌机生成周期、提高敌机血量和速度倍率。
     * 各参数均有上限（最大数量不超过 15、生成周期不低于 10 帧、倍率不超过 2 倍），
     * 并在控制台输出当前难度信息。
     * </p>
     */
    @Override
    protected void difficultyLevelUp() {
        this.enemyMaxNumber = Math.min(15, this.enemyMaxNumber + 1);
        this.enemySpawnCycle = Math.max(10, this.enemySpawnCycle * 0.9);
        this.enemyHpFactor = Math.min(2, this.enemyHpFactor * 1.1);
        this.enemySpeedFactor = Math.min(2, this.enemySpeedFactor * 1.1);
        System.out.println("==========提高难度===========");
        System.out.println(String.format("敌机最大数量 = %d 个",enemyMaxNumber));
        System.out.println(String.format("敌机产生周期 = %.2f 秒",(double) enemySpawnCycle * 50 / 1000));
        System.out.println(String.format("敌机血量增值 = %.2f 倍",enemyHpFactor));
        System.out.println(String.format("敌机速度增值 = %.2f 倍",enemySpeedFactor));
        System.out.println("============================");
    }

}
