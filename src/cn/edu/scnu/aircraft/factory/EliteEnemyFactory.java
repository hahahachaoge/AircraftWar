package cn.edu.scnu.aircraft.factory;

import cn.edu.scnu.aircraft.AbstractAircraft;
import cn.edu.scnu.aircraft.EliteEnemy;
import cn.edu.scnu.aircraft.EnemyType;
import cn.edu.scnu.shoot.StraightShoot;

/**
 * 精英敌机工厂，实现 EnemyFactory 接口。
 * <p>
 * 负责创建精英敌机（EliteEnemy）实例，并为敌机装配单排直射射击策略。
 * 精英敌机的初始属性（生命值、水平速度、垂直速度）由构造时传入的倍率因子
 * 结合 EnemyType.ELITE 的基础属性计算得出，支持在游戏运行时通过 setParams
 * 方法动态调整难度参数。
 * </p>
 *
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public class EliteEnemyFactory implements EnemyFactory {
    private int hp;
    private int speedX;
    private int speedY;

    /**
     * 构造精英敌机工厂，根据难度倍率因子初始化敌机属性。
     *
     * @param enemyHpFactor    生命值倍率因子（基于 EnemyType.ELITE 基础血量相乘）
     * @param enemySpeedFactor 速度倍率因子（基于 EnemyType.ELITE 基础速度相乘）
     */
    public EliteEnemyFactory(double enemyHpFactor, double enemySpeedFactor) {
        setParams(enemyHpFactor, enemySpeedFactor);
    }

    /**
     * 在指定坐标处创建一架精英敌机，并为其装配单排直射射击策略。
     *
     * @param x 生成位置的 x 坐标（像素）
     * @param y 生成位置的 y 坐标（像素）
     * @return 已配置好射击策略的 EliteEnemy 实例
     */
    @Override
    public AbstractAircraft createEnemy(int x, int y) {
        EliteEnemy enemy = new EliteEnemy(x, y, speedX, speedY, hp);
        enemy.setShootStrategy(new StraightShoot(1)); // 单排直射
        return enemy;
    }

    /**
     * 根据难度倍率因子重新计算精英敌机的属性参数。
     * <p>
     * 该方法将倍率因子与 EnemyType.ELITE 的基础属性相乘，
     * 得到实际的生命值、水平速度和垂直速度，供后续创建的敌机使用。
     * </p>
     *
     * @param enemyHpFactor    生命值倍率因子
     * @param enemySpeedFactor 速度倍率因子
     */
    @Override
    public void setParams(double enemyHpFactor, double enemySpeedFactor) {
        this.hp = (int) (EnemyType.ELITE.getHp() * enemyHpFactor);
        this.speedX = (int) (EnemyType.ELITE.getSpeedX() * enemySpeedFactor);
        this.speedY = (int) (EnemyType.ELITE.getSpeedY() * enemySpeedFactor);
    }
}
