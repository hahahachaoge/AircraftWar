package cn.edu.scnu.application.game;

import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import cn.edu.scnu.aircraft.AbstractAircraft;
import cn.edu.scnu.aircraft.EnemyType;
import cn.edu.scnu.application.ImageManager;
import cn.edu.scnu.prop.AbstractProp;
import cn.edu.scnu.rank.Difficulty;

/**
 * @author 黄彪骐
 */
public class BeginningGame extends AbstractGame {

    public BeginningGame() {
        super();
        this.difficulty = Difficulty.BEGINNER;
    }

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

    @Override
    protected void triggerProp(AbstractAircraft enemyAircraft) {
        AbstractProp newProp = enemyAircraft.obtainProp(enemyAircraft, Math.random());
        if (newProp != null) {
            props.add(newProp);
        }

    }

    @Override
    protected boolean shouldSpawnBoss() {
        return false;
    }

}
