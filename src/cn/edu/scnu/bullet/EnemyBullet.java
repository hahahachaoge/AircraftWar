package cn.edu.scnu.bullet;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import cn.edu.scnu.prop.PropType;
import cn.edu.scnu.prop.observer.EnemyObserver;
import cn.edu.scnu.prop.observer.ObserverManager;

/**
 * 敌机子弹
 * 
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public class EnemyBullet extends BaseBullet implements EnemyObserver {
    private int originalSpeedX;
    private int originalSpeedY;
    private volatile boolean isSlow = false;
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> recoveryTask;

    public EnemyBullet(int locationX, int locationY, int speedX, int speedY, int power) {
        super(locationX, locationY, speedX, speedY, power);
        ObserverManager.getInstance().addObserver(this, PropType.BOMB);
        ObserverManager.getInstance().addObserver(this, PropType.FROZEN);
    }

    @Override
    public void vanish() {
        // 在敌机消失前 先要释放定时器资源
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
        ObserverManager.getInstance().removeObserver(this, PropType.BOMB);
        ObserverManager.getInstance().removeObserver(this, PropType.FROZEN);
        super.vanish();
    }

    @Override
    public void onBombActivated() {
        // * System.out.println("炸弹道具生效 敌机子弹消失...");
        this.vanish();
    }

    @Override
    public void onFrozenActivated() {
        // * System.out.println("冰冻道具生效 敌机子弹静止5s后恢复...");
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
            // 状态：已经延迟5秒了 可以恢复原速度了
            this.speedX = originalSpeedX;
            this.speedY = originalSpeedY;
            isSlow = false;
        }, 5, TimeUnit.SECONDS);
    }

}
