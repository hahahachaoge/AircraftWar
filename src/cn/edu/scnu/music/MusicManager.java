package cn.edu.scnu.music;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

/**
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public class MusicManager {

    private static volatile MusicManager instance;

    private MusicManager() {
        initAudioPaths();
        preloadAllClips();
    }

    public static MusicManager getInstance() {
        if (instance == null) {
            synchronized (MusicManager.class) {
                if (instance == null) {
                    instance = new MusicManager();
                }
            }
        }
        return instance;
    }

    private final Map<MusicType, String> audioPaths = new HashMap<>();
    // 所有音频（BGM + 音效）统一用 Clip 预加载
    private final Map<MusicType, Clip> clips = new ConcurrentHashMap<>();
    // 当前正在播放的 BGM
    private MusicType currentBgm = null;

    public enum MusicType {
        BGM,
        BGM_BOSS,
        BOMB_EXPLOSION,
        BULLET_HIT,
        GAME_OVER,
        GET_SUPPLY
    }

    private void initAudioPaths() {
        audioPaths.put(MusicType.BGM_BOSS, "src/videos/bgm_boss.wav");
        audioPaths.put(MusicType.BGM, "src/videos/bgm.wav");
        audioPaths.put(MusicType.BOMB_EXPLOSION, "src/videos/bomb_explosion.wav");
        audioPaths.put(MusicType.BULLET_HIT, "src/videos/bullet_hit.wav");
        audioPaths.put(MusicType.GAME_OVER, "src/videos/game_over.wav");
        audioPaths.put(MusicType.GET_SUPPLY, "src/videos/get_supply.wav");
    }

    private void preloadAllClips() {
        // 同步预加载所有音频（启动时一次性加载，换取运行时流畅）
        for (MusicType key : audioPaths.keySet()) {
            try {
                AudioInputStream ais = AudioSystem.getAudioInputStream(new File(audioPaths.get(key)));
                Clip clip = AudioSystem.getClip();
                clip.open(ais);
                ais.close();

                // 设置音量
                if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                    FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    if (key == MusicType.BGM || key == MusicType.BGM_BOSS) {
                        gainControl.setValue(6.0f);  // 背景音乐音量
                    } else if (key == MusicType.BOMB_EXPLOSION) {
                        gainControl.setValue(-20.0f);
                    } else {
                        gainControl.setValue(-8.0f);
                    }
                }
                clips.put(key, clip);
            } catch (Exception e) {
                System.err.println("预加载音频失败: " + key);
            }
        }
    }

    /**
     * 播放背景音乐（瞬间切换，零卡顿）
     */
    public void playBgmMusic(MusicType key, boolean loop) {
        // 如果已经是这个 BGM 在播放，不做任何事
        if (currentBgm == key && clips.containsKey(key) && clips.get(key).isRunning()) {
            return;
        }
        // 停止当前 BGM
        stopCurrentBgm();

        Clip clip = clips.get(key);
        if (clip == null) return;

        clip.setFramePosition(0);
        if (loop) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } else {
            clip.start();
        }
        currentBgm = key;
    }

    /**
     * 停止指定 BGM
     */
    public void stopBgmMusic(MusicType key) {
        if (currentBgm == key) {
            stopCurrentBgm();
        }
    }

    private void stopCurrentBgm() {
        if (currentBgm != null) {
            Clip clip = clips.get(currentBgm);
            if (clip != null) {
                clip.stop();
                clip.setFramePosition(0);
            }
            currentBgm = null;
        }
    }

    /**
     * 播放短音效
     */
    public void playEffectMusic(MusicType key) {
        Clip clip = clips.get(key);
        if (clip == null) return;
        // Clip 的 start 是非阻塞的，立即返回
        clip.setFramePosition(0);
        clip.start();
    }

    /**
     * 停止所有音乐
     */
    public void stopAllMusic() {
        stopCurrentBgm();
        for (Clip clip : clips.values()) {
            clip.stop();
            clip.setFramePosition(0);
        }
    }

    /**
     * 游戏完全结束时调用
     */
    public void shutdown() {
        stopAllMusic();
        for (Clip clip : clips.values()) {
            clip.close();
        }
    }
}
