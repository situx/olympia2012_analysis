package de.unifrankfurt.texttechnologie.olympia;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.xml.sax.SAXException;

/**
 * Parses page related information into article objects.
 * @author Benjamin Bronder, Timo Homburg
 *
 */
public class CorpusThread extends Thread implements CorpusAPI{
	/**The list of URL to process.*/
    private List<String> pages;
    /**The set of articles representing the result.*/
    private Set<Artikel> corpus;
    /**RDFData object for collecting relationships.*/
    private RDFDataAPI rdfdata;
    /**Start and end indicator for parsing.*/
    private Integer start,end;
    /**FileWriter for writing corpus information.*/
    private FileWriter corpuswriter;
    /**CountDownLatch for thread coordination purposes.*/
    private CountDownLatch countdownlatch;
    /**Revision map for the revision export.*/
	private Map<String, Integer> revisions;
    /**
     * Constructor for Corpus.
     * @param pages the list of URLs to parse
     * @param olympia Olympia object as storing entity
     * @param start point for parsing
     * @param end end point for parsing
     * @param cl CountDownLatch for indicating the termination of threads
     */
    public CorpusThread(final Set<String> pages, final OlympiaAPI olympia,final int start,final int end,final CountDownLatch cl){
        super();
        this.countdownlatch = cl;
        this.pages = new ArrayList<String>(pages);
        this.corpus =olympia.getCorpus();
        this.rdfdata=olympia.getRDFData();
        this.start=start;
        this.end=end;
        this.corpuswriter=olympia.getCorpusWriter();
        this.revisions=olympia.getRevisions();
    }
    
