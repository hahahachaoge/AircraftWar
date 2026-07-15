package cn.edu.scnu.application;

import cn.edu.scnu.aircraft.HeroAircraft;
import cn.edu.scnu.application.game.AbstractGame;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 英雄机控制类
 * 监听鼠标，控制英雄机的移动
 * 
 * @author 黄彪骐
 */
@SuppressWarnings("all")
public class HeroController {
    // private Game game;
    private AbstractGame game;
    private HeroAircraft heroAircraft;
    private MouseAdapter mouseAdapter;

    public HeroController(AbstractGame game, HeroAircraft heroAircraft) {
        this.game = game;
        this.heroAircraft = heroAircraft;

        mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // 暂停时禁止移动飞机
                if (game.isPaused()) {
                    return;
                }
                super.mouseDragged(e);
                int halfW = heroAircraft.getWidth() / 2;
                int halfH = heroAircraft.getHeight() / 2;
                int x = Math.max(halfW, Math.min(Main.WINDOW_WIDTH - halfW, e.getX()));
                int y = Math.max(halfH, Math.min(Main.WINDOW_HEIGHT - halfH, e.getY()));
                heroAircraft.setLocation(x, y);
            }
        };

        game.addMouseListener(mouseAdapter);
        game.addMouseMotionListener(mouseAdapter);
    }

}
