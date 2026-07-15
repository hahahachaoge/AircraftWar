package cn.edu.scnu.application;

import java.time.LocalDateTime;
import javax.swing.SwingUtilities;

import cn.edu.scnu.rank.Difficulty;
import cn.edu.scnu.rank.PlayRecord;
import cn.edu.scnu.rank.PlayRecordDaoImpl;

/**
 * 排行榜业务逻辑类，负责管理游戏排行榜的展示与记录持久化。
 * <p>
 * 该类作为排行榜模块的入口，封装了以下核心功能：
 * <ul>
 *   <li>设置当前游戏模式（如单人/双人模式）</li>
 *   <li>在 GUI 窗口中按难度展示排行榜信息</li>
 *   <li>添加当前玩家的游戏记录（分数、玩家名、难度等）</li>
 *   <li>将各个难度的游戏记录同步写入文件</li>
 * </ul>
 * </p>
 *
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public class RankingBoard {
    private final PlayRecordDaoImpl recordDao;

    /**
     * 构造排行榜管理器。
     *
     * @param recordDao 游戏记录数据访问对象，用于操作记录的增删改查及文件读写
     */
    public RankingBoard(PlayRecordDaoImpl recordDao) {
        this.recordDao = recordDao;
    }

    /**
     * 设置当前游戏模式。
     * <p>
     * 游戏模式会影响记录的存储与展示逻辑，例如单人模式与双人模式的排行榜可能分开管理。
     * </p>
     *
     * @param mode 游戏模式枚举值（如 SINGLE_PLAYER / DOUBLE_PLAYER）
     */
    public void setGameMode(GameMode mode) {
        recordDao.setGameMode(mode);
    }

    /**
     * 在控制台展示指定难度的排行榜。
     * <p>
     * 通过 Swing 事件调度线程（EDT）异步弹出排行榜窗口 {@link RankingFrame}，
     * 窗口内容根据传入的难度从记录数据源中加载。
     * </p>
     *
     * @param difficulty 游戏难度（如 EASY / NORMAL / HARD）
     */
    public void showRankInfo(Difficulty difficulty) {
        SwingUtilities.invokeLater(()->{
            new RankingFrame(difficulty, recordDao);
        });
    }

    /**
     * 添加当前玩家的游戏记录。
     * <p>
     * 根据传入的玩家名、分数、难度和当前游戏模式构造一条 {@link PlayRecord}，
     * 加入内存中的记录列表，然后从文件重新读取该难度的记录以同步数据，
     * 最后将结果写回文件进行持久化。
     * </p>
     *
     * @param playerName 玩家名称
     * @param score      本次游戏得分
     * @param difficulty 游戏难度
     */
    public void addCurRecord(String playerName, int score, Difficulty difficulty) {
        // 创建记录并加入内存中的列表
        PlayRecord newPlayRecord = new PlayRecord(score, playerName, LocalDateTime.now(), difficulty, recordDao.getGameMode());
        recordDao.addRecord(newPlayRecord);
        recordDao.readFromFile(difficulty);
        recordDao.writeToFile(difficulty);

    }

    /**
     * 将指定难度的游戏记录同步到文件中。
     * <p>
     * 调用数据访问对象的文件写入方法，将当前内存中该难度的所有记录
     * 持久化到对应的磁盘文件中。
     * </p>
     *
     * @param difficulty 游戏难度，用于确定写入哪个难度对应的文件
     */
    public void writeRecordToFile(Difficulty difficulty) {
        recordDao.writeToFile(difficulty);
    }
}
