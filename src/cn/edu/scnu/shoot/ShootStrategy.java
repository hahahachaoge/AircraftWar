package cn.edu.scnu.shoot;

import java.util.List;

import cn.edu.scnu.bullet.BaseBullet;
/***
 * 射击策略模式接口
 * @author 黄彪骐
 */
public interface ShootStrategy {
 /**
     * 执行射击
     * @param shooterX 射击者的X坐标
     * @param shooterY 射击者的Y坐标
     * @param speedY 射击者的Y速度
     * @param direction 射击方向（1向下，-1向上）
     * @param power 子弹威力
     * @return 生成的子弹列表
     */
    List<BaseBullet> shoot(int shooterX, int shooterY,int speedY ,int direction, int power);
}
