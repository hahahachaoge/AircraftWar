package cn.edu.scnu.aircraft;

import cn.edu.scnu.application.Main;
import cn.edu.scnu.bullet.BaseBullet;
import cn.edu.scnu.prop.AbstractProp;
import java.util.LinkedList;
import java.util.List;

/**
 * 普通敌机
 * 不可射击、不掉落道具
 * 
 * @author 黄彪骐
 */
public class MobEnemy extends AbstractAircraft {

    public MobEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
    }

    @Override
    public void forward() {
        super.forward();
        // 判定 y 轴向下飞行出界
        if (locationY >= Main.WINDOW_HEIGHT) {
            vanish();
        }
    }

    @Override
    public List<BaseBullet> shoot() {
        return new LinkedList<>();
    }

    @Override
    public AbstractProp obtainProp(AbstractAircraft enemyAircraft, double rand) {
        return null;
    }

    /**
     * 炸弹道具生效 普通敌机坠毁 获得对应分数
     */
    @Override
    public void onBombActivated() {
        // System.out.println("炸弹道具生效 普通敌机坠毁...");
        this.vanish();
    }

    @Override
    public void onFrozenActivated() {
        // System.out.println("冰冻道具生效 普通敌机永久静止...");
        this.speedX = 0;
        this.speedY = 0;
    }
}
