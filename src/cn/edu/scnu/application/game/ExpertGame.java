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
public class ExpertGame extends AbstractGame{

    public ExpertGame(GameMode mode) {
        super(mode);
        this.difficulty = Difficulty.EXPERT;
    }

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

    @Override
    protected boolean shouldSpawnBoss() {
        return !bossSpawned && score >= scoreThreshold && (bossEnemy == null || bossEnemy.notValid());
    }

    @Override
    protected int setBossEnemyHp() {
        this.bossEnemyHp += 200;
        return this.bossEnemyHp;
    }

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
