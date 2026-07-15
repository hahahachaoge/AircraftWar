package cn.edu.scnu.shoot;

import java.util.LinkedList;
import java.util.List;

import cn.edu.scnu.bullet.BaseBullet;
import cn.edu.scnu.bullet.EnemyBullet;
import cn.edu.scnu.bullet.HeroBullet;

/**
 * 散射模式
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public class ScatterShoot implements ShootStrategy {
    private int bulletNum = 3; // 子弹数量
    
    public ScatterShoot(int bulletNum) {
        this.bulletNum = bulletNum;
    }

    @Override
    public List<BaseBullet> shoot(int shooterX, int shooterY, int speedY, int direction, int power) {
                List<BaseBullet> res = new LinkedList<>();
        // 散射参数
        int spreadAngle = 2; // 散射角度
        int baseSpeedY = speedY + direction * 5; // 基础垂直速度

        for (int i = 0; i < bulletNum; i++) {
            int offsetX = (i - bulletNum / 2) * spreadAngle;

            BaseBullet bullet = direction == -1 ? new HeroBullet(shooterX, shooterY, offsetX, baseSpeedY, power)
                    : new EnemyBullet(shooterX, shooterY, offsetX, baseSpeedY, power);

            res.add(bullet);
        }

        return res;
    }

}
