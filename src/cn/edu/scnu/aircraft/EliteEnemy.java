package cn.edu.scnu.aircraft;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import cn.edu.scnu.application.Main;
import cn.edu.scnu.prop.AbstractProp;
import cn.edu.scnu.prop.PropFactory;
import cn.edu.scnu.prop.PropType;

/**
 * @author 黄彪骐
 */
public class EliteEnemy extends AbstractAircraft {
    private int originalSpeedX;
    private int originalSpeedY;
    private volatile boolean isSlow = false;
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> recoveryTask;

    public EliteEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        this.power = 5;
        this.direction = 1;
    }

    @Override
    public void vanish() {
        // 在敌机消失前 先要释放定时器资源
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
        super.vanish();
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
    public AbstractProp obtainProp(AbstractAircraft enemyAircraft, double rand) {
        if (rand < Math.random()) {
            return null;
        }
        int propX = enemyAircraft.getLocationX();
        int propY = enemyAircraft.getLocationY();
        AbstractProp newProp = null;
        double typeRandom = Math.random();

        if (typeRandom < 0.33) {
            newProp = PropFactory.createProp(PropType.HP, propX, propY);
        } else if (typeRandom < 0.66) {
            newProp = PropFactory.createProp(PropType.FIRE, propX, propY);
        } else {
            newProp = PropFactory.createProp(PropType.FIRE_PLUS, propX, propY);
        }

        return newProp;
    }

    @Override
    public void onBombActivated() {
        // System.out.println("炸弹道具生效 精英敌机坠毁...");
        this.vanish();
    }

    @Override
    public void onFrozenActivated() {
        // System.out.println("冰冻道具生效 精英敌机静止4s后恢复...");
        if (isSlow) {
            // 状态：已经处于减速状态
            // 执行：取消上一次的恢复任务 刷新定时结束时间
            if (recoveryTask != null && !recoveryTask.isDone()) {
                // 状态：已经处于减速状态 而且还没有恢复
                recoveryTask.cancel(false);
            }
        } else {
            // 状态：未处于减速状态
            // 执行：保留原始速度 确认进入减速状态
            originalSpeedX = this.speedX;
            originalSpeedY = this.speedY;
            isSlow = true;
        }

        this.speedX = 0;
        this.speedY = 0;

        recoveryTask = scheduler.schedule(() -> {
            // 状态：已经延迟3秒了 可以恢复原速度了
            this.speedX = originalSpeedX;
            this.speedY = originalSpeedY;
            isSlow = false;
        }, 4, TimeUnit.SECONDS);
    }

}
