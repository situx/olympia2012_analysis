package de.unifrankfurt.texttechnologie.olympia;

import java.io.*;
import java.net.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.xml.sax.SAXException;
/**Class for parsing categories and articles of the Olympic Games 2012.
 * 
 * @author Timo Homburg, Benjamin Broder
 *
 */
public class Olympia implements OlympiaAPI{
	/**List of parsed category objects.*/
	private final transient Set<String> categories;
	/**List of all Artikel representing the corpus.*/
	protected final transient Set<Artikel> corpus;
	/**FileWriter for exporting the corpus information.*/
	protected transient FileWriter corpuswriter;
	/**The date to quit the evaluation.*/
	private Date enddate;
	/**FileWriter for exporting the dates and revisions for Matlab.*/
	private transient FileWriter matlabwriter,matlabwriter2;
	/**The list of parsed pages.*/
	private final transient Set<String> pages;
	/**Query path for Wikipedia.*/
	private String path;
	/**SimpleDateFormat for exporting timestamps for Matlab.*/
	private final transient SimpleDateFormat matlabdateformat;
	/**Map of revisions including a date string and the number of revisions at that date.*/
	private final transient Map<String,Integer> revisions;
	/**The start date for beginning the evaluation.*/
	private Date startdate;
	/**Object for storing RDF export information.*/
	protected transient RDFDataAPI rdfdata;
	/**Number of threads for parsing pages out of Wikipedia.*/
	private int threadnumber;
	
	/**Constructor for Olympia. Initialises the SimpleDateFormats and the revision map.
	 * 
	 */
    public Olympia() {
    	super();
    	this.categories=new TreeSet<String>();
        this.corpus=new TreeSet<Artikel>();
        this.matlabdateformat= new SimpleDateFormat(Olympia.MATLABFORMAT);
        this.pages=new TreeSet<String>();
        this.revisions=new TreeMap<String, Integer>();
        this.threadnumber=Olympia.DEFAULTTHREADNUMBER;
    	this.rdfdata=new RDFData();
    }
    /**
     * Constructor for giving a threadnumber to Olympia.
     * @param threadnumber the number of threads for parsing the pages
     */
    public Olympia(final Integer threadnumber){
    	this();
    	this.threadnumber=threadnumber;
    }
    
    /**
     * Constructor containing a selectable startdate and enddate.
     * @param startdate the date to start evaluating
     * @param enddate the date to end evaluating
     */
    public Olympia(final String startdate, final String enddate) {
		this();
    	final SimpleDateFormat consoledateformat=new SimpleDateFormat(Olympia.CONSOLEFORMAT);
    	try {
			this.startdate=consoledateformat.parse(startdate);
	    	this.enddate=consoledateformat.parse(enddate);
    	} catch (final ParseException e) {
			e.printStackTrace();
		}
	}
    
    /**
     * Constructor containing a varying threadnumber and a start and enddate.
     * @param threadnumber the number of threads
     * @param startdata the date to start evaluating
     * @param endddate the date to end evaluating
     */
    public Olympia(final String startdate, final String enddate,final Integer threadnumber) {
		this();
		this.threadnumber=threadnumber;
    	final SimpleDateFormat consoledateformat=new SimpleDateFormat(Olympia.CONSOLEFORMAT);
    	try {
			this.startdate=consoledateformat.parse(startdate);
	    	this.enddate=consoledateformat.parse(enddate);
    	} catch (final ParseException e) {
			e.printStackTrace();
		}
	}
    
