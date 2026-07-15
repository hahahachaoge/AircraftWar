package cn.edu.scnu.controller;

import cn.edu.scnu.element.ElementObj;
import cn.edu.scnu.element.Plane;
import cn.edu.scnu.manager.ElementManager;
import cn.edu.scnu.manager.GameElement;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 全局输入监听器，统一处理键盘和鼠标事件，并将其分发给所有己方飞机（Plane）对象。
 * <p>
 * 键盘方面，维护一个按下的键集合（{@link #keySet}），避免长按时重复触发，
 * 在按键按下/释放时通过 {@link Plane#setKeyState(int, boolean)} 通知每架飞机。
 * 鼠标方面，将鼠标拖动/移动光标的坐标通过 {@link Plane#setTarget(int, int)}
 * 转发给所有玩家飞机，用于控制瞄准位置。
 * </p>
 *
 * @author 黄彪骐、岳孝彬、丁俊哲
 */
public class GameListener implements KeyListener, MouseMotionListener {
    /** 元素管理器，用于获取当前存活的所有游戏元素。 */
    private ElementManager em = ElementManager.getManager();

    /** 当前处于按下状态的键盘按键集合，用于防抖（防止持续按住时重复触发）。 */
    private Set<Integer> keySet = new HashSet<>();

    /**
     * 键盘按键被按下时调用。若该键尚未记录为按下状态，则加入集合，
     * 并通知所有玩家飞机将该键标记为按下。
     *
     * @param e 键盘事件对象，包含被按下键的键码等信息
     */
    @Override public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (keySet.contains(key)) return;
        keySet.add(key);
        for (ElementObj obj : em.getElementsByKey(GameElement.PLAY))
            if (obj instanceof Plane) ((Plane) obj).setKeyState(key, true);
    }

    /**
     * 键盘按键被释放时调用。从按键集合中移除该键，
     * 并通知所有玩家飞机将该键标记为释放。
     *
     * @param e 键盘事件对象，包含被释放键的键码等信息
     */
    @Override public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (!keySet.contains(key)) return;
        keySet.remove(key);
        for (ElementObj obj : em.getElementsByKey(GameElement.PLAY))
            if (obj instanceof Plane) ((Plane) obj).setKeyState(key, false);
    }

    /**
     * 键盘按键被"键入"（按下并释放）时调用。
     * 本游戏不需要处理该事件，方法体为空。
     *
     * @param e 键盘事件对象
     */
    @Override public void keyTyped(KeyEvent e) {}

    /**
     * 鼠标拖拽（按下并移动）时调用。将当前鼠标坐标转发给所有玩家飞机，
     * 用于更新瞄准目标位置。
     *
     * @param e 鼠标事件对象，包含鼠标当前的 X、Y 坐标
     */
    @Override public void mouseDragged(MouseEvent e) {
        for (ElementObj obj : em.getElementsByKey(GameElement.PLAY))
            if (obj instanceof Plane) ((Plane) obj).setTarget(e.getX(), e.getY());
    }

    /**
     * 鼠标移动（未按下按键的纯移动）时调用。
     * 本游戏中鼠标移动与拖拽处理逻辑相同，直接委托给 {@link #mouseDragged(MouseEvent)}。
     *
     * @param e 鼠标事件对象，包含鼠标当前的 X、Y 坐标
     */
    @Override public void mouseMoved(MouseEvent e) { mouseDragged(e); }
}
