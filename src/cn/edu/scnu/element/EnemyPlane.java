package cn.edu.scnu.element;

import cn.edu.scnu.application.Main;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 * 包含游戏中的所有敌机类型（Mob、Elite、Veteran、Ace、Boss）。
 * 管理敌机的位置、移动、血量、得分、火力等核心属性，
 * 并提供绘制与移动逻辑的统一实现。
 *
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public class EnemyPlane extends ElementObj {
    private int score;
    private String enemyType = "MOB";

    /**
     * 设置敌机类型
     *
     * @param t 敌机类型字符串，可选值: "MOB", "ELITE", "VETERAN", "ACE", "BOSS"
     */
    public void setEnemyType(String t) { enemyType = t; }

    /**
     * 获取敌机类型
     *
     * @return 敌机类型字符串
     */
    public String getEnemyType() { return enemyType; }

    /**
     * 获取敌机被击落后提供的分数
     *
     * @return 击落该敌机获得的分数值
     */
    public int getScore() { return score; }

    /**
     * 初始化敌机的完整属性
     *
     * @param x   初始横坐标（中心点位置）
     * @param y   初始纵坐标（中心点位置）
     * @param sx  水平移动速度（像素/帧）
     * @param sy  垂直移动速度（像素/帧）
     * @param hp  初始生命值
     * @param sc  被击落后获得的分数
     * @param pow 火力值（子弹威力）
     * @param img 敌机外观图片
     */
    public void init(int x, int y, int sx, int sy, int hp, int sc, int pow, BufferedImage img) {
        setX(x); setY(y); speedX = sx; speedY = sy;
        this.hp = hp; this.maxHp = hp; this.score = sc; this.power = pow; setImage(img);
    }

    /**
     * 绘制敌机到画布上
     * 以敌机中心坐标为基准，将图片绘制到画布上
     *
     * @param g 图形上下文对象，用于绘制图像
     */
    @Override public void showElement(Graphics g) {
        BufferedImage img = getImage();
        if (img != null) g.drawImage(img, getX()-img.getWidth()/2, getY()-img.getHeight()/2, null);
    }

    /**
     * 敌机的移动逻辑
     * 每帧根据水平/垂直速度更新坐标。
     * 左右达到边界时反弹（水平速度取反）；
     * 超出屏幕底部时标记为死亡。
     */
    @Override protected void move() {
        setX(getX()+speedX); setY(getY()+speedY);
        if (getX() < 30 || getX() > Main.WINDOW_WIDTH-30) speedX = -speedX;
        if (getY() > Main.WINDOW_HEIGHT+50) setLive(false);
    }
}
