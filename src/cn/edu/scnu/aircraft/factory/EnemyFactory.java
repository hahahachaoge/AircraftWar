package cn.edu.scnu.aircraft.factory;

import cn.edu.scnu.aircraft.AbstractAircraft;

/**
 * 敌机工厂接口 —— 定义创建敌机对象的工厂方法。
 *
 * <p>本接口采用工厂方法模式，所有具体敌机类型（如精英敌机、普通敌机等）的创建
 * 均通过实现此接口的工厂类来完成。外部调用方只需依赖此接口，无需关心具体敌机
 * 的实例化逻辑，从而实现了创建者与产品之间的解耦。</p>
 *
 * <p>游戏中通过 {@link #setParams(double, double)} 统一调节敌机的基础属性系数，
 * 再调用 {@link #createEnemy(int, int)} 生成指定位置的敌机实例。</p>
 *
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public interface EnemyFactory {
    /**
     * 创建一个敌机实例。
     *
     * @param x 敌机的初始 x 坐标（像素）
     * @param y 敌机的初始 y 坐标（像素）
     * @return 根据具体工厂实现生成的敌机对象，类型为 {@link AbstractAircraft}
     */
    AbstractAircraft createEnemy(int x, int y);

    /**
     * 设置敌机的基础属性系数。
     * <p>在游戏难度变化或关卡切换时调用，用于统一调整所有由该工厂产出的敌机
     * 的生命值与移动速度，从而实现难度动态缩放。</p>
     *
     * @param enemyHpFactor   敌机生命值倍率因子（1.0 表示基准值）
     * @param enemySpeedFactor 敌机移动速度倍率因子（1.0 表示基准值）
     */
    void setParams(double enemyHpFactor, double enemySpeedFactor);
}
