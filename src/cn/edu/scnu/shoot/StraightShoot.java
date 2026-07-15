package cn.edu.scnu.shoot;

import java.util.LinkedList;
import java.util.List;

import cn.edu.scnu.bullet.BaseBullet;
import cn.edu.scnu.bullet.EnemyBullet;
import cn.edu.scnu.bullet.HeroBullet;

/**
 * 直射模式：通过bulletNum来实现单排直射，双排直射。。。
 * @author 黄彪骐
 */
public class StraightShoot implements ShootStrategy {

    private int bulletNum = 0;


    public StraightShoot(int bulletNum) {
        this.bulletNum = bulletNum;
    }


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
