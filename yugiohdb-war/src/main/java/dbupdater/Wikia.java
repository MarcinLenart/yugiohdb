package dbupdater;

import entities.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utilities.*;


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author marcin
 */

public class Wikia extends ObservableImpl implements DownloadStrategy {
    private EntityManager em;    
    private String typeOfAddon;
    private boolean update;
    private Config configuration;

    public boolean savePhoto(String path, String linkDoFoci) {
        //sprawdzanie czy fota istnieje a jeśli nie to pobranie foty boostera
        if(!configuration.savePictures)
            return true;
        File _f = new File(path + ".png");
        if(!_f.exists()) {
            for(int i=0; i<10; i++) {
                try {
                    BufferedImage buf = ImageIO.read(new URL(linkDoFoci));
                    ImageIO.write(buf, "png", _f);
                    break;
                } catch (IOException ex) {
                    if(i>=10)
                        return false;
                    
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ex1) {}
                }
            }
        }
        return true;
    }
    
    public Date getDate(String dataS) throws GeneralException {
        Date data = null;
        String[] dateFormat = new String[9];
        dateFormat[0] = "MMMM dd, yyyy"; dateFormat[1] = "dd'th' MMMM yyyy"; dateFormat[2] = "dd MMMM, yyyy"; dateFormat[3] = "'Early' MMMM yyyy"; 
        dateFormat[4] = "'Late' MMMM yyyy"; dateFormat[5] = "MMMM, dd yyyy"; dateFormat[6] = "MMMM, yyyy"; dateFormat[7] = "'08 - 'dd MMMM, yyyy";
        dateFormat[8] = "MMMM yyyy";
        
        for (String dateFormat1 : dateFormat) {
            SimpleDateFormat df = new SimpleDateFormat(dateFormat1, Locale.US);
            try {
                data = df.parse(dataS);
                break;
            } catch (ParseException ex) {
                //Logger.log(Logger.TYPE.LOG, "warning, błąd parsowania daty wyjścia boostera, format: " + dateFormat[i]);
                data = null;
                //return;
            }
        }
        if(data==null)
            throw new GeneralException("Error! Nie można zrozumieć daty wydania dodatku");
        return data;
    }
    
    public Booster createBooster(Element main) throws GeneralException {
        Booster b = new Booster();
        
        //ustawianie id i nazwy
        b.setId(main.text());
        b.setName(main.text());
        
        //ustawianie typu dodatku
        b.setType(typeOfAddon);
        if(configuration.simpleCheck)
            return b;
        String url;
        //tworzenie linku do wnętrza dodatku
        url = "http://yugioh.wikia.com" + main.attr("href");
        
        //specjalny przypadek dla premium packow
        if(b.getName().toLowerCase().contains("premium pack"))
            url = "http://yugioh.wikia.com" + main.attr("href") + "_(TCG)";
        
        if(b.getName().contains("Gates of the Underworld"))
            url = "http://yugioh.wikia.com/wiki/Structure_Deck:_Gates_of_the_Underworld";
        
        if(b.getName().contains("Lost Sanctuary"))
            url = "http://yugioh.wikia.com/wiki/Structure_Deck:_Lost_Sanctuary_(TCG)";
        
        if(b.getName().contains("Warrior's Strike"))
            url = "http://yugioh.wikia.com/wiki/Structure_Deck:_Warrior%27s_Strike_(TCG)";
        
        if(b.getName().contains("Legacy of Darkness"))
            b.setNumberOfCards("101");
        
        //łączenie się z dodatkiem i pobieranie strony
        Document doc2 = connectToSite(url);

        //tabelka z danymi nt boosta
        Elements boost_data = doc2.select("#mw-content-text table.infobox tr");
        //if(!boost_data.text().equals(""))
        if(!boost_data.isEmpty()) {
            //sprawdzanie czy fota istnieje a jeśli nie to pobranie foty boostera
            String linkDoFoci = doc2.select("#mw-content-text table.infobox tr:nth-of-type(2) a").attr("href");
            if(!savePhoto(Booster.PATH + b.getId(), linkDoFoci))
                Logger.log(Logger.TYPE.LOG, "Warrning, nie zapisano foty dla dodatku: " + b.getName());
            
            //pętla po elementach w głównej tabeli dodatku
            for(Element el : boost_data) {
                //ustawianie liczby kart  w dodatku
                if(el.text().contains("Number of cards")) {
                    if(!el.select("td").text().contains("OCG"))
                        b.setNumberOfCards(el.select("td").text().split(" ")[0]);
                    else {
                        b.setNumberOfCards(el.select("td ul li").last().text().split(" ")[0]);
                    }
                }

                //ustawianie nazwy cover karty
                if(el.text().contains("Cover card"))
                    b.setCoverCard(el.text().replace("Cover card ", ""));
                
                //ustawianie dnia wyjścia dodatku
                if(el.text().contains("English") && el.select("th span a.image").isEmpty()) {
                    String dataS;
                    if(!el.select("td ul li").isEmpty())
                        dataS = el.select("td ul li").first().text().replace("(Sneak Peek", "");
                    else
                        dataS = el.select("td").first().text().replace("(Sneak Peek", "");
                    Date d;
                    try {
                        d = getDate(dataS);
                        
                        if(b.getReleseDate()==null)
                            b.setReleseDate(d);
                        else 
                            if(d.before(b.getReleseDate()))
                                b.setReleseDate(d);
                    }catch(GeneralException e) {}
                }
                
                //ustawianie dnia wyjścia SD 2006 i 2009
                if(el.text().contains("Japanese") && el.select("th span a.image").isEmpty()) {
                    if(b.getName().contains("Starter Deck 2006") || b.getName().contains("Starter Deck 2009")) {
                        String dataS;
                        if(!el.select("td ul li").isEmpty())
                            dataS = el.select("td ul li").first().text().replace("(Sneak Peek", "");
                        else
                            dataS = el.select("td").first().text().replace("(Sneak Peek", "");
                        Date d;
                        try {
                            d = getDate(dataS);

                            if(b.getReleseDate()==null)
                                b.setReleseDate(d);
                            else 
                                if(d.before(b.getReleseDate()))
                                    b.setReleseDate(d);
                        }catch(GeneralException e) {}
                    }
                }
                    
            }
        } else {
            //Logger.log(Logger.TYPE.LOG, "Warrning, alternatywnie sprawdzam wnętrze boosta: " + b.getId());
            
            //nowa tabelka z danymi dodatku
            boost_data = doc2.select("#mw-content-text table tbody tr td ul li");

            //sprawdzanie czy fota istnieje a jeśli nie to pobranie foty boostera
            String linkDoFoci = doc2.select("#mw-content-text table tbody tr td a.image").first().attr("href");
            if(!savePhoto(Booster.PATH + b.getId(), linkDoFoci))
                Logger.log(Logger.TYPE.LOG, "Warrning, nie zapisano foty dla dodatku: " + b.getName());
            
            //ustawianie liczby kart  w dodatku
            b.setNumberOfCards("-1");
            
            for(Element el : boost_data) {
                //ustawianie nazwy cover karty
                if(el.text().contains("Cover card"))
                    b.setCoverCard(el.text().replace("Cover card: ", ""));

                //ustawianie dnia wyjścia dodatku
                if(el.text().contains("Release Date:") && !el.text().toLowerCase().contains("ocg")) {
                    if(b.getReleseDate()==null) {
                        String dataS;
                        if(!el.select("b").isEmpty())
                            dataS = el.select("b").first().text();
                        else
                            dataS = el.text().split(": ")[1];
                        b.setReleseDate(getDate(dataS));
                    }
                }

            }
        }
        
        //test czy znalazł datę wydania
        if(b.getReleseDate()==null)
            throw new GeneralException("Error! nie było daty angielskiego wydania! dodatek: " + b.getName());
        
        return b;
    }
    
    public Document connectToSite(String link2) throws GeneralException {
        //łączenie z stroną tworzonej karty
        if(!link2.startsWith("http://"))
            link2 = "http://yugioh.wikia.com" + link2;
        
        Document doc3 = null;
        for(int i=0; i<10; i++) {
            try {
                doc3 = Jsoup.connect(link2).get();
                break;
            } catch (IOException ex) {
                if(i>=10) {
                    Logger.log(Logger.TYPE.LOG, "error! próbowałem połączyć się ze stroną karty" + link2 + " " + i + " razy, wychodzę... \n exception: " + ex.getLocalizedMessage());
                    throw new GeneralException("Error! Nie udało się połączyć z stroną: " + link2);
                }
                try {
                    //Logger.log(Logger.TYPE.LOG, "warrning, nie udało się połączyć z stroną karty: " + link2 + ", próbuję jeszcze raz ");
                    Thread.sleep(2000);
                } catch (InterruptedException _ie) {}
            }
        }
        
        return doc3;
    }
    
    public Card createCard(String pseudoNazwaKarty, Booster b, String[] links, String cardType) throws GeneralException {
        Card c = new Card();
                
        if(links[0].equals(""))
            throw new GeneralException("Warning, link pusty, prawdopodobnie to nie karta, wyświetlam: " + pseudoNazwaKarty);
        
        //łączenie z stroną tworzonej karty
        Document doc3 = connectToSite(links[0]);
        
        for(int i=1; i<links.length; i++) {
            if(doc3.select("td.navbox-list").first()==null || doc3.select("th.cardtable-header").first()==null) {
                Logger.log(Logger.TYPE.LOG, "Warrning! normalny link do karty nie działa! używam alternatywnego, niedziałający link: " + links[0]);
                doc3 = connectToSite(links[i]);
                
                if(i==links.length-1)
                    throw new GeneralException("Error! nie mogę połączyć się ze stroną karty! karta: " + pseudoNazwaKarty + ", dodatek: " + b.getId());
                
            } else {
                break;
            }
        }

        //ustawianie id
        c.setId(pseudoNazwaKarty);

        //ustawianie nazwy karty
        if(!doc3.select("th.cardtable-header span").isEmpty())
            doc3.select("th.cardtable-header span").first().remove();
        
        c.setName(doc3.select("th.cardtable-header").first().text());
        
        //pobieranie tabelki z opisem karty
        Elements opisKarty = doc3.select("#mw-content-text table tbody tr");

        //ustawianie rodzaju karty
        cardType = cardType.toLowerCase();
        if(!cardType.equals("")) {            
            if(cardType.contains(Card.MONSTER)) {
                c.setCardType(Card.MONSTER);
            } else if(cardType.contains(Card.SPELL)) {
                c.setCardType(Card.SPELL);
            } else if(cardType.contains(Card.TRAP)) {
                c.setCardType(Card.TRAP);
            } else {
                throw new GeneralException("Error, rodz nie jest nullem ale nie można określić rodzaju karty, karta: " + pseudoNazwaKarty + ", dodatek: " + b.getId());
            }
        } else {
            if(opisKarty.hasText()) {
                if(!opisKarty.select("th a[title=Attribute]").text().equals("")) {                    
                    c.setCardType(Card.MONSTER);
                }
                else 
                    if(!opisKarty.select("[title=Property]").text().equals("")) {
                        if(!opisKarty.select("td a[title=Spell Card]").text().equals(""))
                            c.setCardType(Card.SPELL);
                        else 
                            if(!opisKarty.select("td a[title=Trap Card]").text().equals(""))
                                c.setCardType(Card.TRAP);
                            else 
                                throw new GeneralException("Bład w rezerwowym sprawdzaniu rodzaju karty: " + pseudoNazwaKarty + ", dodatek: " + b.getId());
                    } else
                        throw new GeneralException("Bład w sprawdzaniu rodzaju karty2: " + pseudoNazwaKarty + ", dodatek: " + b.getId());
            } else
                throw new GeneralException("Błąd!!! Brak rodzaju karty");
        }

        //ustawianie numeru karty
        if(opisKarty.select("[title=Card Number]").hasText())
            c.setCardNumber(opisKarty.select("[title=Card Number]").parents().eq(1).select(".cardtablerowdata a").first().text());
        else {
            c.setCardNumber("alt-" + c.getName().replace("/", "").replace("\\", ""));
            Logger.log(Logger.TYPE.LOG, "warrning, karta: " + c.getName() + " nie ma numeru! ustawiam: " + c.getCardNumber());
        }
        
        //sprawdzanie czy fota istnieje a jeśli nie to pobranie foty boostera
        String linkDoFoci = doc3.select("#mw-content-text table.cardtable td.cardtable-cardimage a").attr("href");
        if(!savePhoto(Card.PATH + c.getCardNumber(), linkDoFoci))
            Logger.log(Logger.TYPE.LOG, "Warrning, nie zapisano foty dla karty: " + c.getName());

        //sprawdzanie czy jest monsterem
        if(c.getCardType().equals(Card.MONSTER)) {
            //ustawianie atrybutu monstera
            c.setMonsterAttribute(opisKarty.select("[title=Attribute]").parents().eq(1).select(".cardtablerowdata a").first().html());

            //ustawianie typów monstera
            List<String> l = new ArrayList<String>();
            Elements el = opisKarty.select("[title=Type]").parents().eq(1).select(".cardtablerowdata a");
            for(Element _el : el) {
                l.add(_el.html());
            }                    
            c.setMonsterTypes(l);

            //ustawianie lvl/rank monstera
            if(opisKarty.select("th a[title=Level]").hasText())
                c.setMonsterLvl(Integer.parseInt(opisKarty.select("[title=Level]").parents().eq(1).select(".cardtablerowdata a").first().text()));
            else 
            if(opisKarty.select("th a[title=Rank]").hasText())
                c.setMonsterLvl(Integer.parseInt(opisKarty.select("[title=Rank]").parents().eq(1).select(".cardtablerowdata a").first().text()));
            else {
                Logger.log(Logger.TYPE.LOG, "Error! Nie ma ranku/lvl!");
                return null;
            }
            //ustawianie atk/def monstera
            String ll[] = new String[2];
            el = opisKarty.select("[title=ATK]").parents().eq(1).select(".cardtablerowdata a");
            ll[0] = el.first().html();
            ll[1] = el.last().html();
            c.setMonsterStats(ll);

            //ustawianie typów effektów monstera
            Elements trs = opisKarty.select("th.cardtablerowheader:first-of-type");
            for(Element _el : trs) {
                if(_el.html().equals("Card effect types")) {
                    l = new ArrayList<String>();
                    el = _el.parents().eq(0).select(".cardtablerowdata a");
                    for(Element _ell : el) {
                        l.add(_ell.html());
                    }
                    c.setMonsterEffectTypes(l);
                    break;
                }

                //tu sprawdzać materialsy jeśli potrzeba

            }
        } else 
            if(c.getCardType().equals(Card.SPELL) || c.getCardType().equals(Card.TRAP))
                c.setSTproperty(opisKarty.select("[title=Property]").parents().eq(1).select(".cardtablerowdata a").first().html());
            else
                throw new GeneralException("Error! nie ma ustalonego rodzaju karty!");

        //ustawianie description
        Element des = doc3.select("td.navbox-list").first();
        des.select("br").append(":enter:");
        String text = des.text();
        text = text.replace(":enter:", "\n");
        c.setDescription(text);
        
        return c;
    }
    
    public String convertRarityFromLongToShort(String longRarity) {
        Map<String, String> rarity = new HashMap<String,String>();
        rarity.put("Common", "C");
        rarity.put("Short Print", "SP");
        rarity.put("Super Short Print", "SSP");
        rarity.put("Rare", "R");
        rarity.put("Super Rare", "SR");
        rarity.put("Holofoil Rare", "HFR");
        rarity.put("Ultra Rare", "UR");
        rarity.put("Ultimate Rare", "UtR");
        rarity.put("Secret Rare", "ScR");
        rarity.put("Ultra Secret Rare", "UScR");
        rarity.put("Secret Ultra Rare", "ScUR");
        rarity.put("Prismatic Secret Rare", "PScR");
        rarity.put("Ghost Rare", "GR");
        rarity.put("Parallel Rare", "PR");
        rarity.put("Parallel Common", "PC");
        rarity.put("Super Parallel Rare", "SPR");
        rarity.put("Ultra Parallel Rare", "UPR");
        rarity.put("Duel Terminal Parallel Common", "DPC");
        rarity.put("Duel Terminal Rare Parallel Rare", "DRPR");
        rarity.put("Duel Terminal Super Parallel Rare", "DSPR");
        rarity.put("Duel Terminal Ultra Parallel Rare", "DUPR");
        rarity.put("Duel Terminal Secret Parallel Rare", "DScPR");
        rarity.put("Gold Ultra Rare", "GUR");
        
        //wercja OCG only
        rarity.put("Normal", "N");
        rarity.put("Normal Rare", "NR");
        rarity.put("Holographic Rare", "HGR");
        rarity.put("Normal Parallel Rare", "NPR");
        rarity.put("Duel Terminal Normal Parallel Rare", "DNPR");
        
        return rarity.get(longRarity);
    }
    
    public void addRarity(Booster booster, Card card, String rarity, String cardNrInBooster) {
        Rarity rar = new Rarity();
        rar.setBooster(booster);
        rar.setCard(card);
        rar.setRarity(rarity);
        rar.setBoosterCode(cardNrInBooster);
        if(!card.getRarity().contains(rar)) {
            card.getRarity().add(rar);
        }
    }
    
    private void preConfigurations() {
        em = DBManager.getManager().createEntityManager();
        //tworzenie folderów jeśli nie istnieją
        new File(configuration.boosterPath).mkdir();
        new File(configuration.cardPath).mkdir();
        new File(Logger.getPath()).mkdir();
    }
    
    private ArrayList<Elements> getBoosters() throws GeneralException{
        //tablica z linkami do dodatków
        String[] linksToBoosters = new String[4];
        linksToBoosters[0] = "http://yugioh.wikia.com/wiki/TCG_Set_Galleries:_Boosters";
        linksToBoosters[1] = "http://yugioh.wikia.com/wiki/TCG_Set_Galleries:_Decks";
        linksToBoosters[2] = "http://yugioh.wikia.com/wiki/TCG_Set_Galleries:_Packs";
        linksToBoosters[3] = "http://yugioh.wikia.com/wiki/TCG_Set_Galleries:_Promos";
        
        //em = DBManager.getManager().createEntityManager();
        ArrayList<Elements> boost = new ArrayList<Elements>();
        try {
            for(int z=0; z<linksToBoosters.length; z++) {
                
                    configuration.simpleCheck = false;
                    switch(z) {
                        case 0: {
                            typeOfAddon = "Booster";
                            break;
                        }
                        case 1: {
                            typeOfAddon = "Deck";
                            break;
                        }
                        case 2: {
                            typeOfAddon = "Pack";
                            break;
                        }
                        case 3: {
                            configuration.simpleCheck = true;
                            typeOfAddon = "Promotion";
                            break;
                        }
                    }

                    //łączenie z stroną zawierjającą dane dodatki
                    Document doc = connectToSite(linksToBoosters[z]);

                    //wyciągnięcie z strony elementów związanych z dodatkami
                    Elements boosters = doc.select(".wikia-gallery-item  .lightbox-caption");
                    boost.add(boosters);
                    //pasek ładowania
                    //for(int ii=0; ii<boosters.size(); ii++)
                    //    System.out.print("|");
                    //System.out.println();
                    
         
                //Logger.log(Logger.TYPE.LOG, "-------------------------------------------------");
                //System.out.println('\n');
            }
        }finally {
            em.close();
           
        }
        configuration.simpleCheck=false;
        return boost;
    }

    private void createCards(ArrayList<Elements> boost) {
        //rozpoczynamy pętle po wszystkich boosterach na stronie
        em = DBManager.getManager().createEntityManager();
        for(Elements boosters : boost) {
            //pasek ładowania
            System.out.println();
            for(int ii=0; ii<boosters.size(); ii++)
                System.out.print("|");
            System.out.println();
            for(Element e : boosters) {
                try {
                    //dodanie kreski za kazdy przetwarzany booster do paska ladowania
                    System.out.print("|");

                    //element boostera
                    Element main = e.select("a").first();

                    if(main.text().equals("Custom Token Card"))
                        continue;

                    Logger.log(Logger.TYPE.LOG, "--Rozpoczynam przetwarzanie dodatku: " + main.text());

                    //sprawdzamy czy booster jest juz w bazie i czy jest w całości
                    Booster b = em.find(Booster.class, main.text());
                    if(b!=null) {
                        if(b.getChecked()) {
                            Logger.log(Logger.TYPE.LOG, "--Dodatek: " + main.text() + " jest już w bazie, sprawdzam następny");
                            continue;
                        }
                    }

                    //tu powinien wystrzelić wyjątek zamiast zwracać nulla
                    b = createBooster(main);

                    //jeżeli dodatek jeszcze nie wyszedł to przejdź do następnego
                    if(!configuration.simpleCheck) 
                        if(b.getReleseDate().after(new Date()))
                            throw new GeneralException("warrning, dodatek: " + b.getName() + " jeszcze nie wyszedł, data wyjścia: " + new SimpleDateFormat("MMMM dd, yyyy").format(b.getReleseDate()));

                    //zapisywanie dodatku w bazie
                    em.getTransaction().begin();
                    em.merge(b);
                    em.getTransaction().commit();

                    Document docCardGallery = connectToSite(e.select("a[title^=Set Card Galleries]").first().attr("href"));
                    if(b.getName().contains("Sneak Preview Participation Cards"))
                        docCardGallery = connectToSite("/wiki/Set_Card_Galleries:Sneak_Peek_Participation_Cards_(TCG-EN-LE)");

                    if(b.getName().contains("Duelist League 3"))
                        docCardGallery = connectToSite("/wiki/Set_Card_Galleries:Duelist_League_2011_Prize_Cards_(TCG-EN-UE)");

                    //elementy z kartami
                    Elements cards = null;
                    int realDiffrentCards = -1;
                    if(docCardGallery!=null) {
                        cards = docCardGallery.select(".wikia-gallery .wikia-gallery-item");
                        realDiffrentCards = docCardGallery.select("div#gallery-0 span.wikia-gallery-item").size();
                    }

                    //licznik kart
                    int licznik = 0;

        //--------------------------------------------------------------------------------------------------------------------
                    if(cards!=null && !cards.isEmpty()) {
                        for(Element e2 : cards) {
                            try {
                                if(!e2.select("div.lightbox-caption").text().contains("(") && !e2.select("div.lightbox-caption").text().contains(")")) {
                                    licznik++;
                                    throw new GeneralException("99478");
                                }
                                //pobieranie nazwy aktualnie przetwarzanej karty w formie "bez spacji"
                                String pseudoNazwaKarty = e2.select("div.lightbox-caption a:nth-of-type(3)").attr("href");

                                //sprawdzanie czy w bazie jest już taka karta
                                Card c = em.find(Card.class, pseudoNazwaKarty);

                                //tworzenie nowej karty jeśli nie ma jej w bazie
                                if(c==null) {
                                    String[] links = new String[2];
                                    links[0] = e2.select("div.lightbox-caption a:nth-of-type(1)").attr("href");
                                    links[1] = e2.select("div.lightbox-caption a:nth-of-type(3)").attr("href");
                                    String cardType = "";
                                    c = createCard(pseudoNazwaKarty, b, links, cardType);
                                    if(update)
                                        updateNotify("new Card");
                                }

                                //dodaje boostera do danej karty
                                if(!c.getBoosterki().contains(b))
                                    c.getBoosterki().add(b);

                                //dodawnie rarity do danej karty
                                addRarity(b, c, e2.select("div.lightbox-caption a:nth-of-type(2)").text(),e2.select("div.lightbox-caption a:nth-of-type(1)").text());

                                //dodawanie karty do bazy
                                em.getTransaction().begin();
                                em.merge(c);
                                em.getTransaction().commit();
                                licznik++;
                            }catch(GeneralException ex) {
                                if(!ex.getMessage().contains("99478"))
                                    Logger.log(Logger.TYPE.LOG, ex.getMessage());
                            } 
                        } //koniec pętli po kartach w dodatku
                    } else {
                        //throw new GeneralException("Error! brak ogólnej struktury tabelki z kartami: " + cards.html());
        //--------------------------------------------------------------------------------------------------------------------
                        Document altDocCardList = connectToSite(main.attr("href"));
                        if(altDocCardList!=null && !altDocCardList.select(".tabbertab").isEmpty()) {
                            cards = altDocCardList.select(".tabbertab").first().select("tr");
                            int[] header = {-1,-1,-1};
                            for(Element e2 : cards) {
                                if(!e2.select("th").isEmpty() && e2.select("th").first().text().toLowerCase().contains("set number")) {
                                    int iteratorek = 1;
                                    for(Element e3 : e2.select("th")) {
                                        if(e3.text().toLowerCase().contains("set number"))
                                            header[0] = iteratorek;

                                        if(e3.text().toLowerCase().contains("name"))
                                            header[1] = iteratorek;

                                        if(e3.text().toLowerCase().contains("rarity"))
                                            header[2] = iteratorek;

                                        iteratorek++;
                                    }
                                    e2.remove();
                                    break;
                                }
                            }
                            if(header[0]==-1 || header[1]==-1 || header[2]==-1)
                                throw new GeneralException("Error, nie znaleziono wszystkich potrzebnych elementów w alternatywnej tabelce: " + header);
        //--------------------------------------------------------------------------------------------------------------------
                            for(Element e2 : cards) {
                                try {
                                    licznik++;
                                    if(!e2.select("th").isEmpty())
                                        continue;

                                    //pobieranie nazwy aktualnie przetwarzanej karty w formie "bez spacji"
                                    String pseudoNazwaKarty = e2.select(":nth-of-type("+ header[0] +") a").attr("href");

                                    //sprawdzanie czy w bazie jest już taka karta
                                    Card c = em.find(Card.class, pseudoNazwaKarty);

                                    //tworzenie nowej karty jeśli nie ma jej w bazie
                                    if(c==null) {
                                        String[] links = new String[3];
                                        links[0] = e2.select(":nth-of-type("+ header[0] +") a").attr("href");
                                        links[1] = e2.select(":nth-of-type("+ header[1] +") a").attr("href");
                                        String cardType = "";
                                        c = createCard(pseudoNazwaKarty, b, links, cardType);
                                        if(update)
                                            updateNotify("new Card");
                                    }

                                    //dodaje boostera do danej karty
                                    if(!c.getBoosterki().contains(b))
                                        c.getBoosterki().add(b);

                                    for(Element _e : e2.select(":nth-of-type("+ header[2] +") a")) {
                                        //dodaje do karty dodatek i rarity w jakim w nim jest
                                        addRarity(b, c, convertRarityFromLongToShort(_e.text()), e2.select(":nth-of-type("+ header[0] +") a").text());
                                    }

                                    //dodawanie karty do bazy
                                    em.getTransaction().begin();
                                    em.merge(c);
                                    em.getTransaction().commit();
                                }catch(GeneralException ex) {
                                    if(!ex.getMessage().contains("99478"))
                                        Logger.log(Logger.TYPE.LOG, ex.getMessage());
                                }
                            } //koniec pętli po kartach w dodatku
                        } else {
                            if(altDocCardList!=null && !altDocCardList.select("#mw-content-text>ul li a").isEmpty()) {
                                Element e4 = altDocCardList.select("div#mw-content-text > ul li").first();

                                String pseudoNazwaKarty = e4.select("a:nth-of-type(2)").attr("href");

                                //sprawdzanie czy w bazie jest już taka karta
                                Card c = em.find(Card.class, pseudoNazwaKarty);

                                //tworzenie nowej karty jeśli nie ma jej w bazie
                                if(c==null) {
                                    String[] links = new String[3];
                                    links[0] = e4.select("a:nth-of-type(2)").attr("href");
                                    links[1] = e4.select("a:nth-of-type(1)").attr("href");
                                    String cardType = "";
                                    c = createCard(pseudoNazwaKarty, b, links, cardType);
                                    if(update)
                                        updateNotify("new Card");
                                }

                                //dodaje boostera do danej karty
                                if(!c.getBoosterki().contains(b))
                                    c.getBoosterki().add(b);

                                //dodaje do karty dodatek i rarity w jakim w nim jest
                                if(!e4.select("a:nth-of-type(3)").isEmpty())
                                    addRarity(b, c, e4.select("a:nth-of-type(3)").text(), e4.select("a:nth-of-type(1)").text());
                                else {
                                    addRarity(b, c, "?", e4.select("a:nth-of-type(1)").text());
                                    Logger.log(Logger.TYPE.LOG, "warning, nie ma raritości, ustawiam '?', karta: " + c.getName() + ", dodatek: " + b.getName());
                                }

                                //dodawanie karty do bazy
                                em.getTransaction().begin();
                                em.merge(c);
                                em.getTransaction().commit();
                                //}
                            } else {
                                throw new GeneralException("Error! brak struktury tabelki z kartami");
                            }
                        }
                    }
        //--------------------------------------------------------------------------------------------------------------------

                    if(b.getNumberOfCards()!=null) {
                        if(licznik == cards.size()) {
                            //ustawiamy ze caly dodatek jest zbazowany
                            b.setChecked(true);
                        }else {
                            Logger.log(Logger.TYPE.LOG, "Error! Nie wszystkie karty zostały zapisane do dodatku, cards size: " + cards.size() + ",licznik:" + licznik + " liczba kart w dodatku: " + b.getNumberOfCards() + " realCardSize: " + realDiffrentCards);
                            b.setChecked(false);
                        }
                    } else {
                        if(b.getName().contains("Dark Revelation Volume 2"))
                            b.setNumberOfCards(String.valueOf(licznik));
                        else
                        if(!configuration.simpleCheck)
                            Logger.log(Logger.TYPE.LOG, "Error! numberOfCards jest nullem w dodatku: " + b.getId());
                    }

                    //zapisujemy to w bazie
                    em.getTransaction().begin();
                    em.merge(b);
                    em.getTransaction().commit();

                    Logger.log(Logger.TYPE.LOG, "--Skutecznie zapisałem dodatek: " + b.getId());
                }catch(GeneralException ex2) {
                    Logger.log(Logger.TYPE.LOG, ex2.getMessage());
                    errorNotify(ex2.getMessage());
                } catch (Exception ex) {
                    Logger.log(Logger.TYPE.LOG, "nieznany błąd: " + ex.getMessage());
                    errorNotify(ex.getMessage());
                }
                
                
            }
        }
    }
    
    @Override
    public boolean createDB(Config c) {
        update = false;
        return updateDB(c);
    }

    //fasada
    @Override
    public boolean updateDB(Config c) {
        configuration = c;
        Logger.log(Logger.TYPE.LOG, ">>>>>>>>>>>>>>>>>>>>>>Zaczynam pracę<<<<<<<<<<<<<<<<<<<<<<<<");
        
        preConfigurations();
        
        ArrayList<Elements> el;
        try {
            el = getBoosters();
        } catch (GeneralException ex) {
            Logger.log(Logger.TYPE.LOG, ex.getMessage());
            return false;
        }
        
        createCards(el);
        
        return true;
    }

    
}