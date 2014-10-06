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
public class Config {
    public boolean simpleCheck;
    public boolean savePictures;
    public String boosterPath = "boosters_picture/";
    public String cardPath = "cards_picture/";
    
    public Config(boolean simpleCheck, boolean savePic) {
        this.savePictures = savePic;
        this.simpleCheck = simpleCheck;
    }
    
    public static Config getConfiguration() {
        return new Config(false, false);
    }
}
