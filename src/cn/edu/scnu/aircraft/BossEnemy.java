package cn.edu.scnu.aircraft;

import cn.edu.scnu.application.Main;
import cn.edu.scnu.prop.AbstractProp;
import cn.edu.scnu.prop.PropFactory;
import cn.edu.scnu.prop.PropType;

/**
 * BOSS敌机类，继承自 AbstractAircraft，代表游戏中的 BOSS 级敌机实体。
 * <p>
 * BOSS 敌机具有较高的攻击力（power = 5），沿 y 轴向下飞行，
 * 当飞出窗口底部时会自动消失。
 * 被击毁时按概率掉落各类道具（回血、火力增强、炸弹、冰冻等），
 * 并且对炸弹道具和冰冻道具效果免疫。
 * </p>
 *
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public class BossEnemy extends AbstractAircraft {

    /**
     * 构造 BOSS 敌机实例。
     *
     * @param locationX 初始横坐标
     * @param locationY 初始纵坐标
     * @param speedX    x 方向速度
     * @param speedY    y 方向速度
     * @param hp        初始生命值
     */
    public BossEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        this.power = 5;
        this.direction = 1;
    }

    /**
     * 向前移动一步（每帧调用一次）。
     * 调用父类的 forward() 执行移动逻辑，
     * 然后判定是否飞出窗口底部（y 轴），若是则令自身消失。
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
     * 使 BOSS 敌机消失。
     * 调用父类的 vanish() 执行销毁逻辑，
     * 并在控制台输出销毁时的剩余血量信息。
     */
    @Override
    public void vanish() {
        super.vanish();
        System.out.println("BOSS敌机消失了.. 血量为 " + this.hp);
    }

    /**
     * 获取 BOSS 敌机被击毁时掉落的道具。
     * 根据随机数按不同概率生成不同类型的道具：
     * <ul>
     *   <li>50% 概率掉落回血道具 (HP)</li>
     *   <li>20% 概率掉落火力道具 (FIRE)</li>
     *   <li>10% 概率掉落超级火力道具 (FIRE_PLUS)</li>
     *   <li>10% 概率掉落炸弹道具 (BOMB)</li>
     *   <li>10% 概率掉落冰冻道具 (FROZEN)</li>
     * </ul>
     *
     * @param enemyAircraft 被击毁的敌机对象，用于获取掉落位置
     * @param rand          随机种子（当前实现未使用）
     * @return 生成的道具对象，若未生成则返回 null
     */
    @Override
    public AbstractProp obtainProp(AbstractAircraft enemyAircraft, double rand) {
        int propX = enemyAircraft.getLocationX();
        int propY = enemyAircraft.getLocationY();
        AbstractProp newProp = null;
        double typeRandom = Math.random();
        if (typeRandom < 0.5) {
            newProp = PropFactory.createProp(PropType.HP, propX, propY);
        } else if (typeRandom < 0.7) {
            newProp = PropFactory.createProp(PropType.FIRE, propX, propY);
        } else if (typeRandom < 0.8) {
            newProp = PropFactory.createProp(PropType.FIRE_PLUS, propX, propY);
        } else if (typeRandom < 0.9) {
            newProp = PropFactory.createProp(PropType.BOMB, propX, propY);
        } else {
            newProp = PropFactory.createProp(PropType.FROZEN, propX, propY);
        }
        System.out.println("产生" + newProp.getClass() + " 横坐标：" + propX);

        return newProp;

    }

    /**
     * 炸弹道具生效时的回调。
     * BOSS 敌机对炸弹道具效果免疫，故本方法为空实现。
     */
    @Override
    public void onBombActivated() {
        // System.out.println("炸弹道具生效 BOSS敌机不受影响...");
    }

    /**
     * 冰冻道具生效时的回调。
     * BOSS 敌机对冰冻道具效果免疫，故本方法为空实现。
     */
    @Override
    public void onFrozenActivated() {
        // System.out.println("冰冻道具生效 BOSS敌机不受影响...");
    }
}
