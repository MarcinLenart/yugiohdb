/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

/**
 *
 * @author marcin
 */

public class Rarity implements Serializable {
    private static final long serialVersionUID = 1L;
    //@Id
    //@GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String boosterCode;
    private String rarity;
     
    //@ManyToOne
    private Booster booster;
    
    //@ManyToOne
    private Card card;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public String getRarity() {
        return this.rarity;
    }
    public void setRarity(String rarity) {
        this.rarity = rarity;
    }
    
    public Booster getBooster() {
        return this.booster;
    }
    public void setBooster(Booster booster) {
        this.booster = booster;
    }
    
    public Card getCard() {
        return this.card;
    }
    public void setCard(Card card) {
        this.card = card;
    }
    
    public String getBoosterCode() {
        return this.boosterCode;
    }
    public void setBoosterCode(String boosterCode) {
        this.boosterCode = boosterCode;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object b) {
        if(!(b instanceof Rarity))
            return false;
        Rarity bb = (Rarity)b;
        if(this.card.equals(bb.getCard()) && this.booster.equals(bb.getBooster()) && this.rarity.equals(bb.getRarity()))
            return true;
        return false;
    }
    
    @Override
    public String toString() {
        return this.booster.getId() + " (" + this.card.getName() + " - " + this.rarity + ")";
    }
    
}