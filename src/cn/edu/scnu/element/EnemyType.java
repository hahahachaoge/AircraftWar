package cn.edu.scnu.element;

/**
 * 敌机类型枚举，定义了游戏中所有敌方单位的属性模板。
 * <p>
 * 每种敌机类型包含四项基本属性：击落后的得分、生命值、
 * 水平移动速度以及垂直移动速度。游戏通过此枚举为不同敌机
 * 提供统一的属性配置，便于扩展新的敌机种类。
 * </p>
 *
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public enum EnemyType {
    /** 普通敌机：低分、低血量、无水平速度 */
    MOB(10,10,0,3),
    /** 精英敌机：中等分数、中等血量、无水平速度 */
    ELITE(20,50,0,5),
    /** 老兵敌机：较高分数、中等血量、具备水平速度 */
    VETERAN(30,50,3,7),
    /** 王牌敌机：高分、中等血量、高水平速度 */
    ACE(50,50,5,9),
    /** Boss 敌机：最高分、极高血量、具备水平速度、不向下移动 */
    BOSS(100,500,5,0);

    /** 击落该敌机可获得的分值 */
    private int score;
    /** 敌机的生命值 */
    private int hp;
    /** 敌机在水平方向上的移动速度（像素/帧） */
    private int speedX;
    /** 敌机在垂直方向上的移动速度（像素/帧） */
    private int speedY;

    /**
     * 构造敌机类型枚举常量。
     *
     * @param s 击落该敌机可获得的分值
     * @param h 敌机的生命值
     * @param sx 敌机在水平方向上的移动速度（像素/帧）
     * @param sy 敌机在垂直方向上的移动速度（像素/帧）
     */
    EnemyType(int s, int h, int sx, int sy) {
        score = s;
        hp = h;
        speedX = sx;
        speedY = sy;
    }

    /**
     * 获取击落该敌机可获得的分值。
     *
     * @return 分值
     */
    public int getScore() { return score; }

    /**
     * 获取敌机的生命值。
     *
     * @return 生命值
     */
    public int getHp() { return hp; }

    /**
     * 获取敌机在水平方向上的移动速度。
     *
     * @return 水平速度（像素/帧）
     */
    public int getSpeedX() { return speedX; }

    /**
     * 获取敌机在垂直方向上的移动速度。
     *
     * @return 垂直速度（像素/帧）
     */
    public int getSpeedY() { return speedY; }
}
