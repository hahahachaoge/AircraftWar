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
 * 中级难度游戏模式。
 * <p>
 * 继承自 {@link AbstractGame}，实现了中级难度下的游戏逻辑。
 * 该模式在普通模式和困难模式之间取得平衡，提供了适中的挑战性：
 * <ul>
 *   <li>英雄机初始生命值为 1000，Boss 敌机生命值为 1000</li>
 *   <li>敌机生成周期和射击周期均为 15 帧</li>
 *   <li>敌机血量和速度分别以 1.2 倍系数增长</li>
 *   <li>道具掉落概率为 60%</li>
 *   <li>难度等级上升间隔为 1500 帧</li>
 *   <li>随机生成的敌机类型涵盖普通 (40%)、精英 (25%)、精锐 (20%)、王牌 (15%)</li>
 *   <li>击毁 Boss 后下次 Boss 出现分数阈值按当前分数 1.2 倍提升</li>
 * </ul>
 * </p>
 *
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public class IntermendtateGame extends AbstractGame {

    /**
     * 构造一个中级难度游戏实例。
     *
     * @param mode 游戏模式（通常为 {@link GameMode#INTERMEDIATE}）
     */
    public IntermendtateGame(GameMode mode) {
        super(mode);
        this.difficulty = Difficulty.INTERMEDIATE;
    }

    /**
     * 初始化中级难度下的游戏参数。
     * <p>
     * 设置背景图片、英雄机血量、Boss 血量、敌机数量上限、敌机生成/射击周期、
     * 敌机血量和速度系数、Boss 出现分数阈值、道具掉落概率以及难度升级间隔。
     * </p>
     */
    @Override
    protected void initGameSettings() {
        try {
            ImageManager.BACKGROUND_IMAGE = ImageIO.read(new FileInputStream("src/images/bg3.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.initHeroHp = 1000;
        this.bossEnemyHp = 1000;

        this.enemyMaxNumber = 10;
        this.enemySpawnCycle = 15;
        this.heroShootCycle = 15;
        this.enemyShootCycle = 15;
        this.enemyHpFactor = 1.2;
        this.enemySpeedFactor = 1.2;

        this.scoreThreshold = 1000;

        this.propRand = 0.6;
        this.difficultyLevelUpInterval = 1500;
    }

    /**
     * 根据概率随机生成敌机类型。
     * <p>
     * 概率分布：
     * <ul>
     *   <li>普通敌机 (MOB)：40%</li>
     *   <li>精英敌机 (ELITE)：25%</li>
     *   <li>精锐敌机 (VETERAN)：20%</li>
     *   <li>王牌敌机 (ACE)：15%</li>
     * </ul>
     * </p>
     *
     * @return 随机选取的 {@link EnemyType} 枚举值
     */
    @Override
    protected EnemyType getRandomEnemyType() {
        double rand = Math.random();
        if (rand < 0.4)
            return EnemyType.MOB; // 40% 普通
        if (rand < 0.65)
            return EnemyType.ELITE; // 25% 精英
        if (rand < 0.85)
            return EnemyType.VETERAN; // 20% 精锐
        return EnemyType.ACE; // 15% 王牌
    }

    /**
     * 判断是否应该生成 Boss 敌机。
     * <p>
     * 当满足以下所有条件时返回 {@code true}：
     * <ul>
     *   <li>Boss 尚未生成 ({@code bossSpawned == false})</li>
     *   <li>当前分数已达到或超过分数阈值 ({@code score >= scoreThreshold})</li>
     *   <li>当前场地上没有有效的 Boss ({@code bossEnemy == null || bossEnemy.notValid()})</li>
     * </ul>
     * </p>
     *
     * @return 如果应该生成 Boss 则返回 {@code true}，否则返回 {@code false}
     */
    @Override
    protected boolean shouldSpawnBoss() {
        return !bossSpawned && score >= scoreThreshold && (bossEnemy == null || bossEnemy.notValid());
    }

    /**
     * 返回 Boss 敌机的生命值。
     *
     * @return Boss 敌机生命值（固定为 1000）
     */
    @Override
    protected int setBossEnemyHp() {
        return this.bossEnemyHp;
    }

    /**
     * 触发敌机被击毁时的道具掉落逻辑。
     * <p>
     * 对于非 Boss 敌机，按 {@code propRand} 概率掉落一个道具；
     * 对于 Boss 敌机，必定掉落三个道具（概率 100%），
     * 同时重置 {@code bossSpawned} 状态并按照当前分数的 1.2 倍更新下次 Boss 出现的分数阈值。
     * </p>
     *
     * @param enemyAircraft 被击毁的敌机实例
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
            // System.out.println("BOSS敌机被击毁 下次出现的分数阈值为：" + scoreThreshold);
        }

    }

    /**
     * 提升游戏难度等级。
     * <p>
     * 每次调用时逐步增加难度参数：
     * <ul>
     *   <li>敌机最大数量增加 1，最多不超过 15</li>
     *   <li>敌机生成周期减少 10%，最低不低于 10 帧</li>
     *   <li>敌机血量系数增加 10%，最高不超过 2 倍</li>
     *   <li>敌机速度系数增加 10%，最高不超过 2 倍</li>
     * </ul>
     * 同时向控制台输出当前难度参数。
     * </p>
     */
    @Override
    protected void difficultyLevelUp() {
        this.enemyMaxNumber = Math.min(15, this.enemyMaxNumber + 1);
        this.enemySpawnCycle = Math.max(10, this.enemySpawnCycle * 0.9);
        this.enemyHpFactor = Math.min(2, this.enemyHpFactor * 1.1);
        this.enemySpeedFactor = Math.min(2, this.enemySpeedFactor * 1.1);
        System.out.println("==========提高难度===========");
        System.out.println(String.format("敌机最大数量 = %d 个", enemyMaxNumber));
        System.out.println(String.format("敌机产生周期 = %.2f 秒", (double) enemySpawnCycle * 50 / 1000));
        System.out.println(String.format("敌机血量增值 = %.2f 倍", enemyHpFactor));
        System.out.println(String.format("敌机速度增值 = %.2f 倍", enemySpeedFactor));
        System.out.println("============================");
    }
}
