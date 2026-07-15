package cn.edu.scnu.application;

// 用于创建窗口 设置尺寸 获取屏幕信息等
import javax.swing.*;

/**
 * 游戏主入口类。
 * <p>
 * 该类是整个飞机大战应用程序的启动入口。它定义了游戏窗口的固定尺寸（宽 512px、高 768px），
 * 并在 {@link #main(String[])} 方法中初始化 Swing 界面，设置系统默认外观，
 * 随后创建并显示 {@link MainMenuFrame}（主菜单窗体），由主菜单进一步引导至游戏主界面。
 * </p>
 *
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public class Main {

    /** 游戏窗口的标准宽度（像素） */
    public static final int WINDOW_WIDTH = 512;

    /** 游戏窗口的标准高度（像素） */
    public static final int WINDOW_HEIGHT = 768;

    /**
     * 应用程序主方法 —— 飞机大战的启动入口。
     * <p>
     * 该方法首先在控制台输出启动信息，然后通过 {@link SwingUtilities#invokeLater(Runnable)}
     * 确保 Swing 组件在事件分发线程（EDT）上创建。在 EDT 中，依次执行以下操作：
     * <ol>
     *   <li>将 Swing 外观设置为操作系统默认外观，以提升视觉一致性；</li>
     *   <li>创建 {@link MainMenuFrame} 实例，主菜单窗口由此显示。</li>
     * </ol>
     * 最后在控制台输出主菜单已显示的提示信息。
     * </p>
     *
     * @param args 命令行参数（当前未使用）
     */
    public static void main(String[] args) {
        System.out.println("飞机大战启动...");

        // 使用SwingUtilities.invokeLater确保Swing组件在事件分发线程上创建
        SwingUtilities.invokeLater(() -> {
            try {
                // 设置Swing外观为系统默认
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 创建并显示主菜单
            new MainMenuFrame();
        });

        System.out.println("主菜单显示完成");
    }
}
