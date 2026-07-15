package cn.edu.scnu.shoot;

import java.util.LinkedList;
import java.util.List;

import cn.edu.scnu.bullet.BaseBullet;
import cn.edu.scnu.bullet.EnemyBullet;
import cn.edu.scnu.bullet.HeroBullet;

/**
 * 直射射击策略，通过 bulletNum 控制子弹数量，
 * 实现单排直射、双排直射等多排平行弹幕效果。
 * 子弹沿垂直方向匀速飞行，水平方向上均匀分布在 shooter 周围。
 *
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public class StraightShoot implements ShootStrategy {

    private int bulletNum = 0;

    /**
     * 构造一个直射策略实例。
     *
     * @param bulletNum 每轮射击发射的子弹数量（1 = 单排，2 = 双排，依此类推）
     */
    public StraightShoot(int bulletNum) {
        this.bulletNum = bulletNum;
    }

    /**
     * 执行一轮直射，生成一排水平排列的子弹。
     * 子弹的横坐标以 shooterX 为中心均匀分布，纵坐标固定为 shooterY，
     * 速度为 baseSpeedY（在 speedY 基础上叠加方向偏移）。
     *
     * @param shooterX  射击者（飞机）的横坐标
     * @param shooterY  射击者（飞机）的纵坐标
     * @param speedY    射击者当前的垂直速度
     * @param direction 射击方向：-1 表示向上（英雄机），1 表示向下（敌机）
     * @param power     子弹攻击力
     * @return 包含本轮所有子弹的列表
     */
    @Override
    public List<BaseBullet> shoot(int shooterX, int shooterY, int speedY, int direction, int power) {
        List<BaseBullet> res = new LinkedList<>();
        int baseSpeedY = speedY + direction * 5;

        for (int i = 0; i < bulletNum; i++) {

            int locationX = shooterX + (i * 2 + 1 - bulletNum) * 10;

            BaseBullet bullet = direction == -1 ? new HeroBullet(locationX, shooterY, 0, baseSpeedY, power)
                    : new EnemyBullet(locationX, shooterY, 0, baseSpeedY, power);

            res.add(bullet);
        }
        return res;
    }
}
