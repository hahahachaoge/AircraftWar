package cn.edu.scnu.application;

import cn.edu.scnu.aircraft.AceEnemy;
import cn.edu.scnu.aircraft.BossEnemy;
import cn.edu.scnu.aircraft.EliteEnemy;
import cn.edu.scnu.aircraft.HeroAircraft;
import cn.edu.scnu.aircraft.MobEnemy;
import cn.edu.scnu.aircraft.VeteranEnemy;
import cn.edu.scnu.bullet.EnemyBullet;
import cn.edu.scnu.bullet.HeroBullet;
import cn.edu.scnu.prop.supply.BombSupply;
import cn.edu.scnu.prop.supply.FirePlusSupply;
import cn.edu.scnu.prop.supply.FireSupply;
import cn.edu.scnu.prop.supply.FrozenSupply;
import cn.edu.scnu.prop.supply.HpSupply;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 图片资源管理器。
 * <p>
 * 负责统一加载游戏中的所有静态图片资源（飞机、子弹、道具、背景、爆炸动画等），
 * 并建立类名到图片的映射关系，提供静态方法供游戏中其他模块按类名或对象实例快速获取对应图片。
 * 所有图片在类加载时通过静态代码块一次性初始化，加载失败则直接终止程序。
 * </p>
 *
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public class ImageManager {

    /**
     * 类名-图片映射表。
     * <p>
     * 键为各实体类（飞机、子弹、道具）的全限定类名字符串，
     * 值为对应的 {@link BufferedImage} 图片对象。
     * 可通过 {@link #get(String)} 或 {@link #get(Object)} 快速访问。
     * </p>
     */
    // 建立类名->图片的映射关系 静态 共享
    private static final Map<String, BufferedImage> CLASSNAME_IMAGE_MAP = new HashMap<>();

    /** 游戏背景图片 */
    public static BufferedImage BACKGROUND_IMAGE;

    /** 爆炸动画帧序列，共 8 帧，每帧尺寸 66×66 像素 */
    public static BufferedImage[] BANG_FRAMES = new BufferedImage[8];

    /** 英雄机图片（默认蓝色，单人玩家使用） */
    public static BufferedImage HERO_IMAGE;

    /** 英雄机图片（紫色，双人模式中玩家2使用） */
    public static BufferedImage HERO_PURPLE_IMAGE;

    /** 普通敌机图片 */
    public static BufferedImage MOB_ENEMY_IMAGE;

    /** 精英敌机图片 */
    public static BufferedImage ELITE_ENEMY_IMAGE;

    /** 精锐敌机图片 */
    public static BufferedImage VETERAN_ENEMY_IMAGE;

    /** 王牌敌机图片 */
    public static BufferedImage ACE_ENEMY_IMAGE;

    /** BOSS 敌机图片 */
    public static BufferedImage BOSS_ENEMY_IMAGE;

    /** 英雄机子弹图片 */
    public static BufferedImage HERO_BULLET_IMAGE;

    /** 敌机子弹图片 */
    public static BufferedImage ENEMY_BULLET_IMAGE;

    /** 加血道具图片 */
    public static BufferedImage HP_SUPPLY_IMAGE;

    /** 火力道具图片 */
    public static BufferedImage FIRE_SUPPLY_IMAGE;

    /** 超级火力道具图片 */
    public static BufferedImage FIREPLUS_SUPPLY_IMAGE;

    /** 炸弹道具图片 */
    public static BufferedImage BOMB_SUPPLY_IMAGE;

    /** 冰冻道具图片 */
    public static BufferedImage FROZEN_SUPPLY_IMAGE;

    // 静态代码块 在类加载时执行一次
    static {
        // 尝试加载图片 并建立映射关系 如果失败（抛出IOException）那么就退出系统（符合逻辑）
        try {

            HERO_IMAGE = ImageIO.read(new FileInputStream("src/images/hero.png"));
            HERO_PURPLE_IMAGE = ImageIO.read(new FileInputStream("src/images/hero_purple.png"));
            MOB_ENEMY_IMAGE = ImageIO.read(new FileInputStream("src/images/mob.png"));
            ELITE_ENEMY_IMAGE = ImageIO.read(new FileInputStream("src/images/elite.png"));
            VETERAN_ENEMY_IMAGE = ImageIO.read(new FileInputStream("src/images/elitePlus.png"));
            ACE_ENEMY_IMAGE = ImageIO.read(new FileInputStream("src/images/elitePro.png"));
            BOSS_ENEMY_IMAGE = ImageIO.read(new FileInputStream("src/images/boss.png"));

            HERO_BULLET_IMAGE = ImageIO.read(new FileInputStream("src/images/bullet_hero.png"));
            ENEMY_BULLET_IMAGE = ImageIO.read(new FileInputStream("src/images/bullet_enemy.png"));

            HP_SUPPLY_IMAGE = ImageIO.read(new FileInputStream("src/images/prop_blood.png"));
            FIRE_SUPPLY_IMAGE = ImageIO.read(new FileInputStream("src/images/prop_bullet.png"));
            FIREPLUS_SUPPLY_IMAGE = ImageIO.read(new FileInputStream("src/images/prop_bulletPlus.png"));
            BOMB_SUPPLY_IMAGE = ImageIO.read(new FileInputStream("src/images/prop_bomb.png"));
            FROZEN_SUPPLY_IMAGE = ImageIO.read(new FileInputStream("src/images/prop_freeze.png"));

            // 加载爆炸动画帧（bang.png 528×66，8 帧每帧 66×66）
            BufferedImage bangSheet = ImageIO.read(new FileInputStream("src/images/bang.png"));
            for (int i = 0; i < 8; i++) {
                BANG_FRAMES[i] = bangSheet.getSubimage(i * 66, 0, 66, 66);
            }

            // 建立类名->图片的映射关系
            // 飞机类
            CLASSNAME_IMAGE_MAP.put(HeroAircraft.class.getName(), HERO_IMAGE);
            CLASSNAME_IMAGE_MAP.put(MobEnemy.class.getName(), MOB_ENEMY_IMAGE);
            CLASSNAME_IMAGE_MAP.put(EliteEnemy.class.getName(), ELITE_ENEMY_IMAGE);
            CLASSNAME_IMAGE_MAP.put(VeteranEnemy.class.getName(), VETERAN_ENEMY_IMAGE);
            CLASSNAME_IMAGE_MAP.put(AceEnemy.class.getName(), ACE_ENEMY_IMAGE);
            CLASSNAME_IMAGE_MAP.put(BossEnemy.class.getName(), BOSS_ENEMY_IMAGE);
            // 子弹类
            CLASSNAME_IMAGE_MAP.put(HeroBullet.class.getName(), HERO_BULLET_IMAGE);
            CLASSNAME_IMAGE_MAP.put(EnemyBullet.class.getName(), ENEMY_BULLET_IMAGE);
            // 道具类
            CLASSNAME_IMAGE_MAP.put(HpSupply.class.getName(), HP_SUPPLY_IMAGE);
            CLASSNAME_IMAGE_MAP.put(FireSupply.class.getName(), FIRE_SUPPLY_IMAGE);
            CLASSNAME_IMAGE_MAP.put(FirePlusSupply.class.getName(), FIREPLUS_SUPPLY_IMAGE);
            CLASSNAME_IMAGE_MAP.put(BombSupply.class.getName(), BOMB_SUPPLY_IMAGE);
            CLASSNAME_IMAGE_MAP.put(FrozenSupply.class.getName(), FROZEN_SUPPLY_IMAGE);


        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * 根据类名字符串获取对应的图片。
     *
     * @param className 实体的全限定类名字符串（如 "cn.edu.scnu.aircraft.HeroAircraft"）
     * @return 该类名对应的 {@link BufferedImage} 图片对象；若映射表中不存在则返回 {@code null}
     */
    public static BufferedImage get(String className) {
        return CLASSNAME_IMAGE_MAP.get(className);
    }

    /**
     * 根据对象实例获取其所属类对应的图片。
     * <p>
     * 内部通过调用 {@link #get(String)} 并传入对象的全限定类名字符串实现。
     * 若传入 {@code null}，则直接返回 {@code null}。
     * </p>
     *
     * @param obj 游戏实体对象实例（飞机、子弹或道具等）
     * @return 该对象所属类对应的 {@link BufferedImage} 图片对象；
     *         若 obj 为 {@code null} 或映射表中不存在则返回 {@code null}
     */
    public static BufferedImage get(Object obj) {
        if (obj == null) {
            return null;
        }
        return get(obj.getClass().getName());
    }

}
