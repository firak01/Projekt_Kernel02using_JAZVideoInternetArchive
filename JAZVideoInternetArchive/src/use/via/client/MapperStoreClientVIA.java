package use.via.client;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.util.data.DataFieldZZZ;
import basic.zBasic.util.data.DataStoreZZZ;
import basic.zKernel.KernelZZZ;
import use.via.MapperStoreHttpZZZ;

public class MapperStoreClientVIA extends MapperStoreHttpZZZ {

	public MapperStoreClientVIA(KernelZZZ objKernel) throws ExceptionZZZ {
		super(objKernel);
		this.loadAll(); //Ausführen der überiebenen abstrakten Methoden.
	}
	 
	public DataStoreZZZ getDataStoreExportPanel() throws ExceptionZZZ{
		return this.getDataStoreByAlias("ExportPanel") ;
	}
	
	public void loadDataStoreAll() throws ExceptionZZZ {
		//TODO Idee: Das Panel übergeben im Konstruktor //DataStoreZZZ objDataExportPanel = new DataStoreZZZ("ExportPanel");  //DataStore für das "Carrier - Dokument"
		DataStoreZZZ objDataExportPanel = new DataStoreZZZ("ExportPanel");  //DataStore für das "Carrier - Dokument"
		hmDataStore.put("ExportPanel", objDataExportPanel);
	}
	 
