package cn.edu.scnu.aircraft.factory;

import cn.edu.scnu.aircraft.AbstractAircraft;
import cn.edu.scnu.aircraft.EnemyType;
import cn.edu.scnu.aircraft.MobEnemy;

/**
 * 普通敌机工厂，实现了 {@link EnemyFactory} 接口。
 * 负责创建游戏中的普通敌机（{@link MobEnemy}）实例。
 * 该类根据传入的生命值与速度倍率因子，结合 {@link EnemyType#MOB} 的基础属性，
 * 计算并设置普通敌机的生命值、水平速度和垂直速度。
 * 该工厂生成的敌机仅向下方移动，不可左右移动，且不发射子弹。
 *
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public class MobEnemyFactory implements EnemyFactory {
    private int hp;
    private int speedX;
    private int speedY;

    /**
     * 构造一个普通敌机工厂实例。
     *
     * @param enemyHpFactor    生命值倍率因子，用于乘以 {@link EnemyType#MOB} 的基础生命值
     * @param enemySpeedFactor 速度倍率因子，用于乘以 {@link EnemyType#MOB} 的基础速度
     */
    public MobEnemyFactory(double enemyHpFactor, double enemySpeedFactor) {
        setParams(enemyHpFactor, enemySpeedFactor);
    }

    /**
     * 在指定坐标处创建一个普通敌机对象。
     *
     * @param x 敌机生成的横坐标（像素）
     * @param y 敌机生成的纵坐标（像素）
     * @return 新创建的 {@link MobEnemy} 实例，其属性由当前工厂参数决定
     */
    @Override
    public AbstractAircraft createEnemy(int x, int y) {
        return new MobEnemy(x, y, speedX, speedY, hp);
    }

    /**
     * 设置普通敌机的参数，根据倍率因子乘以 {@link EnemyType#MOB} 的基础属性值。
     *
     * @param enemyHpFactor    生命值倍率因子
     * @param enemySpeedFactor 速度倍率因子
     */
    @Override
    public void setParams(double enemyHpFactor, double enemySpeedFactor) {
        this.hp = (int) (EnemyType.MOB.getHp() * enemyHpFactor);
        this.speedX = (int) (EnemyType.MOB.getSpeedX() * enemySpeedFactor);
        this.speedY = (int) (EnemyType.MOB.getSpeedY() * enemySpeedFactor);
    }
}
