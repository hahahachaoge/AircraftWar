package cn.edu.scnu.shoot;

import java.util.LinkedList;
import java.util.List;

import cn.edu.scnu.bullet.BaseBullet;
import cn.edu.scnu.bullet.EnemyBullet;
import cn.edu.scnu.bullet.HeroBullet;

/**
 * 环形射击策略实现，沿圆周均匀分布方向发射多枚子弹，形成环形弹幕效果。
 * <p>
 * 本策略根据指定的子弹数量，将子弹均匀分布在 360 度（向上射击时）或 180 度
 * （向下射击时）的弧度范围内。每个子弹沿各自的计算方向飞行，适用于范围覆盖
 * 或弹幕压制场景。
 * </p>
 *
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public class RingShoot implements ShootStrategy {

    /**
     * 每轮射击发射的子弹数量。
     */
    private int bulletNum = 20;

    /**
     * 构造一个环形射击策略实例。
     *
     * @param bulletNum 每轮射击发射的子弹数量，均匀分布在圆周上
     */
    public RingShoot(int bulletNum) {
        this.bulletNum = bulletNum;
    }

    /**
     * 执行环形射击，生成均匀分布在圆周上的子弹列表。
     * <p>
     * 当方向为向上（direction = -1，英雄机射击）时，子弹在完整 360 度范围内均匀分布；
     * 当方向为向下（direction = 1，敌机射击）时，子弹在半圆 180 度范围内均匀分布。
     * 每个子弹的速度由基础速度沿对应角度分解为 x、y 分量。
     * </p>
     *
     * @param shooterX 射击者的 X 坐标（像素）
     * @param shooterY 射击者的 Y 坐标（像素）
     * @param speedY   射击者在 Y 方向的基础速度大小（像素/帧），用于计算子弹速度
     * @param direction 射击方向，-1 表示向上（英雄机），1 表示向下（敌机）
     * @param power    子弹的攻击威力值
     * @return 包含所有生成子弹的列表，不会返回 {@code null}
     */
    @Override
    public List<BaseBullet> shoot(int shooterX, int shooterY, int speedY, int direction, int power) {
        List<BaseBullet> res = new LinkedList<>();
        double speed = speedY + direction * 5;

        for (int i = 0; i < bulletNum; i++) {
            // 向上射击时覆盖 360°，向下射击时覆盖 180°
            double angle = direction == -1 ? 2 * Math.PI * i / bulletNum : Math.PI * i / bulletNum;

            int bulletSpeedX = (int) (speed * Math.cos(angle));
            int bulletSpeedY = (int) (speed * Math.sin(angle));

            BaseBullet bullet = direction == -1 ? new HeroBullet(shooterX, shooterY, bulletSpeedX, bulletSpeedY, power)
                    : new EnemyBullet(shooterX, shooterY, bulletSpeedX, bulletSpeedY, power);

            res.add(bullet);
        }

        return res;
    }

}
