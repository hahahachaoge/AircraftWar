package cn.edu.scnu.basic;

import cn.edu.scnu.application.ImageManager;
import cn.edu.scnu.application.Main;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 * 飞行对象的抽象基类。
 * 所有飞机、子弹、道具的父类，定义坐标、速度、碰撞检测、图片懒加载等通用功能。
 *
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public abstract class AbstractFlyingObject {
    protected int locationX;
    protected int locationY;
    protected int speedX;
    protected int speedY;
    protected BufferedImage image;
    private boolean valid = true;

    protected int width;
    protected int height;

    public AbstractFlyingObject() {}

    public AbstractFlyingObject(int locationX, int locationY, int speedX, int speedY) {
        this.locationX = locationX;
        this.locationY = locationY;
        this.speedX = speedX;
        this.speedY = speedY;
    }

    public void forward() {
        locationX += speedX;
        locationY += speedY;
        if (locationX <= 0 || locationX >= Main.WINDOW_WIDTH) {
            speedX = -speedX;
        }
    }

    public void vanish() { valid = false; }
    public boolean notValid() { return !valid; }

    public boolean crash(AbstractFlyingObject other) {
        if (other == null) return false;
        return getRectangle().intersects(other.getRectangle());
    }

    public Rectangle getRectangle() {
        int halfW = getWidth() / 2;
        int halfH = getHeight() / 2;
        return new Rectangle(locationX - halfW, locationY - halfH, getWidth(), getHeight());
    }

    public int getWidth() {
        if (width != 0) return width;
        if (image != null) return image.getWidth();
        return 20;
    }

    public int getHeight() {
        if (height != 0) return height;
        if (image != null) return image.getHeight();
        return 20;
    }

    public int getLocationX() { return locationX; }
    public int getLocationY() { return locationY; }
    public int getSpeedX() { return speedX; }
    public int getSpeedY() { return speedY; }
    public void setLocation(int x, int y) { this.locationX = x; this.locationY = y; }

    public BufferedImage getImage() {
        if (image == null) image = ImageManager.get(this);
        return image;
    }
}
