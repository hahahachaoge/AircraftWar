package cn.edu.scnu.bullet;

/**
 * 英雄机子弹类，继承自 {@link BaseBullet}。
 * <p>
 * 该类的实例代表由英雄机发射的子弹，用于攻击敌机。
 * 子弹拥有位置、速度与攻击力属性，碰撞到敌机后将对其造成伤害。
 * 与敌机子弹不同，英雄机子弹通常向上飞行，且可能具有不同的外观与弹道逻辑。
 * </p>
 *
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public class HeroBullet extends BaseBullet {

    /**
     * 构造一个英雄机子弹实例。
     *
     * @param locationX 子弹初始位置的 X 坐标（像素）
     * @param locationY 子弹初始位置的 Y 坐标（像素）
     * @param speedX    子弹在 X 方向上的移动速度（像素/帧）
     * @param speedY    子弹在 Y 方向上的移动速度（像素/帧）
     * @param power     子弹对敌机造成的伤害值
     */
    public HeroBullet(int locationX, int locationY, int speedX, int speedY, int power) {
        super(locationX, locationY, speedX, speedY, power);
    }

}
