package cn.edu.scnu.rank;

import cn.edu.scnu.application.GameMode;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public class PlayRecord implements Comparable<PlayRecord>,Serializable {

    private int score; // 玩家分数
    private String name; // 玩家名
    private int rank; // 名次
    private LocalDateTime dateTime; // 记录时间
    private Difficulty difficulty; // 难度
    private GameMode gameMode; // 游戏模式（单人/双人）

    /**
     * * *********************
     * * 构造器部分
     * * *********************
     */
    public PlayRecord(int score, String name, LocalDateTime dateTime, Difficulty difficulty) {
        this.score = score;
        this.name = name;
        this.dateTime = dateTime;
        this.difficulty = difficulty;
        this.gameMode = GameMode.SINGLE; // 默认单人
    }

    public PlayRecord(int score, String name, LocalDateTime dateTime, Difficulty difficulty, GameMode gameMode) {
        this.score = score;
        this.name = name;
        this.dateTime = dateTime;
        this.difficulty = difficulty;
        this.gameMode = gameMode;
    }

    /**
     * * *********************
     * * Getter和Setter部分
     * * *********************
     */
    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    /**
     * * *********************
     * * 比较器部分
     * * *********************
     * 先按难度排序（难度高在前） 
     * 然后按照分数排序（分数高在前） 
     * 最后按照时间先后排序（最新时间在前）
     */
    @Override
    public int compareTo(PlayRecord o) {
        int difficultyCompare = o.difficulty.ordinal() - this.difficulty.ordinal();
        if (difficultyCompare != 0) {
            return difficultyCompare;
        }
        int scoreCompare = o.score - this.score;
        if (scoreCompare != 0) {
            return scoreCompare;
        }
        return o.dateTime.compareTo(this.dateTime);
    }

    /**
     * * *********************
     * * toString部分
     * * *********************
     */

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd HH:mm");
        return String.format("%-10s\t%-10d\t%-10s", name, score, dateTime.format(formatter));
    }

}
