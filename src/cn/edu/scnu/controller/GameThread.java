package cn.edu.scnu.controller;

import cn.edu.scnu.manager.ElementManager;
import cn.edu.scnu.manager.GameElement;

/**
 * 本类继承自 {@link Thread}，作为整个游戏的生命周期控制器。
 * 它持有游戏运行所需的全局状态，包括游戏时间、得分、暂停标记、
 * 敌机生成参数、Boss 出场警告计时等，并通过 {@link ElementManager}
 * 统一管理所有游戏元素（飞机、子弹、道具等）。
 * <p>
 * 调用 {@link #start()} 启动线程后，{@link #run()} 方法会在事件调度线程
 * （EDT）上弹出主菜单窗口 {@code MainMenuFrame}，由用户选择难度后
 * 再进入正式游戏循环。
 * </p>
 *
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public class GameThread extends Thread {
    public ElementManager em;
    public long gameTime = 0;
    public int score = 0;
    public boolean paused = false;
    public boolean gameOverFlag = false;
    public double enemySpawnCycle = 50;
    public int enemyMaxNumber = 6;
    public int scoreThreshold = 500;
    public boolean bossWarningActive = false;
    public int bossWarningTimer = 0;
    public static final int BOSS_WARNING_DELAY = 60;
    public java.util.List<int[]> explosions = new java.util.ArrayList<>();

    /**
     * 构造一个游戏主线程实例。
     * <p>
     * 初始化时通过 {@link ElementManager#getManager()} 获取全局唯一的
     * 元素管理器单例，供后续游戏循环中各组件访问。
     * </p>
     */
    public GameThread() {
        em = ElementManager.getManager();
    }

    /**
     * 线程入口方法，在 {@link #start()} 被调用后自动执行。
     * <p>
     * 本实现将 {@link #start()} 调用转交给 Swing 事件调度线程（EDT），
     * 并在 EDT 上创建 {@code MainMenuFrame} 主菜单窗口，确保所有
     * Swing 组件操作线程安全。
     * </p>
     */
    @Override
    public void run() {
        javax.swing.SwingUtilities.invokeLater(
            () -> new cn.edu.scnu.application.MainMenuFrame()
        );
    }
}
