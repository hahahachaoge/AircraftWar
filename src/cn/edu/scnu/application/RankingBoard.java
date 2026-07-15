package cn.edu.scnu.application;

import java.time.LocalDateTime;
import javax.swing.SwingUtilities;

import cn.edu.scnu.rank.Difficulty;
import cn.edu.scnu.rank.PlayRecord;
import cn.edu.scnu.rank.PlayRecordDaoImpl;

/**
 * @author 黄彪骐
 */
public class RankingBoard {
    private final PlayRecordDaoImpl recordDao;

    public RankingBoard(PlayRecordDaoImpl recordDao) {
        this.recordDao = recordDao;
    }

    public void setGameMode(GameMode mode) {
        recordDao.setGameMode(mode);
    }

    /**
     * 在控制台展示指定难度的排行榜
     * 
     * @param difficulty 游戏难度
     */
    public void showRankInfo(Difficulty difficulty) {
        SwingUtilities.invokeLater(()->{
            new RankingFrame(difficulty, recordDao);
        });
    }

    /**
     * 添加当前玩家游戏记录
     */
    public void addCurRecord(String playerName, int score, Difficulty difficulty) {
        // 创建-》加入内存中的列表
        PlayRecord newPlayRecord = new PlayRecord(score, playerName, LocalDateTime.now(), difficulty, recordDao.getGameMode());
        recordDao.addRecord(newPlayRecord);
        recordDao.readFromFile(difficulty);
        recordDao.writeToFile(difficulty);

    }

    /**
     * 将各个难度的游戏记录同步到文件中
     */
    public void writeRecordToFile(Difficulty difficulty) {
        recordDao.writeToFile(difficulty);
    }
}
