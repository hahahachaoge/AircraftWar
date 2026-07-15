package cn.edu.scnu.music;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.*;

/**
 * @author 黄彪骐
 */
public class MusicThread extends Thread {
    private String filePath;
    private boolean loop;
    private volatile boolean isRunning;

    private AudioInputStream stream;
    private SourceDataLine line;

    

    public MusicThread(String filePath, boolean loop) {
        this.filePath = filePath;
        this.loop = loop;
    }

    // 内部清理（不改标志）
    private void closeResources() {
        if (line != null) {
            line.drain();
            line.stop();
            line.close();
            // line = null;
        }
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            stream = null;
        }
    }

    /**
     * 停止播放
     */
    public void stopPlayback() {
        isRunning = false;
        closeResources();
    }

    // 播放逻辑
    public void play() throws Exception {
        while (loop && isRunning) {
            // 确保上一轮资源已清理（安全）
            closeResources();

            // 完整的一次播放流程
            stream = AudioSystem.getAudioInputStream(new File(filePath));
            AudioFormat format = stream.getFormat();
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();

            // 设置音效音量
            if (line.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gainControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(6.0f);

            }

            byte[] buffer = new byte[8192];
            int bytesRead;
            do {
                // 每次读取前可额外检查 isRunning，加快响应
                if (!isRunning)
                    break;
                bytesRead = stream.read(buffer);
                if (bytesRead != -1) {
                    line.write(buffer, 0, bytesRead);
                }
            } while (bytesRead != -1 && isRunning);

            // 播放完一遍，清理资源（不改 isRunning，以便下一轮重新打开）
            closeResources();
        }
    }

    public boolean isPlaying() {
        return line != null && line.isActive();
    }

    @Override
    public void run() {
        isRunning = true;
        try {
            play();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            isRunning = false;
        }
    }
}