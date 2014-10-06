/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Marcin
 */
public class DBManager {
    private static DBManager instance;
    private EntityManagerFactory emf;
    
    private DBManager(){
    }
    
    public synchronized static DBManager getManager(){
        if(instance == null){
            instance = new DBManager();
        }
        return instance;
    }
    
    public EntityManagerFactory createEntityManagerFactory(){
        if(emf == null){
            emf = Persistence.createEntityManagerFactory("ygodb");
            
        }
        return emf;
    }
    
    public EntityManager createEntityManager(){
        return this.createEntityManagerFactory().createEntityManager();
    }
    
    public void closeEntityManagerFactory(){
        if(emf != null)
            emf.close();
    }
}