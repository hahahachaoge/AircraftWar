package cn.edu.scnu.element;

import cn.edu.scnu.application.Main;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 * 英雄机和敌机子弹共用此类，子弹飞出边界后自动标记为死亡状态。
 * 通过 {@code enemy} 字段区分英雄子弹与敌机子弹，敌机子弹向下移动，英雄子弹向上移动。
 *
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public class Bullet extends ElementObj {
    private boolean enemy;

    /**
     * 初始化子弹的位置、速度与属性。
     *
     * @param x  子弹初始 x 坐标（中心位置）
     * @param y  子弹初始 y 坐标（中心位置）
     * @param sx 水平方向速度分量（像素/帧）
     * @param sy 垂直方向速度分量（像素/帧）；正值表示向下（敌机子弹），负值表示向上（英雄子弹）
     * @param p  子弹攻击力，击中目标时扣除的耐久值
     */
    public void init(int x, int y, int sx, int sy, int p) {
        setX(x); setY(y); speedX = sx; speedY = sy; power = p; enemy = sy > 0;
    }

    /**
     * 判断该子弹是否为敌机子弹。
     *
     * @return {@code true} 表示为敌机子弹，{@code false} 表示为英雄子弹
     */
    public boolean isEnemy() { return enemy; }

    /**
     * 为该子弹设置显示图片。
     *
     * @param img 子弹的 BufferedImage 对象
     */
    public void setBulletImage(BufferedImage img) { setImage(img); }

    /**
     * 绘制子弹图像到画布上，以子弹中心坐标为准进行绘制。
     *
     * @param g 图形上下文对象，用于绘制图像
     */
    @Override public void showElement(Graphics g) {
        BufferedImage img = getImage();
        if (img != null) g.drawImage(img, getX()-img.getWidth()/2, getY()-img.getHeight()/2, null);
    }

    /**
     * 移动子弹位置，根据速度分量更新坐标。
     * 如果子弹超出游戏窗口边界（上下各延伸 50 像素缓冲），则标记为死亡状态。
     */
    @Override protected void move() {
        setX(getX()+speedX); setY(getY()+speedY);
        if (getY() < -50 || getY() > Main.WINDOW_HEIGHT+50 || getX() < -50 || getX() > Main.WINDOW_WIDTH+50)
            setLive(false);
    }
}
