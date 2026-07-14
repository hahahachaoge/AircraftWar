package cn.edu.scnu.prop.supply;

import cn.edu.scnu.aircraft.AceEnemy;
import cn.edu.scnu.aircraft.EliteEnemy;
import cn.edu.scnu.aircraft.EnemyType;
import cn.edu.scnu.aircraft.HeroAircraft;
import cn.edu.scnu.aircraft.MobEnemy;
import cn.edu.scnu.aircraft.VeteranEnemy;
import cn.edu.scnu.application.game.AbstractGame;
import cn.edu.scnu.prop.AbstractProp;
import cn.edu.scnu.prop.PropType;
import cn.edu.scnu.prop.observer.EnemyObserver;
import cn.edu.scnu.prop.observer.ObserverManager;

/**
 * 爆炸道具：
    普通敌机 坠毁
    精英敌机 坠毀
    精锐敌机 坠毀
    王牌敌机 掉血
    Boss敌机 不受影响
    敌机子弹 消失
 * @author 黄彪骐
 */
public class BombSupply extends AbstractProp {

    public BombSupply(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public void activate(HeroAircraft heroAircraft, AbstractGame game) {
        // System.out.println("爆炸道具..");
        ObserverManager observerManager = ObserverManager.getInstance();
        // System.out.println(game.getScore());

        // 爆炸道具的加分规则
        for(EnemyObserver enemyObserver : observerManager.getObservers(PropType.BOMB)){
            if(enemyObserver instanceof MobEnemy){
                game.addScore(EnemyType.MOB.getScore());
            }else if(enemyObserver instanceof EliteEnemy){
                game.addScore(EnemyType.ELITE.getScore());
            }else if(enemyObserver instanceof VeteranEnemy){
                game.addScore(EnemyType.VETERAN.getScore());
            }else if(enemyObserver instanceof AceEnemy){
                if(((AceEnemy)enemyObserver).getHp() - 10 <= 0){
                    game.addScore(EnemyType.ACE.getScore());
                }
            }
        }
        // System.out.println(game.getScore());
        observerManager.notifyObservers(PropType.BOMB);
    }

}
