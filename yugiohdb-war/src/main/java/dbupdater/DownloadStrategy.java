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
//Strategia
public interface DownloadStrategy {
    public boolean createDB(Config c);
    public boolean updateDB(Config c);
}
