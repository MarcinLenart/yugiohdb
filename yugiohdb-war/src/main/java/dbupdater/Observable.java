package dbupdater;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author marcin
 */
public interface Observable {
    public void addObserver(Observer ob);
    public void deleteObserver(Observer ob);
}
