package cn.edu.scnu.aircraft;

import cn.edu.scnu.bullet.BaseBullet;
import cn.edu.scnu.prop.GetProp;
import cn.edu.scnu.prop.PropType;
import cn.edu.scnu.prop.observer.EnemyObserver;
import cn.edu.scnu.prop.observer.ObserverManager;
import cn.edu.scnu.shoot.ShootStrategy;
import cn.edu.scnu.basic.AbstractFlyingObject;

import java.util.LinkedList;
import java.util.List;

/**
 * 所有种类飞机的抽象父类。
 * <p>
 * 该类定义了游戏中所有飞机（包括敌机与玩家战机）的通用属性和行为，
 * 包括生命值管理、射击策略、射击方向与子弹威力等核心机制。
 * 继承自 {@link AbstractFlyingObject}，同时实现了 {@link GetProp} 和 {@link EnemyObserver} 接口，
 * 具备获取道具以及作为观察者响应炸弹、冰冻等道具事件的能力。
 * 射击行为采用策略模式，通过 {@link ShootStrategy} 实现不同的射击方式。
 * </p>
 *
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public abstract class AbstractAircraft extends AbstractFlyingObject implements GetProp, EnemyObserver {

    // 最大生命值
    protected int maxHp;
    protected int hp;

    // 射击策略模式
    protected ShootStrategy shootStrategy;

    // 射击方向（1向下，-1向上）
    protected int direction = 1;

    // 子弹威力
    protected int power = 10;

    /**
     * 构造器，初始化飞机的位置、速度与生命值，
     * 并向观察者管理器注册炸弹和冰冻两种道具事件的监听。
     *
     * @param locationX 初始位置的 X 坐标
     * @param locationY 初始位置的 Y 坐标
     * @param speedX    X 方向的速度分量
     * @param speedY    Y 方向的速度分量
     * @param hp        初始生命值
     */
    public AbstractAircraft(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY);
        ObserverManager.getInstance().addObserver(this, PropType.BOMB);
        ObserverManager.getInstance().addObserver(this, PropType.FROZEN);
        this.hp = hp;
        this.maxHp = hp;
    }

    /**
     * 使飞机消失，并在消失前从观察者管理器中移除炸弹和冰冻事件的注册。
     */
    @Override
    public void vanish() {
        super.vanish();
        ObserverManager.getInstance().removeObserver(this, PropType.BOMB);
        ObserverManager.getInstance().removeObserver(this, PropType.FROZEN);
    }

    /**
     * 扣除一定量的生命值，若生命值降至零或以下则触发消失。
     *
     * @param decrease 扣除的生命值量
     */
    public void decreaseHp(int decrease) {
        hp -= decrease;
        if (hp <= 0) {
            hp = 0;
            vanish(); // 标记消失
        }
    }

    /**
     * 获取当前生命值。
     *
     * @return 当前生命值
     */
    public int getHp() {
        return hp;
    }

    /**
     * 获取最大生命值。
     *
     * @return 最大生命值
     */
    public int getMaxHp() {
        return maxHp;
    }

    /**
     * 设置当前生命值。
     *
     * @param hp 新的生命值
     */
    public void setHp(int hp) {
        this.hp = hp;
    }

    /**
     * 设置射击策略，用于实现不同的射击模式（策略模式）。
     *
     * @param shootStrategy 射击策略对象
     */
    public void setShootStrategy(ShootStrategy shootStrategy) {
        this.shootStrategy = shootStrategy;
    }

    /**
     * 使用当前设置的射击策略进行射击，返回本次发射的所有子弹对象。
     * 若未设置射击策略，则返回空列表。
     *
     * @return 本次发射的子弹列表，若未设置策略则为空列表
     */
    public List<BaseBullet> shoot() {
        if (shootStrategy != null) {
            return shootStrategy.shoot(
                    this.getLocationX(),
                    this.getLocationY(),
                    this.getSpeedY(), // 传入子弹速度
                    this.direction, // 传入方向
                    this.power // 传入威力
            );
        }
        return new LinkedList<>();
    }

}
