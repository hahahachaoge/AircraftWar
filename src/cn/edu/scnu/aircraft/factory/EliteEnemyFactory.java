package cn.edu.scnu.aircraft.factory;

import cn.edu.scnu.aircraft.AbstractAircraft;
import cn.edu.scnu.aircraft.EliteEnemy;
import cn.edu.scnu.aircraft.EnemyType;
import cn.edu.scnu.shoot.StraightShoot;

/**
 * 精英敌机工厂：单排直射 + 下方移动
 * @author 黄彪骐
 */
public class EliteEnemyFactory implements EnemyFactory {
    private int hp;
    private int speedX;
    private int speedY;

    public EliteEnemyFactory(double enemyHpFactor, double enemySpeedFactor) {
        setParams(enemyHpFactor, enemySpeedFactor);
    }

    @Override
    public AbstractAircraft createEnemy(int x, int y) {
        EliteEnemy enemy = new EliteEnemy(x, y, speedX, speedY, hp);
        enemy.setShootStrategy(new StraightShoot(1)); // 单排直射
        return enemy;
    }

    @Override
    public void setParams(double enemyHpFactor, double enemySpeedFactor) {
        this.hp = (int) (EnemyType.ELITE.getHp() * enemyHpFactor);
        this.speedX = (int) (EnemyType.ELITE.getSpeedX() * enemySpeedFactor);
        this.speedY = (int) (EnemyType.ELITE.getSpeedY() * enemySpeedFactor);
    }
}