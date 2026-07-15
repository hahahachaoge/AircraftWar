package cn.edu.scnu.game;

import cn.edu.scnu.application.MainMenuFrame;

/**
 * 程序唯一入口。
 * <p>
 * 启动主菜单界面，所有游戏流程由此开始。
 * </p>
 *
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public class GameStart {

    /**
     * 程序入口方法。
     * <p>
     * 在控制台输出启动提示信息，通过 Swing 事件线程创建并显示主菜单窗口。
     * </p>
     *
     * @param args 命令行参数（当前未使用）
     */
    public static void main(String[] args) {
        System.out.println("飞机大战启动...");

        javax.swing.SwingUtilities.invokeLater(() -> {
            try {
                javax.swing.UIManager.setLookAndFeel(
                    javax.swing.UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new MainMenuFrame();
        });

        System.out.println("主菜单显示完成");
    }
}
