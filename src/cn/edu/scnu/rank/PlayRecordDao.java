package cn.edu.scnu.rank;

import java.util.List;

/**
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public interface PlayRecordDao {
    /**
     * * *********************
     * * 删除部分
     * * *********************
     */

    /**
     * 根据玩家名删除对应的所有玩家记录
     */
    void deleteAllRecords(String playerName);

    void deleteByRank(Difficulty difficulty, int rank);

    /**
     * * *********************
     * * 查找部分
     * * *********************
     */

    /**
     * 查找所有玩家的得分记录
     */
    List<PlayRecord> getAllScores();

    /**
     * 根据玩家名得到该玩家所有的玩家记录
     */
    List<PlayRecord> getAllPlayRecords(String playerName);

    /**
     * 根据难度类型得到对应的所有玩家记录
     */
    List<PlayRecord> getAllPlayRecords(Difficulty difficulty);

    /**
     * * *********************
     * * 添加部分
     * * *********************
     */

    /**
     * 添加一个游戏记录
     */
    void addRecord(PlayRecord record);

    /**
     * * *********************
     * * 修改部分
     * * *********************
     */

    /**
     * * *********************
     * * 文件操作部分
     * * *********************
     */
    /**
     * 从文件中读取数据
     */
    void readFromFile(Difficulty difficulty);

    /**
     * 将数据写入文件中
     */
    void writeToFile(Difficulty difficulty);
}
