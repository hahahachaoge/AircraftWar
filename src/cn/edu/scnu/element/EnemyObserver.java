package cn.edu.scnu.element;

/**
 * 观察者接口，实现该接口的敌机可在炸弹或冰冻道具被激活时做出响应。
 *
 * <p>本接口定义了两种道具效果的响应方法：</p>
 * <ul>
 *   <li>{@link #onBombActivated()} —— 炸弹生效时调用，敌机通常在此方法中执行销毁逻辑</li>
 *   <li>{@link #onFrozenActivated()} —— 冰冻生效时调用，敌机通常在此方法中进入冻结状态（暂停移动）</li>
 * </ul>
 *
 * <p>该接口是对老师框架的扩展，通过观察者模式将道具事件与敌机行为解耦。</p>
 *
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public interface EnemyObserver {
    /**
     * 当炸弹道具被激活时调用。
     *
     * <p>实现类应当在此方法中执行敌机被炸弹波及后的逻辑，
     * 例如扣减生命值、触发爆炸特效或直接销毁自身。</p>
     */
    void onBombActivated();

    /**
     * 当冰冻道具被激活时调用。
     *
     * <p>实现类应当在此方法中使敌机进入冻结状态，
     * 例如暂停移动或降低攻击频率，待冻结时间结束后恢复正常行为。</p>
     */
    void onFrozenActivated();
}
