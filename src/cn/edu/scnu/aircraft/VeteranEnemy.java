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
 * 精锐敌机。
 * <p>
 * 本类继承自 {@link AbstractAircraft}，代表游戏中的精锐级别敌机实体。
 * 精锐敌机相比普通敌机拥有更高的攻击力（power = 5），并且在被冰冻道具命中后
 * 会进入短暂的减速状态（3秒内速度归零，随后恢复原速）。
 * 被炸弹道具命中时直接坠毁消失。
 * 精锐敌机被击毁时有一定概率掉落道具（包括回血、火力增强、超级火力、炸弹四种类型）。
 * </p>
 *
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public class VeteranEnemy extends AbstractAircraft {
    private int originalSpeedX;
    private int originalSpeedY;
    private volatile boolean isSlow = false;
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> recoveryTask;

    /**
     * 构造一个精锐敌机实例。
     *
     * @param locationX 初始 x 轴位置
     * @param locationY 初始 y 轴位置
     * @param speedX    x 轴方向移动速度
     * @param speedY    y 轴方向移动速度
     * @param hp        生命值
     */
    public VeteranEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        this.power = 5;
        this.direction = 1;
    }

    /**
     * 使精锐敌机消失。
     * <p>
     * 在敌机从游戏中消失之前，先释放内部定时器资源（关闭调度线程池），
     * 防止内存泄漏，然后调用父类的 vanish() 完成清理工作。
     * </p>
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
     * 向前移动一帧。
     * <p>
     * 调用父类的 forward() 执行移动逻辑，然后判定是否超出窗口底部边界。
     * 若 y 轴位置超出窗口高度，则使敌机消失。
     * </p>
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
     * 精锐敌机被击毁时尝试获取一个掉落道具。
     * <p>
     * 根据给定的随机阈值 rand 判定是否掉落道具（若 rand < Math.random() 则不掉落）。
     * 掉落道具时，道具类型由随机数决定：
     * <ul>
     *   <li>30% 概率掉落回血道具 (HP)</li>
     *   <li>30% 概率掉落火力增强道具 (FIRE)</li>
     *   <li>20% 概率掉落超级火力道具 (FIRE_PLUS)</li>
     *   <li>20% 概率掉落炸弹道具 (BOMB)</li>
     * </ul>
     * </p>
     *
     * @param enemyAircraft 被击毁的敌机对象，用于获取掉落位置
     * @param rand          掉落概率阈值，取值在 [0,1) 之间。若 rand 大于等于 Math.random() 则掉落道具
     * @return 若判定掉落则返回相应的道具实例，否则返回 null
     */
    @Override
    public AbstractProp obtainProp(AbstractAircraft enemyAircraft, double rand) {
        if (rand < Math.random()) {
            return null;
        }

        int propX = enemyAircraft.getLocationX();
        int propY = enemyAircraft.getLocationY();
        AbstractProp newProp = null;
        // 状态：rand >= Math.random()
        // eg. rand = 0.8 则产生道具的概率为 80%
        double typeRandom = Math.random();
        if (typeRandom < 0.3) {
            newProp = PropFactory.createProp(PropType.HP, propX, propY);
        } else if (typeRandom < 0.6) {
            newProp = PropFactory.createProp(PropType.FIRE, propX, propY);
        } else if (typeRandom < 0.8) {
            newProp = PropFactory.createProp(PropType.FIRE_PLUS, propX, propY);
        } else {
            newProp = PropFactory.createProp(PropType.BOMB, propX, propY);
        }

        return newProp;
    }

    /**
     * 响应炸弹道具生效的回调。
     * <p>
     * 当玩家使用炸弹道具时，所有存活的前进敌机都会收到此回调。
     * 精锐敌机被炸弹命中后直接坠毁消失。
     * </p>
     */
    @Override
    public void onBombActivated() {
        // System.out.println("炸弹道具生效 精锐敌机坠毁...");
        this.vanish();
    }

    /**
     * 响应冰冻道具生效的回调。
     * <p>
     * 当玩家使用冰冻道具时，精锐敌机会进入减速状态，速度和 ySpeed 均置为 0。
     * 若已处于减速状态，则取消上一次的恢复任务并重新计时；若尚未减速，则
     * 先保存当前速度值再置零。3秒后由定时器恢复原速度，退出减速状态。
     * </p>
     */
    @Override
    public void onFrozenActivated() {
        // System.out.println("炸弹道具生效 精锐敌机静止3s后恢复...");
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
        }, 3, TimeUnit.SECONDS);
    }

}
