package cn.edu.scnu.application;

import cn.edu.scnu.aircraft.HeroAircraft;
import cn.edu.scnu.application.game.AbstractGame;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 英雄机控制类。
 * <p>
 * 本类负责监听鼠标事件（拖拽），控制英雄机（HeroAircraft）在游戏窗口内的移动。
 * 通过内部 MouseAdapter 实现鼠标拖拽监听，在鼠标拖拽时更新英雄机的位置，
 * 并确保英雄机不会移出游戏窗口边界。当游戏处于暂停状态时，禁止移动英雄机。
 * </p>
 *
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
@SuppressWarnings("all")
public class HeroController {
    // private Game game;
    private AbstractGame game;
    private HeroAircraft heroAircraft;
    private MouseAdapter mouseAdapter;

    /**
     * 构造英雄机控制器。
     * <p>
     * 初始化控制器并创建鼠标监听器。监听器在鼠标拖拽时根据鼠标位置计算英雄机的
     * 新坐标，同时将英雄机限制在游戏窗口范围内（考虑英雄机自身的宽高）。
     * 将创建的鼠标监听器注册到游戏面板上，以响应鼠标事件。
     * </p>
     *
     * @param game         游戏实例，用于获取暂停状态以及注册鼠标监听器
     * @param heroAircraft 被控制的英雄机实例
     */
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
