package use.via.server.module.create;

import java.util.ArrayList;
import java.util.HashMap;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.data.DataFieldZZZ;
import basic.zBasic.util.data.DataStoreZZZ;
import basic.zNotes.kernel.IKernelNotesZZZ;
import custom.zNotes.kernel.KernelNotesLogZZZ;
import custom.zNotes.kernel.KernelNotesZZZ;
import use.via.MapperStoreHttpZZZ;
import use.via.server.NotesDataStoreZZZ;

public class MapperStoreServerVIA extends MapperStoreHttpZZZ implements IKernelNotesZZZ{
	private KernelNotesZZZ objKernelNotes = null;
	
	public MapperStoreServerVIA(KernelNotesZZZ objKernelNotes) throws ExceptionZZZ {
		super(objKernelNotes.getKernelObject());
		this.objKernelNotes = objKernelNotes;		
		this.loadAll(); //Ausführen der überiebenen abstrakten Methoden.
	}

	public DataStoreZZZ getDataStoreCarrier() throws ExceptionZZZ{
		return this.getDataStoreByAlias("Carrier");
	}

	public DataStoreZZZ getDataStoreFile() throws ExceptionZZZ{
		return this.getDataStoreByAlias("File");
	}

	public DataStoreZZZ getDataStoreMovie() throws ExceptionZZZ{
		return this.getDataStoreByAlias("Movie") ;
	}

	public DataStoreZZZ getDataStoreSerie() throws ExceptionZZZ{
		return this.getDataStoreByAlias("Serie");
	}

	public void setDataStoreCarrier(DataStoreZZZ objData) throws ExceptionZZZ{
		this.setDataStoreByAlias("Carrier", objData);
	}

	public void setDataStoreFile(DataStoreZZZ objData) throws ExceptionZZZ{
		this.setDataStoreByAlias("File", objData);
	}

	public void setDataStoreMovie(DataStoreZZZ objData) throws ExceptionZZZ{
		this.setDataStoreByAlias("Movie", objData);
	}

	public void setDataStoreSerie(DataStoreZZZ objData) throws ExceptionZZZ{
		this.setDataStoreByAlias("Serie", objData);
	}
	
