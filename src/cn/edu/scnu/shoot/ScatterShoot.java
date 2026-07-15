package cn.edu.scnu.shoot;

import java.util.LinkedList;
import java.util.List;

import cn.edu.scnu.bullet.BaseBullet;
import cn.edu.scnu.bullet.EnemyBullet;
import cn.edu.scnu.bullet.HeroBullet;

/**
 * 散射射击策略，向正前方发射多颗子弹，子弹在水平方向上错开一定偏移量，
 * 形成扇形散射效果。适用于需要覆盖较大横向范围的射击场景。
 *
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public class ScatterShoot implements ShootStrategy {
    private int bulletNum = 3; // 子弹数量

    /**
     * 构造一个散射射击策略。
     *
     * @param bulletNum 每次射击发射的子弹数量
     */
    public ScatterShoot(int bulletNum) {
        this.bulletNum = bulletNum;
    }

    /**
     * 执行散射射击，根据子弹数量在水平方向上均匀偏移子弹位置，
     * 使其呈现出扇形散射效果。
     *
     * @param shooterX  射击者的 X 坐标
     * @param shooterY  射击者的 Y 坐标
     * @param speedY    射击者的垂直速度
     * @param direction 射击方向（-1 表示向上，1 表示向下）
     * @param power     每颗子弹的威力值
     * @return 包含所有生成子弹的列表
     */
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
