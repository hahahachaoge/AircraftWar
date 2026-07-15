package cn.edu.scnu.rank;

import cn.edu.scnu.application.GameMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public class PlayRecordDaoImpl implements PlayRecordDao {
    // 位于内存中的游戏记录表
    private List<PlayRecord> records;
    private GameMode currentGameMode = GameMode.SINGLE;

    public PlayRecordDaoImpl(List<PlayRecord> records) {
        this.records = records;
    }

    public void setGameMode(GameMode mode) {
        this.currentGameMode = mode;
    }

    public GameMode getGameMode() {
        return currentGameMode;
    }

    @Override
    public List<PlayRecord> getAllScores() {
        return records;
    }

    @Override
    public void deleteAllRecords(String playerName) {
        throw new UnsupportedOperationException("Unimplemented method 'deleteAllRecords'");
    }

    @Override
    public List<PlayRecord> getAllPlayRecords(String playerName) {
        List<PlayRecord> result = new ArrayList<>();
        for (int i = 0; i < records.size(); i++) {
            PlayRecord record = records.get(i);
            if (record.getName().equals(playerName)) {
                result.add(record);
            }
        }
        Collections.sort(result);
        return result;
    }

    @Override
    public List<PlayRecord> getAllPlayRecords(Difficulty difficulty) {
        List<PlayRecord> result = new ArrayList<>();
        for (int i = 0; i < records.size(); i++) {
            PlayRecord record = records.get(i);
            if (record.getDifficulty().equals(difficulty)) {
                result.add(record);
            }
        }
        Collections.sort(result);
        return result;
    }

    @Override
    @SuppressWarnings("all")
    // ! 必须保障对象是可序列化的 —— implements Serializable
    public void readFromFile(Difficulty difficulty) {
        // 生成目标文件名
        String fileName = getFileNameByDifficulty(difficulty);
        // 创建 File 对象，检查文件是否存在
        File file = new File(fileName);
        // 判断文件是否存在
        if (!file.exists()) {
            return;
        }
        // 文件存在则读取文件内容
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            // 从文件读取序列化的 PlayRecord 列表
            // 注意：这里进行强制类型转换，因为我们知道写入的是 List<PlayRecord>
            List<PlayRecord> loadedRecords = (List<PlayRecord>) ois.readObject();
            // 将loadedRecords加入records
            for (PlayRecord record : loadedRecords) {
                records.add(record);
            }
            // 排序records
            Collections.sort(records);
            // 打印结果
            System.out.println("已从 " + fileName + " 加载 " + loadedRecords.size() + " 条记录");
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    @Override
    // ! 必须保障对象是可序列化的 —— implements Serializable
    public void writeToFile(Difficulty difficulty) {
        // 生成目标文件名
        String fileName = getFileNameByDifficulty(difficulty);
        File dir=new File("data"+File.separator+"rank");
        if (!dir.exists()){
            dir.mkdirs();
        }
        // 创建或者覆盖目标文件名对应的文件
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            // 在内存record中筛选符合对应难度的记录
            List<PlayRecord> difficultyRecords = getAllPlayRecords(difficulty);
            // 将记录写入文件中
            oos.writeObject(difficultyRecords);
            System.out.println("已成功保存" + difficultyRecords.size()
                    + "条" + difficulty + "难度记录到文件: " + fileName);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("写入文件 " + fileName + " 失败...");
        }
    }

    /**
     * 根据难度和游戏模式生成对应的文件名
     * 单人/双人模式使用独立的文件，避免记录冲突
     */
    private String getFileNameByDifficulty(Difficulty difficulty) {
        String modeSuffix = (currentGameMode == GameMode.SINGLE) ? "_single" : "_double";
        return "data" + File.separator + "rank" + File.separator + "rank_"
                + difficulty.name().toLowerCase() + modeSuffix + ".dat";
    }

    @Override
    public void addRecord(PlayRecord record) {
        records.add(record); // 在末尾进行添加
    }

    @Override
    public void deleteByRank(Difficulty difficulty, int rank) {
        if (rank < 1) {
            throw new IllegalArgumentException("排名必须大于0");
        }

        try {
            // 1. 获取指定难度的所有记录（已排序）
            List<PlayRecord> difficultyRecords = getAllPlayRecords(difficulty);

            // 2. 检查排名是否有效
            if (rank > difficultyRecords.size()) {
                throw new IllegalArgumentException(
                        String.format("排名 %d 无效，%s难度下只有 %d 条记录",
                                rank, difficulty, difficultyRecords.size()));
            }

            // 3. 从列表中删除指定排名的记录
            PlayRecord removedRecord = difficultyRecords.remove(rank - 1);
            System.out.println("已删除记录: " + removedRecord);

            // 4. 更新内存中的完整记录列表
            // 先删除该难度下的所有记录
            Iterator<PlayRecord> iterator = records.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().getDifficulty().equals(difficulty)) {
                    iterator.remove();
                }
            }

            // 再重新添加更新后的该难度记录
            records.addAll(difficultyRecords);

            // 5. 将更新后的记录写回文件
            writeToFile(difficulty);

            System.out.println(String.format("成功删除 %s 难度第 %d 名记录", difficulty, rank));

        } catch (Exception e) {
            System.err.println("删除记录失败: " + e.getMessage());
            throw new RuntimeException("删除操作失败", e);
        }
    }

}
