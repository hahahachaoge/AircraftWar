package cn.edu.scnu.shoot;

import java.util.List;

import cn.edu.scnu.bullet.BaseBullet;

/**
 * 射击策略接口 —— 策略模式（Strategy Pattern）
 * <p>
 * 定义统一的射击行为契约，所有具体的射击策略（如单发、散射、扇形弹幕等）
 * 均需实现此接口。通过策略模式，游戏中的不同敌机或英雄可以动态切换射击
 * 方式，而无需修改其自身逻辑，提高了系统的灵活性与可扩展性。
 * </p>
 *
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public interface ShootStrategy {

    /**
     * 执行射击，根据传入的射击者位置、速度、方向及威力参数生成子弹列表。
     *
     * @param shooterX 射击者的 X 坐标（像素）
     * @param shooterY 射击者的 Y 坐标（像素）
     * @param speedY   射击者的 Y 方向速度（像素/帧），可用于计算子弹的初始速度偏移
     * @param direction 射击方向：1 表示向下（敌机射击），-1 表示向上（英雄射击）
     * @param power    单发子弹的基础威力值
     * @return 本次射击生成的子弹列表；若无法射击（如冷却中）可返回空列表，不应返回 {@code null}
     */
    List<BaseBullet> shoot(int shooterX, int shooterY, int speedY, int direction, int power);
}
