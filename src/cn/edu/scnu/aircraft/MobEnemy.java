package cn.edu.scnu.aircraft;

import cn.edu.scnu.application.Main;
import cn.edu.scnu.bullet.BaseBullet;
import cn.edu.scnu.prop.AbstractProp;
import java.util.LinkedList;
import java.util.List;

/**
 * 普通敌机类，继承自 {@link AbstractAircraft}。
 * <p>
 * 该类表示游戏中最基础的敌机类型，具有以下特点：
 * <ul>
 *   <li>不可射击——{@link #shoot()} 始终返回空列表</li>
 *   <li>不掉落道具——{@link #obtainProp(AbstractAircraft, double)} 始终返回 {@code null}</li>
 *   <li>沿 Y 轴向下飞行，飞出窗口边界后自动消失</li>
 *   <li>响应炸弹道具 ({@link #onBombActivated()}) 和冰冻道具 ({@link #onFrozenActivated()}) 的效果</li>
 * </ul>
 * 作为游戏中最常见的敌方单位，普通敌机的主要威胁来自其数量而非攻击能力。
 * </p>
 *
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public class MobEnemy extends AbstractAircraft {

    /**
     * 构造一个普通敌机实例。
     *
     * @param locationX 初始 X 轴坐标（像素）
     * @param locationY 初始 Y 轴坐标（像素）
     * @param speedX    X 轴方向移动速度（像素/帧），可正可负
     * @param speedY    Y 轴方向移动速度（像素/帧），通常为正值表示向下
     * @param hp        初始生命值
     */
    public MobEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
    }

    /**
     * 控制普通敌机向前移动（每帧调用一次）。
     * <p>
     * 调用父类的 {@code forward()} 方法更新位置，然后判定是否已飞出窗口下边界。
     * 若 Y 坐标超出 {@link Main#WINDOW_HEIGHT}，则调用 {@link #vanish()} 将该敌机标记为消失。
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
     * 普通敌机无法射击，始终返回空列表。
     *
     * @return 空列表 {@link LinkedList}（表示无子弹射出）
     */
    @Override
    public List<BaseBullet> shoot() {
        return new LinkedList<>();
    }

    /**
     * 普通敌机被击毁时不会掉落任何道具，始终返回 {@code null}。
     *
     * @param enemyAircraft 被击毁的敌机实例
     * @param rand          随机判定值（0.0 ~ 1.0），本实现未使用
     * @return {@code null}（表示无道具掉落）
     */
    @Override
    public AbstractProp obtainProp(AbstractAircraft enemyAircraft, double rand) {
        return null;
    }

    /**
     * 炸弹道具生效时的回调。
     * <p>
     * 当玩家使用炸弹道具时，所有普通敌机直接坠毁。调用 {@link #vanish()} 令自身消失，
     * 并由游戏主逻辑根据敌机类型给予玩家对应分数。
     * </p>
     */
    @Override
    public void onBombActivated() {
        // System.out.println("炸弹道具生效 普通敌机坠毁...");
        this.vanish();
    }

    /**
     * 冰冻道具生效时的回调。
     * <p>
     * 当玩家使用冰冻道具时，普通敌机的速度被置为零，永久静止在原位。
     * </p>
     */
    @Override
    public void onFrozenActivated() {
        // System.out.println("冰冻道具生效 普通敌机永久静止...");
        this.speedX = 0;
        this.speedY = 0;
    }
}
