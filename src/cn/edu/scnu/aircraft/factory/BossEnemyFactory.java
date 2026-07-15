package cn.edu.scnu.aircraft.factory;

import cn.edu.scnu.aircraft.AbstractAircraft;
import cn.edu.scnu.aircraft.BossEnemy;
import cn.edu.scnu.aircraft.EnemyType;
import cn.edu.scnu.shoot.RingShoot;

/**
 * BOSS敌机工厂：在屏幕上方 + 左右移动 + 悬浮 + 环射（20个弹道）
 */
public class BossEnemyFactory implements EnemyFactory {
    private int hp;
    private int speedX;
    private int speedY;

    public BossEnemyFactory(double enemyHpFactor, double enemySpeedFactor) {
        setParams(enemyHpFactor, enemySpeedFactor);
    }

    @Override
    public AbstractAircraft createEnemy(int x, int y) {
        BossEnemy enemy = new BossEnemy(x, y, speedX, speedY, hp);
        enemy.setShootStrategy(new RingShoot(20));
        return enemy;
    }

    @Override
    public void setParams(double enemyHpFactor, double enemySpeedFactor) {
        this.hp = (int) (EnemyType.BOSS.getHp() * enemyHpFactor);
        this.speedX = (int) (EnemyType.BOSS.getSpeedX() * enemySpeedFactor);
        this.speedY = (int) (EnemyType.BOSS.getSpeedY() * enemySpeedFactor);
    }
}