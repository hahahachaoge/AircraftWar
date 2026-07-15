package cn.edu.scnu.prop.supply;

import cn.edu.scnu.aircraft.HeroAircraft;
import cn.edu.scnu.application.game.AbstractGame;
import cn.edu.scnu.prop.AbstractProp;
import cn.edu.scnu.prop.PropEffectTimer;
import cn.edu.scnu.shoot.RingShoot;

/**
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public class FirePlusSupply extends AbstractProp {

    public FirePlusSupply(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public void activate(HeroAircraft heroAircraft, AbstractGame game) {
        // 道具生效
        heroAircraft.setShootStrategy(new RingShoot(20));
        // 道具定时恢复
        PropEffectTimer timer = new PropEffectTimer(5, heroAircraft);
        game.setFireTimer(timer);
        new Thread(timer).start();
        // System.out.println("FirePlusSupply active!");
    }

}
