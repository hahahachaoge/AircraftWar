package cn.edu.scnu.application;

import cn.edu.scnu.aircraft.HeroAircraft;
import cn.edu.scnu.application.game.AbstractGame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 英雄机鼠标控制器。监听鼠标拖拽，控制英雄机移动。
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public class HeroController {
    private AbstractGame game;
    private HeroAircraft heroAircraft;

    public HeroController(AbstractGame game, HeroAircraft heroAircraft) {
        this.game = game;
        this.heroAircraft = heroAircraft;

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (game.isPaused()) return;
                int x = e.getX();
                int y = e.getY();
                int hw = heroAircraft.getWidth() / 2;
                int hh = heroAircraft.getHeight() / 2;
                x = Math.max(hw, Math.min(Main.WINDOW_WIDTH - hw, x));
                y = Math.max(hh, Math.min(Main.WINDOW_HEIGHT - hh, y));
                heroAircraft.setLocation(x, y);
            }
        };

        game.addMouseListener(mouseAdapter);
        game.addMouseMotionListener(mouseAdapter);
    }
}
