package de.unifrankfurt.texttechnologie.olympia;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Class representing the data needed for the RDFExport and implementing the export itself.
 * @author Benjamin Bronder, Timo Homburg
 *
 */
public class RDFData implements RDFDataAPI{
	/**Map of sportsmen who have achieved any amount of bronze medals.*/
	private final transient Set<String> bronzeToSportsmen;
	/**Map of sportsmen who have achieved any amount of gold medals.*/
	private final transient Set<String> goldToSportsmen;
	/**Set of the parsed nations.*/
	private final transient Map<String,Artikel> nations;
	/**Map of nations who have achieved any amount of bronze medals.*/
	private final transient Map<String,Set<String>> nationToBronze;
	/**Map of nations who have achieved any amount of gold medals.*/
	private final transient Map<String,Set<String>> nationToGold;
	/**Map of nations who have achieved any amount of silver medals.*/
	private final transient Map<String,Set<String>> nationToSilver;
	/**Lists sportsmen of a nation.*/
    private final transient Map<String,Set<String>> nationToSportsmen;
    /**Lists the sports in which a nation participates.*/
    private final transient Map<String,Set<String>> nationToSport;
    /**Map of sportsmen who have achieved any amount of silver medals.*/
	private final transient Set<String> silverToSportsmen;
	/**Set of the parsed sports.*/
	private final transient Map<String,Artikel> sport;
	/**Map of sports with their participating nations.*/
	private final transient Map<String,Set<String>> sportToNation;
	/**Map of sports with their participants.*/
	private final transient Map<String,Set<String>> sportToSportsmen;
	/**Set of the parsed sportsmen.*/
	private final transient Map<String,Artikel> sportsmen;
	/**Map of sportsmen who have achieved beonze medals including the amount.*/
	private final transient Map<String,Integer> sportsmenToBronze;
	/**Map of sportsmen who have achieved gold medals including the amount.*/
	private final transient Map<String,Integer> sportsmenToGold;
	/**Map of sportsmen to their corresponding nation.*/
	private final transient Map<String,String> sportsmenToNation;
	/**Map of sportsmen who have achieved silver medals including the amount.*/
	private final transient Map<String,Integer> sportsmenToSilver;
	/**Map of sportsmen attending a certain sport.*/
    private final transient Map<String,Set<String>> sportsmenToSport;
    /**Constructor for RDFData.
     *
     */
    public RDFData(){
        this.bronzeToSportsmen=new TreeSet<String>();
        this.goldToSportsmen=new TreeSet<String>();
        this.nations=new TreeMap<String,Artikel>();
        this.nationToBronze=new TreeMap<String,Set<String>>();
        this.nationToGold=new TreeMap<String,Set<String>>();
        this.nationToSilver=new TreeMap<String,Set<String>>();
        this.nationToSport=new TreeMap<String,Set<String>>();
        this.nationToSportsmen=new TreeMap<String,Set<String>>();
        this.silverToSportsmen=new TreeSet<String>();
        this.sport=new TreeMap<String,Artikel>();
        this.sportToNation=new TreeMap<String,Set<String>>();
        this.sportToSportsmen=new TreeMap<String,Set<String>>();
        this.sportsmen=new TreeMap<String,Artikel>();
        this.sportsmenToBronze=new TreeMap<String, Integer>();
        this.sportsmenToGold=new TreeMap<String,Integer>();
        this.sportsmenToNation=new TreeMap<String,String>();
        this.sportsmenToSilver=new TreeMap<String,Integer>();
        this.sportsmenToSport=new TreeMap<String,Set<String>>();
    }
    /**
     * Adds a nation to the set of nations.
     * @param nation the nation to add
     * @param article the article representing the nation
     */
    @Override
    public void addNation(final String nation,final Artikel article){
        this.nations.put(nation,article);
    }

    /**
     * Add a sportman to the set of sportsmen.
     * @param sportsman the sportsman to add
     * @param article the article representing the sportsman
     */
    @Override
    public void addSportsman(final String sportsman,final Artikel article){
        this.sportsmen.put(sportsman,article);
    }

    /**
     * Adds a sport to the list of sports.
     * @param sport the sport to add
     * @param article the article representing the sport
     */
    @Override
    public void addSport(final String sport,final Artikel article){
        this.sport.put(sport,article);
    }
    
