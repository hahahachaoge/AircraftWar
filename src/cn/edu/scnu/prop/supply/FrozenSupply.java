package cn.edu.scnu.prop.supply;

import cn.edu.scnu.aircraft.HeroAircraft;
import cn.edu.scnu.application.game.AbstractGame;
import cn.edu.scnu.prop.AbstractProp;
import cn.edu.scnu.prop.PropType;
import cn.edu.scnu.prop.observer.ObserverManager;


/**
 * 冰冻道具，激活后通知观察者触发冰冻效果，使敌方单位暂时无法行动。
 *
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public class FrozenSupply extends AbstractProp {
    public FrozenSupply(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public void activate(HeroAircraft heroAircraft, AbstractGame game) {
        System.out.println("冰冻道具");
        ObserverManager.getInstance().notifyObservers(PropType.FROZEN);
    }


}
