package cn.edu.scnu.application.game;

import cn.edu.scnu.aircraft.AbstractAircraft;
import cn.edu.scnu.aircraft.EnemyType;
import cn.edu.scnu.aircraft.HeroAircraft;
import cn.edu.scnu.application.GameMode;
import cn.edu.scnu.application.ImageManager;
import cn.edu.scnu.application.Main;
import cn.edu.scnu.application.MainMenuFrame;
import cn.edu.scnu.prop.AbstractProp;
import cn.edu.scnu.prop.PropFactory;
import cn.edu.scnu.prop.PropType;
import cn.edu.scnu.rank.Difficulty;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author 黄彪骐
 */
public class TutorialGame extends AbstractGame {

    private String[] tips;
    private int tipIndex = 0;
    private boolean tutorialComplete = false;
    private boolean hasMoved = false;
    private boolean player2Action = false;
    private boolean hasPicked = false;
    private int enemyKillCount = 0;
    private final int requiredKills = 5;
    private boolean firstEnemyKilled = false;
    private long stepStartTime = 0;
    private final long MIN_STEP_DURATION = 2000;
    private int player2InitX, player2InitY;

    public TutorialGame(GameMode mode) {
        super(mode);
        this.difficulty = Difficulty.BEGINNER;
        initTutorialTips();
        if (gameMode == GameMode.DOUBLE && heroes.size() > 1) {
            player2InitX = heroes.get(1).getLocationX();
            player2InitY = heroes.get(1).getLocationY();
        }
        stepStartTime = System.currentTimeMillis();
    }

    private void initTutorialTips() {
        if (gameMode == GameMode.SINGLE) {
            tips = new String[]{
                    "欢迎来到教学关！\n鼠标拖动移动你的飞机",
                    "飞机将自动射击，消灭敌机",
                    "拾取道具",
                    "消灭 " + requiredKills + " 架敌机完成教学！"
            };
        } else {
            tips = new String[]{
                    "欢迎来到双人教学！\n玩家1（键盘）WASD控制左机\n玩家2（鼠标）控制右机",
                    "飞机将自动射击，消灭敌机",
                    "拾取道具",
                    "共同消灭 " + requiredKills + " 架敌机完成教学！"
            };
        }
    }

    @Override
    protected void initGameSettings() {
        try {
            ImageManager.BACKGROUND_IMAGE = ImageIO.read(new FileInputStream("src/images/bg.jpg"));
        } catch (IOException e) {
            BufferedImage defaultBg = new BufferedImage(Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = defaultBg.createGraphics();
            g2d.setColor(new Color(30, 30, 60));
            g2d.fillRect(0, 0, Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT);
            g2d.dispose();
            ImageManager.BACKGROUND_IMAGE = defaultBg;
        }

        this.initHeroHp = 9999;
        this.enemyMaxNumber = (gameMode == GameMode.DOUBLE) ? 4 : 2;
        this.enemySpawnCycle = 100;
        this.heroShootCycle = 10;
        this.enemyShootCycle = 100;
        this.enemyHpFactor = 0.5;
        this.enemySpeedFactor = 0.5;
        this.propRand = 0.6;
        this.difficultyLevelUpInterval = 0;
        this.scoreThreshold = Integer.MAX_VALUE;
    }

    @Override
    protected EnemyType getRandomEnemyType() {
        return EnemyType.MOB;
    }

    @Override
    protected boolean shouldSpawnBoss() {
        return false;
    }

    @Override
    protected void triggerProp(AbstractAircraft enemyAircraft) {
        if (enemyAircraft.notValid()) return;
        if (!firstEnemyKilled) {
            firstEnemyKilled = true;
            int propX = Math.max(0, Math.min(Main.WINDOW_WIDTH, enemyAircraft.getLocationX()));
            int propY = Math.max(0, Math.min(Main.WINDOW_HEIGHT, enemyAircraft.getLocationY()));
            AbstractProp forcedProp = PropFactory.createProp(PropType.HP, propX, propY);
            if (forcedProp != null) {
                props.add(forcedProp);
            }
            return;
        }
        AbstractProp newProp = enemyAircraft.obtainProp(enemyAircraft, propRand);
        if (newProp != null) {
            props.add(newProp);
        }
    }

    @Override
    protected void difficultyLevelUp() {
        // 教学关不升级
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 20));
        if (!tutorialComplete && tipIndex < tips.length) {
            String[] lines = tips[tipIndex].split("\n");
            int y = 100;
            for (String line : lines) {
                if (gameMode == GameMode.DOUBLE && line.contains("键盘")) {
                    g.setColor(Color.YELLOW);
                } else {
                    g.setColor(Color.WHITE);
                }
                g.drawString(line, 50, y);
                y += 30;
            }
        } else if (tutorialComplete) {
            g.setColor(Color.GREEN);
            g.drawString("教学完成！", 150, 350);
            g.drawString("即将返回主菜单...", 130, 390);
        }

