package cn.edu.scnu.prop;

/**
 * 道具类型枚举，定义了游戏中所有可拾取的道具种类。
 * <p>
 * 每种道具对应不同的游戏效果：
 * <ul>
 *   <li>{@link #HP} - 恢复生命值</li>
 *   <li>{@link #FIRE} - 提升普通火力</li>
 *   <li>{@link #FIRE_PLUS} - 提升超级火力</li>
 *   <li>{@link #BOMB} - 全屏炸弹清场</li>
 *   <li>{@link #FROZEN} - 冻结敌人行动</li>
 * </ul>
 * </p>
 *
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public enum PropType {
    /** 恢复生命值道具 */
    HP,
    /** 普通火力提升道具 */
    FIRE,
    /** 超级火力提升道具 */
    FIRE_PLUS,
    /** 全屏炸弹道具，可清除场上所有敌人 */
    BOMB,
    /** 冰冻道具，可冻结敌人使其无法行动 */
    FROZEN
}
