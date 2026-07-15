package cn.edu.scnu.show;

import cn.edu.scnu.application.ImageManager;
import cn.edu.scnu.application.Main;
import cn.edu.scnu.controller.GameThread;
import cn.edu.scnu.element.ElementObj;
import cn.edu.scnu.manager.ElementManager;
import cn.edu.scnu.manager.GameElement;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 游戏主渲染面板，负责游戏画面的整体绘制与刷新。
 * <p>
 * 作为 {@link JPanel} 的子类，该面板承担以下渲染任务：
 * <ul>
 *   <li>背景图片的纵向滚动绘制，模拟飞机向前飞行的视觉效果</li>
 *   <li>按固定层级顺序绘制所有游戏元素（敌机子弹、玩家子弹、敌机、Boss、道具、玩家）</li>
 *   <li>爆炸动画帧序列的播放与生命周期管理</li>
 *   <li>游戏分数（SCORE）等 HUD 信息的实时显示</li>
 * </ul>
 * 该类同时实现了 {@link Runnable} 接口，可通过独立线程以固定帧率（约 100 FPS）
 * </p>
 *
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public class GameMainJPanel extends JPanel implements Runnable {
    /** 游戏元素管理器，负责获取所有待渲染元素的集合 */
    private ElementManager em;
    /** 游戏主逻辑线程引用，用于读取分数、爆炸动画等运行时数据 */
    public GameThread gameThread;
    /** 背景图片纵向滚动偏移量，值域为 [0, WINDOW_HEIGHT)，循环累加实现无缝滚动 */
    private int bgTop = 0;
    /** 背景缓冲图片 */
    public BufferedImage bgImage;

    /**
     * 构造一个游戏主渲染面板。
     * <p>
     * 初始化时通过 {@link ElementManager#getManager()} 获取全局唯一的元素管理器实例，
     * 后续的绘制操作将从该管理器读取所有游戏元素。
     * </p>
     */
    public GameMainJPanel() {
        em = ElementManager.getManager();
    }

    /**
     * 设置游戏主逻辑线程的引用。
     * <p>
     * 该引用用于在绘制过程中访问游戏运行时数据，包括当前分数和爆炸动画状态。
     * 应在游戏线程创建后、面板开始工作前调用此方法完成绑定。
     * </p>
     *
     * @param gt 游戏主逻辑线程对象，不能为 {@code null}；
     *           若传入 {@code null} 则后续绘制时将跳过爆炸动画和分数显示
     */
    public void setGameThread(GameThread gt) {
        this.gameThread = gt;
    }

    /**
     * 绘制游戏画面的每一帧。
     * <p>
     * 渲染流程按以下顺序执行：
     * <ol>
     *   <li>调用父类 {@link JPanel#paint(Graphics)} 清空画布</li>
     *   <li>绘制纵向滚动的背景：同时绘制两幅背景图实现无缝衔接</li>
     *   <li>按固定层级顺序绘制所有游戏元素（子弹 → 敌机 → Boss → 道具 → 玩家）</li>
     *   <li>如果 {@link #gameThread} 已绑定，绘制爆炸动画帧并清理已播完的动画</li>
     *   <li>在左上角显示当前游戏分数</li>
     * </ol>
     * </p>
     *
     * @param g 画笔对象，由 Swing 框架传入，用于执行本次帧的所有绘制操作
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (bgImage != null) {
            g.drawImage(bgImage, 0, bgTop - Main.WINDOW_HEIGHT, null);
            g.drawImage(bgImage, 0, bgTop, null);
            if (++bgTop >= Main.WINDOW_HEIGHT) bgTop = 0;
        } else {
            g.setColor(new Color(10, 10, 30));
            g.fillRect(0, 0, Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT);
        }
        Map<GameElement, List<ElementObj>> all = em.getGameElements();
        for (ElementObj obj : all.get(GameElement.ENEMY_BULLET)) obj.showElement(g);
        for (ElementObj obj : all.get(GameElement.PLAY_BULLET)) obj.showElement(g);
        for (ElementObj obj : all.get(GameElement.ENEMY)) obj.showElement(g);
        for (ElementObj obj : all.get(GameElement.BOSS)) obj.showElement(g);
        for (ElementObj obj : all.get(GameElement.PROP)) obj.showElement(g);
        for (ElementObj obj : all.get(GameElement.PLAY)) obj.showElement(g);

        if (gameThread != null) {
            Iterator<int[]> it = gameThread.explosions.iterator();
            while (it.hasNext()) {
                int[] e = it.next();
                if (++e[2] < 8 && ImageManager.BANG_FRAMES[e[2]] != null)
                    g.drawImage(ImageManager.BANG_FRAMES[e[2]], e[0] - 33, e[1] - 33, null);
                else it.remove();
            }
            g.setColor(Color.RED);
            g.setFont(new Font("SansSerif", Font.BOLD, 22));
            g.drawString("SCORE: " + gameThread.score, 10, 25);
        }
    }

    /**
     * 游戏渲染主循环。
     * <p>
     * 以约 10 毫秒的间隔（约 100 FPS）持续调用 {@link #repaint()} 刷新画面。
     * 该方法在独立的渲染线程中运行，通过 {@code while (true)} 无限循环
     * 保持画面持续更新。捕获到 {@link InterruptedException} 时打印异常堆栈。
     * </p>
     */
    @Override
    public void run() {
        while (true) {
            repaint();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
