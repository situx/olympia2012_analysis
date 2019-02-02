package de.unifrankfurt.texttechnologie.olympia;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;
/**
 * Interface storing thread related functions and constants.
 * @author Benjamin Bronder, Timo Homburg
 *
 */
public interface CorpusAPI {
	/**Backlinks tag for continues.*/
	public static final String BACKLINKS="backlinks";
	/**Backlinks tag to read.*/
	public static final String BL="bl";
	/**Continue attribute for backlinks.*/
	public static final String BLCONTINUE="blcontinue";
	/**Continue tag for categories.*/
	public static final String CATEGORIES="categories";
	/**Category for categorising living people.*/
	public static final String CATLIVINGPEOPLE="Category:Living people";
	/**Category for categorising nations.*/
	public static final String CATNATIONS="Category:Nations at the 2012 Summer Olympics";
	/**Category for events.*/
	public static final String CATEVENTS="Category:2012 Summer Olympics events";
	/**Category attribute.*/
	public static final String CL="cl";
	/**Continue attribute for categories.*/
	public static final String CLCONTINUE="clcontinue";
	/**Category tag.*/
	public static final String CM="cm";
	/**Indicates if more query results are available.*/
	public static final String CMCONTINUE="cmcontinue";
	/**Continue tag for links.*/
	public static final String LINKS="links";
	/**Format string for formatting a matlab date.*/
	public static final String MATLABFORMAT="yyyy MM dd 0 0 0";
	/**Indicates the number of subarticles in a document.*/
	public static final String NS="ns";
	/**Page tag for parsing page information.*/
	public static final String PAGE="page";
    /**Indicates the pages' unique identifier.*/
	public static final String PAGEID="pageid";
	/**Link tag.*/
	public static final String PL="pl";
	/**Continue tag for links.*/
	public static final String PLCONTINUE="plcontinue";
	/**Properties for the further reading of backlinks.*/
	public static final String PROPSBL="&bllimit=max&list=backlinks&bltitle=";
	/**Properties for reading categories.*/
	public static final String PROPSCAT="&prop=categories";
	/**Properties for reading links.*/
	public static final String PROPSLINKS="&prop=links";
	/**Properties for reading revisions.*/
	public static final String PROPSREV="&rvlimit=max&prop=revisions&rvprop=timestamp|user";
    /**Revisiontag for reading revisions.*/
	public static final String REV="rev";
	/**Revisiontag for parsing continues.*/
	public static final String REVISIONS="revisions";
	/**Continue tag for revisions.*/
	public static final String RVCONTINUE="rvcontinue";
	/**Timestamp attribute for revisions.*/
	public static final String TIMESTAMP="timestamp";
	/**Title attribute.*/
	public static final String TITLE="title";
    /**Parsing String for the Wikipedia SimpleDateFormat.*/
	public static final String WIKIFORMAT="yyyy-MM-dd'T'HH:mm:ss'Z'";
    /**German URL for reading pages.*/
	public static final String WIKIPAGEURL="http://de.wikipedia.org/w/api.php?action=query&titles=";
    /**English URL for reading pages.*/
	public static final String WIKIPAGEURLEN="http://en.wikipedia.org/w/api.php?action=query&titles=";
	
	 /**
     * Reads the category information of a page. 
     * @param art the article to fill
     * @param path the path for reading
     * @param continueStr continueString to indicate if a continue page is read
     * @throws ParserConfigurationException 
     * @throws SAXException 
     */
    public void readPageCategories(Artikel art,String path, String continueStr) throws SAXException, ParserConfigurationException;
    
    /**
     * Reads a page and converts the information into an Artikel.
     * @param continueStr a continue String if necessary
     * @param path the url to parse from
     * @param art the article to fill
     * @return the article
     */
    public Artikel readPage(String continueStr, Artikel art,String path);
    
    /**
     * Reads the link information of a page. 
     * @param art the article to fill
     * @param path the path for reading
     * @param continueStr continueString to indicate if a continue page is read
     * @param backlinks a boolean indicating if links or backlinks are processed
     */
    public void readLinks(Artikel art,String path, String continueStr, Boolean backlinks);
    
    /**
     * Reads the revision information of a page. 
     * @param art the article to fill
     * @param path the path for reading
     * @param continueStr continueString to indicate if a continue page is read
     */
    public void readRevisions(Artikel art,String path, String continueStr);
}
