package cn.edu.scnu.prop;

import cn.edu.scnu.aircraft.HeroAircraft;
import cn.edu.scnu.shoot.ShootStrategy;
import cn.edu.scnu.shoot.StraightShoot;

/**
 * 道具效果计时器。当英雄机拾取道具时，该计时器会在一段持续时间内保持道具效果，
 * 并在时间结束后恢复英雄机的原始射击策略。可通过 cancel 方法提前终止计时。
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public class PropEffectTimer implements Runnable {
    private int durationSeconds;
    private HeroAircraft heroAircraft;
    private ShootStrategy originalStrategy = new StraightShoot(3);
    private volatile boolean isCancelled = false;

    public PropEffectTimer(int durationSeconds, HeroAircraft heroAircraft) {
        this.durationSeconds = durationSeconds;
        this.heroAircraft = heroAircraft;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < durationSeconds; i++) {
                if (isCancelled) {
                    break;
                }
                Thread.sleep(1000);
            }
            if (!isCancelled) {
                heroAircraft.setShootStrategy(originalStrategy);
                System.out.println("恢复原始射击.......");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void cancel() {
        this.isCancelled = true;
    }
}
