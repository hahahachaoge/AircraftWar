package cn.edu.scnu.prop.supply;

import cn.edu.scnu.aircraft.HeroAircraft;
import cn.edu.scnu.application.game.AbstractGame;
import cn.edu.scnu.prop.AbstractProp;

/**
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public class HpSupply extends AbstractProp {
    private int recoverHp = 20; // 恢复的生命值

    public HpSupply(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public void activate(HeroAircraft heroAircraft, AbstractGame game) {
        int curHp = heroAircraft.getHp();
        int maxHp = heroAircraft.getMaxHp();
        heroAircraft.setHp(Math.min(maxHp, curHp + recoverHp));
    }



}
