package cn.edu.scnu.prop;

import cn.edu.scnu.aircraft.AbstractAircraft;

/**
 * 获取道具接口，定义了从敌机获取道具的规范。
 * 实现该接口的类需要根据传入的敌机对象和随机数来决定生成何种道具。
 *
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public interface GetProp {
    AbstractProp obtainProp(AbstractAircraft enemyAircraft, double rand);
}
