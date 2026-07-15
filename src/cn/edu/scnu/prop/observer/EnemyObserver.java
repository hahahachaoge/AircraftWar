package cn.edu.scnu.prop.observer;

/**
 * 敌人观察者接口，定义了敌人对道具效果（炸弹、冰冻）的响应行为。
 * 实现了观察者模式，当道具被使用时通知所有注册的敌人观察者。
 *
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public interface EnemyObserver  {
    void onBombActivated();
    void onFrozenActivated();
}
