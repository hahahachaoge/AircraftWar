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
 * 精英敌机类，继承自 AbstractAircraft。
 * 精英敌机是游戏中具有特殊能力的敌方单位，其特点包括：
 * <ul>
 *   <li>拥有较高的攻击力（power = 5）</li>
 *   <li>被冰冻道具命中后会进入减速状态，速度降为零并在4秒后恢复</li>
 *   <li>被炸弹道具命中后会立即坠毁消失</li>
 *   <li>被击落后有一定概率掉落道具（血量、火力或超级火力之一）</li>
 * </ul>
 * 该类使用 ScheduledExecutorService 实现减速恢复的定时任务管理。
 *
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public class EliteEnemy extends AbstractAircraft {
    /** 减速前的原始水平速度 */
    private int originalSpeedX;
    /** 减速前的原始垂直速度 */
    private int originalSpeedY;
    /** 是否处于减速状态 */
    private volatile boolean isSlow = false;
    /** 用于管理减速恢复定时任务的线程池 */
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    /** 减速恢复任务的 Future 引用，用于取消或检查状态 */
    private ScheduledFuture<?> recoveryTask;

    /**
     * 构造一个精英敌机实例。
     *
     * @param locationX 初始位置的 x 坐标（像素）
     * @param locationY 初始位置的 y 坐标（像素）
     * @param speedX    水平速度（像素/帧），正数向右
     * @param speedY    垂直速度（像素/帧），正数向下
     * @param hp        生命值
     */
    public EliteEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        this.power = 5;
        this.direction = 1;
    }

    /**
     * 使精英敌机消失。
     * 在调用父类的 vanish 方法之前，先关闭定时器线程池以释放资源，
     * 避免内存泄漏或任务被意外执行。
     */
    @Override
    public void vanish() {
        // 在敌机消失前 先要释放定时器资源
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
        super.vanish();
    }

    /**
     * 更新精英敌机的位置并检查是否出界。
     * 调用父类的 forward 方法移动位置，然后判断敌机是否已经飞出窗口底部，
     * 若是则调用 vanish 将其移除。
     */
    @Override
    public void forward() {
        super.forward();
        // 判定 y 轴向下飞行出界
        if (locationY >= Main.WINDOW_HEIGHT) {
            vanish();
        }
    }

    /**
     * 精英敌机被击落后生成掉落道具。
     * 先根据随机概率判断是否掉落道具（概率为 1 - rand），
     * 若掉落则从三种道具中随机选择一种：血量道具（33%）、
     * 火力道具（33%）或超级火力道具（34%）。
     *
     * @param enemyAircraft 被击落的敌机实例，用于获取掉落位置
     * @param rand          掉落概率阈值（0~1），大于 Math.random() 时才掉落
     * @return 生成的道具对象，若未掉落则返回 null
     */
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

    /**
     * 炸弹道具生效时的回调方法。
     * 当玩家使用炸弹道具时，所有精英敌机立即坠毁消失。
     */
    @Override
    public void onBombActivated() {
        // System.out.println("炸弹道具生效 精英敌机坠毁...");
        this.vanish();
    }

    /**
     * 冰冻道具生效时的回调方法。
     * 精英敌机进入减速状态，速度降为零，并在4秒后恢复原始速度。
     * <ul>
     *   <li>如果已经处于减速状态且尚未恢复，则取消上一次的恢复任务并重新计时</li>
     *   <li>如果未处于减速状态，则记录原始速度并设置为减速状态</li>
     * </ul>
     * 使用 ScheduledExecutorService 调度延迟任务，在4秒后将速度恢复为原始值。
     */
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