	public HashMap loadFieldMapAll() throws ExceptionZZZ{
		this.getKernelNotesLogObject().writeLog(ReflectCodeZZZ.getMethodCurrentName() + "# Start");
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
			
			
			//+++ Carrier - Dokument ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			ArrayList[] alsFielda = new ArrayList[3];
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_ALIAS] = new ArrayList();
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_NAME_HTTP] = new ArrayList();
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_FIELDNAME] = new ArrayList();
			
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_ALIAS].add("Number");        //Alias
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_NAME_HTTP].add("carrierid");        //HTTP-Parameter Name
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_FIELDNAME].add("IDCarrier");       //Notes-Feldname        (Merke: Wird als Wert für Fieldname in das Objektstore-Objekt eingebracht.	
			
			//Merke: Hier in diesem Speziellen Fall ist das ggf. eine Formel/bzw. eher eine Funktion ausgefürht, die einen leerenWert holt, die ausgeführt werden kann. 
			
			
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_ALIAS].add("Title");   
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_NAME_HTTP].add("carriertitle");   
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_FIELDNAME].add("CarrierTitle");   			
			
			
			
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_ALIAS].add("Type");   
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_NAME_HTTP].add("carriertype");   
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_FIELDNAME].add("CarrierType");  		
			
			
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_ALIAS].add("Created");   
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_NAME_HTTP].add("carriercreated");   
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_FIELDNAME].add("DateCarrierCreated");  
			
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_ALIAS].add("Sequence");   
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_NAME_HTTP].add("carriersequence");   
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_FIELDNAME].add("");   //wird nicht gespeichert, sondern nur ggf. zur Berechnung einer CarrierID verwendet ist der Bestandteil hinter dem #.  
			
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_ALIAS].add("Remark");   
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_NAME_HTTP].add("carrierremark");   
			alsFielda[MapperStoreHttpZZZ.iPARAMETER_FIELDNAME].add("CarrierRemark"); 		
			
			objReturn.put("Carrier", alsFielda);
			
			
			//+++ Datei-Dokument ++++++++++++++++++++++++++++++++++++'
			ArrayList[] alsFielda2 = new ArrayList[3];		
			alsFielda2[MapperStoreHttpZZZ.iPARAMETER_ALIAS] = new ArrayList();
			alsFielda2[MapperStoreHttpZZZ.iPARAMETER_NAME_HTTP] = new ArrayList();
			alsFielda2[MapperStoreHttpZZZ.iPARAMETER_FIELDNAME] = new ArrayList();
			
			
			
			alsFielda2[MapperStoreHttpZZZ.iPARAMETER_ALIAS].add("Number");   
			alsFielda2[MapperStoreHttpZZZ.iPARAMETER_NAME_HTTP].add("fileid");   
			alsFielda2[MapperStoreHttpZZZ.iPARAMETER_FIELDNAME].add("IDFile");  	
			//dies gibt es nicht im Frontend
	
			alsFielda2[MapperStoreHttpZZZ.iPARAMETER_ALIAS].add("Name");   
			alsFielda2[MapperStoreHttpZZZ.iPARAMETER_NAME_HTTP].add("filename");   
			alsFielda2[MapperStoreHttpZZZ.iPARAMETER_FIELDNAME].add("FileName"); 
			
			
			alsFielda2[MapperStoreHttpZZZ.iPARAMETER_ALIAS].add("Size");   
			alsFielda2[MapperStoreHttpZZZ.iPARAMETER_NAME_HTTP].add("filesize");   
			alsFielda2[MapperStoreHttpZZZ.iPARAMETER_FIELDNAME].add("FileSize");  
		
			alsFielda2[MapperStoreHttpZZZ.iPARAMETER_ALIAS].add("Date");   
			alsFielda2[MapperStoreHttpZZZ.iPARAMETER_NAME_HTTP].add("filedate");   
			alsFielda2[MapperStoreHttpZZZ.iPARAMETER_FIELDNAME].add("DateFileCreated");  		
			
			alsFielda2[MapperStoreHttpZZZ.iPARAMETER_ALIAS].add("CompressionType");   
			alsFielda2[MapperStoreHttpZZZ.iPARAMETER_NAME_HTTP].add("filecompressiontype");   
			alsFielda2[MapperStoreHttpZZZ.iPARAMETER_FIELDNAME].add("FileCompressionType");  
			
			
			alsFielda2[MapperStoreHttpZZZ.iPARAMETER_ALIAS].add("Remark");   
			alsFielda2[MapperStoreHttpZZZ.iPARAMETER_NAME_HTTP].add("fileremark");   
			alsFielda2[MapperStoreHttpZZZ.iPARAMETER_FIELDNAME].add("FileRemark");  			
						
			objReturn.put("File", alsFielda2);
			
			
			//+++ Film - Dokument +++++++++++++++++++++++++++++++++++++++++++++++
			ArrayList[] alsFielda3 = new ArrayList[3];
			alsFielda3[MapperStoreHttpZZZ.iPARAMETER_ALIAS] = new ArrayList();
			alsFielda3[MapperStoreHttpZZZ.iPARAMETER_NAME_HTTP] = new ArrayList();
			alsFielda3[MapperStoreHttpZZZ.iPARAMETER_FIELDNAME] = new ArrayList();
	
			
			alsFielda3[MapperStoreHttpZZZ.iPARAMETER_ALIAS].add("Title");   
			alsFielda3[MapperStoreHttpZZZ.iPARAMETER_NAME_HTTP].add("movietitle");   
			alsFielda3[MapperStoreHttpZZZ.iPARAMETER_FIELDNAME].add("MovieTitle");	
			
			alsFielda3[MapperStoreHttpZZZ.iPARAMETER_ALIAS].add("Remark");   
			alsFielda3[MapperStoreHttpZZZ.iPARAMETER_NAME_HTTP].add("movieremark");   
			alsFielda3[MapperStoreHttpZZZ.iPARAMETER_FIELDNAME].add("MovieRemark");
		
			objReturn.put("Movie", alsFielda3);
			
			//+++ Serien-Dokument +++++++++++++++++++++++++++++++++++++++++++++++++
			ArrayList[] alsFielda4 = new ArrayList[3];
			alsFielda4[MapperStoreHttpZZZ.iPARAMETER_ALIAS] = new ArrayList();
			alsFielda4[MapperStoreHttpZZZ.iPARAMETER_NAME_HTTP] = new ArrayList();
			alsFielda4[MapperStoreHttpZZZ.iPARAMETER_FIELDNAME] = new ArrayList();
						
			alsFielda4[MapperStoreHttpZZZ.iPARAMETER_ALIAS].add("Title");   
			alsFielda4[MapperStoreHttpZZZ.iPARAMETER_NAME_HTTP].add("serietitle");   
			alsFielda4[MapperStoreHttpZZZ.iPARAMETER_FIELDNAME].add("SerieTitle");  	
			
			alsFielda4[MapperStoreHttpZZZ.iPARAMETER_ALIAS].add("Remark");   
			alsFielda4[MapperStoreHttpZZZ.iPARAMETER_NAME_HTTP].add("serieremark");   
			alsFielda4[MapperStoreHttpZZZ.iPARAMETER_FIELDNAME].add("SerieRemark"); 			//gibt es nicht im Frontend
			
			objReturn.put("Serie", alsFielda4);
						
		}
		this.getKernelNotesLogObject().writeLog(ReflectCodeZZZ.getMethodCurrentName() + "#Ende");
		return objReturn;
	}
	
	public void loadFieldDataStoreFieldStructureAll() throws ExceptionZZZ{
		this.getKernelNotesLogObject().writeLog(ReflectCodeZZZ.getMethodCurrentName());
		
		//Merke: Die Feldnamen korrespondieren mit den Feldnamen in einer Notes Maske
		//Merke2: Die Metadaten werden in alle Dokumente eingetragen und befinden sich in einer Subform
		
		//Merke 3: FGL 2010-09-04 Entsprechend dieses Mappings wird nun eine XSD - Datei zur Verfügung gestellt, über die eine XML-Beans basierte .jar Datei erstellt wird.
		
		
		//TODO: Die Feldnamen für die NotesFelder werden schon im Servlet-gemapped. Dieses Mapping hier übergeben und dann die Werte dynamisch auslesen pro Alias und Datastore.
		
		//### Carrier
		DataFieldZZZ objField = new DataFieldZZZ("Title");
		objField.Datatype=DataFieldZZZ.sSTRING;
		objField.Fieldname="CarrierTitle";
		//TODO: AN dieser Stelle muss es möglich sein eine Regel anzugeben, ob der neue Wert den bestehenden Wert erssezt, ergänzt, etc.
		//Dies sollte ggf. in @Formula gehalten sein. Damit es mit session.evaluate ausgeführt werden kann.
		
		
		//20080127  Durch diese  Konfigurationen wird definiert, wie ein ggf.  bestehender Wert verändert werden soll.
		//Wird dann ein Dokument gefunden, muss man dieses Dokument weiter bearbeiten !!! Dadurch lassen sich z.B. keine Kommentare zu Filmen ergänzen !!!
		objField.Targetvaluehandling = DataFieldZZZ.sTARGET_VALUE_REPLACE;
		
		
		this.getDataStoreCarrier().setField(objField);
				
		objField = new DataFieldZZZ("Type");    //CD, DVD, ....
		objField.Datatype=DataFieldZZZ.sSTRING;
		objField.Fieldname="CarrierType";
		objField.Targetvaluehandling = DataFieldZZZ.sTARGET_VALUE_REPLACE; 		
		this.getDataStoreCarrier().setField(objField);
				
		objField = new DataFieldZZZ("Created");  //Das Brenndatum
		objField.Datatype=DataFieldZZZ.sDATE;
		objField.Fieldname="DateCarrierCreated";
		objField.Format="dd.MM.yyyy";		
		objField.Targetvaluehandling = DataFieldZZZ.sTARGET_VALUE_REPLACE;
		this.getDataStoreCarrier().setField(objField);
		
		objField = new DataFieldZZZ("Sequence");  //Das Brenndatum
		objField.Datatype=DataFieldZZZ.sSTRING;
		objField.Fieldname="";
		objField.Format="";		
		objField.Targetvaluehandling = DataFieldZZZ.sTARGET_VALUE_REPLACE;
		this.getDataStoreCarrier().setField(objField);
		
		//Merke: Das Brenndatum ist ein Parameter in der Berechnung und muss zuvor gestzt worden sein
		objField = new DataFieldZZZ("Number");
		objField.Datatype=DataFieldZZZ.sSTRING;
		objField.Fieldname="IDCarrier";										//Brenndatum(JJMMDD) + # +  lfd. Nummer z.B. 060907#11, die fortlaufende Nummer wird dann automatisch berechnet.			
		objField.CustomClassPostTargetInsert="use.via.server.module.create.CarrierVIA";
		objField.CustomMethodPostTargetInsert = "customExampleForPostTargetInsert";       //Hier soll die CarrierID berechnet werden, eine Methode aus DocumentCreator geerbt.
		objField.Targetvaluehandling = DataFieldZZZ.sTARGET_VALUE_KEEP;  //Schlüsselwert, darum behalten
		this.getDataStoreCarrier().setField(objField);
		
				
		objField = new DataFieldZZZ("Remark");
		objField.Datatype=DataFieldZZZ.sNOTESRICHTEXT;
		objField.Fieldname="CarrierRemark";
		objField.Targetvaluehandling = DataFieldZZZ.sTARGET_VALUE_APPEND;
		this.getDataStoreCarrier().setField(objField);
		
				
		//### File
		objField = new DataFieldZZZ("Number");  
		objField.Datatype=DataFieldZZZ.sSTRING;         
		objField.Fieldname="IDFile";                                           //fortlaufende Nummer über alle Dateidokumente
		objField.Targetvaluehandling = DataFieldZZZ.sTARGET_VALUE_KEEP; //Schlüsselwert, darum behalten
		this.getDataStoreFile().setField(objField);
		
		objField = new DataFieldZZZ("Name");
		objField.Datatype=DataFieldZZZ.sSTRING;
		objField.Fieldname="FileName";
		objField.Targetvaluehandling=DataFieldZZZ.sTARGET_VALUE_KEEP; //Schlüsselwert, darum behalten
		this.getDataStoreFile().setField(objField);
		
		objField = new DataFieldZZZ("Size");
		objField.Datatype=DataFieldZZZ.sDOUBLE;
		objField.Fieldname="FileSize";
		objField.Targetvaluehandling=DataFieldZZZ.sTARGET_VALUE_KEEP; //Schlüsselwert, darum behalten
		this.getDataStoreFile().setField(objField);
	
		objField = new DataFieldZZZ("Date");
		objField.Datatype=DataFieldZZZ.sDATE;
		objField.Fieldname="DateFileCreated";
		objField.Format="dd.MM.yyyy";
		objField.Targetvaluehandling=DataFieldZZZ.sTARGET_VALUE_KEEP; //Schlüsselwert, darum behalten
		this.getDataStoreFile().setField(objField);
				
		objField = new DataFieldZZZ("CompressionType");  //z.B. DivX 5.11
		objField.Datatype=DataFieldZZZ.sSTRING;
		objField.Fieldname="FileCompressionType";
		objField.Targetvaluehandling=DataFieldZZZ.sTARGET_VALUE_REPLACE;    //Dies kann ja ggf. beim erneuten Einlesen anders, d.h. korregiert, angegeben werden
		this.getDataStoreFile().setField(objField);
		
		objField = new DataFieldZZZ("Remark");
		objField.Datatype=DataFieldZZZ.sNOTESRICHTEXT;
		objField.Fieldname="FileRemark";
		objField.Targetvaluehandling=DataFieldZZZ.sTARGET_VALUE_APPEND;
		this.getDataStoreFile().setField(objField);
		
		
		//### Movie
		objField = new DataFieldZZZ("Title");
		objField.Datatype=DataFieldZZZ.sSTRING;
		objField.Fieldname="MovieTitle";
		objField.Targetvaluehandling=DataFieldZZZ.sTARGET_VALUE_KEEP; //Schlüsselwert, darum behalten
		this.getDataStoreMovie().setField(objField);
		
		objField = new DataFieldZZZ("Remark");
		objField.Datatype=DataFieldZZZ.sNOTESRICHTEXT;
		objField.Fieldname="MovieRemark";
		objField.Targetvaluehandling=DataFieldZZZ.sTARGET_VALUE_APPEND;
		this.getDataStoreMovie().setField(objField);
		
		//### Serie
		objField = new DataFieldZZZ("Title");
		objField.Datatype=DataFieldZZZ.sSTRING;
		objField.Fieldname="SerieTitle";
		objField.Targetvaluehandling=DataFieldZZZ.sTARGET_VALUE_KEEP; //Schlüsselwert, darum behalten
		this.getDataStoreSerie().setField(objField);
		
		objField = new DataFieldZZZ("Remark");
		objField.Datatype=DataFieldZZZ.sNOTESRICHTEXT;
		objField.Fieldname="SerieRemark";
		objField.Targetvaluehandling=DataFieldZZZ.sTARGET_VALUE_APPEND;
		this.getDataStoreSerie().setField(objField);
		
		//### Metadaten, für jedes Dokument
		objField = new DataFieldZZZ("Creator");
		objField.Datatype=DataFieldZZZ.sNOTESNAME;
		objField.Fieldname="CreatorVIA";
		objField.Targetvaluehandling=DataFieldZZZ.sTARGET_VALUE_KEEP;
		this.getDataStore().setField(objField);
		
		objField = new DataFieldZZZ("Modifier");
		objField.Datatype=DataFieldZZZ.sNOTESNAME;
		objField.Fieldname="ModifierLastVIA";
		objField.Targetvaluehandling=DataFieldZZZ.sTARGET_VALUE_REPLACE;
		this.getDataStore().setField(objField);
		
		objField = new DataFieldZZZ("ModifierHistory");
		objField.Datatype=DataFieldZZZ.sNOTESNAME;
		objField.Fieldname="ModifierHistoryVIA";
		objField.Targetvaluehandling=DataFieldZZZ.sTARGET_VALUE_PREPEND;
		this.getDataStore().setField(objField);
		
		
		objField = new DataFieldZZZ("Reader");
		objField.Datatype = DataFieldZZZ.sNOTESREADER;
		objField.Fieldname = "ReaderVIA";
		objField.Targetvaluehandling=DataFieldZZZ.sTARGET_VALUE_APPEND_UNIQUE;  //Falls z.B. ein Datenträgerdokument von mehrern Personen durch Hinzufügen von Filmen verändert wird, oder eine Serie 
		this.getDataStore().setField(objField);
		
		objField = new DataFieldZZZ("ImportDate");
		objField.Datatype=DataFieldZZZ.sDATE;
		objField.Fieldname="LastImportDateVIA";             //!!! hier wird kein Format übergeben, statt dessen soll das heutige Datum ausgerechnet werden. Als echtes Date-Objekt.
		objField.Targetvaluehandling=DataFieldZZZ.sTARGET_VALUE_REPLACE;
		this.getDataStore().setField(objField);
		
		objField = new DataFieldZZZ("Author");
		objField.Datatype = DataFieldZZZ.sNOTESAUTHOR;
		objField.Fieldname = "AuthorVIA";
		objField.Targetvaluehandling=DataFieldZZZ.sTARGET_VALUE_APPEND_UNIQUE;  //Fehlerbehebund 20080513
		this.getDataStore().setField(objField);		
	}

	public KernelNotesZZZ getKernelNotesObject() {
		return this.objKernelNotes;
	}

	public void setKernelNotesObject(KernelNotesZZZ objKernelNotes) {
		this.objKernelNotes = objKernelNotes;
	}

	public KernelNotesLogZZZ getKernelNotesLogObject() throws ExceptionZZZ {
		return this.objKernelNotes.getKernelNotesLogObject();
	}

	public void setKernelNotesLogObject(KernelNotesLogZZZ objKernelNotesLogIn) {
		this.objKernelNotes.setKernelNotesLogObject(objKernelNotesLogIn);
	}

	public void loadDataStoreAll() throws ExceptionZZZ {
//		Erstellen der weiteren DataStore-Objekte
		this.getKernelNotesLogObject().writeLog(ReflectCodeZZZ.getMethodCurrentName()+"#frmCarrierVIA");
		NotesDataStoreZZZ objDataCarrier = new NotesDataStoreZZZ(this.getKernelNotesObject(), "frmCarrierVIA");  //DataStore für das "Carrier - Dokument"
		hmDataStore.put("Carrier", objDataCarrier);
		this.getKernelNotesLogObject().writeLog(ReflectCodeZZZ.getMethodCurrentName()+"#frmFileVIA");
		NotesDataStoreZZZ objDataFile = new NotesDataStoreZZZ(this.getKernelNotesObject(), "frmFileVIA"); //DataStore für das "File - Dokument"
		hmDataStore.put("File", objDataFile);
		this.getKernelNotesLogObject().writeLog(ReflectCodeZZZ.getMethodCurrentName()+"#frmMovieVIA");
		NotesDataStoreZZZ objDataMovie = new NotesDataStoreZZZ(this.getKernelNotesObject(),"frmMovieVIA"); //DataStrore für das "Film - Dokument"
		hmDataStore.put("Movie", objDataMovie);
		this.getKernelNotesLogObject().writeLog(ReflectCodeZZZ.getMethodCurrentName()+"#frmSerieVIA");
		NotesDataStoreZZZ objDataSerie = new NotesDataStoreZZZ(this.getKernelNotesObject(), "frmSerieVIA"); //DataStore für das "Serie - Dokument"
		hmDataStore.put("Serie", objDataSerie);		
	}

}