    /**
     * Adds a sportsman to a medal type.
     * @param sportsman the sportsman to add
     * @param medal indicator for the medal map to add to
     */
    @Override
    public void addSportsmanToMedal(final String sportsman,final Integer medal){
    	Map<String,Integer>medalmap=null;
        System.out.println(sportsman);
    	switch(medal){
    		case 1: medalmap=this.sportsmenToGold;this.goldToSportsmen.add(sportsman);
                if(this.nationToGold.get(sportsman)!=null){
                    this.nationToGold.get(this.sportsmenToNation.get(sportsman)).add(sportsman);
                }else{
                    Set<String> newset=new TreeSet<String>();
                    newset.add(sportsman);
                        this.nationToGold.put(this.sportsmenToNation.get(sportsman),newset);

                }
                break;
    		case 2: medalmap=this.sportsmenToSilver;this.silverToSportsmen.add(sportsman);
                if(this.nationToSilver.get(sportsman)!=null){
                this.nationToSilver.get(this.sportsmenToNation.get(sportsman)).add(sportsman);
            }else{
                Set<String> newset=new TreeSet<String>();
                newset.add(sportsman);

                        this.nationToSilver.put(this.sportsmenToNation.get(sportsman),newset);

            }break;
    		case 3: medalmap=this.sportsmenToBronze;this.bronzeToSportsmen.add(sportsman);
                if(this.nationToBronze.get(sportsman)!=null){
                    this.nationToBronze.get(this.sportsmenToNation.get(sportsman)).add(sportsman);
            }else{
                Set<String> newset=new TreeSet<String>();
                newset.add(sportsman);

                this.nationToBronze.put(this.sportsmenToNation.get(sportsman),newset);

            }break;
    		default: medalmap=this.sportsmenToBronze;break;
    	}
    	if(medalmap.containsKey(sportsman)){
    		medalmap.put(sportsman,medalmap.get(sportsman)+1);
    	}
    	else{
    		medalmap.put(sportsman, 1);
    	}

    }

