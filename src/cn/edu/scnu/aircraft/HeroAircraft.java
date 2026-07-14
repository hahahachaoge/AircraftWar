package cn.edu.scnu.aircraft;

import cn.edu.scnu.prop.AbstractProp;
import cn.edu.scnu.shoot.StraightShoot;

/**
 * 英雄飞机，游戏玩家操控
 * 
 * @author 黄彪骐
 */
public class HeroAircraft extends AbstractAircraft {
    // 私有静态实例
    private static volatile HeroAircraft instance = null;

    // 每次射击发射子弹数量
    private int shootNum = 3;

    // 构造器私有化 把构造权掌握在自己手中
    private HeroAircraft(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        this.power = 30;
        this.direction = -1;
        this.setShootStrategy(new StraightShoot(shootNum));
    }

    public static void reset() {
        instance = null;
    }

    // 提供全局访问点 双重锁定检查
    public static HeroAircraft getInstance(int locationX, int locationY, int speedX, int speedY, int hp) {
        if (instance == null) {
            synchronized (HeroAircraft.class) {
                if (instance == null) {
                    instance = new HeroAircraft(locationX, locationY, speedX, speedY, hp);
                }
            }
        }

        return instance;
    }

    // 重载方法
    public static HeroAircraft getInstance() {
        if (instance == null) {
            throw new IllegalStateException("HeroAircraft尚未初始化，请先调用getInstance(int, int, int, int, int)");
        }
        return instance;
    }

    @Override
    public void forward() {
        // 英雄机由鼠标控制，不通过forward函数移动
    }

    @Override
    public AbstractProp obtainProp(AbstractAircraft enemyAircraft, double rand) {
        return null;
    }

    @Override
    public void onBombActivated() {
        // System.out.println("炸弹道具生效 英雄机不受影响...");
    }

    @Override
    public void onFrozenActivated() {
        // System.out.println("冰冻道具生效 英雄机不受影响...");
    }
}
