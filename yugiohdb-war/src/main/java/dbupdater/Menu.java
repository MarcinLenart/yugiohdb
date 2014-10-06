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

//dekorator
public class Menu extends ObservableImpl implements DownloadStrategy{
    DownloadStrategy mainStrategy;
    
    public void setMainStrategy(DownloadStrategy strategy) {
        this.mainStrategy = strategy;
    }
    
    @Override
    public boolean createDB(Config c) {
        if(!mainStrategy.createDB(c)) {
            errorNotify("");
            return false;
        }
        return true;
        
    }
    
    @Override
    public boolean updateDB(Config c) {
        if(!mainStrategy.updateDB(c)) {
            errorNotify("");
            return false;
        }
        return true;
    }
    
    public static void go() {
        Config configuration = Config.getConfiguration();
        
        Menu m = new Menu();
        m.setMainStrategy(new Wikia());
        m.createDB(configuration);
    }
}
