package dbupdater;
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author marcin
 */
public class ObservableImpl implements Observable{
    private static ArrayList<Observer> observers;
    
    protected ArrayList<Observer> getObservers() {
        if(observers==null)
            observers = new ArrayList<Observer>();
        return observers;
    }
    @Override
    public void addObserver(Observer ob) {        
        getObservers().add(ob);
    }

    @Override
    public void deleteObserver(Observer ob) {
        getObservers().remove(ob);
    }
    
    protected void updateNotify(String msg) {
        if(getObservers().isEmpty())
            return;
        for(Observer o : getObservers()) {
            o.dbHasBeenUpdated(msg);
        }
    }
    
    protected void errorNotify(String msg) {
        if(getObservers().isEmpty())
            return;
        for(Observer o : getObservers()) {
            o.updateError(msg);
        }
    }
}
