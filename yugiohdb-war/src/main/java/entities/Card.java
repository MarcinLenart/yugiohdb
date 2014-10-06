/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

/**
 *
 * @author marcin
 */
/*@Entity
@NamedQueries({
    @NamedQuery(
            name="searchByText",
            query="select u from Card u where u.name like :name"
    )
})*/
public class Card implements Serializable {
    //@ManyToMany(mappedBy = "ownedCards")
    private List<Users> owners;
    private static final long serialVersionUID = 1L;
    //@Id
    //@GeneratedValue(strategy = GenerationType.AUTO)
    private String id;    
    private String name;
    private String cardType; //monster/spell/trap
    private String monsterAttribute;   
    private List<String> monsterTypes;
    private int monsterLvl;
    private String[] monsterStats;
    private String STproperty;
    private String cardNumber;
    private List<String> monsterEffectTypes;
    
    //@Lob
    private String description;
    
    public static final String MONSTER = "monster";
    public static final String SPELL = "spell";
    public static final String TRAP = "trap";
    
    public static final String PATH = "cards_picture/";
    
    /*@ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "card_boosters", joinColumns= {
        @JoinColumn(name = "cards_ID")
    }, inverseJoinColumns = {
        @JoinColumn(name = "boosters_ID")
    })*/
    private List<Booster> boosterki;
    
    
    //@OneToMany(mappedBy = "card")
    private List<Rarity> rarity;
    
    public List getBoosterki() {
        if(this.boosterki == null)
            this.boosterki = new ArrayList<Booster>();
        return boosterki;
    }
    public void setBoosterki(List<Booster> _boost) {
        this.boosterki = _boost;
    }
    
    public List getRarity() {
        if(this.rarity == null)
            this.rarity = new ArrayList<Rarity>();
        return rarity;
    }
    public void setRarity(List _rar) {
        this.rarity = _rar;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }    
    
    public String getName() {
        return name;
    }
    public void setName(String _name) {
        name = _name;
    }
    
    public String getCardType() {
        return cardType;
    }
    public void setCardType(String _cardType) {
        cardType = _cardType;
    }
    
    public String getMonsterAttribute() {
        return monsterAttribute;
    }
    public void setMonsterAttribute(String _monsterAttribute) {
        monsterAttribute = _monsterAttribute;
    }
    
    public List<String> getMonsterTypes() {
        return monsterTypes;
    }
    public void setMonsterTypes(List _monsterTypes) {
        monsterTypes = _monsterTypes;
    }
    
    public int getMonsterLvl() {
        return monsterLvl;
    }
    public void setMonsterLvl(int _monsterLvl) {
        monsterLvl = _monsterLvl;
    }
    
    public String[] getMonsterStats() {
        return monsterStats;
    }
    public void setMonsterStats(String[] _monsterStats) {
        monsterStats = _monsterStats;
    }
    
    public String getSTproperty() {
        return STproperty;
    }
    public void setSTproperty(String _STproperty) {
        STproperty = _STproperty;
    }
    
    public String getCardNumber() {
        return cardNumber;
    }
    public void setCardNumber(String _cardNumber) {
        cardNumber = _cardNumber;
    }
    
    public List getMonsterEffectTypes() {
        if(this.monsterEffectTypes == null) {
            this.monsterEffectTypes = new ArrayList<String>();
            this.monsterEffectTypes.add("N/A");
        }
        return monsterEffectTypes;
    }
    public void setMonsterEffectTypes(List _monsterEffectTypes) {
        monsterEffectTypes = _monsterEffectTypes;
    }
    
    public String getDescription() {
        return description;
    }
    public void setDescription(String _description) {
        description = _description;
    }    

    @Override
    public boolean equals(Object b) {
        if(!(b instanceof Card))
            return false;
        Card bb = (Card)b;
        if(this.id.equals(bb.getId()))
            return true;
        return false;
    }
    
    @Override
    public String toString() {
        String st = "Card: " +
                "\nRodzaj: " + this.getCardType() +
                "\nName: " + this.getName() +                
                "\nNumber: " + this.getId();
        if(this.getCardType().equals(MONSTER))
            st+= 
                "\nAttribute: " + this.getMonsterAttribute() +
                "\nTypes: " + this.getMonsterTypes().toString() +
                "\nATK/DEF: " + this.getMonsterStats()[0] + "/" + this.getMonsterStats()[1] +
                "\nLevel: " + this.getMonsterLvl() +
                "\nEffect types: " + this.getMonsterEffectTypes().toString();
        if(this.getCardType().equals(SPELL) || this.getCardType().equals(TRAP))
            st+=
                "\nSTProperty: " + this.getSTproperty();
        
        st+=
                "\n\nDescription: \n" + this.getDescription();
        
        return st;
    }
    
}
