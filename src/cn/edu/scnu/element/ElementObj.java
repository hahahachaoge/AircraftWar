package cn.edu.scnu.element;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 * 所有游戏对象的抽象父类，定义了游戏中各类元素（如飞机、子弹、道具等）共有的
 * 基本属性和行为。包括：
 * <ul>
 *   <li>位置（x, y）、尺寸（w, h）</li>
 *   <li>移动速度（speedX, speedY）</li>
 *   <li>生命值与最大生命值（hp, maxHp）</li>
 *   <li>攻击力 / 威力（power）</li>
 *   <li>碰撞检测（矩形相交检测）</li>
 *   <li>生命周期管理（live 标志）</li>
 * </ul>
 * 子类需实现 {@link #showElement(Graphics)} 以定义渲染逻辑，并可选择性重写
 * {@link #updateImage(long)}、{@link #move()}、{@link #add(long)} 来定制
 * 帧更新行为（通过 {@link #model(long)} 统一驱动）。
 *
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public abstract class ElementObj {
    protected int x, y, w, h, speedX, speedY, hp, maxHp, power;
    private BufferedImage image;
    private boolean live = true;

    /** 无参构造方法。 */
    public ElementObj() {}

    /**
     * 带初始位置、尺寸与图像的构造方法。
     *
     * @param x     初始 X 坐标（锚点，通常为中心点）
     * @param y     初始 Y 坐标（锚点，通常为中心点）
     * @param w     对象宽度
     * @param h     对象高度
     * @param image 对象显示图像
     */
    public ElementObj(int x, int y, int w, int h, BufferedImage image) {
        this.x = x; this.y = y; this.w = w; this.h = h; this.image = image;
    }

    /**
     * 绘制元素到画布。
     * <p>
     * 每个子类必须实现此方法以定义自身的渲染逻辑。
     *
     * @param g 图形上下文对象
     */
    public abstract void showElement(Graphics g);

    /**
     * 帧更新模型方法（模板方法模式）。
     * <p>
     * 每一帧由游戏循环调用，依次执行：
     * <ol>
     *   <li>更新图像（{@link #updateImage(long)}）</li>
     *   <li>移动（{@link #move()}）</li>
     *   <li>生成子对象或执行附加逻辑（{@link #add(long)}）</li>
     * </ol>
     * 若对象已死亡（{@code live == false}），则直接返回，不做任何更新。
     *
     * @param gameTime 当前游戏已运行的时间（毫秒），可用于时间相关的状态变化
     */
    public final void model(long gameTime) {
        if (!live) return;
        updateImage(gameTime); move(); add(gameTime);
    }

    /**
     * 更新图像资源。
     * <p>
     * 子类可重写此方法以实现帧动画或根据游戏时间切换图片的逻辑。
     *
     * @param gameTime 当前游戏已运行的时间（毫秒）
     */
    protected void updateImage(long gameTime) {}

    /**
     * 移动对象。
     * <p>
     * 子类可重写此方法以定义移动逻辑（如根据速度更新坐标、边界约束等）。
     */
    protected void move() {}

    /**
     * 附加逻辑（如生成子弹、道具等）。
     * <p>
     * 子类可重写此方法以在每一帧执行额外的游戏逻辑，例如敌机开火、道具生成等。
     *
     * @param gameTime 当前游戏已运行的时间（毫秒）
     */
    protected void add(long gameTime) {}

    /**
     * 按键事件回调。
     * <p>
     * 子类可重写此方法以响应键盘输入（例如英雄机的移动与开火）。
     *
     * @param bl  按键状态：{@code true} 表示按下，{@code false} 表示释放
     * @param key 按键的键码（{@code java.awt.event.KeyEvent} 常量）
     */
    public void keyClick(boolean bl, int key) {}

    /**
     * 死亡回调。
     * <p>
     * 子类可重写此方法以在对象死亡时执行额外逻辑（如播放爆炸动画、掉落道具等）。
     */
    public void die() {}

    /**
     * 工厂方法 —— 根据字符串数据创建元素实例。
     * <p>
     * 子类可重写此方法以支持从配置文件或数据字符串构造自身。
     *
     * @param str 包含元素初始化数据的字符串（格式由子类定义）
     * @return 创建的元素实例，若解析失败则返回 {@code null}
     */
    public ElementObj createElement(String str) { return null; }

    /**
     * 获取对象的碰撞检测矩形。
     * <p>
     * 若图像不为 {@code null}，则以图像宽高的中心为锚点构造矩形；
     * 否则以对象宽高 {@code w}、{@code h} 为准。
     *
     * @return 用于碰撞检测的 {@link Rectangle} 对象
     */
    public Rectangle getRectangle() {
        int hw = image != null ? image.getWidth()/2 : w/2;
        int hh = image != null ? image.getHeight()/2 : h/2;
        return new Rectangle(x - hw, y - hh, hw*2, hh*2);
    }

    /**
     * 碰撞检测 —— 判断当前对象是否与另一对象发生碰撞。
     *
     * @param obj 待检测的目标元素对象
     * @return 若两对象的碰撞矩形相交则返回 {@code true}，否则返回 {@code false}
     */
    public boolean pk(ElementObj obj) {
        return this.getRectangle().intersects(obj.getRectangle());
    }

    /** @return 当前 X 坐标 */
    public int getX() { return x; }

    /** @param x 新的 X 坐标 */
    public void setX(int x) { this.x = x; }

    /** @return 当前 Y 坐标 */
    public int getY() { return y; }

    /** @param y 新的 Y 坐标 */
    public void setY(int y) { this.y = y; }

    /** @return 对象宽度 */
    public int getW() { return w; }

    /** @return 对象高度 */
    public int getH() { return h; }

    /** @return 当前显示的图像，可能为 {@code null} */
    public BufferedImage getImage() { return image; }

    /** @param img 要设置的新图像 */
    public void setImage(BufferedImage img) { this.image = img; }

    /** @return {@code true} 表示对象存活，{@code false} 表示已死亡 */
    public boolean isLive() { return live; }

    /** @param live 存活状态 */
    public void setLive(boolean live) { this.live = live; }
}