	/**
	 * Creates the corpus of the given articles and the map of revisions.
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	@Override
	public void createCorpus() throws ParserConfigurationException, SAXException{
	    Integer counter=-1;
	    this.rdfdata=new RDFData();
	    try {
	        this.corpuswriter=new FileWriter(new File("korpus.txt"));
	        this.matlabwriter=new FileWriter(new File("matlab.txt"));
	        this.matlabwriter2=new FileWriter(new File("matlab2.txt"));
	        this.corpuswriter.write("ID\tTitle\tTimestamps\tCategories\tSubarticles\tLinks\n");
	        this.path=Olympia.WIKICATURLEN;
	        this.readCat("");
	        this.readSubCat();
	        System.out.println("Liste aller relevanten Artikel angelegt");
	        System.out.println(this.pages);
	        int i=0;
	        this.threadnumber=16;
	        CountDownLatch cd = new CountDownLatch(this.threadnumber);
	        int ppt = this.pages.size()/this.threadnumber;
	        for (i=0; i< threadnumber;i++){ //Creating threads for parsing the articles out of wikipedia
	          Thread teilcorpora =new CorpusThread(this.pages,this,0+i*ppt,ppt+ppt*i,cd);
	            teilcorpora.start();
	        }
	        cd.await();
	        System.out.println("Corpus Size: "+this.corpus.size());
	        System.out.println("Nations: "+this.rdfdata.getNations().size()+" "+this.rdfdata.getNations().keySet());
	        System.out.println("Sports: "+this.rdfdata.getSports().size()+" "+this.rdfdata.getSports().keySet());
	        System.out.println("Sportmen: "+this.rdfdata.getSportsmen().size()+" "+this.rdfdata.getSportsmen().keySet());
	        this.corpuswriter.close();
	        final ArrayList<String> revarray=new ArrayList<String>(this.getRevisions().keySet());
	        if(this.startdate==null){
	            this.startdate=this.matlabdateformat.parse(revarray.get(0));
	        }
	        if(this.enddate==null){
	            this.enddate=this.matlabdateformat.parse(revarray.get(revarray.size()-1));
	        }
	        Date currentdate=this.startdate;
	        while(currentdate.before(this.enddate)){
	            currentdate.setTime(currentdate.getTime()+Olympia.SECONDSOFDAY);
	            if(!this.getRevisions().containsKey(this.matlabdateformat.format(currentdate))){
	                this.getRevisions().put(this.matlabdateformat.format(currentdate),0);
	            }
	        }
	        for(String date :this.getRevisions().keySet()){
	            //this.matlabwriter.write(date+"\n");
	            this.matlabwriter.write(++counter+"\n");
	            this.matlabwriter2.write(this.getRevisions().get(date)+"\n");
	        }
	        System.out.println(this.getRevisions());
	        this.matlabwriter.close();
	        this.matlabwriter2.close();
	
	        this.rdfdata.rdfExport();
	    } catch (final IOException e) {
	        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
	    } catch (final ParseException e) {
	        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
	    } catch (final InterruptedException e) {
	        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
	    }
	}
	
	/**
     * Reads categories. 
     * @param continueStr continueString for the recursion
     */
    @SuppressWarnings("unchecked")
	@Override
    public void readCat(final String continueStr) {
	String cmcontinue = "";
	try {
		// zuerst eine neue XMLInputFactory erstellen
		final XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		// einen neuen eventReader einrichten
		final URL url = new URL(this.path+"&cmcontinue="+continueStr);
		final XMLEventReader eventReader = inputFactory.createXMLEventReader(url.openStream());
		XMLEvent event;
		// das XML-Dokument lesen
		while (eventReader.hasNext()) {
			event = eventReader.nextEvent();
			//wenn eintrag beginnt
			if (event.isStartElement()) {
				//prufe ob es ein categorie member ist
				if (Olympia.CM.equals(event.asStartElement().getName().getLocalPart())) {	
					// get attributes  fur categorie vorsortierung
					StartElement element = event.asStartElement();
					Iterator<Attribute> attributes = element.getAttributes();
					int ns = 0;
					while( attributes.hasNext() ) {
						Attribute attribute = (Attribute)attributes.next();
						if (Olympia.NS.equals(attribute.getName().toString())){  ns=Integer.valueOf( attribute.getValue());   } //zum categorie erkennen
					}
					//und nochmal zum attribute auslesen
					attributes = element.getAttributes();
            		while( attributes.hasNext() ) {
                		Attribute attribute = (Attribute)attributes.next();
                		if (Olympia.TITLE.equals(attribute.getName().toString())){
                				if (ns!=0){
                					this.categories.add(attribute.getValue());   
	                			}
	                			else{
	                				this.pages.add(attribute.getValue());	
	                			}
                		}
            		}
				}
				if (Olympia.CATEGORYMEMBERS.equals(event.asStartElement().getName().getLocalPart())) {
					StartElement element = event.asStartElement();
					Iterator<Attribute> attributes = element.getAttributes();
					while( attributes.hasNext() ) {
						Attribute attribute = (Attribute)attributes.next();
						// fuge titel attribute der liste hinzu
						if (Olympia.CMCONTINUE.equals(attribute.getName().toString())){  cmcontinue=attribute.getValue();   }
					}
				}
        	//event = eventReader.nextEvent();
			continue;
			}
		}
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		} catch (final XMLStreamException e) {
			e.printStackTrace();
		} catch (final MalformedURLException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		//rekursion wegen beschraenkung auf 10 ergebnisse als free user
		if (!"".equals(cmcontinue)){this.readCat(cmcontinue);}
    }
  
    /**
	 * Reads a subcategory of a category.
	 */
	@Override
	public void readSubCat(){
	//subkategorien
		final Set<String> subcats = new TreeSet<String>();
		String subcatname;
		subcats.addAll(this.categories);
	    System.out.println(this.categories);
	    System.out.println(this.categories.size());
		this.categories.clear();
		for (Iterator<String> iter=subcats.iterator();iter.hasNext();){
			subcatname=iter.next().replaceAll("\\s","_");
			this.path=Olympia.WIKISUBCATURLEN+subcatname+"&format=xml";
			this.readCat("");
		}
	//rekursion fur subsubkategorien usw
		if (!this.categories.isEmpty()){
			this.readSubCat();
		}
	}
	
	/**
	 * Gets the set of articles.
	 * @return the set of articles
	 */
	@Override
	public Set<Artikel> getCorpus() {
		return this.corpus;
	}
	/**
	 * Gets the FileWriter for writing the corpus
	 * @return the FileWriter
	 */
	@Override
	public FileWriter getCorpusWriter() {
		return this.corpuswriter;
	}
	/**
	 * Gets the RDFData object.
	 * @return the RDFData object
	 */
	@Override
	public RDFDataAPI getRDFData() {
		return this.rdfdata;
	}
	/**
     * Gets the set of revisions.
     * @return the set of revisions.
     */
	@Override
	public Map<String,Integer> getRevisions() {
		return this.revisions;
	}
	
	/**
	 * Main method of the program.
	 * @param args arguments containing possible dates
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	public static void main(final String args[]) throws ParserConfigurationException, SAXException {
		final Olympia main;
		switch(args.length){
			case 0: main=new Olympia();break;
			case 1: main=new Olympia(Integer.valueOf(args[0]));break;
			case 2: main=new Olympia(args[0],args[1]);break;
			case 3: main=new Olympia(args[1],args[2],Integer.valueOf(args[0]));break;
			default: main=new Olympia();
		}
		main.createCorpus();
	}
}
