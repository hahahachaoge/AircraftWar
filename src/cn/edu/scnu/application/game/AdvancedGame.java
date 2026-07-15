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
 * @author 黄彪骐
 */
public class AdvancedGame extends AbstractGame {

    public AdvancedGame(GameMode mode) {
        super(mode);
        this.difficulty = Difficulty.ADVANCED;
    }

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

    @Override
    protected boolean shouldSpawnBoss() {
        return !bossSpawned && score >= scoreThreshold && (bossEnemy == null || bossEnemy.notValid());
    }

    @Override
    protected int setBossEnemyHp() {
        this.bossEnemyHp += 100;
        return this.bossEnemyHp;
    }

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
