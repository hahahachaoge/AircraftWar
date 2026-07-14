package cn.edu.scnu.application;

// 用于创建窗口 设置尺寸 获取屏幕信息等
import javax.swing.*;

/**
 * 主入口
 *
 * @author 黄彪骐
 */
public class Main {
    // 定义游戏窗口的宽度和高度，作为公共静态常量
    public static final int WINDOW_WIDTH = 512;
    public static final int WINDOW_HEIGHT = 768;

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
