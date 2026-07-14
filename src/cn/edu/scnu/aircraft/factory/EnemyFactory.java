package cn.edu.scnu.aircraft.factory;

import cn.edu.scnu.aircraft.AbstractAircraft;

// 抽象敌机工厂
/**
 * @author 黄彪骐
 */
public interface EnemyFactory {
    AbstractAircraft createEnemy(int x, int y);
    void setParams(double enemyHpFactor, double enemySpeedFactor);
}
