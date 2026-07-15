package cn.edu.scnu.manager;

/**
 * 游戏难度枚举，定义所有可选的难度级别。
 * <p>
 * 该枚举用于控制游戏的整体挑战程度，包括敌机生成频率、敌机属性（血量、速度、火力）、
 * Boss 出场条件以及道具掉落概率等参数。每一档难度由具体的游戏策略类
 * （如 BeginnerGame、BasicGame、IntermediateGame、AdvancedGame、ExpertGame）实现其逻辑。
 * </p>
 * <p>
 * 难度分级由易到难依次为：
 * <ul>
 *   <li>{@link #BEGINNER}       — 新手难度，敌机少且弱，适合初次体验；</li>
 *   <li>{@link #BASIC}          — 基础难度，稍有挑战；</li>
 *   <li>{@link #INTERMEDIATE}   — 中等难度，敌机强度与数量明显提升；</li>
 *   <li>{@link #ADVANCED}       — 高级难度，需要较熟练的操作；</li>
 *   <li>{@link #EXPERT}         — 专家难度，最高挑战，敌机密集且属性极高。</li>
 * </ul>
 * </p>
 *
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public enum Difficulty {
    BEGINNER, BASIC, INTERMEDIATE, ADVANCED, EXPERT
}
