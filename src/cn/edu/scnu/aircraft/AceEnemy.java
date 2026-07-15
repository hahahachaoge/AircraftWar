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
 * 王牌敌机类，继承自 {@link AbstractAircraft}。
 * <p>
 * 该类表示游戏中的精英/王牌敌机，具有较高的攻击力（power = 5），
 * 被击毁后可随机掉落多种道具（生命恢复、火力增强、超级火力、炸弹、冰冻），
 * 并且支持冰冻道具效果：被冰冻后速度减半，3 秒后自动恢复原始速度。
 * </p>
 *
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public class AceEnemy extends AbstractAircraft {
    private int originalSpeedX;
    private int originalSpeedY;
    private volatile boolean isSlow = false;
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> recoveryTask;

    /**
     * 构造方法，创建一个王牌敌机实例。
     *
     * @param locationX 初始 x 坐标
     * @param locationY 初始 y 坐标
     * @param speedX    x 方向速度
     * @param speedY    y 方向速度
     * @param hp        初始生命值
     */
    public AceEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        this.power = 5;
        this.direction = 1;
    }

    /**
     * 重写父类的 vanish 方法，在敌机消失前先释放定时器资源（关闭调度线程池），
     * 再调用父类的 vanish 完成最终的消失逻辑。
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
     * 重写父类的 forward 方法，先调用父类的移动逻辑，
     * 然后判定敌机 y 轴是否飞出窗口下边界，若是则使其消失。
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
     * 重写父类的 obtainProp 方法，根据随机数决定是否掉落道具以及掉落哪种道具。
     * <p>
     * 掉落概率分布：
     * <ul>
     *   <li>30% — 生命恢复 (HP)</li>
     *   <li>30% — 火力增强 (FIRE)</li>
     *   <li>20% — 超级火力 (FIRE_PLUS)</li>
     *   <li>10% — 炸弹 (BOMB)</li>
     *   <li>10% — 冰冻 (FROZEN)</li>
     * </ul>
     * </p>
     *
     * @param enemyAircraft 被击毁的敌机对象，用于获取掉落位置
     * @param rand          掉落判定随机数，若小于 {@code Math.random()} 则不掉落
     * @return 生成的道具对象，若不掉落则返回 {@code null}
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

        if (typeRandom < 0.3) {
            newProp = PropFactory.createProp(PropType.HP, propX, propY);
        } else if (typeRandom < 0.6) {
            newProp = PropFactory.createProp(PropType.FIRE, propX, propY);
        } else if (typeRandom < 0.8) {
            newProp = PropFactory.createProp(PropType.FIRE_PLUS, propX, propY);
        } else if (typeRandom < 0.9) {
            newProp = PropFactory.createProp(PropType.BOMB, propX, propY);
        } else {
            newProp = PropFactory.createProp(PropType.FROZEN, propX, propY);
        }
        return newProp;
    }

    /**
     * 重写父类的 onBombActivated 方法，响应炸弹道具效果，生命值减少 10 点。
     */
    @Override
    public void onBombActivated() {
        // System.out.println("炸弹道具生效 王牌敌机掉血...");
        decreaseHp(10);
    }

    /**
     * 重写父类的 onFrozenActivated 方法，响应冰冻道具效果。
     * <p>
     * 将敌机速度减半，并启动一个 3 秒的定时任务恢复原始速度。
     * 如果在减速状态下再次被冰冻，则取消上一次的恢复任务并重新计时，
     * 从而实现冰冻效果的刷新。
     * </p>
     */
    @Override
    public void onFrozenActivated() {
        // System.out.println("冰冻道具生效 王牌敌机减速3s后恢复...");
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

        this.speedX = originalSpeedX / 2;
        this.speedY = originalSpeedY / 2;

        recoveryTask = scheduler.schedule(() -> {
            // 状态：已经延迟3秒了 可以恢复原速度了
            this.speedX = originalSpeedX;
            this.speedY = originalSpeedY;
            isSlow = false;
        }, 3, TimeUnit.SECONDS);
    }
}
