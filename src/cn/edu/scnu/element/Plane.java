package cn.edu.scnu.element;

import cn.edu.scnu.application.ImageManager;
import cn.edu.scnu.application.Main;
import cn.edu.scnu.manager.ElementManager;
import cn.edu.scnu.manager.GameElement;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 * 玩家飞机类。
 * <p>
 * 支持鼠标和键盘两种操控方式，自动射击。
 * 鼠标模式下通过 {@link #setTarget(int, int)} 设定目标位置，飞机向该位置移动；
 * 键盘模式下通过 {@link #setKeyState(int, boolean)} 响应 WASD 按键进行移动。
 * 飞机在每帧游戏更新时自动发射三发子弹（呈扇形分布）。
 * </p>
 *
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public class Plane extends ElementObj {
    private int targetX, targetY, shootCounter = 0, shootCycle = 10;
    private boolean keyboardMode = false, keyUp, keyDown, keyLeft, keyRight;

    /**
     * 初始化玩家飞机。
     *
     * @param x   初始 X 坐标（屏幕横坐标）
     * @param y   初始 Y 坐标（屏幕纵坐标）
     * @param hp  初始生命值
     * @param img 飞机外观图片
     */
    public void init(int x, int y, int hp, BufferedImage img) {
        setX(x); setY(y); this.hp = hp; this.maxHp = hp; this.power = 30;
        this.targetX = x; this.targetY = y; setImage(img);
    }

    /**
     * 设置鼠标模式下的目标坐标。飞机将自动向该坐标移动。
     *
     * @param x 目标 X 坐标
     * @param y 目标 Y 坐标
     */
    public void setTarget(int x, int y) { this.targetX = x; this.targetY = y; }

    /**
     * 设置键盘模式下指定按键的按下/释放状态。
     * 调用此方法会自动切换为键盘操控模式。
     *
     * @param key     按键键码（KeyEvent.VK_W / VK_S / VK_A / VK_D）
     * @param pressed true 表示按下，false 表示释放
     */
    public void setKeyState(int key, boolean pressed) {
        keyboardMode = true;
        switch (key) {
            case java.awt.event.KeyEvent.VK_W: keyUp = pressed; break;
            case java.awt.event.KeyEvent.VK_S: keyDown = pressed; break;
            case java.awt.event.KeyEvent.VK_A: keyLeft = pressed; break;
            case java.awt.event.KeyEvent.VK_D: keyRight = pressed; break;
        }
    }

    /**
     * 绘制玩家飞机到画布。
     * 以飞机中心坐标为基准绘制图片，使鼠标点击瞄准更加直观。
     *
     * @param g 图形上下文对象
     */
    @Override public void showElement(Graphics g) {
        BufferedImage img = getImage();
        if (img != null) g.drawImage(img, getX()-img.getWidth()/2, getY()-img.getHeight()/2, null);
    }

    /**
     * 执行玩家飞机的移动逻辑。
     * <ul>
     *   <li>键盘模式：根据 WASD 按键状态沿对应方向移动 8 像素</li>
     *   <li>鼠标模式：向目标坐标方向移动，每帧最多靠近 8 像素</li>
     * </ul>
     * 移动后对坐标进行边界约束，确保飞机不超出窗口范围。
     */
    @Override protected void move() {
        if (keyboardMode) {
            int s = 8;
            if (keyLeft) setX(getX()-s); if (keyRight) setX(getX()+s);
            if (keyUp) setY(getY()-s); if (keyDown) setY(getY()+s);
        } else {
            int dx = targetX - getX(), dy = targetY - getY();
            if (Math.abs(dx) > 3) setX(getX() + Integer.signum(dx) * 8);
            if (Math.abs(dy) > 3) setY(getY() + Integer.signum(dy) * 8);
        }
        BufferedImage img = getImage();
        if (img != null) {
            int hw = img.getWidth()/2, hh = img.getHeight()/2;
            setX(Math.max(hw, Math.min(Main.WINDOW_WIDTH-hw, getX())));
            setY(Math.max(hh, Math.min(Main.WINDOW_HEIGHT-hh, getY())));
        }
    }

    /**
     * 飞机每帧的附加逻辑：射击。
     * 每隔 {@link #shootCycle} 帧发射一次，同时射出三发子弹，
     * 分别偏离中心 -10、0、+10 像素，呈扇形向前飞行。
     *
     * @param gameTime 游戏已运行的帧数（或时间戳），本实现中未直接使用
     */
    @Override protected void add(long gameTime) {
        shootCounter++;
        if (shootCounter >= shootCycle) {
            shootCounter = 0;
            for (int i = -1; i <= 1; i++) {
                Bullet b = new Bullet();
                b.init(getX()+i*10, getY(), 0, -15, power);
                b.setBulletImage(ImageManager.HERO_BULLET_IMAGE);
                ElementManager.getManager().addElement(b, GameElement.PLAY_BULLET);
            }
        }
    }
}
