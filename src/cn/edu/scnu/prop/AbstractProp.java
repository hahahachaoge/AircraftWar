package cn.edu.scnu.prop;

import cn.edu.scnu.aircraft.HeroAircraft;
import cn.edu.scnu.application.Main;
import cn.edu.scnu.application.game.AbstractGame;
import cn.edu.scnu.basic.AbstractFlyingObject;

/**
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public abstract class AbstractProp extends AbstractFlyingObject {

    public AbstractProp(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public void forward() {
        super.forward();
        // 检测是否移动出屏幕底部（道具只向下移动）
        if (locationY >= Main.WINDOW_HEIGHT) {
            vanish(); // 标记为无效
        }
    }

    /**
     * 道具生效的抽象方法
     * 
     * @param heroAircraft 英雄机实例，道具效果作用于此
     * @param game         游戏实例，用于访问游戏状态（如敌机列表、分数等）
     */
    public abstract void activate(HeroAircraft heroAircraft, AbstractGame game);
}
