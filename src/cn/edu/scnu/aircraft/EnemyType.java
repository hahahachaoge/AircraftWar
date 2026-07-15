package cn.edu.scnu.aircraft;

/**
 * 敌机类型枚举，定义了游戏中所有敌机种类的属性配置。
 * <p>
 * 每种敌机类型包含得分、生命值、水平速度和垂直速度四项基本属性。
 * 系统预定义了五种敌机类型：MOB（普通敌机）、ELITE（精英敌机）、
 * VETERAN（老兵敌机）、ACE（王牌敌机）和BOSS（Boss敌机），
 * 它们按难度递增排列，为游戏提供差异化的敌机行为与挑战。
 * </p>
 *
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public enum EnemyType {
    MOB(10,10,0,3),
    ELITE(20,50,0,5),
    VETERAN(30,50,3,7),
    ACE(50,50,5,9),
    BOSS(100,100,5,0);

    private int score;
    private int hp;
    private int speedX;
    private int speedY;

    /**
     * 构造一个敌机类型实例。
     *
     * @param score  击落该类型敌机可获得的分数
     * @param hp     该类型敌机的生命值
     * @param speedX 该类型敌机在水平方向上的移动速度
     * @param speedY 该类型敌机在垂直方向上的移动速度
     */
    EnemyType(int score,int hp,int speedX,int speedY){
        this.score = score;
        this.hp = hp;
        this.speedX = speedX;
        this.speedY = speedY;
    }

    /**
     * 获取击落该类型敌机可获得的分数。
     *
     * @return 该敌机类型对应的分数值
     */
    public int getScore(){
        return score;
    }

    /**
     * 获取该类型敌机的生命值（即需要承受多少次攻击才能被击落）。
     *
     * @return 该敌机类型对应的生命值
     */
    public int getHp() {
        return hp;
    }

    /**
     * 获取该类型敌机在水平方向上的移动速度。
     *
     * @return 水平方向速度值
     */
    public int getSpeedX() {
        return speedX;
    }

    /**
     * 获取该类型敌机在垂直方向上的移动速度。
     *
     * @return 垂直方向速度值
     */
    public int getSpeedY() {
        return speedY;
    }

}