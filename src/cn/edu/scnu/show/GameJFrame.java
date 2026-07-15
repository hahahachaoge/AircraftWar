package cn.edu.scnu.show;

import javax.swing.*;
import java.awt.event.KeyListener;
import java.awt.event.MouseMotionListener;

/**
 * 游戏主窗口类，继承自 {@link JFrame}，是整个游戏的最顶层容器。
 * <p>
 * 该类负责以下核心职责：
 * <ul>
 *   <li>创建并配置游戏窗口（大小、标题、关闭行为等）</li>
 *   <li>嵌入游戏面板（{@link JPanel}）作为内容面板</li>
 *   <li>注册键盘监听器（{@link KeyListener}）与鼠标移动监听器（{@link MouseMotionListener}）</li>
 *   <li>启动游戏主线程（{@link Thread}）驱动游戏循环</li>
 * </ul>
 * </p>
 * <p>
 * 典型用法：构造实例后依次调用各 setter 注入组件，最后调用 {@link #start()} 启动窗口与游戏。
 * </p>
 *
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public class GameJFrame extends JFrame {
    /** 游戏面板，负责游戏画面的绘制与更新 */
    private JPanel panel;

    /** 键盘监听器，处理玩家键盘输入 */
    private KeyListener keyListener;

    /** 鼠标移动监听器，处理玩家鼠标输入 */
    private MouseMotionListener mouseListener;

    /** 游戏主线程，驱动游戏循环 */
    private Thread gameThread;

    /**
     * 构造一个默认大小的游戏主窗口。
     * <p>
     * 窗口大小为 512 x 768 像素，标题为 "飞机大战"，
     * 点击关闭按钮时退出程序，窗口居中显示且不可调整大小。
     * </p>
     */
    public GameJFrame() {
        setSize(512, 768);
        setTitle("飞机大战");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    /**
     * 启动游戏窗口与游戏循环。
     * <p>
     * 依次执行以下操作：
     * <ol>
     *   <li>将已注入的游戏面板设置为内容面板</li>
     *   <li>注册键盘与鼠标监听器</li>
     *   <li>启动游戏主线程</li>
     *   <li>显示窗口</li>
     *   <li>若面板实现了 {@link Runnable} 接口，则另起线程驱动面板自身的逻辑</li>
     * </ol>
     * </p>
     */
    public void start() {
        if (panel != null) {
            setContentPane(panel);
        }
        if (keyListener != null) {
            addKeyListener(keyListener);
        }
        if (mouseListener != null) {
            addMouseMotionListener(mouseListener);
        }
        if (gameThread != null) {
            gameThread.start();
        }
        setVisible(true);
        if (panel instanceof Runnable) {
            new Thread((Runnable) panel).start();
        }
    }

    /**
     * 设置游戏面板。
     *
     * @param p 游戏面板实例，负责画面绘制与游戏逻辑更新
     */
    public void setPanel(JPanel p) {
        this.panel = p;
    }

    /**
     * 设置键盘监听器。
     *
     * @param k 键盘监听器实例，用于处理玩家的键盘按键事件
     */
    public void setKeyListener(KeyListener k) {
        this.keyListener = k;
    }

    /**
     * 设置鼠标移动监听器。
     *
     * @param m 鼠标移动监听器实例，用于处理玩家的鼠标移动事件
     */
    public void setMouseMotionListener(MouseMotionListener m) {
        this.mouseListener = m;
    }

    /**
     * 设置游戏主线程。
     *
     * @param t 游戏主线程实例，驱动游戏循环执行
     */
    public void setGameThread(Thread t) {
        this.gameThread = t;
    }
}
