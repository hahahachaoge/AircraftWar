package cn.edu.scnu.application;

import cn.edu.scnu.aircraft.HeroAircraft;
import cn.edu.scnu.application.game.AbstractGame;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * 键盘控制器：用于双人模式的第二名玩家
 * @author 黄彪骐
 */
public class KeyboardController extends KeyAdapter {
    private HeroAircraft hero;
    private int upKey, downKey, leftKey, rightKey;
    private boolean up, down, left, right;

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

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == upKey) up = true;
        if (key == downKey) down = true;
        if (key == leftKey) left = true;
        if (key == rightKey) right = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == upKey) up = false;
        if (key == downKey) down = false;
        if (key == leftKey) left = false;
        if (key == rightKey) right = false;
    }

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