    /**
	 * Creates relationships between the parsed nations, sportsmen and sports in the given maps.
	 */
	@Override
	public void createRelationships(){
	    for (String nation:this.nations.keySet()){
	        this.nationToSportsmen.put(nation, new TreeSet<String>());
	    }
	    this.nationToSportsmen.put("Unknown",new TreeSet<String>());
	    for (String sportart:this.sport.keySet()){
	        this.sportToSportsmen.put(sportart,new TreeSet<String>());
	    }
	    for (String sportsman:this.sportsmen.keySet()){
	        this.sportsmenToSport.put(sportsman,new TreeSet<String>());
	    }
	    System.out.println("CREATE RELATIONSHIPS");
	    System.out.println(sportsmen.size());
	    String currentnation;
		for(Artikel sportsman:this.sportsmen.values()){
			System.out.println(sportsman.getLinks().size());
	        boolean unknown =true;
	        for(String link:sportsman.getLinks()){
	        	for(String nation:this.nations.keySet()){
	        		if(link.equals(nation) && sportsman.getBacklinks().contains(nation+RDFData.SUMMEROLYMPICS)){
		                System.out.println("1: "+sportsman.getTitle()+" - "+nation);
		                this.sportsmenToNation.put(sportsman.getTitle(), nation);
		                this.nationToSportsmen.get(nation).add(sportsman.getTitle());
		                unknown=false;
		                break;
	        		}
	        	}
	        }
	        if(unknown){
	        	for(String backlink:sportsman.getBacklinks()){ //Filling the relations sportsmenToNation and nationToSportsmen
	        		for(String nation:this.nations.keySet()){
	        			if(backlink.equals(nation+" at the 2012 Summer Olympics")){
	        				System.out.println("1: "+sportsman.getTitle()+" - "+nation);
	        				this.sportsmenToNation.put(sportsman.getTitle(), nation);
	        				this.nationToSportsmen.get(nation).add(sportsman.getTitle());
		                    unknown=false;
		                    break;
	        			}
	        		}
	        	}
	        }
	        if (unknown){
	            currentnation="Unknown";
	            System.out.println("1: "+sportsman.getTitle()+" - "+currentnation);
	            this.sportsmenToNation.put(sportsman.getTitle(), currentnation);
	            this.nationToSportsmen.get(currentnation).add(sportsman.getTitle());
	
	        }
	        System.out.println("1. BLOCK FERTIG!!!");
	        System.out.println("Categories: "+sportsman.getCategories());
	        for(String category:sportsman.getCategories()){ //Filling the relations sportToSportsmen and sportsmenToSport
	        	System.out.println(this.sport.keySet());
	            for(String sport:this.sport.keySet()){
	                System.out.println("Sport: "+sport+" - Category: "+category);
	                System.out.println("Comparison: "+sport.substring(0,3)+" - "+category.substring(9,12));
	                if(sport.substring(0,3).equals(category.substring(9,12))){ //If the first 3 letters of a category equal to the sport we found a match
	                    System.out.println("2: " + sportsman.getTitle() + " - " + sport);
	                       this.sportToSportsmen.get(sport).add(sportsman.getTitle());
	                       this.sportsmenToSport.get(sportsman.getTitle()).add(sport);
	
	                }
	            }
	        }
		}
		System.out.println("SportsmenToNation "+this.sportsmenToNation.keySet());
	    for(String sportsman:this.sportsmenToNation.keySet()){ //Filling the relations sportToNation and NationTo Sport with the help of the previous relations
	       if(sportsmenToSport.containsKey(sportsman)){
	           System.out.println("3: "+this.sportsmenToSport.get(sportsman)+" - "+this.sportsmenToSport.get(sportsman));
	           this.nationToSport.put(this.sportsmenToNation.get(sportsman),this.sportsmenToSport.get(sportsman));
	           for(String sport:this.sportsmenToSport.get(sportsman)){
	               if(this.sportToNation.containsKey(sport)){
	                   this.sportToNation.get(sport).add(this.sportsmenToNation.get(sportsman));
	               }
	               else{
	                   TreeSet<String> newlist=new TreeSet<String>();
	                   newlist.add(this.sportsmenToNation.get(sportsman));
	                   this.sportToNation.put(sport,newlist);
	               }
	
	           }
	
	       }
	    }
	}
	/**
	     * Parses sportsmen and medal ranks out of medal tables on the corresponding pages.
	     *
	     * @throws ParserConfigurationException if the parser was malconfigured
	     * @throws IOException if there is a html error
	     * @throws SAXException on parsing
	     */
    	@Override
	    public void parseMedals() throws ParserConfigurationException, IOException, SAXException {
	        SAXParser sax= SAXParserFactory.newInstance().newSAXParser();
	        DefaultHttpClient client = new DefaultHttpClient();
	        HttpResponse response;
	        String toParse;
	        MedalHandler medal=new MedalHandler(this);
	        URI uri = null;
	
	        for(Artikel current:this.sport.values()){
	            try {
	                uri = new URI("http://en.wikipedia.org/w/api.php?action=parse&page="+current.getTitle().replaceAll("\\s","_")+"&prop=text&format=xml");
	            } catch (URISyntaxException e) {
	                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
	            }
	            response=client.execute(new HttpGet(uri));
	            toParse= EntityUtils.toString(response.getEntity()).replaceAll("&gt;",">").replaceAll("&lt;","<").replaceAll("&quot;","\"").replaceAll("&amp;","&").replaceAll("&#035;","#").replaceAll("&#039;","'").replaceAll("&nbsp;"," ").replaceAll("<br>","<br/>");
	//        toParse=toParse.substring(toParse.indexOf("<")-1);
	            try {
	                sax.parse(new InputSource( new StringReader( toParse ) ),medal);
	            } catch (SAXException e) {
	                e.printStackTrace(); System.out.println(current.getTitle()+" ist nicht wohlgeformt");  //To change body of catch statement use File | Settings | File Templates.
	            }
	            System.out.println("SportsmenToGold: "+this.sportsmenToGold);
	            System.out.println("SportsmenToSilver: "+this.sportsmenToSilver);
	            System.out.println("SportsmenToBronze: "+this.sportsmenToBronze+"\n");
	
	            System.out.println("NationToGold: "+this.nationToGold);
	            System.out.println("NationToSilver: "+this.nationToSilver);
	            System.out.println("NationToBronze: "+this.nationToBronze);
	        }
	
	        //String test =URLEncoder.encode);
	
	    }

