package cn.edu.scnu.element;

import cn.edu.scnu.application.Main;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 * 游戏中的各类道具实体，英雄机拾取后可触发对应效果。
 * <p>
 * 道具类型包括：
 * <ul>
 *   <li>HP（回血）—— 恢复英雄机一定生命值</li>
 *   <li>FIRE（火力）—— 提升英雄机火力等级</li>
 *   <li>FIRE_PLUS（超级火力）—— 大幅提升英雄机火力</li>
 *   <li>BOMB（炸弹）—— 清除屏幕内所有敌机</li>
 *   <li>FROZEN（冰冻）—— 冻结敌机行动</li>
 * </ul>
 * 道具生成后以固定速度向下移动，移出屏幕底部后自动消亡。
 *
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public class PropEntity extends ElementObj {
    /** 道具类型（由外部传入的整型常量标识） */
    private int propType;

    /** 回血道具每次恢复的生命值点数 */
    private int recoverHp = 20;

    /**
     * 初始化道具对象，设置位置、类型和显示图像。
     *
     * @param x    道具初始横坐标（像素）
     * @param y    道具初始纵坐标（像素）
     * @param type 道具类型编号，用于标识 HP / FIRE / FIRE_PLUS / BOMB / FROZEN 等
     * @param img  道具对应的显示图片
     */
    public void init(int x, int y, int type, BufferedImage img) {
        setX(x);
        setY(y);
        speedY = 3;
        this.propType = type;
        setImage(img);
    }

    /**
     * 获取当前道具的类型编号。
     *
     * @return 道具类型编号（整型）
     */
    public int getPropType() {
        return propType;
    }

    /**
     * 获取回血道具每次恢复的生命值点数。
     *
     * @return 恢复的生命值点数
     */
    public int getRecoverHp() {
        return recoverHp;
    }

    /**
     * 绘制道具对象到游戏画面。
     * 以道具图片的中心点为基准进行绘制。
     *
     * @param g 绘图上下文对象，用于绘制到游戏画布
     */
    @Override
    public void showElement(Graphics g) {
        BufferedImage img = getImage();
        if (img != null) {
            g.drawImage(img, getX() - img.getWidth() / 2, getY() - img.getHeight() / 2, null);
        }
    }

    /**
     * 道具移动逻辑，每帧向下移动。
     * 当道具移出屏幕底部时，将其标记为死亡状态。
     */
    @Override
    protected void move() {
        setY(getY() + speedY);
        if (getY() >= Main.WINDOW_HEIGHT) {
            setLive(false);
        }
    }
}
