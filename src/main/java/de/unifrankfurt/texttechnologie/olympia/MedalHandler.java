package de.unifrankfurt.texttechnologie.olympia;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.LinkedList;
import java.util.List;
/**
 * A DefaultHandler for the SAXParser to handle medals of the Olympic Games.
 * @author Benjamin Bronder, Timo Homburg
 *
 */
public class MedalHandler extends DefaultHandler {
	/**Indicates the current row.*/
	private transient Integer currentrow;
	/**Booleans for saving the state of the SAX parser.*/
	private transient Boolean event,firsttd,medals,table,td,th;
	/**The amount of columns to jump over until the algorithm finds the next medals column.*/
	private transient Integer jump;
	/**RDFData object for storing the results.*/
	private final transient RDFDataAPI rdf;
	/**Counter for the medaltype and the parsed rows.*/
	private transient Integer medalcounter, rowamount; //Counts from 1-3 if Gold, Silver or Bronze is parsed
	/**Temporary list for saving the first row of a medal table, as the position of the medals in the table has to be evaluated for each table.*/
    private final transient List<String> firstRow;
    
    /**Constructor for MedalHandler.
     * 
     * @param rdf rdfdata object to save the parse medaldata
     */
    public MedalHandler(final RDFDataAPI rdf){
		this.medalcounter=0;
		this.medals=false;
		this.rdf=rdf;
		this.table=false;
		this.td=false;
		this.th=false;
        this.jump=2;
        this.event=false;
        this.rowamount =0;
        this.currentrow=0;
        this.firsttd=false;
        this.firstRow=new LinkedList<String>();
	}
    
	/**
	 * Checks if a String found is a sportsman and adds the corresponding sportsman to the medalmap.
	 * @param chars the chars to check
	 */
	public void addData(String chars){
	    if(!chars.contains("(") && !chars.contains("\n") && !"".equals(chars) && !" ".equals(chars) && !chars.matches("[0-9]+")){
	        System.out.println("Sportsman or Nation: "+chars);
	        if(this.rdf.getSportsmen().containsKey(chars)){
	            this.rdf.addSportsmanToMedal(chars, this.medalcounter);
	        }
	         switch(medalcounter){
	         case 1: System.out.println("Winner Gold: "+chars);break;
	         case 2: System.out.println("Winner Silver: "+chars);break;
	         case 3: System.out.println("Winner Bronze: "+chars);break;
	         default: System.out.println("Winner Other Case????? ("+this.medalcounter+") "+chars);
	        }
	    }
	}

	/**
	 * Character detection of the SAXParser DefaultHandler.
	 */
	@Override
	public void characters(final char[] ch, final int start, final int length)
			throws SAXException {
		String chars=new String(ch,start,length);
		if(this.th && (chars.equals("Event"))){
            this.event=true;
		}
        else if(this.th && this.event && chars.equals("Gold")){
            this.medals=true;
            this.firsttd=true;
            this.rowamount=0;
            this.firstRow.clear();
        }
		else if(this.td && this.medalcounter!=(-1)){
            if(this.firsttd){
                this.firstRow.add(chars);
            }else{
                this.addData(chars);
            }
		}
	}
	
	/**
	 * EndElement detection of the SAXParser DefaultHandler.
	 */
	@Override
	public void endElement(final String uri, final String localName, final String qName)
			throws SAXException {
		if(qName.equals("td")){
			this.td=false;
            this.currentrow++;
            if(this.firsttd){
                this.rowamount++;
                this.firstRow.add("");
            }
            else if(this.currentrow%this.jump==1 || (this.jump==1 && currentrow>0)){
                 this.medalcounter++;
            }
        }
		if(qName.equals("tr")){
            this.currentrow=0;
            if(this.firsttd){
                System.out.println("Rowamount: "+this.rowamount);
                this.jump=((this.rowamount -1)/3);
                System.out.println("Jump: "+this.jump);
                for(String s:this.firstRow){
                    if("".equals(s) ){
                            this.currentrow++;
                        if(this.currentrow%this.jump==1 || (this.jump==1 && currentrow>0))
                            this.medalcounter++;
                    }else{
                        this.addData(s);
                }
            }
                if(this.th){
                    this.th=false;
                }else{
                    this.firsttd=false;
                }
            }
            this.medalcounter=-1;
            this.currentrow=0;

		}
		if(qName.equals("table")){
			this.table=false;
			this.medals=false;
			this.th=false;
            this.event=false;
		}
	}

	/**
	 * StartElement detection of the SAXParser DefaultHandler.
	 */
	@Override
	public void startElement(final String uri, final String localName, final String qName,
			final Attributes attributes) throws SAXException {
		if(this.table && this.medals && qName.equals("td")){
			this.td=true;
		}
		else if(this.table && qName.equals("th")){
			this.th=true;
		}
		else if(qName.equals("tr")){
	        this.medalcounter=0;
		}
		else if(qName.equals("table")){
			this.table=true;
		}		
	}
}