	/**Exports the given data into a RDF file.*/
	@Override
	public void rdfExport() throws IOException, SAXException, ParserConfigurationException {
	    this.createRelationships();
	    this.parseMedals();
	    final FileWriter rdfwriter=new FileWriter(RDFData.RDFEXPORTFILE);
	    final StringBuffer tempbuffer=new StringBuffer(this.sportsmen.keySet().size()*16);
	    rdfwriter.write(RDFData.RDFSTARTTAG);
	    FileWriter writer= new FileWriter(RDFData.NATIONFILE);
	    for(String nation:this.nations.keySet()){ //Writing the nations including their relations
	        writer.write(nation+"\n");
	        rdfwriter.write(RDFData.RDFDESCRIPTIONOPTAG+this.nations.get(nation).getTitle().replaceAll(" ", "_")+"\">\n");
	    	rdfwriter.append("<tt:isrepresented"+RDFData.RDFPARSETYPECOL);
	        Set<String> tempsports=new TreeSet<String>();
	        for(String sportsman:this.nationToSportsmen.get(nation)){
	            try{
	            rdfwriter.write(RDFData.RDFDESCRIPTIONOPTAG+sportsman.replaceAll(" ", "_")+"\"/>\n");
	        	for(String sport:this.sportsmenToSport.get(sportsman)){
	        		tempsports.add(sport);
	        	}
	            }catch(NullPointerException e){
	                System.out.println(e.getMessage());
	            }
	        }
	        rdfwriter.write("</tt:isrepresented>\n");
	        rdfwriter.write("<tt:participates"+RDFData.RDFPARSETYPECOL);
	        for(String sport:tempsports){
	            rdfwriter.write(RDFData.RDFDESCRIPTIONOPTAG+sport.replaceAll(" ", "_")+RDFData.SUMMEROLYMPICSUS+"\"/>\n");
	        }
	        rdfwriter.write("</tt:participates>\n");
	        rdfwriter.write(RDFData.RDFDESCRIPTIONCLTAG);
	    }
	    writer.close();
	    writer=new FileWriter(new File(RDFData.SPORTSMENFILE));
	    for(String sportsman:this.sportsmen.keySet()){ // Writing the sportsmen including their relations
	        writer.write(sportsman+"\n");
	        rdfwriter.write(RDFData.RDFDESCRIPTIONOPTAG+sportsman.replaceAll(" ", "_")+"\">\n");
	        rdfwriter.write("<tt:competing"+RDFData.RDFPARSETYPECOL);
	        for(String sport:this.sportsmenToSport.get(sportsman)){
	            rdfwriter.write(RDFData.RDFDESCRIPTIONOPTAG+sport.replaceAll(" ", "_")+RDFData.SUMMEROLYMPICSUS+"\"/>\n");
	        }
	        System.out.println("sportsman "+sportsman);
	        System.out.println("nation"+this.sportsmenToNation.get(sportsman));
	        rdfwriter.write("</tt:competing>\n");
	        rdfwriter.write("<tt:represents rdf:about=\""+this.sportsmenToNation.get(sportsman).replaceAll(" ", "_")+"\"/>\n");
	        if(!this.sportsmenToGold.containsKey(sportsman)){
	        	rdfwriter.write("<tt:gold rdf:about=\""+sportsman.replaceAll(" ", "_")+"\">0"+RDFData.GOLDCLTAG);
	        }else{
	        	rdfwriter.write("<tt:gold rdf:about=\""+sportsman.replaceAll(" ", "_")+"\">"+this.sportsmenToGold.get(sportsman)+RDFData.GOLDCLTAG);
	        }
	        if(!this.sportsmenToSilver.containsKey(sportsman)){
	        	rdfwriter.write("<tt:silver rdf:about=\""+sportsman.replaceAll(" ", "_")+"\">0"+RDFData.SILVERCLTAG);
	        }else{
	        	rdfwriter.write("<tt:silver rdf:about=\""+sportsman.replaceAll(" ", "_")+"\">"+this.sportsmenToSilver.get(sportsman)+RDFData.SILVERCLTAG);
	        }
	        if(!this.sportsmenToBronze.containsKey(sportsman)){
	        	rdfwriter.write("<tt:bronze rdf:about=\""+sportsman.replaceAll(" ", "_")+"\">0"+RDFData.BRONZECLTAG);
	        }else{
	        	rdfwriter.write("<tt:bronze rdf:about=\""+sportsman.replaceAll(" ", "_")+"\">"+this.sportsmenToBronze.get(sportsman)+RDFData.BRONZECLTAG);
	        }
	        rdfwriter.write(RDFData.RDFDESCRIPTIONCLTAG);
	    }
	    writer.close();
	    writer=new FileWriter(new File(RDFData.SPORTFILE));
	    for(String sport:this.sport.keySet()){ //Writing the sports including their relations
	        writer.write(sport+"\n");
	        rdfwriter.write(RDFData.RDFDESCRIPTIONOPTAG+sport.replaceAll(" ", "_")+"\">\n");
	        tempbuffer.append("<tt:nation"+RDFData.RDFPARSETYPECOL);
	        rdfwriter.write("<tt:sportsman"+RDFData.RDFPARSETYPECOL);
	        for(String nation:this.sportToNation.get(sport)){
	        	tempbuffer.append(RDFData.RDFDESCRIPTIONOPTAG+nation.replaceAll(" ", "_")+"\"/>\n");
	        }
            for(String sportsman:this.sportToSportsmen.get(sport)){
            	rdfwriter.write(RDFData.RDFDESCRIPTIONOPTAG+sportsman.replaceAll(" ", "_")+"\"/>\n");
            }
	        rdfwriter.write("</tt:sportsman>\n");
	        tempbuffer.append("</tt:nation>\n");
	        rdfwriter.write(tempbuffer.toString());
	        tempbuffer.delete(0, tempbuffer.length());
	        rdfwriter.write(RDFData.RDFDESCRIPTIONCLTAG);
	    }
	    rdfwriter.write(RDFData.RDFDESCRIPTIONOPTAG+"Bronze\">\n");
	    rdfwriter.write("<tt:sportsman"+RDFData.RDFPARSETYPECOL);
	    for(String sportsman:this.bronzeToSportsmen){
	        rdfwriter.write(RDFData.RDFDESCRIPTIONOPTAG+sportsman.replaceAll(" ", "_")+"\"/>\n");
	    }
	    rdfwriter.write("</tt:sportsman>");
	    rdfwriter.write(RDFData.RDFDESCRIPTIONCLTAG);
	    rdfwriter.write(RDFData.RDFDESCRIPTIONOPTAG+"Gold\">\n");
	    rdfwriter.write("<tt:sportsman"+RDFData.RDFPARSETYPECOL);
	    for(String sportsman:this.goldToSportsmen){
	        rdfwriter.write(RDFData.RDFDESCRIPTIONOPTAG+sportsman.replaceAll(" ", "_")+"\"/>\n");
	    }
	    rdfwriter.write("</tt:sportsman>\n");
	    rdfwriter.write(RDFData.RDFDESCRIPTIONCLTAG);
	    rdfwriter.write(RDFData.RDFDESCRIPTIONOPTAG+"Silver\">\n");
	    rdfwriter.write("<tt:sportsman"+RDFData.RDFPARSETYPECOL);
	    for(String sportsman:this.silverToSportsmen){
	        rdfwriter.write(RDFData.RDFDESCRIPTIONOPTAG+sportsman.replaceAll(" ", "_")+"\"/>\n");
	    }
	    rdfwriter.write("</tt:sportsman>\n");
	    rdfwriter.write(RDFData.RDFDESCRIPTIONCLTAG);
	    writer.close();
	    rdfwriter.write("</rdf:RDF>\n");
	    rdfwriter.close();
	}

	/**
     * Gets the set of nations.
     * @return the set of nations
     */
	@Override
    public Map<String,Artikel> getNations(){
        return this.nations;
    }

    /**
     * Gets the set of sports.
     * @return the set of sports
     */
	@Override
    public Map<String,Artikel> getSports() {
        return this.sport;
    }

    /**
     * Gets the set of sportsmen.
     * @return the set of sportsmen
     */
	@Override
    public Map<String,Artikel> getSportsmen() {
        return this.sportsmen;
    }

}