        g.setColor(Color.CYAN);
        g.setFont(new Font("SansSerif", Font.BOLD, 18));
        g.drawString("敌机击杀: " + enemyKillCount + " / " + requiredKills, 50, 500);
    }

    @Override
    protected void crashCheckAction() {
        super.crashCheckAction();

        long now = System.currentTimeMillis();
        boolean enoughTime = (now - stepStartTime) >= MIN_STEP_DURATION;

        // 步骤0 → 步骤1：键盘玩家（左侧，index 0）移动 或 鼠标玩家（右侧，index 1）操作
        if (tipIndex == 0 && enoughTime) {
            boolean p1Moved = false;
            if (heroes.size() > 0) {
                HeroAircraft keyboardHero = heroes.get(0);
                // 单人模式下初始 X = WINDOW_WIDTH/2，双人模式下键盘初始 X = WINDOW_WIDTH/3
                int initX = (gameMode == GameMode.DOUBLE) ? Main.WINDOW_WIDTH / 3 : Main.WINDOW_WIDTH / 2;
                if (!hasMoved && keyboardHero.getLocationX() != initX) {
                    hasMoved = true;
                    p1Moved = true;
                }
            }

            boolean p2Acted = false;
            if (gameMode == GameMode.DOUBLE && heroes.size() > 1) {
                HeroAircraft mouseHero = heroes.get(1);
                if (!player2Action && (mouseHero.getLocationX() != player2InitX || mouseHero.getLocationY() != player2InitY)) {
                    player2Action = true;
                }
                p2Acted = player2Action;
            }

            if (p1Moved || p2Acted) {
                tipIndex++;
                stepStartTime = now;
            }
        }

        // 步骤1 → 步骤2：击杀>0 或 拾取道具
        if (tipIndex == 1 && enoughTime) {
            if (!hasPicked && !props.isEmpty()) {
                hasPicked = true;
            }
            if (hasPicked || enemyKillCount > 0) {
                tipIndex++;
                stepStartTime = now;
            }
        }

        // 步骤2 → 步骤3：击杀达到要求
        if (tipIndex == 2 && enoughTime && enemyKillCount >= requiredKills) {
            tipIndex++;
            stepStartTime = now;
        }

        if (enemyKillCount >= requiredKills && tipIndex < tips.length - 1 && enoughTime) {
            tipIndex = tips.length - 1;
            stepStartTime = now;
        }
    }

    @Override
    public void addScore(int points) {
        super.addScore(points);
        enemyKillCount++;
        if (enemyKillCount >= requiredKills) {
            tutorialComplete = true;
        }
    }

    @Override
    protected void checkResultAction() {
        if (tutorialComplete) {
            timer.cancel();
            musicManager.stopAllMusic();
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this, "恭喜完成教学！");
                closeGameWindow();
                new MainMenuFrame();
            });
        }
    }
}
