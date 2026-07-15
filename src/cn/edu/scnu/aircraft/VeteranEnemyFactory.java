package cn.edu.scnu.aircraft;

import cn.edu.scnu.shoot.StraightShoot;
import cn.edu.scnu.aircraft.factory.EnemyFactory;

/**
 * 精锐敌机工厂：负责创建具备左右移动能力的精锐敌机。
 * 此类实现了 {@link EnemyFactory} 接口，根据传入的难度系数（血量和速度倍率）初始化敌机属性，
 * 并随机决定敌机的水平移动方向。创建的敌机配备双排直射子弹射击策略，
 * 在向下移动的同时可按设定周期向下发射双排子弹。
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public class VeteranEnemyFactory implements EnemyFactory {
    private int hp;
    private int speedX;
    private int speedY;

    /**
     * 构造一个精锐敌机工厂，并根据难度系数初始化敌机参数。
     *
     * @param enemyHpFactor     血量倍率，用于计算精锐敌机的生命值
     * @param enemySpeedFactor  速度倍率，用于计算精锐敌机的水平和垂直移动速度
     */
    public VeteranEnemyFactory(double enemyHpFactor, double enemySpeedFactor) {
        setParams(enemyHpFactor, enemySpeedFactor);
    }

    /**
     * 在指定位置创建一架精锐敌机，其水平移动方向随机（向左或向右），
     * 并为其设置双排直射子弹的射击策略。
     *
     * @param x 敌机初始位置的 x 坐标
     * @param y 敌机初始位置的 y 坐标
     * @return 创建好的 {@link VeteranEnemy} 实例
     */
    @Override
    public AbstractAircraft createEnemy(int x, int y) {
        VeteranEnemy enemy = Math.random() > 0.5 ? new VeteranEnemy(x, y, speedX, speedY, hp)
                : new VeteranEnemy(x, y, -speedX, speedY, hp);
        enemy.setShootStrategy(new StraightShoot(2)); // 双排直射
        return enemy;
    }

    /**
     * 根据难度倍率更新精锐敌机的生命值、水平速度和垂直速度参数。
     *
     * @param enemyHpFactor     血量倍率，乘以基础血量得到最终生命值
     * @param enemySpeedFactor  速度倍率，乘以基础速度得到最终移动速度
     */
    @Override
    public void setParams(double enemyHpFactor, double enemySpeedFactor) {
        this.hp = (int) (EnemyType.VETERAN.getHp() * enemyHpFactor);
        this.speedX = (int) (EnemyType.VETERAN.getSpeedX() * enemySpeedFactor);
        this.speedY = (int) (EnemyType.VETERAN.getSpeedY() * enemySpeedFactor);
    }
}
