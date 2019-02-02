package de.unifrankfurt.texttechnologie.olympia;

import java.io.FileWriter;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

/**
 * Interface for Olympia containing constants and method definitions.
 * @author Benjamin Bronder, Timo Homburg
 *
 */
public interface OlympiaAPI {
	/**Categories to which the article belongs are listed in those tags.*/
	public static final String CATEGORYMEMBERS="categorymembers";
	/**Category tag.*/
	public static final String CM="cm";
	/**Indicates if more query results are available.*/
	public static final String CMCONTINUE="cmcontinue";
	/**Format string for parsing a date input from the console.*/
	public static final String CONSOLEFORMAT="dd.MM.yyyy";
	/**Format string for formatting a matlab date.*/
	public static final String MATLABFORMAT="yyyy MM dd 0 0 0";
	/**Indicates the number of subarticles in a document.*/
	public static final String NS="ns";
	/**RV tag for parsing revision information.*/
    public static final String RV="rv";
    /**Amount of seconds per day for date calculation purposes.*/
	public static final Integer SECONDSOFDAY=86400;
	/**The default number of threads initialised*/
	public static final Integer DEFAULTTHREADNUMBER=2;
	/**Title attribute.*/
	public static final String TITLE="title";
	/**URL to parse the relevant categories in Wikipedia.*/
	public static final String WIKICATURL="http://de.wikipedia.org/w/api.php?action=query&list=categorymembers&cmtitle" +
			"=Category:Olympische_Sommerspiele_2012&format=xml";
    public static final String WIKICATURLEN="http://en.wikipedia.org/w/api.php?action=query&list=categorymembers&cmtitle" +
            "=Category:2012_Summer_Olympics&format=xml";
	/**German URL for reading subcategories.*/
	public static final String WIKISUBCATURL="http://de.wikipedia.org/w/api.php?action=query&list=categorymembers&cmtitle=";
    /**English URL for reading subcategories.*/
	public static final String WIKISUBCATURLEN="http://en.wikipedia.org/w/api.php?action=query&list=categorymembers&cmtitle=";

	/**
	 * Creates the requested corpus from the acquired data.
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
    public void createCorpus() throws ParserConfigurationException, SAXException;
	/**
	 * Gets the set of articles.
	 * @return the set of articles
	 */
	public Set<Artikel> getCorpus();
	/**
	 * Gets the FileWriter for writing the corpus
	 * @return the FileWriter
	 */
	public FileWriter getCorpusWriter();
	/**
	 * Gets the RDFData object.
	 * @return the RDFData object
	 */
	public RDFDataAPI getRDFData();
	/**
	 * Gets the set of revisions.
	 * @return the set of revisions.
	 */
	public Map<String,Integer> getRevisions();
	/**
     * Reads a category.
     * @param cmcont
     */
    public void readCat(String cmcont);
    /**
     * Reads a subcategory of a category.
     */
    public void readSubCat();

}