    /**
     * Exceution method of the thread
     */
    @Override
    public void run(){
        System.out.println("Thread started "+this.start +" "+this.end);
        int i=0;
        Artikel current;
        for(i=start ;i<end;i++){

            if((i%100)==0){
                float total = end-start;

                System.out.println("Thread "+start+" "+this.pages.get(i)+" ("+(float)((i-start)/total)*100 +"%)");
            }
            String tmp=this.pages.get(i).replaceAll("\\s","_");
            current=(this.readPage("", new Artikel(),CorpusThread.WIKIPAGEURLEN + tmp + "&format=xml"));
            this.corpus.add(current);
            for(String cat : current.getCategories()){
                if(CorpusThread.CATLIVINGPEOPLE.equals(cat)){
                    for(String cat2:current.getCategories()){
                        if(cat2.contains(" at the 2012 Summer Olympics")){
                            this.rdfdata.addSportsman(current.getTitle(),current);
                            break;
                        }
                    }
                    break;
                }
                if(CorpusThread.CATNATIONS.equals(cat)){
                    this.rdfdata.addNation(current.getTitle().split(" at")[0],current);
                    break;
                }
                if(CorpusThread.CATEVENTS.equals(cat)){
                    this.rdfdata.addSport(current.getTitle().split(" at")[0],current);
                    break;
                }
            }
            try {
                this.corpuswriter.write(current.toString());
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        System.out.println("Thread end");
         countdownlatch.countDown();
    }
    
    /**
   	 * Reads the link or backlink information of a page. 
   	 * @param article the article to fill
   	 * @param path the path for reading
   	 * @param continueStr continueString to indicate if a continue page is read
   	 * @param backlinks a boolean indicating if links or backlinks are processed
   	 */
   	@SuppressWarnings("unchecked")
	@Override
   	public void readLinks(final Artikel article,final String path,final String continueStr,final Boolean backlinks) {
   	    Attribute attribute;
   	    Iterator<Attribute> attributes;
   	    String plcontinue = "";
   	    StartElement element;
   	    try {
   	        // zuerst eine neue XMLInputFactory erstellen
   	        final XMLInputFactory inputFactory = XMLInputFactory.newInstance();
   	        // einen neuen eventReader einrichten
   	        URL url;
   	        if ("".equals(continueStr))       {
   	            url = new URL(path);
   	        }
   	        else if (!backlinks){
   	            url = new URL(path+"&plcontinue="+continueStr);
   	        }
   	        else{
   	            url = new URL(path+"&blcontinue="+continueStr);
   	        }
   	        final XMLEventReader eventReader = inputFactory.createXMLEventReader(url.openStream());
   	        XMLEvent event;
   	        // das XML-Dokument lesen
   	        //System.out.println("send XML request an Wikipedia");
   	        while (eventReader.hasNext()) {
   	            event = eventReader.nextEvent();
   	            //wenn eintrag beginnt
   	            if (event.isStartElement()) {
   	                element=event.asStartElement();
   	                attributes = element.getAttributes();
   	                        if (CorpusThread.PL.equals(event.asStartElement().getName().getLocalPart())){
   	                            while( attributes.hasNext() ) {
   	                                attribute = (Attribute)attributes.next();
   	                                if(CorpusThread.TITLE.equals(attribute.getName().toString())){
   	                                    article.addLink(attribute.getValue());
   	                                }
   	                            }
   	                        }
   	                        else if (CorpusThread.BL.equals(event.asStartElement().getName().getLocalPart())){
   	                            while( attributes.hasNext() ) {
   	                                attribute = (Attribute)attributes.next();
   	                                if(CorpusThread.TITLE.equals(attribute.getName().toString())){
   	                                    article.addBackLink(attribute.getValue());
   	                                }
   	                            }
   	                        }
   	                else if (CorpusThread.LINKS.equals(event.asStartElement().getName().getLocalPart())) {
   	                    while( attributes.hasNext() ) {
   	                        attribute = (Attribute)attributes.next();
   	                        // fuge titel attribute der liste hinzu
   	                        if (CorpusThread.PLCONTINUE.equals(attribute.getName().toString())){  plcontinue=attribute.getValue();   }
   	                    }
   	                }
   	                else if (CorpusThread.BACKLINKS.equals(event.asStartElement().getName().getLocalPart())) {
   	                    while( attributes.hasNext() ) {
   	                         attribute = (Attribute)attributes.next();
   	                         // fuge titel attribute der liste hinzu
   	                         if (CorpusThread.BLCONTINUE.equals(attribute.getName().toString())){  plcontinue=attribute.getValue();   }
   	                    }
   	                }
   	            }
   	            continue;
   	         }
   	
   	        if (!"".equals(plcontinue)){readLinks(article,path,plcontinue,backlinks);}
   	    } catch (final MalformedURLException e) {
   	        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
   	    } catch (final IOException e) {
   	        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
   	    } catch (final XMLStreamException e) {
   	        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
   	    }
   	}
   	/**
        * Analyses an article page and gets the provided attributes.
        * @param rvcont indicates if there are more revisions to parse (recursion condition)
        * @param article the article page to analyse
        * @return an Artikel object including the desired data
        */
       @SuppressWarnings("unchecked")
	@Override
       public Artikel readPage(final String rvcont,Artikel article,String path) {
           Attribute attribute;
       	Iterator<Attribute> attributes;
           StartElement element;
           try {
               // zuerst eine neue XMLInputFactory erstellen
               final XMLInputFactory inputFactory = XMLInputFactory.newInstance();
               // einen neuen eventReader einrichten
               URL url;
               if ("".equals(rvcont))       {
                   url = new URL(path);
               }
               else{
                   url = new URL(path+"&rvcontinue="+rvcont);
               }
               final XMLEventReader eventReader = inputFactory.createXMLEventReader(url.openStream());
               XMLEvent event;
               // das XML-Dokument lesen
               while (eventReader.hasNext()) {
                   event = eventReader.nextEvent();
                   //wenn eintrag beginnt
                   if (event.isStartElement()) {
                       element=event.asStartElement();
                       attributes = element.getAttributes();
                       //prufe ob es ein categorie member ist
                       if(CorpusThread.PAGE.equals(event.asStartElement().getName().getLocalPart()) && "".equals(rvcont)){
                           while(attributes.hasNext()){
                               attribute = (Attribute)attributes.next();
                               if (CorpusThread.TITLE.equals(attribute.getName().toString())){
                                   article.setTitle(attribute.getValue());
                               }
                               if(CorpusThread.PAGEID.equals(attribute.getName().toString())){
                                   article.setPageID(Integer.valueOf(attribute.getValue()));
                               }
                               if(CorpusThread.NS.equals(attribute.getName().toString())){
                                   article.setSubarticles(Integer.valueOf(attribute.getValue()));
                               }
                           }
                       }

                       continue;
                   }
               }
           this.readRevisions(article,path+CorpusThread.PROPSREV,"");
           this.readLinks(article,path+CorpusThread.PROPSLINKS,"",false);
           this.readLinks(article,path+CorpusThread.PROPSBL+article.getTitle().replaceAll("\\s","_"),"",true);
           this.readPageCategories(article,path+CorpusThread.PROPSCAT,"");
           } catch (final FileNotFoundException e) {
               e.printStackTrace();
           } catch (final XMLStreamException e) {
               e.printStackTrace();
           } catch (final MalformedURLException e) {
               e.printStackTrace();
           } catch (final IOException e) {
               e.printStackTrace();
           } catch (final SAXException e) {
               e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
           } catch (final ParserConfigurationException e) {
               e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
           }
           return article;
       }
       
       
       /**
   	 * Reads the category information of a page. 
   	 * @param article the article to fill
   	 * @param path the path for reading
   	 * @param continueStr continueString to indicate if a continue page is read
   	 */
   	@SuppressWarnings("unchecked")
	@Override
   	public void readPageCategories(final Artikel article,final String path,final String continueStr) throws SAXException, ParserConfigurationException {
   	    Attribute attribute;
   	    Iterator<Attribute> attributes;
   	    String clcontinue = "";
   	    StartElement element;
   	    try {
   	        // zuerst eine neue XMLInputFactory erstellen
   	        final XMLInputFactory inputFactory = XMLInputFactory.newInstance();
   	        // einen neuen eventReader einrichten
   	        URL url;
   	        if ("".equals(continueStr)){
   	            url = new URL(path);
   	        }
   	        else{
   	            url = new URL(path+"&clcontinue="+continueStr);
   	        }
   	        final XMLEventReader eventReader = inputFactory.createXMLEventReader(url.openStream());
   	        XMLEvent event;
   	        // das XML-Dokument lesen
   	        while (eventReader.hasNext()) {
   	            event = eventReader.nextEvent();
   	            //wenn eintrag beginnt
   	            if (event.isStartElement()) {
   	                element=event.asStartElement();
   	                attributes = element.getAttributes();
   	                if (CorpusThread.CL.equals(event.asStartElement().getName().getLocalPart()) ) { //Lese Attribut cl für Kategorien
   	                    //kategorien auslesen
   	                    while( attributes.hasNext() ) {
   	                        attribute = (Attribute)attributes.next();
   	                        if (CorpusThread.TITLE.equals(attribute.getName().toString())){
   	                            article.addCategory(attribute.getValue());
   	                        }
   	                    }
   	                }
   	                else if (CorpusThread.CATEGORIES.equals(event.asStartElement().getName().getLocalPart())) {
   	                    while( attributes.hasNext() ) {
   	                        attribute = (Attribute)attributes.next();
   	                        // fuge titel attribute der liste hinzu
   	                        if (CorpusThread.CLCONTINUE.equals(attribute.getName().toString())){  clcontinue=attribute.getValue();   }
   	                    }
   	                }
   	            }
   	            continue;
   	        }
   	        if (!"".equals(clcontinue)){this.readPageCategories(article, path, clcontinue);}
   	    } catch (final MalformedURLException e) {
   	        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
   	    } catch (final IOException e) {
   	        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
   	    } catch (final XMLStreamException e) {
   	        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
   	    }
   	}
   	
   	/**
        * Reads the revision information of a page. 
        * @param article the article to fill
        * @param path the path for reading
        * @param continueStr continueString to indicate if a continue page is read
        */
       @SuppressWarnings("unchecked")
	@Override
       public void readRevisions(final Artikel article,final String path,final String continueStr) {
           Attribute attribute;
           Iterator<Attribute> attributes;
           String rvcontinue = "";
           StartElement element;
           Integer temp;
           String tempdate;
           try {
               // zuerst eine neue XMLInputFactory erstellen
               final XMLInputFactory inputFactory = XMLInputFactory.newInstance();
               // einen neuen eventReader einrichten
               URL url;
               if ("".equals(continueStr)){
                   url = new URL(path);
               }
               else{
                   url = new URL(path+"&rvcontinue="+continueStr);
               }
               final XMLEventReader eventReader = inputFactory.createXMLEventReader(url.openStream());
               XMLEvent event;
               // das XML-Dokument lesen
               while (eventReader.hasNext()) {
                   event = eventReader.nextEvent();
                   //wenn eintrag beginnt
                   if (event.isStartElement()) {
                       element=event.asStartElement();
                       attributes = element.getAttributes();
                       //prufe ob es ein categorie member ist
                       if (CorpusThread.REV.equals(event.asStartElement().getName().getLocalPart())){
                           while( attributes.hasNext() ) {
                               attribute = (Attribute)attributes.next();
                               if(CorpusThread.TIMESTAMP.equals(attribute.getName().toString())){
                                   article.addTimestamp(new SimpleDateFormat(CorpusThread.WIKIFORMAT).parse(attribute.getValue()));
                                   tempdate=new SimpleDateFormat(CorpusThread.MATLABFORMAT).format(article.getTimestamps().get(article.getTimestamps().size()-1));
                                   if(this.revisions.containsKey(tempdate)){
                                       temp=this.revisions.get(tempdate);
                                       this.revisions.put(tempdate,++temp);
                                   }else{
                                       this.revisions.put(tempdate,1);
                                   }
                               }
                           }
                       }
                       else if (CorpusThread.REVISIONS.equals(event.asStartElement().getName().getLocalPart())) {
                           while( attributes.hasNext() ) {
                               attribute = (Attribute)attributes.next();
                               // fuge titel attribute der liste hinzu
                               if (CorpusThread.RVCONTINUE.equals(attribute.getName().toString())){  rvcontinue=attribute.getValue();   }
                           }
                       }
                       continue;
                   }
               }
               if (!"".equals(rvcontinue)){this.readRevisions(article,path,rvcontinue);}
           } catch (MalformedURLException e) {
               e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
           } catch (IOException e) {
               e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
           } catch (ParseException e) {
               e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
           } catch (XMLStreamException e) {
               e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
           }
       }
}
