package cn.edu.scnu.show;

import cn.edu.scnu.manager.Difficulty;
import cn.edu.scnu.manager.GameMode;
import cn.edu.scnu.rank.PlayRecord;
import cn.edu.scnu.rank.PlayRecordDaoImpl;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * 游戏结束姓名输入面板。
 * <p>
 * 当一局游戏结束后，弹出此面板显示玩家的最终得分，
 * 提示玩家输入姓名（允许匿名），并将得分记录持久化到排行榜文件中。
 * 该面板继承自 {@link JPanel}，内部使用 {@link JOptionPane} 实现交互，
 * 无需在主窗口上绘制额外组件。
 * </p>
 *
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public class NameInputPanel extends JPanel {

    /**
     * 构造一个姓名输入面板。
     * <p>
     * 弹出一个确认对话框，显示当前得分并提示玩家输入姓名。
     * 如果玩家点击"确定"且输入了非空姓名，则使用该姓名；
     * 否则使用"匿名玩家"作为默认名称。
     * 随后将得分记录写入对应难度与游戏模式的排行榜文件，
     * 最后恢复主菜单窗口的标题栏。
     * </p>
     *
     * @param diff  当前游戏使用的难度等级（BEGINNER / BASIC / INTERMEDIATE / ADVANCED / EXPERT）
     * @param score 本局游戏最终得分
     * @param mode  游戏模式（SINGLE 单人 / DOUBLE 双人）
     * @param frame 主菜单窗口引用，用于在记录保存后恢复标题栏显示
     */
    public NameInputPanel(Difficulty diff, int score, GameMode mode, cn.edu.scnu.application.MainMenuFrame frame) {
        setLayout(new GridBagLayout()); setBackground(Color.WHITE);
        JTextField nameField = new JTextField(15);
        Object[] msg = {"游戏结束！得分: " + score, "请输入玩家姓名:", nameField};
        int opt = JOptionPane.showConfirmDialog(this, msg, "记录得分", JOptionPane.OK_CANCEL_OPTION);
        String name = (opt == JOptionPane.OK_OPTION && !nameField.getText().trim().isEmpty())
                ? nameField.getText().trim() : "匿名玩家";
        PlayRecordDaoImpl dao = new PlayRecordDaoImpl(new ArrayList<>());
        cn.edu.scnu.application.GameMode oldMode = mode == GameMode.SINGLE
                ? cn.edu.scnu.application.GameMode.SINGLE : cn.edu.scnu.application.GameMode.DOUBLE;
        dao.setGameMode(oldMode);
        cn.edu.scnu.rank.Difficulty oldDiff = diff == Difficulty.BEGINNER ? cn.edu.scnu.rank.Difficulty.BEGINNER
                : diff == Difficulty.BASIC ? cn.edu.scnu.rank.Difficulty.BASIC
                : diff == Difficulty.INTERMEDIATE ? cn.edu.scnu.rank.Difficulty.INTERMEDIATE
                : diff == Difficulty.ADVANCED ? cn.edu.scnu.rank.Difficulty.ADVANCED
                : cn.edu.scnu.rank.Difficulty.EXPERT;
        dao.readFromFile(oldDiff);
        dao.addRecord(new PlayRecord(score, name, LocalDateTime.now(), oldDiff, oldMode));
        dao.writeToFile(oldDiff);
        frame.restoreTitleBar();
    }
}
