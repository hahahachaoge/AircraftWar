package cn.edu.scnu.basic;

import cn.edu.scnu.aircraft.AbstractAircraft;
import cn.edu.scnu.application.ImageManager;
import cn.edu.scnu.application.Main;

import java.awt.image.BufferedImage;

/**
 * 可飞行对象的抽象基类，是所有游戏中可移动/可绘制对象的共同父类。
 * <p>
 * 该类封装了游戏中飞行对象的核心属性和行为，包括：
 * 位置坐标（图片中心点）、移动速度、图片资源、碰撞检测以及生存状态管理。
 * 子类包括敌机、英雄机、子弹等，它们继承此基类的位置移动、碰撞检测和生命周期控制能力。
 * </p>
 *
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public abstract class AbstractFlyingObject {

    //locationX,locationY为图片中心位置坐标
    protected int locationX;
    protected int locationY;

    // x,y轴移动速度
    protected int speedX;
    protected int speedY;

    //图片, null 表示未设置
    protected BufferedImage image = null;

    //x 轴长度，根据图片尺寸获得, -1 表示未设置
    protected int width = -1;

    //y 轴长度，根据图片尺寸获得, -1 表示未设置
    protected int height = -1;

    //有效（生存）标记，标记为 false的对象会在下次刷新时清除
    protected boolean isValid = true;

    /**
     * 无参构造方法。
     * 所有属性保持默认初始值：位置为 (0,0)，速度为 0，图片为 null，宽高为 -1，有效标记为 true。
     */
    public AbstractFlyingObject() {
    }

    /**
     * 带参构造方法，初始化飞行对象的位置和移动速度。
     *
     * @param locationX 初始 x 轴坐标（图片中心）
     * @param locationY 初始 y 轴坐标（图片中心）
     * @param speedX    x 轴方向移动速度（像素/帧）
     * @param speedY    y 轴方向移动速度（像素/帧）
     */
    public AbstractFlyingObject(int locationX, int locationY, int speedX, int speedY) {
        this.locationX = locationX;
        this.locationY = locationY;
        this.speedX = speedX;
        this.speedY = speedY;
    }

    /**
     * 可飞行对象根据速度移动。
     * 每帧调用一次，按当前 speedX/speedY 更新 locationX/locationY。
     * 若飞行对象触碰到横向边界，横向速度反向（实现反弹效果）。
     */
    public void forward() {
        locationX += speedX;
        locationY += speedY;
        if (locationX <= 0 || locationX >= Main.WINDOW_WIDTH) {
            // 横向超出边界后反向
            speedX = -speedX;
        }
    }

    /**
     * 碰撞检测，判断当前对象是否与另一飞行对象发生碰撞。
     * 当对方坐标进入我方范围，判定我方击中。
     * 碰撞判定基于矩形区域重叠检测：
     * <ul>
     *   <li>非飞机对象区域：横向 [x - width/2, x + width/2]，纵向 [y - height/2, y + height/2]</li>
     *   <li>飞机对象区域：横向 [x - width/2, x + width/2]，纵向 [y - height/4, y + height/4]</li>
     * </ul>
     *
     * @param flyingObject 撞击检测的对方对象
     * @return true 表示发生碰撞；false 表示未发生碰撞
     */
    public boolean crash(AbstractFlyingObject flyingObject) {
        // 缩放因子，用于控制 y轴方向区域范围
        int factor = this instanceof AbstractAircraft ? 2 : 1; //我方
        int fFactor = flyingObject instanceof AbstractAircraft ? 2 : 1;//对方

        //对方坐标、宽度、高度
        int x = flyingObject.getLocationX();
        int y = flyingObject.getLocationY();
        int fWidth = flyingObject.getWidth();
        int fHeight = flyingObject.getHeight();

        return x + (fWidth+this.getWidth())/2 > locationX
                && x - (fWidth+this.getWidth())/2 < locationX
                && y + ( fHeight/fFactor+this.getHeight()/factor )/2 > locationY
                && y - ( fHeight/fFactor+this.getHeight()/factor )/2 < locationY;
    }

    /**
     * 获取当前对象的 x 轴坐标（图片中心）。
     *
     * @return x 轴坐标值
     */
    public int getLocationX() {
        return locationX;
    }

    /**
     * 获取当前对象的 y 轴坐标（图片中心）。
     *
     * @return y 轴坐标值
     */
    public int getLocationY() {
        return locationY;
    }

    /**
     * 设置当前对象的位置坐标（图片中心）。
     *
     * @param locationX 新的 x 轴坐标
     * @param locationY 新的 y 轴坐标
     */
    public void setLocation(double locationX, double locationY){
        this.locationX = (int) locationX;
        this.locationY = (int) locationY;
    }

    /**
     * 获取当前对象在 y 轴方向上的移动速度。
     *
     * @return y 轴速度值（像素/帧）
     */
    public int getSpeedY() {
        return speedY;
    }

    /**
     * 获取当前对象的图片资源。
     * 采用懒加载模式：首次调用时通过 ImageManager 获取图片并缓存，后续直接返回缓存结果。
     *
     * @return 当前对象对应的 BufferedImage 图片；若无可返回 null
     */
    public BufferedImage getImage() {
        if (image == null){
            // 获取图片 this 表示当前对象 图片是同一种类共享的
            // 实现懒加载模式
            image = ImageManager.get(this);
        }
        return image;
    }

    /**
     * 获取当前对象图片的宽度（像素）。
     * 懒加载：首次调用时通过 ImageManager 获取图片宽度并缓存。
     *
     * @return 图片宽度（像素）
     */
    public int getWidth() {
        if (width == -1){
            // 若未设置，则查询图片宽度并设置
            width = ImageManager.get(this).getWidth();
        }
        return width;
    }

    /**
     * 获取当前对象图片的高度（像素）。
     * 懒加载：首次调用时通过 ImageManager 获取图片高度并缓存。
     *
     * @return 图片高度（像素）
     */
    public int getHeight() {
        if (height == -1){
            // 若未设置，则查询图片高度并设置
            height = ImageManager.get(this).getHeight();
        }
        return height;
    }


    /**
     * 标记当前对象为已消失（失效）。
     * 调用后 {@link #notValid()} 返回 true，该对象将在下一帧刷新时被移除。
     */
    public void vanish() {
        isValid = false;
    }

    /**
     * 判断当前对象是否已失效（不再有效）。
     *
     * @return true 表示对象已失效（即将被清除）；false 表示对象仍有效
     */
    public boolean notValid() {
        return !this.isValid;
    }

}
