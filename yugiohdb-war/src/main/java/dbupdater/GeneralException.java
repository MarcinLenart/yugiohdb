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
public class GeneralException extends Throwable{
    private String message;
    
    public GeneralException(String mess) {
        this.message = mess;
    }
    
    @Override
    public String getMessage() {
        return this.message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}