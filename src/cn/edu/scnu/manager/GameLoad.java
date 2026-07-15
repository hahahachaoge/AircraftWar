package cn.edu.scnu.manager;

import cn.edu.scnu.element.ElementObj;
import java.util.HashMap;
import java.util.Map;

/**
 * 负责加载游戏资源（如图片、音效等），并通过反射机制动态创建游戏元素对象。
 * 维护一个对象缓存映射表，提供按 key 快速获取已加载的 ElementObj 实例的功能。
 *
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public class GameLoad {
    private static ElementManager em = ElementManager.getManager();
    public static Map<String, ElementObj> objMap = new HashMap<>();

    /**
     * 根据指定的键名从对象缓存映射中获取对应的游戏元素对象。
     *
     * @param key 元素对象的标识键，通常为图片资源文件名或预定义的枚举名称
     * @return 与 key 对应的 ElementObj 实例；若映射中不存在该键，则返回 null
     */
    public static ElementObj getObj(String key) { return objMap.get(key); }
}
