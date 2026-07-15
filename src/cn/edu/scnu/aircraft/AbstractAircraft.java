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
 * 所有种类飞机的抽象父类
 * 
 * @author 黄彪骐
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

    // 构造器
    public AbstractAircraft(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY);
        ObserverManager.getInstance().addObserver(this, PropType.BOMB);
        ObserverManager.getInstance().addObserver(this, PropType.FROZEN);
        this.hp = hp;
        this.maxHp = hp;
    }

    @Override
    public void vanish() {
        super.vanish();
        ObserverManager.getInstance().removeObserver(this, PropType.BOMB);
        ObserverManager.getInstance().removeObserver(this, PropType.FROZEN);
    }

    // 扣除血量
    public void decreaseHp(int decrease) {
        hp -= decrease;
        if (hp <= 0) {
            hp = 0;
            vanish(); // 标记消失
        }
    }

    // 获取血量
    public int getHp() {
        return hp;
    }

    // 获取最大血量
    public int getMaxHp() {
        return maxHp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public void setShootStrategy(ShootStrategy shootStrategy) {
        this.shootStrategy = shootStrategy;
    }

    // 使用策略模式射击
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
