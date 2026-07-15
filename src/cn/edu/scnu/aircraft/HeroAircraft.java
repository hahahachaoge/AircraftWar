package cn.edu.scnu.aircraft;

import cn.edu.scnu.prop.AbstractProp;
import cn.edu.scnu.shoot.StraightShoot;

/**
 * 英雄飞机类，代表游戏中由玩家操控的飞机。
 * <p>
 * 该类采用单例模式（双重锁定检查）确保全局只有一个英雄机实例，
 * 同时提供公开构造器以支持双人模式创建多个实例。
 * 英雄机通过鼠标控制移动，不通过 {@link #forward()} 方法驱动。
 * 英雄机不受炸弹道具和冰冻道具的影响，自身被击毁也不会掉落道具。
 * </p>
 *
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public class HeroAircraft extends AbstractAircraft {
    /** 私有静态单例实例（volatile 保证可见性） */
    private static volatile HeroAircraft instance = null;

    /** 每次射击发射的子弹数量 */
    private int shootNum = 3;

    /**
     * 构造器，创建英雄飞机实例。
     * <p>
     * 构造器访问权限为 public，支持双人模式创建多个 {@code HeroAircraft} 实例。
     * 初始化攻击力为 30、方向为 -1（向上），并设置默认射击策略为直线射击。
     * </p>
     *
     * @param locationX 初始 X 坐标（像素）
     * @param locationY 初始 Y 坐标（像素）
     * @param speedX    X 方向速度分量
     * @param speedY    Y 方向速度分量
     * @param hp        初始生命值
     */
    public HeroAircraft(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        this.power = 30;
        this.direction = -1;
        this.setShootStrategy(new StraightShoot(shootNum));
    }

    /**
     * 重置单例实例为 {@code null}。
     * <p>
     * 通常用于游戏重新开始时清除旧实例，以便下次调用 {@link #getInstance(int, int, int, int, int)}
     * 时创建新的英雄机对象。
     * </p>
     */
    public static void reset() {
        instance = null;
    }

    /**
     * 获取英雄飞机单例实例（带参数版本）。
     * <p>
     * 采用双重锁定检查（Double-Checked Locking）保证线程安全，
     * 仅在 {@code instance} 为 {@code null} 时使用同步块创建新实例。
     * </p>
     *
     * @param locationX 初始 X 坐标（像素）
     * @param locationY 初始 Y 坐标（像素）
     * @param speedX    X 方向速度分量
     * @param speedY    Y 方向速度分量
     * @param hp        初始生命值
     * @return 英雄飞机单例实例
     */
    public static HeroAircraft getInstance(int locationX, int locationY, int speedX, int speedY, int hp) {
        if (instance == null) {
            synchronized (HeroAircraft.class) {
                if (instance == null) {
                    instance = new HeroAircraft(locationX, locationY, speedX, speedY, hp);
                }
            }
        }

        return instance;
    }

    /**
     * 获取英雄飞机单例实例（无参数重载版本）。
     * <p>
     * 若 {@code instance} 尚未初始化（即为 {@code null}），
     * 则抛出 {@link IllegalStateException}，提示应先调用有参版本的
     * {@link #getInstance(int, int, int, int, int)} 进行初始化。
     * </p>
     *
     * @return 英雄飞机单例实例
     * @throws IllegalStateException 如果英雄机尚未初始化
     */
    public static HeroAircraft getInstance() {
        if (instance == null) {
            throw new IllegalStateException("HeroAircraft尚未初始化，请先调用getInstance(int, int, int, int, int)");
        }
        return instance;
    }

    /**
     * 执行前向移动（空实现）。
     * <p>
     * 英雄机由鼠标控制移动，不通过此方法驱动，
     * 因此方法体为空，不做任何操作。
     * </p>
     */
    @Override
    public void forward() {
        // 英雄机由鼠标控制，不通过forward函数移动
    }

    /**
     * 获取击败敌方飞机后掉落道具（英雄机不掉落道具）。
     *
     * @param enemyAircraft 被击败的敌方飞机
     * @param rand          随机概率值
     * @return 始终返回 {@code null}，表示不掉落任何道具
     */
    @Override
    public AbstractProp obtainProp(AbstractAircraft enemyAircraft, double rand) {
        return null;
    }

    /**
     * 炸弹道具生效时的回调方法（英雄机不受影响）。
     * <p>
     * 当游戏中使用炸弹道具时，此方法会被调用，
     * 但英雄机不受炸弹效果影响，故不执行任何操作。
     * </p>
     */
    @Override
    public void onBombActivated() {
        // 炸弹道具生效 英雄机不受影响...
    }

    /**
     * 冰冻道具生效时的回调方法（英雄机不受影响）。
     * <p>
     * 当游戏中使用冰冻道具时，此方法会被调用，
     * 但英雄机不受冰冻效果影响，故不执行任何操作。
     * </p>
     */
    @Override
    public void onFrozenActivated() {
        // 冰冻道具生效 英雄机不受影响...
    }
}
