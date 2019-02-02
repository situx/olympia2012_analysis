package de.unifrankfurt.texttechnologie.olympia;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Map;
/**
 * Interface for RDFData containing constants and method definitions.
 * @author Benjamin Bronder, Timo Homburg
 *
 */
public interface RDFDataAPI {
	/**Closing tag for Bronze output.*/
	public static final String BRONZECLTAG="</tt:bronze>\n";
	public static final String GOLDOPTAG="<tt:gold rdf:about=";
	/**Closing tag for Gold output.*/
	public static final String GOLDCLTAG="</tt:gold>\n";
	
	/**Output file name for the nations.*/
	public static final String NATIONFILE="output/nations.txt";
	/**Opening tag for an RDF description.*/
	public static final String RDFDESCRIPTIONOPTAG="<rdf:Description rdf:about=\"http://en.wikipedia.org/wiki/";
	/**Closing tag for an RDF decription.*/
	public static final String RDFDESCRIPTIONCLTAG="</rdf:Description>\n";
	/**Output file name for the rdf export.*/
	public static final String RDFEXPORTFILE="output/rdfExport.rdf";
	/**Defines a RDF parsetype collection.*/
	public static final String RDFPARSETYPECOL=" rdf:parseType=\"Collection\">\n";
	/**Starting tag for the RDF document.*/
	public static final String RDFSTARTTAG="<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:tt=\"http://www.uni-frankfurt.de/rdf/\">\n";
	/**Closing tag for the silver relation.*/
	public static final String SILVERCLTAG="</tt:silver>\n";
	/**Output file name for the sports.*/
	public static final String SPORTFILE="output/sport.txt";
	/**Output file name for the sportsmen.*/
	public static final String SPORTSMENFILE="output/sportsmen.txt";
	/**Opening tag for writing a sport collection.*/
    public static final String SPORTOPTAG="<sport rdf:parseType=\"Collection\">";
	/**End string to cut at several occasions.*/
    public static final String SUMMEROLYMPICS=" at the 2012 Summer Olympics";
    
    public static final String SUMMEROLYMPICSUS="_at_the_2012_Summer_Olympics";
    
	/**
     * Adds a nation to the set of nations.
     * @param nation the nation to add
     * @param article the article representing the nation
     */
    public void addNation(String nation,Artikel article);
    
    /**
     * Adds a sport to the list of sports.
     * @param sport the sport to add
     * @param article the article representing the sport
     */
    public void addSport(String sport,Artikel article);
    
    /**
     * Add a sportman to the set of sportsmen.
     * @param sportsman the sportsman to add
     * @param article the article representing the sportsman
     */
    public void addSportsman(String sportsman,Artikel article);
    
    /**
     * Adds a sportsman to a medal type.
     * @param sportsman the sportsman to add
     * @param medal indicator for the medal map to add to
     */
    public void addSportsmanToMedal(String sportsman,Integer medal);
    
    /**
     * Creates relationships between the parsed nations, sportsmen and sports in the given maps.
     */
    public void createRelationships();
    
	/**
     * Gets the set of nations.
     * @return the set of nations
     */
    public Map<String,Artikel> getNations();
    
    /**
     * Gets the set of sports.
     * @return the set of sports
     */
    public Map<String,Artikel> getSports();
    
    /**
     * Gets the set of sportsmen.
     * @return the set of sportsmen
     */
    public Map<String,Artikel> getSportsmen();
    
    /**
     * Parses sportsmen and medal ranks out of medal tables on the corresponding pages.
     *
     * @throws ParserConfigurationException if the parser was malconfigured
     * @throws IOException if there is a html error
     * @throws SAXException on parsing
     */
    public void parseMedals() throws ParserConfigurationException, IOException, SAXException;
    
    
    /**Manages the export of the requested data as RDF.
	 * 
	 * @throws IOException on writing errors
	 */
	public void rdfExport() throws IOException, SAXException, ParserConfigurationException;
	
}
