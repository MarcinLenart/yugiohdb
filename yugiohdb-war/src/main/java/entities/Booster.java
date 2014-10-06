/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;

/**
 *
 * @author marcin
 */

public class Booster implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;
    private String name;
    private String type;
    private String numberOfCards;
    private String coverCard;
    private boolean checked;
    
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date releseDate;
    
    /*@ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "card_boosters", joinColumns= {
        @JoinColumn(name = "boosters_ID")
    }, inverseJoinColumns = {
        @JoinColumn(name = "cards_ID")
    })*/
    private List<Card> cards;
    
    @OneToMany(mappedBy = "booster")
    private List<Rarity> rarity;
    
    public static final String PATH = "boosters_picture/";
    
    public List getRarity() {
        if(this.rarity == null)
            this.rarity = new ArrayList<Rarity>();
        return rarity;
    }
    public void setRarity(List _rar) {
        this.rarity = _rar;
    }
    
    public List getCards() {
        return this.cards;
    }
    public void setCards(List<Card> _card) {
        this.cards = _card;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public String getNumberOfCards() {
        return this.numberOfCards;
    }

    public void setNumberOfCards(String numberOfCards) {
        this.numberOfCards = numberOfCards;
    }
    
    public String getCoverCard() {
        return this.coverCard;
    }

    public void setCoverCard(String coverCard) {
        this.coverCard = coverCard;
    }
    
    public Date getReleseDate() {
        return this.releseDate;
    }

    public void setReleseDate(Date releseDate) {
        this.releseDate = releseDate;
    }
    
    public boolean getChecked() {
        return this.checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Booster)) {
            return false;
        }
        Booster other = (Booster) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Booster[ id=" + id + " ]";
    }
    
}
