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
public interface Observer {
    public void dbHasBeenUpdated(String msg);
    
    public GeneralException updateError(String msg);
}
