package cn.edu.scnu.aircraft.factory;

import cn.edu.scnu.aircraft.AbstractAircraft;
import cn.edu.scnu.aircraft.BossEnemy;
import cn.edu.scnu.aircraft.EnemyType;
import cn.edu.scnu.shoot.RingShoot;

/**
 * BOSS敌机工厂：在屏幕上方 + 左右移动 + 悬浮 + 环射（20个弹道）
 *
 * <p>该类实现了 {@link EnemyFactory} 接口，专门用于创建 BOSS 类型的敌机。
 * BOSS 敌机拥有较高的生命值，能够在屏幕上方左右移动并悬浮，
 * 同时使用环形射击策略发射 20 个弹道，是游戏中的高难度敌人。
 * 工厂通过敌人生命值系数和速度系数来动态调整 BOSS 敌机的属性。 </p>
 */
public class BossEnemyFactory implements EnemyFactory {
    private int hp;
    private int speedX;
    private int speedY;

    /**
     * 构造 BOSS 敌机工厂，使用指定的生命值系数和速度系数初始化 BOSS 属性。
     *
     * @param enemyHpFactor    敌人生命值倍率系数，用于缩放 BOSS 的初始生命值
     * @param enemySpeedFactor 敌人速度倍率系数，用于缩放 BOSS 的移动速度
     */
    public BossEnemyFactory(double enemyHpFactor, double enemySpeedFactor) {
        setParams(enemyHpFactor, enemySpeedFactor);
    }

    /**
     * 在指定坐标创建一个 BOSS 敌机实例，并为其设置环形射击策略。
     *
     * @param x 创建位置的 x 坐标（像素）
     * @param y 创建位置的 y 坐标（像素）
     * @return 配置完成的 BOSS 敌机实例
     */
    @Override
    public AbstractAircraft createEnemy(int x, int y) {
        BossEnemy enemy = new BossEnemy(x, y, speedX, speedY, hp);
        enemy.setShootStrategy(new RingShoot(20));
        return enemy;
    }

    /**
     * 根据游戏难度系数重新设置 BOSS 敌机的生命值和速度属性。
     *
     * <p>该方法从 {@link EnemyType#BOSS} 枚举中读取 BOSS 的基础属性，
     * 然后乘以对应的倍率系数以适应当前游戏难度。</p>
     *
     * @param enemyHpFactor    敌人生命值倍率系数
     * @param enemySpeedFactor 敌人速度倍率系数
     */
    @Override
    public void setParams(double enemyHpFactor, double enemySpeedFactor) {
        this.hp = (int) (EnemyType.BOSS.getHp() * enemyHpFactor);
        this.speedX = (int) (EnemyType.BOSS.getSpeedX() * enemySpeedFactor);
        this.speedY = (int) (EnemyType.BOSS.getSpeedY() * enemySpeedFactor);
    }
}
