package cn.edu.scnu.aircraft.factory;

import cn.edu.scnu.aircraft.*;
import cn.edu.scnu.shoot.ScatterShoot;

/**
 * 王牌敌机工厂类，实现了 {@link EnemyFactory} 接口。
 * <p>
 * 该类负责创建具有以下特性的王牌敌机：
 * <ul>
 *   <li>以固定速度向屏幕下方移动（Y 轴方向）</li>
 *   <li>具备左右水平移动能力（X 轴方向，随机取正或负速度）</li>
 *   <li>使用扇形散射弹道，单次同时发射 3 颗子弹</li>
 * </ul>
 * 血量与速度由外部传入的 {@code enemyHpFactor} 和 {@code enemySpeedFactor} 因子动态计算。
 * </p>
 *
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public class AceEnemyFactory implements EnemyFactory {
    /** 王牌敌机的生命值 */
    private int hp;
    /** 王牌敌机在 X 轴方向上的移动速度（水平方向） */
    private int speedX;
    /** 王牌敌机在 Y 轴方向上的移动速度（向下） */
    private int speedY;

    /**
     * 构造一个王牌敌机工厂，并使用指定的难度因子初始化敌机参数。
     *
     * @param enemyHpFactor    敌机血量倍率因子（基于基础血量的乘数）
     * @param enemySpeedFactor 敌机速度倍率因子（基于基础速度的乘数）
     */
    public AceEnemyFactory(double enemyHpFactor, double enemySpeedFactor) {
        setParams(enemyHpFactor, enemySpeedFactor);
    }

    /**
     * 在指定坐标位置创建一架王牌敌机。
     * <p>
     * 敌机的水平移动方向随机决定：以 50% 概率向右（{@code +speedX}），
     * 50% 概率向左（{@code -speedX}）。
     * 创建后会为其设置扇形散射射击策略（每次发射 3 颗子弹）。
     * </p>
     *
     * @param x 敌机出生的 X 轴坐标
     * @param y 敌机出生的 Y 轴坐标
     * @return 生成的王牌敌机实例
     */
    @Override
    public AbstractAircraft createEnemy(int x, int y) {
        AceEnemy enemy = Math.random() > 0.5 ? new AceEnemy(x, y, speedX, speedY, hp)
                : new AceEnemy(x, y, -speedX, speedY, hp);
        enemy.setShootStrategy(new ScatterShoot(3)); // 散射
        return enemy;
    }

    /**
     * 根据难度因子设置王牌敌机的血量、水平速度和垂直速度。
     * <p>
     * 各属性值由 {@link EnemyType#ACE} 中定义的对应基础值乘以对应的因子得到。
     * </p>
     *
     * @param enemyHpFactor    敌机血量倍率因子
     * @param enemySpeedFactor 敌机速度倍率因子
     */
    @Override
    public void setParams(double enemyHpFactor, double enemySpeedFactor) {
        this.hp = (int) (EnemyType.ACE.getHp() * enemyHpFactor);
        this.speedX = (int) (EnemyType.ACE.getSpeedX() * enemySpeedFactor);
        this.speedY = (int) (EnemyType.ACE.getSpeedY() * enemySpeedFactor);
    }
}
