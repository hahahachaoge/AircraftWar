package cn.edu.scnu.prop;

import cn.edu.scnu.aircraft.AbstractAircraft;

/**
 * @author 黄彪骐
 */
public interface GetProp {
    AbstractProp obtainProp(AbstractAircraft enemyAircraft, double rand);
}
