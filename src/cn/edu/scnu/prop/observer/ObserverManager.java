package cn.edu.scnu.prop.observer;

import java.util.ArrayList;
import java.util.List;

import cn.edu.scnu.prop.PropType;

/**
 * @author 黄彪骐
 */
public class ObserverManager {
    private static ObserverManager instance;
    private final List<EnemyObserver> bombObservers = new ArrayList<>();
    private final List<EnemyObserver> frozenObservers = new ArrayList<>();

    private ObserverManager() {
    }

    public static ObserverManager getInstance() {
        if (instance == null) {
            synchronized (ObserverManager.class) {
                if (instance == null) {
                    instance = new ObserverManager();
                }
            }
        }
        return instance;
    }

    public void addObserver(EnemyObserver observer, PropType propType) {
        switch (propType) {
            case BOMB:
                if (!bombObservers.contains(observer)) {
                    bombObservers.add(observer);
                }
                break;
            case FROZEN:
                if (!frozenObservers.contains(observer)) {
                    frozenObservers.add(observer);
                }
                break;
            default:
                break;
        }

    }

    public void removeObserver(EnemyObserver observer, PropType propType) {
        switch (propType) {
            case BOMB:
                bombObservers.remove(observer);
                break;
            case FROZEN:
                frozenObservers.remove(observer);
                break;
            default:
                break;
        }

    }

    public List<EnemyObserver> getObservers(PropType propType) {
        switch (propType) {
            case BOMB:
                return bombObservers;
            case FROZEN:
                return frozenObservers;
            default:
                return new ArrayList<>();
        }
    }

    public void notifyObservers(PropType propType) {
        List<EnemyObserver> copyList;
        switch (propType) {
            case BOMB:
                copyList = new ArrayList<>(bombObservers);
                for (EnemyObserver observer : copyList) {
                    observer.onBombActivated();
                }
                break;
            case FROZEN:
                copyList = new ArrayList<>(frozenObservers);
                for (EnemyObserver observer : copyList) {
                    observer.onFrozenActivated();
                }
                break;
            default:
                break;
        }

    }
}
