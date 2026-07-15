package cn.edu.scnu.application;

import cn.edu.scnu.aircraft.HeroAircraft;
import cn.edu.scnu.application.game.AbstractGame;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * 键盘控制器：用于双人模式中控制第二名玩家的英雄机移动。
 * 该控制器监听键盘事件（按下与释放），通过记录按键状态，
 * 在每一帧更新时根据当前按下的方向键计算位移量，
 * 并调用 HeroAircraft 的 setLocation 方法实现移动，
 * 同时确保英雄机不会超出游戏窗口边界。
 *
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public class KeyboardController extends KeyAdapter {
    private HeroAircraft hero;
    private int upKey, downKey, leftKey, rightKey;
    private boolean up, down, left, right;

    /**
     * 构造一个键盘控制器。
     *
     * @param game  游戏实例，控制器将向该游戏注册键盘监听器
     * @param hero  被控制的英雄机对象
     * @param up    上移键的键码（KeyEvent 常量）
     * @param down  下移键的键码
     * @param left  左移键的键码
     * @param right 右移键的键码
     */
    public KeyboardController(AbstractGame game, HeroAircraft hero,
                              int up, int down, int left, int right) {
        this.hero = hero;
        this.upKey = up;
        this.downKey = down;
        this.leftKey = left;
        this.rightKey = right;
        game.addKeyListener(this);
        game.setFocusable(true);
        game.requestFocusInWindow();
    }

    /**
     * 按键按下事件处理。当玩家按下已注册的方向键时，
     * 将对应的状态标记设为 true，表示该方向正在被按住。
     *
     * @param e 键盘事件对象，包含按下的键码等信息
     */
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == upKey) up = true;
        if (key == downKey) down = true;
        if (key == leftKey) left = true;
        if (key == rightKey) right = true;
    }

    /**
     * 按键释放事件处理。当玩家松开已注册的方向键时，
     * 将对应的状态标记设为 false，停止该方向的移动。
     *
     * @param e 键盘事件对象，包含释放的键码等信息
     */
    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == upKey) up = false;
        if (key == downKey) down = false;
        if (key == leftKey) left = false;
        if (key == rightKey) right = false;
    }

    /**
     * 更新英雄机位置。根据当前按键状态计算水平和垂直位移，
     * 并应用移动速度（每帧 8 像素），
     * 同时确保英雄机不超出游戏窗口边界。
     * 该方法应在游戏主循环的每一帧中被调用。
     */
    public void updatePosition() {
        int speed = 8;
        int dx = 0, dy = 0;
        if (left) dx -= speed;
        if (right) dx += speed;
        if (up) dy -= speed;
        if (down) dy += speed;
        if (dx != 0 || dy != 0) {
            int newX = Math.max(0, Math.min(Main.WINDOW_WIDTH - hero.getWidth(), hero.getLocationX() + dx));
            int newY = Math.max(0, Math.min(Main.WINDOW_HEIGHT - hero.getHeight(), hero.getLocationY() + dy));
            hero.setLocation(newX, newY);
        }
    }
}
