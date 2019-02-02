package de.unifrankfurt.texttechnologie.olympia;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Class representing an article in the Wikipedia.
 * @author Benjamin Bronder, Timo Homburg
 *
 */
public class Artikel implements Comparable<Object>{
	/**Unique page id of the Wikipedia article.*/
    private Integer pageID;
    /**List of timestamps of the revisions of the article.*/
    private final List<Date> timestamps;
    /**List of categories of the article.*/
    private final Set<String> categories;
    /**List of links included in the article.*/
    private final Set<String> links;
    /**List of links included in the article.*/
    private final Set<String> backlinks;
    /**Amount of subarticles of this article.*/
    private transient Integer subarticles;
    /**Title of the article.*/
    private String title;

    /**
	 * Constructor for Artikel just initializing the lists.
	 */
	public Artikel(){
        this.backlinks=new TreeSet<String>();
	    this.categories=new TreeSet<String>();
	    this.links=new TreeSet<String>();
		this.timestamps=new LinkedList<Date>();
	}

	/**
	 * Adds a backlink to the list of backlinks.
	 * @param backlink the link to add
	 */
	public void addBackLink(final String backlink) {
	    this.backlinks.add(backlink);
	}

	/**Adds a category to the list of categories.
	 * 
	 * @param category the category to add as String
	 */
	public void addCategory(final String category) {
	    this.categories.add(category);
	}

	/**
	 * Adds a link to the list of links.
	 * @param link the link to add
	 */
	public void addLink(final String link) {
	    this.links.add(link);
	}

	/**
	 * Adds a timestamp to the list of timestamps.
	 * @param date the timestamp as date
	 */
	public void addTimestamp(final Date date){
	    this.timestamps.add(date);
	}

	@Override
	public int compareTo(final Object o){
	    if(o instanceof Artikel){
	        return this.title.compareTo(((Artikel)o).title);
	    }
	    return -1;
	}

	/**Gets the backlinks ofthis article.
	 *
	 * @return the backlinks as list of String
	 */
	public Set<String> getBacklinks(){
	    return this.backlinks;
	}

	/**
	 * Gets the list of categories of the article.
	 * @return the List of categories
	 */
    public Set<String> getCategories(){
        return this.categories;
    }

	/**
	 * Gets the linklist of this article.
	 * @return the list of links
	 */
	public Set<String> getLinks() {
		return this.links;
	}

	/**
	 * Gets the pageid of this article.
	 * @return the page id as Integer
	 */
	public Integer getPageID() {
		return this.pageID;
	}

	/**
	 * Gets  the list of timestamps.
	 * @return the list of timestamps
	 */
	public List<Date> getTimestamps(){
		return this.timestamps;
	}

	/**
	 * Gets the title of the article.
	 * @return the title as String
	 */
    public String getTitle(){
        return this.title;
    }

	/**
	 * Sets the pageID of the article.
	 * @param pageID the pageID as Integer
	 */
    public void setPageID(final Integer pageID) {
        this.pageID=pageID;
    }

    /**
     * Sets the amount of subarticles of the article.
     * @param subarticles the amount as Integer
     */
    public void setSubarticles(final Integer subarticles) {
        this.subarticles=subarticles;
    }

    /**
     * Sets the title of the article.
     * @param title the title to set as String
     */
    public void setTitle(final String title) {
        this.title=title;
    }
    
    /**ToString method for exporting the article to .txt.*/
    @Override
    public String toString(){
        return this.pageID+"\t"+this.title+"\t"+this.timestamps.size()+"\t"+this.categories+"\t"+this.subarticles+"\t"+this.links+"\n";
    }

}
