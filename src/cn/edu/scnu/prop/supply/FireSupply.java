package cn.edu.scnu.prop.supply;

import cn.edu.scnu.aircraft.HeroAircraft;
import cn.edu.scnu.application.game.AbstractGame;
import cn.edu.scnu.prop.AbstractProp;
import cn.edu.scnu.prop.PropEffectTimer;
import cn.edu.scnu.shoot.ScatterShoot;

/**
 * 火焰道具，拾取后英雄机将切换为散射射击模式，
 * 并启动道具效果计时器，在计时结束后恢复原有射击策略。
 *
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public class FireSupply extends AbstractProp{

    public FireSupply(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public void activate(HeroAircraft heroAircraft, AbstractGame game) {
        // 道具生效
        heroAircraft.setShootStrategy(new ScatterShoot(5));
        // 道具定时恢复
        PropEffectTimer timer = new PropEffectTimer(2, heroAircraft);
        game.setFireTimer(timer);
        new Thread(timer).start();
        // System.out.println("FireSupply active!");
    }

}
