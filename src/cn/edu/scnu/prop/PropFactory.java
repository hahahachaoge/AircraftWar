package cn.edu.scnu.prop;

import cn.edu.scnu.prop.supply.BombSupply;
import cn.edu.scnu.prop.supply.FirePlusSupply;
import cn.edu.scnu.prop.supply.FireSupply;
import cn.edu.scnu.prop.supply.FrozenSupply;
import cn.edu.scnu.prop.supply.HpSupply;

/**
 * 道具简单工厂，根据道具类型创建对应的道具对象。
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public class PropFactory {
    public static AbstractProp createProp(PropType type, int x, int y) {
        switch (type) {
            case HP:
                return new HpSupply(x, y, 0, 3);
            case FIRE:
                return new FireSupply(x, y, 0, 3);
            case FIRE_PLUS:
                return new FirePlusSupply(x, y, 0, 3);
            case BOMB:
                return new BombSupply(x, y, 0, 3);
            case FROZEN:
                return new FrozenSupply(x, y, 0, 3);
            default:
                throw new IllegalArgumentException("Undefined : " + type);
        }
    }
}
