package cn.edu.scnu.aircraft;

/**
 * @author 黄彪骐
 */
public enum EnemyType {
    MOB(10,10,0,3), 
    ELITE(20,20,0,5), 
    VETERAN(30,30,3,7),
    ACE(50,50,5,9), 
    BOSS(100,100,5,0);

    private int score;
    private int hp;
    private int speedX;
    private int speedY;

    EnemyType(int score,int hp,int speedX,int speedY){
        this.score = score;
        this.hp = hp;
        this.speedX = speedX;
        this.speedY = speedY;
    }

    public int getScore(){
        return score;
    }

    public int getHp() {
        return hp;
    }

    public int getSpeedX() {
        return speedX;
    }

    public int getSpeedY() {
        return speedY;
    }
    
}