package cn.edu.scnu.shoot;

import java.util.LinkedList;
import java.util.List;

import cn.edu.scnu.bullet.BaseBullet;
import cn.edu.scnu.bullet.EnemyBullet;
import cn.edu.scnu.bullet.HeroBullet;

/**
 * @author 黄彪骐
 */
public class RingShoot implements ShootStrategy {

    private int bulletNum = 20;

    public RingShoot(int bulletNum) {
        this.bulletNum = bulletNum;
    }

    @Override
    public List<BaseBullet> shoot(int shooterX, int shooterY, int speedY, int direction, int power) {
        List<BaseBullet> res = new LinkedList<>();
        double speed = speedY + direction * 5;

        for (int i = 0; i < bulletNum; i++) {
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
