package cn.edu.scnu.manager;

import cn.edu.scnu.element.ElementObj;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 以 {@link GameElement} 枚举为分类键，管理游戏中所有 {@link ElementObj} 实例的生命周期。
 * 提供元素的增删查操作，是整个游戏实体对象的统一管理入口。
 *
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public class ElementManager {
    private static ElementManager instance = null;
    private Map<GameElement, List<ElementObj>> gameElements;

    /**
     * 私有构造方法，禁止外部直接实例化；初始化元素容器
     */
    private ElementManager() { init(); }

    /**
     * 获取单例实例（线程安全的懒加载）
     *
     * @return ElementManager 唯一实例
     */
    public static synchronized ElementManager getManager() {
        if (instance == null) instance = new ElementManager();
        return instance;
    }

    /**
     * 初始化元素容器。为每一个 {@link GameElement} 枚举值创建一个空的 ArrayList，
     * 用于后续存放对应类别的游戏元素对象。
     */
    public void init() {
        gameElements = new HashMap<>();
        for (GameElement ge : GameElement.values())
            gameElements.put(ge, new ArrayList<>());
    }

    /**
     * 获取完整的元素映射表（按 GameElement 分类）
     *
     * @return key 为元素类型、value 为对应元素对象列表的 Map
     */
    public Map<GameElement, List<ElementObj>> getGameElements() { return gameElements; }

    /**
     * 向指定类型的元素列表中添加一个元素对象
     *
     * @param obj 待添加的游戏元素对象
     * @param ge  元素类型分类键
     */
    public void addElement(ElementObj obj, GameElement ge) {
        if (obj == null) return;
        gameElements.get(ge).add(obj);
    }

    /**
     * 根据元素类型获取该类别下所有的元素对象列表
     *
     * @param ge 元素类型枚举值
     * @return 对应类型的元素对象列表，若该类型未被注册则返回 null
     */
    public List<ElementObj> getElementsByKey(GameElement ge) {
        return gameElements.get(ge);
    }
}