	public HashMap loadFieldMapAll() throws ExceptionZZZ {
		HashMap objReturn = new HashMap();
		main:{
			/* Aufgabe:
			 * Eine Datenstruktur zur Vefügung stellen, in der die Parameter gemapped werden: liste(sMaske) = sParameter_namen.
			   Aufgrund des HTTP-Parameternamens soll Alias-Parametername ermittelt werden (damit das DataStore-Objekt dazu später den passenden Feldnamen hat.
			   
			   Diese Datenstruktur wird dann in eine HashMap gepackt, welche als Key den Namen des Datastores besitzt.
			   
			/* Merke1: Aus Dokumentationsgründen (und vielleicht ist das ja auch später so verwendbar, dass der Feldname beim Laden der DataStructure nicht mehr "hart ausprogrammiert" zu werden braucht)
			 *  wird der Feldname als 3. Parameter einfliessen.
			
			/* Merke2: Weil es keine einfache (!nicht wie für JTabel die Tabellen Modelle), schon fertige Tabellenstruktur gibt (hab danach mehrere Stunden gesucht),
			*  entscheide ich mich dafür, dass die drei Werte: Feldname-Alias, HPPT-Name, Notes-Feldname in eine dreidimensionale ArrayList gepackt wird.				
			*  
			*    TODO: Klasse, die Methoden bietet, auf die mehrdimensionale ArrayList zuzugreifen.
			 * 
			 */
			
								
			//TODO - Behandeln von mehreren gleichnamigen Parametern mit getParameterValues()
			
			ArrayList[] alsFielda = new ArrayList[3];
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_ALIAS] = new ArrayList();
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_NAME_HTTP] = new ArrayList();
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_FIELDNAME] = new ArrayList();
						
			
			//++++ Carrier
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_ALIAS].add("CarrierID");        		   //Alias
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_NAME_HTTP].add("carrierid");        //HTTP-Parameter Name
			
			//TODO: IDEE: Momentan wird hier direkt mit den aliasnamen der Swing Komponenten gearbeitet. Es sollte aber mit den Aliasnamen der "DataFieldZZZ"-Objekte gearbeitet werden.
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_FIELDNAME].add("CarrierID"); //Swing-Komponenten-alias. D.h. Die Beizeichnung eines DataFieldZZZ, ggf. ist darin eine Formel eingebaut.		
																														  //Merke: Hier in diesem speziellen Fall ist das sogar eine Formel, die ausgeführt werden muss (mit JEXL) 
						
			
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_ALIAS].add("CarrierTitle");   
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_NAME_HTTP].add("carriertitle");   
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_FIELDNAME].add("CarrierTitle");   			
			
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_ALIAS].add("CarrierType");   
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_NAME_HTTP].add("carriertype");   
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_FIELDNAME].add("CarrierType");  		
			
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_ALIAS].add("CarrierCreated");   
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_NAME_HTTP].add("carriercreated");   
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_FIELDNAME].add("DateCarrierCreated");  
			
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_ALIAS].add("CarrierSequenze");   
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_NAME_HTTP].add("carriersequence");  
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_FIELDNAME].add("CarrierSequenzeNumber");
			
			//+++ File
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_ALIAS].add("FileName");   
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_NAME_HTTP].add("filename");   
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_FIELDNAME].add("FileName"); 
						
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_ALIAS].add("FileSize");   
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_NAME_HTTP].add("filesize");   
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_FIELDNAME].add("FileSize");  
			
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_ALIAS].add("FileDate");   
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_NAME_HTTP].add("filedate");   
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_FIELDNAME].add("DateFileModified");  				
			
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_ALIAS].add("FileCompressionType");   
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_NAME_HTTP].add("filecompressiontype");   
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_FIELDNAME].add("FileCompressionType");  
							
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_ALIAS].add("FileRemark");   
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_NAME_HTTP].add("fileremark");   
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_FIELDNAME].add("FileRemark");  				
		
			
			//+++ Movie
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_ALIAS].add("MovieTitle");   
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_NAME_HTTP].add("movietitle");   
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_FIELDNAME].add("MovieTitle");				
			
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_ALIAS].add("Remark");   
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_NAME_HTTP].add("movieremark");   
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_FIELDNAME].add("MovieRemark");
			
			//+++ Serie		
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_ALIAS].add("SerieTitle");   
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_NAME_HTTP].add("serietitle");   
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_FIELDNAME].add("SerieTitle");  
		
			/*
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_ALIAS].add("SerieRemark");   
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_NAME_HTTP].add("serieremark");   
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_FIELDNAME].add("SerieRemark"); 
		*/
		
			objReturn.put("ExportPanel", alsFielda);
		}
		return objReturn;
	}

	public void loadFieldDataStoreFieldStructureAll() throws ExceptionZZZ {
		// TODO Idee: Das befüllen der Feldstruktur ist eigentlich Aufgabe der Klasse DataStoreZZZ
//		Merke: Die Feldnamen korrespondieren mit den Feldnamen in einer Notes Maske
		//Merke2: Die Metadaten werden in alle Dokumente eingetragen und befinden sich in einer Subform
		
		//TODO: Die Feldnamen für die NotesFelder werden schon im Servlet-gemapped. Dieses Mapping hier übergeben und dann die Werte dynamisch auslesen pro Alias und Datastore.
		
		//### ExportPanel (Merke: Das ist für eine Swing-Maske)
		DataFieldZZZ objField = new DataFieldZZZ("CarrierID");  //MErke: Diese Aliaswerte entsprechen den gemappten Werten aus loadFileMapAll() für MapperStoreHttpZZZ.iPARAMETER_FIELDNAME
		objField.Datatype=DataFieldZZZ.sSTRING;                //Datentyp. Wird beachtet beim Setzen der Werte ins Ziel. 
																				//               Entspricht dem Rückgabewert der Datamethod beim Auslesen der Werte aus der Quelle.
		objField.Fieldclass = JTextField.class.getName();
		objField.Fieldmethod="getText";
		
		//Name der Methode, mit der aus der Quelle der Wert ausgelesen werden soll.
		//a) @Z-Methode: Mit JEXL Unterstützung diese Formel auflösen
		//b) Normale Java-MEthode: Per Reflection-API mit invoke(...) aufrufen	
		
		//		Das ist zu einfach. objField.Fieldmethod="CarrierCreated+'#'+CarrierSequenze";	
		//Ziel der Formel ist, dass der Wert nur gesetzt wird, wenn es einen Wert für CarrierSequenze gibt.
		//objField.Fieldmethod = "CarrierSequenze<?/>CarrierCreated<+/>'#'<+/>CarrierSequenze<:/>''";  //Dat hab ich mir selber ausgedacht. Heisst: WEnn CarrierSequenz gefüllt ist, dann ... ansonsten CarrierID''.
		//TODO: DAS REALISIEREN 
		//objField.Fieldmethod = "CarrierID<?/>CarrierID<:/>CarrierCreated<+/>'#'<+/>CarrierSequenze";  //Dat hab ich mir selber ausgedacht. Heisst: WEnn CarrierID gefüllt ist, dann ... ansonsten rechne aus''.
		objField.Fieldname="textCarrierId";        ////Im Notes: DerFeldname der Maske, Im Swing: Der Aliasname der Komponente, wie er im ZKernel hinzugefügt wurde.
		objField.Zclass="@Z"; 									 //!!!Hier: Es soll eine @Z - Methode ausgeführt werden, sonst: Klasse der Komponente
		//objField.Zmethod="CarrierID<?/>CarrierID<:/>CarrierCreated<+/>'#'<+/>CarrierSequenze";  //Dat hab ich mir selber ausgedacht. Heisst: WEnn CarrierID gefüllt ist, dann ... ansonsten rechne aus''.
		objField.Zmethod="CarrierID<?/>CarrierID<:/>''";  //Dat hab ich mir selber ausgedacht. Heisst: WEnn CarrierID gefüllt ist, dann ... ansonsten rechne aus''.
		this.getDataStoreExportPanel().setField(objField);
		
		objField = new DataFieldZZZ("CarrierSequenzeNumber");    //Merke: Dies dies ist aber z.B. ein Feld, dass im HTTP-Mapping nicht vorhanden ist. Sondern nur in einer Formel verwendet wird. Trotzdem muss der Alias in loadFieldMapAll() auftauchen.
		objField.Datatype = DataFieldZZZ.sSTRING;
		objField.Fieldclass = JTextField.class.getName();
		objField.Fieldmethod = "getText";
		objField.Fieldname = "textCarrierSequenze";
		this.getDataStoreExportPanel().setField(objField);
		
		objField = new DataFieldZZZ("CarrierTitle");   //Im Konstruktor wird der Alias mitgegeben. Siehe MapperStoreHttpZZZ.loadFieldMapAllDefault
		objField.Datatype=DataFieldZZZ.sSTRING;                //Datentyp. Wird beachtet beim Setzen der Werte ins Ziel. 
																				 //               Entspricht dem Rückgabewert der Datamethod beim Auslesen der Werte aus der Quelle.
		objField.Fieldclass = JLabel.class.getName();       //Klasse der Komponente
		objField.Fieldmethod="getText";							//Name der Methode, mit der aus der Quelle der Wert ausgelesen werden soll. Merke: Diese Methode soll per Reflection-API mit invoke(...) aufgerufen werden.
		objField.Fieldname="labelCarrierTitle";                            //Im Notes: DerFeldname der Maske, Im Swing: Der Aliasname der Komponente, wie er im ZKernel hinzugefügt wurde.					
		this.getDataStoreExportPanel().setField(objField);
				
		objField = new DataFieldZZZ("CarrierType");
		objField.Datatype=DataFieldZZZ.sSTRING;
		objField.Fieldclass = JComboBox.class.getName();       //Klasse der Komponente
		objField.Fieldmethod="getSelectedItem";		
		objField.Fieldname="comboCarrierType";
		this.getDataStoreExportPanel().setField(objField);
				
		objField = new DataFieldZZZ("DateCarrierCreated");  //Das Brenndatum
		objField.Datatype=DataFieldZZZ.sDATE;
		objField.Fieldclass = JTextField.class.getName();       //Klasse der Komponente
		objField.Fieldmethod="getText";		
		objField.Fieldname="textCarrierCreated";
		objField.Format="dd.MM.yyyy";		
		this.getDataStoreExportPanel().setField(objField);
		
		//Merke: Das Brenndatum ist ein Parameter in der Berechnung und muss zuvor gestzt worden sein
		objField = new DataFieldZZZ("CarrierSequenze");
		objField.Datatype=DataFieldZZZ.sSTRING;
		objField.Fieldclass = JTextField.class.getName();       //Klasse der Komponente
		objField.Fieldmethod="getText";		
		objField.Fieldname="textCarrierSequenze";										//Brenndatum(JJMMDD) + # +  lfd. Nummer z.B. 060907#11, die fortlaufende Nummer wird dann automatisch berechnet.			
		//objField.CustomClassPostTargetInsert="use.via.server.CarrierVIA";
		//objField.CustomMethodPostTargetInsert = "customExampleForPostTargetInsert";       //Hier soll die CarrierID berechnet werden, eine Methode aus DocumentCreator geerbt.
		this.getDataStoreExportPanel().setField(objField);
						
				
		//### File	
		objField = new DataFieldZZZ("FileName");
		objField.Datatype=DataFieldZZZ.sSTRING;
		objField.Fieldclass = JTextField.class.getName();       //Klasse der Komponente
		objField.Fieldmethod="getText";		
		objField.Fieldname="textFileName";
		this.getDataStoreExportPanel().setField(objField);
		
		objField = new DataFieldZZZ("FileSize");
		objField.Datatype=DataFieldZZZ.sSTRING;
		objField.Fieldclass = JLabel.class.getName();       //Klasse der Komponente
		objField.Fieldmethod="getText";		
		objField.Fieldname="labelFileSize";
		this.getDataStoreExportPanel().setField(objField);
	
		objField = new DataFieldZZZ("DateFileModified");
		objField.Datatype=DataFieldZZZ.sDATE;
		objField.Fieldclass = JLabel.class.getName();       //Klasse der Komponente
		objField.Fieldmethod="getText";		
		objField.Fieldname="labelFileDate";
		objField.Format="dd.MM.yyyy";
		this.getDataStoreExportPanel().setField(objField);
				
		objField = new DataFieldZZZ("FileCompressionType");  //z.B. DivX 5.11
		objField.Datatype=DataFieldZZZ.sSTRING;
		objField.Fieldclass = JComboBox.class.getName();       //Klasse der Komponente
		objField.Fieldmethod="getSelectedItem";		
		objField.Fieldname="comboFileCompression";
		this.getDataStoreExportPanel().setField(objField);
		
		objField = new DataFieldZZZ("FileRemark");
		objField.Datatype=DataFieldZZZ.sSTRING;
		objField.Fieldclass = JTextArea.class.getName();       //Klasse der Komponente
		objField.Fieldmethod="getText";		
		objField.Fieldname="textaFileRemark";
		this.getDataStoreExportPanel().setField(objField);
		
		
		//### Movie
		objField = new DataFieldZZZ("MovieTitle");
		objField.Datatype=DataFieldZZZ.sSTRING;
		objField.Fieldclass = JTextField.class.getName();       //Klasse der Komponente
		objField.Fieldmethod="getText";		
		objField.Fieldname="textMovieTitle";
		this.getDataStoreExportPanel().setField(objField);
		
		objField = new DataFieldZZZ("MovieRemark");
		objField.Datatype=DataFieldZZZ.sSTRING;
		objField.Fieldclass = JTextArea.class.getName();       //Klasse der Komponente
		objField.Fieldmethod="getText";		
		objField.Fieldname="textaMovieRemark";
		this.getDataStoreExportPanel().setField(objField);
		
		//### Serie
		objField = new DataFieldZZZ("SerieTitle");
		objField.Datatype=DataFieldZZZ.sSTRING;
		objField.Fieldclass = JComboBox.class.getName();       //Klasse der Komponente
		objField.Fieldmethod="getSelectedItem";		
		objField.Fieldname="comboSerieTitle";
		this.getDataStoreExportPanel().setField(objField);
				
	}//end .loadFieldStructureAllDefault()
	
}
