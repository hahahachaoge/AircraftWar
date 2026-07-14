package cn.edu.scnu.music;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

/**
 * @author 黄彪骐
 */
public class MusicManager {

    private static volatile MusicManager instance;

    private MusicManager() {
        initAudioPaths();
        preloadedAllSoundClips();
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

    // 音乐名字 和 音频文件路径 的对应哈希表
    private final Map<MusicType, String> audioPaths = new HashMap<>();
    // 音乐名字 和 音频线程 的对应哈希表 而且是正在播放的音乐
    private final Map<MusicType, MusicThread> activeBgmThreads = new ConcurrentHashMap<>();
    // 音乐名字 和 音频播放器 的对应哈希表 而且是短音效
    private final Map<MusicType, Clip> preloadedSoundClips = new ConcurrentHashMap<>();
    // 用于短音效的线程池 最多可以同时播放4种短音效
    private final ExecutorService soundEffectExecutor = Executors.newFixedThreadPool(4);

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

    private void preloadedAllSoundClips() {
        for (MusicType key : audioPaths.keySet()) {
            if (key.equals(MusicType.BGM) || key.equals(MusicType.BGM_BOSS)) {
                continue;
            }
            preloadedSoundClip(key);
        }
    }

    private void preloadedSoundClip(MusicType key) {
        // 异步加载 提高效率 保障游戏启动流畅而不卡顿
        soundEffectExecutor.submit(() -> {
            try {
                AudioInputStream ais = AudioSystem.getAudioInputStream(new File(audioPaths.get(key)));
                Clip clip = AudioSystem.getClip();
                clip.open(ais);
                ais.close();

                // 设置音效音量
                if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                    FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    if (key == MusicType.BOMB_EXPLOSION) {
                        gainControl.setValue(-20.0f);
                    } else {
                        gainControl.setValue(-8.0f);
                    }
                }
                // clip先不用start
                preloadedSoundClips.put(key, clip);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void playBgmMusic(MusicType key, boolean loop) {
        stopBgmMusic(key);
        MusicThread thread = new MusicThread(audioPaths.get(key), loop);
        thread.start();
        activeBgmThreads.put(key, thread);
    }

    public void stopBgmMusic(MusicType key) {
        MusicThread bgm = activeBgmThreads.get(key);
        if (bgm != null) {
            bgm.stopPlayback();
            activeBgmThreads.remove(key);
        }
    }

    public void playEffectMusic(MusicType key) {
        if (!preloadedSoundClips.containsKey(key)) {
            return;
        }

        soundEffectExecutor.submit(() -> {
            Clip clip = preloadedSoundClips.get(key);
            synchronized (clip) {
                clip.setFramePosition(0);
                clip.start();
            }
        });
    }

    // 停止所有音频
    public void stopAllMusic() {
        // 停止长音频线程
        for (MusicThread t : activeBgmThreads.values())
            t.stopPlayback();
        activeBgmThreads.clear();
        // 停止所有正在播放的音效Clip
        for (Clip c : preloadedSoundClips.values()) {
            c.stop();
            c.setFramePosition(0);
        }
        // 注意：不要关闭线程池，除非游戏结束
    }

    // 游戏完全结束时调用
    public void shutdown() {
        stopAllMusic();
        soundEffectExecutor.shutdownNow(); // 关闭线程池
        for (Clip c : preloadedSoundClips.values())
            c.close(); // 释放音频资源
    }
}
