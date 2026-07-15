package cn.edu.scnu.bullet;

import cn.edu.scnu.application.Main;
import cn.edu.scnu.basic.AbstractFlyingObject;

/**
 * 子弹基类
 * @author 黄彪骐
 */
public abstract class BaseBullet extends AbstractFlyingObject {

    private int power = 0;

    public BaseBullet(int locationX, int locationY, int speedX, int speedY, int power) {
        super(locationX, locationY, speedX, speedY);
        this.power = power;
    }

    @Override
    public void forward() {
        super.forward();

        // 判定 x 轴出界
        if (locationX <= 0 || locationX >= Main.WINDOW_WIDTH) {
            vanish();
        }

        // 判定 y 轴出界
        if (speedY > 0 && locationY >= Main.WINDOW_HEIGHT ) {
            // 向下飞行出界
            vanish();
        }else if (locationY <= 0){
            // 向上飞行出界
            vanish();
        }
    }
    // 用于计算在碰撞检测中减少的生命值
    public int getPower() {
        return power;
    }
}
