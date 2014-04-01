package me.sablednah.legendquest.events;

import java.util.ArrayList;
import java.util.EventListener;


public class EventDispatcher {
    
    private final ArrayList<SkillListener> listeners = new ArrayList<SkillListener>();
    private final Object treeLock = new Object();
    
    public void fire(SkillEvent event) {
        synchronized (this.treeLock) {
            for (SkillListener listener : this.listeners)
                event.dispatch(listener);
        }
    }
    
    public void fire(SkillEvent event, EventListener listener) {
        if (!(listener instanceof SkillListener)) {
            return;
        }
        synchronized (this.treeLock) {
            if (this.listeners.contains(listener))
                event.dispatch((SkillListener) listener);
        }
    }
    
    public void add(EventListener eventListener) {
        synchronized (this.treeLock) {
            if ((!this.listeners.contains(eventListener)) &&
                    ((eventListener instanceof SkillListener)))
                this.listeners.add((SkillListener) eventListener);
        }
    }
    
    public void remove(EventListener eventListener) {
        synchronized (this.treeLock) {
            int id = this.listeners.indexOf(eventListener);
            if (id != -1)
                this.listeners.remove(id);
        }
    }
}