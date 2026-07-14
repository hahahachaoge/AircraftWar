package cn.edu.scnu.aircraft.factory;

import cn.edu.scnu.aircraft.*;
import cn.edu.scnu.shoot.ScatterShoot;

/**
 * 王牌敌机工厂：机向屏幕下方移动 + 可左右移动 + 扇形散射弹道（单次同时发射 3 颗子弹）
 * @author 黄彪骐
 */
public class AceEnemyFactory implements EnemyFactory {
    private int hp;
    private int speedX;
    private int speedY;

    public AceEnemyFactory(double enemyHpFactor, double enemySpeedFactor) {
        setParams(enemyHpFactor, enemySpeedFactor);
    }

    @Override
    public AbstractAircraft createEnemy(int x, int y) {
        AceEnemy enemy = Math.random() > 0.5 ? new AceEnemy(x, y, speedX, speedY, hp)
                : new AceEnemy(x, y, -speedX, speedY, hp);
        enemy.setShootStrategy(new ScatterShoot(3)); // 散射
        return enemy;
    }

    @Override
    public void setParams(double enemyHpFactor, double enemySpeedFactor) {
        this.hp = (int) (EnemyType.ACE.getHp() * enemyHpFactor);
        this.speedX = (int) (EnemyType.ACE.getSpeedX() * enemySpeedFactor);
        this.speedY = (int) (EnemyType.ACE.getSpeedY() * enemySpeedFactor);
    }
}