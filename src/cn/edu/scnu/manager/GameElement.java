package cn.edu.scnu.manager;

/**
 * 每个枚举常量代表一种游戏元素类型，在 ElementManager 中每种元素对应一个独立的对象列表，
 * 用于统一管理游戏中各类元素的创建、更新、销毁等生命周期。
 *
 * <p>元素类型包括：</p>
 * <ul>
 *   <li>{@link #MAPS} —— 地图/背景元素</li>
 *   <li>{@link #PLAY} —— 玩家战机（英雄机）</li>
 *   <li>{@link #ENEMY} —— 普通敌机</li>
 *   <li>{@link #BOSS} —— Boss 敌机</li>
 *   <li>{@link #PLAY_BULLET} —— 玩家子弹</li>
 *   <li>{@link #ENEMY_BULLET} —— 敌机子弹</li>
 *   <li>{@link #PROP} —— 道具/补给品</li>
 *   <li>{@link #DIE_EFFECT} —— 死亡特效</li>
 * </ul>
 *
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public enum GameElement {
    MAPS, PLAY, ENEMY, BOSS, PLAY_BULLET, ENEMY_BULLET, PROP, DIE_EFFECT
}
