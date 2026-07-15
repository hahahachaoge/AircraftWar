package cn.edu.scnu.manager;

/**
 * 道具类型常量类，定义了游戏中所有可用道具的类型标识。
 *
 * <p>该类仅包含静态整型常量，每个常量代表一种道具类型：
 * <ul>
 *   <li>{@link #HP} - 生命恢复道具</li>
 *   <li>{@link #FIRE} - 火力升级道具</li>
 *   <li>{@link #FIRE_PLUS} - 超级火力道具</li>
 *   <li>{@link #BOMB} - 炸弹道具（清屏）</li>
 *   <li>{@link #FROZEN} - 冰冻道具（减缓敌机速度）</li>
 * </ul>
 * </p>
 *
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public class PropType {
    /** 生命恢复道具 */
    public static final int HP = 0;
    /** 火力升级道具 */
    public static final int FIRE = 1;
    /** 超级火力道具 */
    public static final int FIRE_PLUS = 2;
    /** 炸弹道具（清屏） */
    public static final int BOMB = 3;
    /** 冰冻道具（减缓敌机速度） */
    public static final int FROZEN = 4;
}
