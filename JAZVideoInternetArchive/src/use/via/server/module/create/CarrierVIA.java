/**
 * 
 */
package use.via.server.module.create;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import org.apache.commons.lang.time.DateUtils;

import use.via.MapperStoreHttpZZZ;
import use.via.server.DocumentCreatorZZZ;
import use.via.server.ICategorizableZZZ;

import lotus.domino.Database;
import lotus.domino.DateTime;
import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.Item;
import lotus.domino.NotesException;
import lotus.domino.Session;
import custom.zNotes.kernel.KernelNotesZZZ;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.data.DataFieldZZZ;
import basic.zBasic.util.data.DataStoreZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zNotes.kernel.KernelNotesUseObjectZZZ;
import basic.zNotes.use.log4j.NotesReportLogZZZ;
import basic.zNotes.use.util.KernelNumberGeneratorZZZ;

/**
 * @author 0823
 *
 */
public class CarrierVIA  extends DocumentCreatorZZZ implements ICategorizableZZZ{
		
	public CarrierVIA(){
		//Wird benötigt, um einfach so per ReflectionAPI ein Dokument zu erzeugen.
	}
	public CarrierVIA(KernelNotesZZZ objKernelNotes){
		super(objKernelNotes);
	}
	public CarrierVIA(KernelNotesZZZ objKernelNotes, MapperStoreServerVIA objStoreHTTPMapper){
		super(objKernelNotes);
		this.setMapperStore(objStoreHTTPMapper);
	}
	
	public boolean validateMapperStore() throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			try{
				KernelNotesZZZ objKernelNotes = this.getKernelNotesObject();			
				Session session = objKernelNotes.getSession();
				
				Database db = objKernelNotes.getDBApplicationCurrent();
				if (db ==null ) break main;
				
				MapperStoreServerVIA objMapper = (MapperStoreServerVIA) this.getMapperStore();
				DataStoreZZZ objData = objMapper.getDataStoreCarrier();	
				
				
				//################################################
				//Die Validierungen
				//es muss entweder ein Dokument mit der CarrierID existieren, oder die anderen Werte müssen gefüllt sein
				
//				### nun die CarrierID setzen, es sei denn sie wurde schon zuvor übergeben und gesetzt							
				String sCarrierIDPassed = objData.getValueString("Number", 0);
				if(StringZZZ.isEmpty(sCarrierIDPassed)){
					NotesReportLogZZZ.write(NotesReportLogZZZ.INFO, "CarrierID: Not passed by http-request-parameter. Other paremters will be used.");
					
					
					//### Das "Brenndatum/Created" des Carriers ist ein ganz wichtiger Parameter, der übergeben worden sein MUSS
					String sDate = objData.getValueString("Created", 0);
					if(StringZZZ.isEmpty(sDate)){
						ExceptionZZZ ez = new ExceptionZZZ("No 'creation date' of the carrier was passed by http-request-parameter.", iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
						throw ez;
					}
					
					//### FALLS eine SequenzeNr übergeben wurde, so muss sie numerisch sein
					String sSequenzeNr = objData.getValueString("Sequence", 0);
					if(!StringZZZ.isEmpty(sSequenzeNr)){
						if(!StringZZZ.isNumeric(sSequenzeNr)){
							ExceptionZZZ ez = new ExceptionZZZ("Passed Carrier-SequenzeNr is not nummeric", iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
							throw ez;
						}
					}
					
				}else{						
					//TODO: Generelles Konzept bzgl. der Validierung
					
					//20080318 Prüfe zuvor ab, ob es eine syntaktisch gültige CarrierId ist
					boolean bIsValid = CarrierVIA.isValidCarrierIdFeedback(sCarrierIDPassed);  //Merke: Das Feedback besteht  in der detailierten Beschreibung in der geworfenen ExceptionZZZ 
					if(bIsValid==false){ //sicherheitshalber noch abprüfen, eigentlich erwarte ich, dass eine ExceptionZZZ zuvor geworfen wurde
						ExceptionZZZ ez = new ExceptionZZZ("The carrierid is not valid '" + sCarrierIDPassed + "'", iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
						throw ez;
					}
					
					//Falls es schon ein Dokument mit dieser CarrierID gibt, dann sind die übrigen validierungen nicht mehr notwendig, es wird nix neues erstellt, sondern das existierende genommen.
					boolean bExists = CarrierVIA.existsCarrierIdInDb(session, db, this.getFormUsed(), objData, sCarrierIDPassed);
					if(bExists == true){
						NotesReportLogZZZ.write(NotesReportLogZZZ.INFO, "A CarrierID '" + sCarrierIDPassed + "' was passed by http-request-parameter. But this exists in the database: '" + db.getTitle() + "'");
						bReturn = true;
						break main;
					}else{
						NotesReportLogZZZ.write(NotesReportLogZZZ.INFO, "A CarrierID '" + sCarrierIDPassed + "' was passed by http-request-parameter. This seems not to exist in the database: '" + db.getTitle() + "'");
					}
				}
				
				//Falls eine Carrier ID übergeben worden ist, sie aber nicht in der Datenbank existiert, dann sind diese Parameter wichtig, um das Carrierdokument zu erstellen.
//				### Das "Brenndatum/Created" des Carriers ist ein ganz wichtiger Parameter, der übergeben worden sein MUSS
				String sDate = objData.getValueString("Created", 0);
				if(StringZZZ.isEmpty(sDate)){
					ExceptionZZZ ez = new ExceptionZZZ("No 'creation date' of the carrier was passed by http-request-parameter.", iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				//### Titel
				String sTitle = objData.getValueString("Title", 0);
				if(StringZZZ.isEmpty(sTitle)){
					ExceptionZZZ ez = new ExceptionZZZ("No 'title' of the carrier was passed by http-request-parameter.", iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				//### Der Typ
				String sType = objData.getValueString("Type", 0);
				if(StringZZZ.isEmpty(sType)){
					ExceptionZZZ ez = new ExceptionZZZ("No 'type' of the carrier was passed by http-request-parameter.", iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
			
								
				
		
			}catch(NotesException ne){
				ExceptionZZZ ez = new ExceptionZZZ(ne.text, iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			bReturn = true; // wenn bis hierhin alles erfolgreich validiert wurde, o.k.
		}
		return bReturn;
	}
	
	
	public Document searchDocumentExisting() throws ExceptionZZZ {
		Document docReturn = null;
		main:{
			//try{
				KernelNotesZZZ objKernelNotes = this.getKernelNotesObject();							
				Database db = objKernelNotes.getDBApplicationCurrent();
				if (db ==null ) break main;
				
				MapperStoreServerVIA objMapper = (MapperStoreServerVIA) this.getMapperStore();
				DataStoreZZZ objData = objMapper.getDataStoreCarrier();	
				
				//Wenn eine Carrier ID explizit übergeben worden ist, dann danach suchen. Eine Suche nach anderen Kriterien ist auszuschliessen.
				String sCarrierIDPassed = objData.getValueString("Number", 0);
				if(! StringZZZ.isEmpty(sCarrierIDPassed)){					
					
					docReturn = this.searchDocumentByCarrierId(objKernelNotes, objMapper);
					if (docReturn !=null) break main;
					
				}else{
					docReturn = this.searchDocumentBySequenceNr(objKernelNotes, objMapper);
					if(docReturn != null) break main;
					
					docReturn = this.searchDocumentByCarrierProperties(objKernelNotes, objMapper);
					if(docReturn != null) break main;
				}
		}
		return docReturn;
	}
	public Document searchDocumentByCarrierId(KernelNotesZZZ objKernelNotes, MapperStoreServerVIA objMapper) throws ExceptionZZZ{
		Document docReturn = null;
		main:{
			try{
				if(objKernelNotes==null){
					ExceptionZZZ ez = new ExceptionZZZ("KernelNotes-Object", iERROR_PARAMETER_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				Database db = objKernelNotes.getDBApplicationCurrent();
				if (db ==null ){
					ExceptionZZZ ez = new ExceptionZZZ("Application Database in the KernelNotes-Object", iERROR_PROPERTY_EMPTY, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				if(objMapper==null){
					ExceptionZZZ ez = new ExceptionZZZ("KernelNotes-Object", iERROR_PARAMETER_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				DataStoreZZZ objData = objMapper.getDataStoreCarrier();	
				if(objData==null){
					ExceptionZZZ ez = new ExceptionZZZ("DataStore for Carrier in the MapperStoreServer-Object", iERROR_PROPERTY_EMPTY, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				
				//################################################
				//Das Ermitteln der Felder/Felwerte, die als Key herangezogen werden
				String sForm = this.getFormUsed();
				if(StringZZZ.isEmpty(sForm)){
					ExceptionZZZ ez = new ExceptionZZZ("FormUsed", iERROR_PROPERTY_EMPTY, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				
				
				//### Falls die CarrierID übergeben worden ist:
				String sCarrierIDPassed = objData.getValueString("Number", 0);
				if(! StringZZZ.isEmpty(sCarrierIDPassed)){					
					NotesReportLogZZZ.write(NotesReportLogZZZ.INFO, "A CarrierID '" + sCarrierIDPassed + "' was passed by http-request-parameter. Does this exist in the database: '" + db.getTitle() + "'");
					String sFieldnameCarrierID = objData.getMetadata("Number", DataFieldZZZ.FIELDNAME);
					if(StringZZZ.isEmpty(sFieldnameCarrierID)){
						ExceptionZZZ ez = new ExceptionZZZ("Unable to read the NotesFieldname for the alias 'Number' (e.g ='CarrierID') from the DataStoreZZZ-Object. May you this Field is not configured there or you passed a wrong DataStoreZZZ-Object to this method.", iERROR_PARAMETER_MISSING, "CreatorDocumentCarrierVIA", ReflectCodeZZZ.getMethodCurrentName());
						throw ez;
					}
					
					String sSearch = "@Lowercase(Form)=\""+ sForm.toLowerCase() + "\" & " + sFieldnameCarrierID + "=\"" + sCarrierIDPassed + "\"";
					NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "Searching for document in database '"+ db.getTitle() + "' with '" + sSearch + "'");
					
					DocumentCollection col = db.search(sSearch);
					if(col.getCount() >= 1){
						docReturn = col.getFirstDocument();
						NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "A carrier document was found");	
						break main;
					}else{
						//FALL: Ein Carrierdokument existiert nicht
						NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "No carrier document was found for the provided carrier id.");
						break main;
					}
				}else{
					NotesReportLogZZZ.write(NotesReportLogZZZ.INFO, "NO CarrierID '" + sCarrierIDPassed + "' was passed by http-request-parameter. You have to search for the carrier document by a more  complex search statement: '" + db.getTitle() + "'");
					break main;
				}
		}catch(NotesException ne){
			ExceptionZZZ ez = new ExceptionZZZ(ne.text, iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}		
		}//end main:
		return docReturn;
	}
	
	public Document searchDocumentBySequenceNr(KernelNotesZZZ objKernelNotes, MapperStoreServerVIA objMapper) throws ExceptionZZZ{
		//###############################################################################
		//### Nun nach einem anderen, zusammengesetzten Schlüssel suchen
		Document docReturn = null;
		main:{
			try{
				if(objKernelNotes==null){
					ExceptionZZZ ez = new ExceptionZZZ("KernelNotes-Object", iERROR_PARAMETER_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				Database db = objKernelNotes.getDBApplicationCurrent();
				if (db ==null ){
					ExceptionZZZ ez = new ExceptionZZZ("Application Database in the KernelNotes-Object", iERROR_PROPERTY_EMPTY, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				if(objMapper==null){
					ExceptionZZZ ez = new ExceptionZZZ("KernelNotes-Object", iERROR_PARAMETER_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				DataStoreZZZ objData = objMapper.getDataStoreCarrier();	
				if(objData==null){
					ExceptionZZZ ez = new ExceptionZZZ("DataStore for Carrier in the MapperStoreServer-Object", iERROR_PROPERTY_EMPTY, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				//################################################
				//+++ Gibt es überhaupt eine SequenceNr ?
				String sSequenceNr = objData.getValueString("Sequence", 0);
				if(StringZZZ.isEmpty(sSequenceNr)){
					NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "NO Sequence Number was provided. You have to use a more complex search for receiving the carrier document.");	
					break main;
				}
				
				
				//################################################
				//Das Ermitteln der Felder/Felwerte, die als Key herangezogen werden
				String sForm = this.getFormUsed();
				if(StringZZZ.isEmpty(sForm)){
					ExceptionZZZ ez = new ExceptionZZZ("FormUsed", iERROR_PROPERTY_EMPTY, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				

				//### Das "Brenndatum/Created" des Carriers ist ein ganz wichtiger Parameter, der übergeben worden sein MUSS
				String sDate = objData.getValueString("Created", 0);
				if(StringZZZ.isEmpty(sDate)){
					ExceptionZZZ ez = new ExceptionZZZ("No 'creation date' of the carrier was passed by http-request-parameter.", iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				
		
				//++++++++ die Suche				
				String sCarrierIDField = objData.getMetadata("Number", DataFieldZZZ.FIELDNAME);
				
				//Date objDate = CarrierVIA.readDateCarrierCreated(session, objReturn, objData);
				Vector vec = objData.getValueVector("Created");  //Merke: Beim Setzen der Werte in den DataStore wird schon der Korrekte Objekt-Datentyp erzeugt. 
				Date objDate = (Date) vec.firstElement();
				String sCarrierID = CarrierVIA.generateCarrierId(objDate, sSequenceNr);
				
			
				String sSearch = "@Lowercase(Form)=\""+ sForm.toLowerCase() + "\" & " + sCarrierIDField + "=\"" + sCarrierID + "\"";
				NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "Searching for document in database '"+ db.getTitle() + "' with '" + sSearch + "'");
		
				DocumentCollection col = db.search(sSearch);
				if(col.getCount() == 1){
					NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "A carrier document was found");		
					docReturn = col.getFirstDocument();						
					break main;
				}else if(col.getCount() >= 2){
						ExceptionZZZ ez = new ExceptionZZZ("More than one document was found", iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
						throw ez;
				}else{
					NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "NO carrier document was found");								
				}				
	}catch(NotesException ne){
		ExceptionZZZ ez = new ExceptionZZZ(ne.text, iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
		throw ez;
	}		
		}//End main:
		return docReturn;
	}
	
	public Document searchDocumentByCarrierProperties(KernelNotesZZZ objKernelNotes, MapperStoreServerVIA objMapper) throws ExceptionZZZ{
		//###############################################################################
		//### Nun nach einem anderen, zusammengesetzten Schlüssel suchen
		Document docReturn = null;
		main:{
			try{
				if(objKernelNotes==null){
					ExceptionZZZ ez = new ExceptionZZZ("KernelNotes-Object", iERROR_PARAMETER_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				Database db = objKernelNotes.getDBApplicationCurrent();
				if (db ==null ){
					ExceptionZZZ ez = new ExceptionZZZ("Application Database in the KernelNotes-Object", iERROR_PROPERTY_EMPTY, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				if(objMapper==null){
					ExceptionZZZ ez = new ExceptionZZZ("KernelNotes-Object", iERROR_PARAMETER_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				DataStoreZZZ objData = objMapper.getDataStoreCarrier();	
				if(objData==null){
					ExceptionZZZ ez = new ExceptionZZZ("DataStore for Carrier in the MapperStoreServer-Object", iERROR_PROPERTY_EMPTY, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				//################################################
				//Das Ermitteln der Felder/Felwerte, die als Key herangezogen werden
				String sForm = this.getFormUsed();
				if(StringZZZ.isEmpty(sForm)){
					ExceptionZZZ ez = new ExceptionZZZ("FormUsed", iERROR_PROPERTY_EMPTY, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				//### Das "Brenndatum/Created" des Carriers ist ein ganz wichtiger Parameter, der übergeben worden sein MUSS
				String sDate = objData.getValueString("Created", 0);
				if(StringZZZ.isEmpty(sDate)){
					ExceptionZZZ ez = new ExceptionZZZ("No 'creation date' of the carrier was passed by http-request-parameter.", iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				//### Titel
				String sTitle = objData.getValueString("Title", 0);
				if(StringZZZ.isEmpty(sTitle)){
					ExceptionZZZ ez = new ExceptionZZZ("No 'title' of the carrier was passed by http-request-parameter.", iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				//### Der Typ
				String sType = objData.getValueString("Type", 0);
				if(StringZZZ.isEmpty(sType)){
					ExceptionZZZ ez = new ExceptionZZZ("No 'type' of the carrier was passed by http-request-parameter.", iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				//++++++++ die Suche				
				String sDateField = objData.getMetadata("Created", DataFieldZZZ.FIELDNAME);
				String sTitleField = objData.getMetadata("Title", DataFieldZZZ.FIELDNAME);
				String sTypeField = objData.getMetadata("Type", DataFieldZZZ.FIELDNAME);
				String sSearch = "@Lowercase(Form)=\""+ sForm.toLowerCase() + "\" & " + sTitleField + "=\"" + sTitle + "\" & " + sTypeField + "=\"" + sType + "\" & @ToTime(" + sDateField + ")=@ToTime(\"" + sDate + "\")";
				NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "Searching for document in database '"+ db.getTitle() + "' with '" + sSearch + "'");
				
				DocumentCollection col = db.search(sSearch);
				if(col.getCount() == 1){
					NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "A carrier document was found");		
					docReturn = col.getFirstDocument();						
					break main;
				}else if(col.getCount()>= 2){
					ExceptionZZZ ez = new ExceptionZZZ("No 'type' of the carrier was passed by http-request-parameter.", iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}else{
					NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "NO carrier document was found");	
					break main;
				}				
				
			}catch(NotesException ne){
				ExceptionZZZ ez = new ExceptionZZZ(ne.text, iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}		
		}//end main:
		return docReturn;
	}
	
	
	public static String generateCarrierIdNew(Session session, Database dbApplication, String sForm, DataStoreZZZ objData, Date objDate) throws ExceptionZZZ{
		String objReturn = null;
		main:{
			try{
				if(objData==null){
					ExceptionZZZ ez = new ExceptionZZZ("DateStore-Object", iERROR_PARAMETER_MISSING, "CreatorDocumentCarrierVIA", ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				if(objDate==null){
					ExceptionZZZ ez = new ExceptionZZZ("Date-Object", iERROR_PARAMETER_MISSING, "CreatorDocumentCarrierVIA", ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				if(StringZZZ.isEmpty(sForm)){
					ExceptionZZZ ez = new ExceptionZZZ("Form-Name", iERROR_PARAMETER_MISSING, "CreatorDocumentCarrierVIA", ReflectCodeZZZ.getMethodCurrentName());
					throw ez;					
				}
				
				if(dbApplication==null){
					ExceptionZZZ ez = new ExceptionZZZ("Database", iERROR_PARAMETER_MISSING, "CreatorDocumentCarrierVIA", ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				if(dbApplication.isOpen()==false){
					ExceptionZZZ ez = new ExceptionZZZ("Database not open.'" + dbApplication.getTitle() + "'. Access ?", iERROR_PARAMETER_VALUE, "CreatorDocumentCarrierVIA", ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
								
				Calendar objCalendar = Calendar.getInstance();
				objCalendar.setTime(objDate);
				
				int iDay = objCalendar.get(Calendar.DAY_OF_MONTH);
				String sDay = Integer.toString(iDay);
				sDay = StringZZZ.right("0" + sDay, 2);
				int iMonth = objCalendar.get(Calendar.MONTH);  //Merke: dabei scheint wohl 0 der erste Monat zu sein. Ergo ist 11 der Dezember				
				String sMonth = Integer.toString(iMonth+1);
				sMonth = StringZZZ.right("0" + sMonth, 2);					
				int iYear = objCalendar.get(Calendar.YEAR);
				String sYear = Integer.toString(iYear);
				sYear = StringZZZ.right("0" + sYear, 2);
				String sIDBase = sYear + sMonth + sDay;
				
				//3. Anzahl der Dokumente in der Datenbank ermitteln, bei denen für den Carrier das Datum gesetzt ist
				java.util.Calendar jdt = java.util.Calendar.getInstance();
			    jdt.set(iYear, iMonth, iDay);
			    DateTime dt = session.createDateTime(jdt);
			    
			    String sFieldnameCreated = objData.getMetadata("Created", DataFieldZZZ.FIELDNAME);				
			    String sDate = dt.getDateOnly();
				String sSearch = "Form =\"" + sForm + "\" & @Text(@Date(" + sFieldnameCreated + "))=\""+ sDate +"\"";  
				DocumentCollection col = dbApplication.search(sSearch);	
				
/*Lösung mit Auffüllen, aber: Das muss jetzt anders ermittelt werden						
				objReturn = sIDBase + "#" + Integer.toString(col.getCount() + 1);
				
				//Nur die Anzahl ist unzureichend. Ggf. gibt es ja die dokumente #1, #2, #4 schon. Nun würde das neue Dokument wieder #4 werden. Was falsch ist !!!
				//Ergo: Prüfen, ob es ein Dokument mit dem Schlüssel schon gibt.
				boolean bExists = CarrierVIA.existsCarrierIdInDb(session, dbApplication, sForm, objData, objReturn);
				if(bExists==true){
					//Nun muss ja irgendwo eine Nummer fehlen. Diesen Platz auffüllen. Aufgefüllt werden soll von vorne nach hinten.
					for(int icount = 1; icount <= col.getCount(); icount ++){
						objReturn = sIDBase + "#" + Integer.toString(icount);
						bExists = CarrierVIA.existsCarrierIdInDb(session, dbApplication, sForm, objData, objReturn);
						if (bExists==false) break main; //Heureka
					}	
					*/
				
				//Aufgrund der Funktionalität, dass ggf schon einmal erfasste Datenträger erneut erfasst werden und dabei ihre IDCarrier behalten,
				//muss bei der Vergabe einer neuen Nr. "nach hinten raus" erweitert werden.
				//Alle gefundenen durchgehen. 
				int imaxFound = 0;
				for(int icount=1; icount <= col.getCount(); icount++){
					Document doctemp = col.getNthDocument(icount);
					if(doctemp!=null){
						String sIDCarrier = doctemp.getItemValueString("IDCarrier");
						String sNr = StringZZZ.right(sIDCarrier, "#");
						if(!StringZZZ.isEmpty(sNr)){
							int itemp = Integer.parseInt(sNr);
							if(itemp > imaxFound){
								imaxFound = itemp; 
							}
						}
					}
				}//end for
				
				//die neue Nr muss nun um 1 höher sein
				imaxFound++;
				
				objReturn = sIDBase + "#" + Integer.toString(imaxFound); 
							
			}catch(NotesException ne){
				ExceptionZZZ ez = new ExceptionZZZ(ne.text, iERROR_RUNTIME, "CreatorDocumentCarrierVIA", ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
		}//end main
		return objReturn;
	}
	public static String generateCarrierId(Date objDate, String sCarrierSequenceNr) throws ExceptionZZZ{
		String sReturn = null;
		main:{
			if(objDate==null){
				ExceptionZZZ ez = new ExceptionZZZ("Date-Object", iERROR_PARAMETER_MISSING, "CreatorDocumentCarrierVIA", ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			if(StringZZZ.isEmpty(sCarrierSequenceNr)){
				ExceptionZZZ ez = new ExceptionZZZ("No Carrier SequenceNr", iERROR_PARAMETER_MISSING, "CreatorDocumentCarrierVIA", ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			if(!StringZZZ.isNumeric(sCarrierSequenceNr)){
				ExceptionZZZ ez = new ExceptionZZZ("Carrier SequenceNr is not numeric: '"+sCarrierSequenceNr+"'", iERROR_PARAMETER_VALUE, "CreatorDocumentCarrierVIA", ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			//#####################
			Calendar objCalendar = Calendar.getInstance();
			objCalendar.setTime(objDate);
			
			int iDay = objCalendar.get(Calendar.DAY_OF_MONTH);
			String sDay = Integer.toString(iDay);
			sDay = StringZZZ.right("0" + sDay, 2);
			int iMonth = objCalendar.get(Calendar.MONTH);  //Merke: dabei scheint wohl 0 der erste Monat zu sein. Ergo ist 11 der Dezember				
			String sMonth = Integer.toString(iMonth+1);
			sMonth = StringZZZ.right("0" + sMonth, 2);					
			int iYear = objCalendar.get(Calendar.YEAR);
			String sYear = Integer.toString(iYear);
			sYear = StringZZZ.right("0" + sYear, 2);
			String sIDBase = sYear + sMonth + sDay;
			

			sReturn = sIDBase + "#" + sCarrierSequenceNr;
			
		}//end main:
		return sReturn;
	}
	
	public static boolean existsCarrierIdInDb(Session session, Database db, String sForm, DataStoreZZZ objStore, String sCarrierID) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			try{
				if(db==null){
					ExceptionZZZ ez = new ExceptionZZZ("Database", iERROR_PARAMETER_MISSING, "CreatorDocumentCarrierVIA", ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				if(StringZZZ.isEmpty(sForm)){
					ExceptionZZZ ez = new ExceptionZZZ("Form String", iERROR_PARAMETER_MISSING, "CreatorDocumentCarrierVIA", ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				if(StringZZZ.isEmpty(sCarrierID)){
					ExceptionZZZ ez = new ExceptionZZZ("CarrierID String", iERROR_PARAMETER_MISSING, "CreatorDocumentCarrierVIA", ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				String sFieldnameCarrierID = objStore.getMetadata("Number", DataFieldZZZ.FIELDNAME);
				if(StringZZZ.isEmpty(sFieldnameCarrierID)){
					ExceptionZZZ ez = new ExceptionZZZ("Unable to read the NotesFieldname for the alias 'Number' (e.g ='CarrierID') from the DataStoreZZZ-Object. May you this Field is not configured there or you passed a wrong DataStoreZZZ-Object to this method.", iERROR_PARAMETER_MISSING, "CreatorDocumentCarrierVIA", ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				String sSearch = "@Lowercase(Form)=\""+ sForm.toLowerCase() + "\" & " + sFieldnameCarrierID + "=\"" + sCarrierID + "\"";
				NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "Searching for document in database '"+ db.getTitle() + "' with '" + sSearch + "'");
				
				DocumentCollection col = db.search(sSearch);
				if(col.getCount() >= 1){
					bReturn = true;
				}else{
					bReturn = false;
				}				
			}catch(NotesException ne){
				ExceptionZZZ ez = new ExceptionZZZ(ne.text, iERROR_RUNTIME, "CreatorDocumentCarrierVIA", ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
		}//ENd main
		return bReturn;
	}
	
	
	public static String readCarrierId(Session session, Document docCurrent, DataStoreZZZ objData) throws ExceptionZZZ{
		String sReturn = null;
		main:{
			try{
				if(objData==null){
					ExceptionZZZ ez = new ExceptionZZZ("DataStoreZZZ-Object", iERROR_PARAMETER_MISSING, "CreatorDocumentCarrierVIA", ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				if(docCurrent==null){
					ExceptionZZZ ez = new ExceptionZZZ("Document", iERROR_PARAMETER_MISSING, "CreatorDocumentCarrierVIA", ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				String sFieldnameID = objData.getMetadata("Number", DataFieldZZZ.FIELDNAME);				
				Item item = docCurrent.getFirstItem(sFieldnameID);
				if(item==null) break main;
								 
				sReturn = item.getValueString();
			}catch(NotesException ne){
				ExceptionZZZ ez = new ExceptionZZZ(ne.text, iERROR_RUNTIME, "CarrierVIA", ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
		}
		return sReturn;
	}
	
	public static Date readDateCarrierCreated(Session session, Document docCurrent, DataStoreZZZ objData) throws ExceptionZZZ{
		Date objReturn = null;
		main:{
			try{
				if(objData==null){
					ExceptionZZZ ez = new ExceptionZZZ("DataStoreZZZ-Object", iERROR_PARAMETER_MISSING, "CarrierVIA", ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				if(docCurrent==null){
					ExceptionZZZ ez = new ExceptionZZZ("Document", iERROR_PARAMETER_MISSING, "CarrierVIA", ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
	//			1. Erstelldatum holen
				String sFieldnameCreated = objData.getMetadata("Created", DataFieldZZZ.FIELDNAME);				
				Item item = docCurrent.getFirstItem(sFieldnameCreated);
				if(item==null) break main;
				
				DateTime ndate = item.getDateTimeValue();
				
	//			2. Erstelldatum parsen
				String stemp = ndate.getDateOnly();
				String[] saFormat = new String[1];
				saFormat[0] = objData.getMetadata("Created", DataFieldZZZ.FORMAT);
	
				try {
					objReturn = DateUtils.parseDate(stemp, saFormat);
				} catch (ParseException e) {
					ExceptionZZZ ez = new ExceptionZZZ(e.getMessage() + ", when parsing the date value: '" + stemp + "'", iERROR_RUNTIME, "CarrierVIA", ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
			}catch(NotesException ne){
				ExceptionZZZ ez = new ExceptionZZZ(ne.text, iERROR_RUNTIME, "CarrierVIA", ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
		}
		return objReturn;
	}
	
	
	public static boolean writeCarrierId(Session session, Document doc, DataStoreZZZ objData, String sID) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			try{
				if(objData==null){
					ExceptionZZZ ez = new ExceptionZZZ("DataStoreZZZ-Object", iERROR_PARAMETER_MISSING, "CarrierVIA", ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				if(doc==null){
					ExceptionZZZ ez = new ExceptionZZZ("Document", iERROR_PARAMETER_MISSING, "CarrierVIA", ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				String sFieldname = objData.getMetadata("Number", DataFieldZZZ.FIELDNAME);
				if (StringZZZ.isEmpty(sFieldname)){
					ExceptionZZZ ez  = new ExceptionZZZ("No DataFiedlZZZ.FIELDNAME availabel for the alias: Number in the provided DataStoreZZZ", iERROR_PARAMETER_VALUE, "CarrierVIA", ReflectCodeZZZ.getMethodCurrentName());
					throw ez; 
				}
				if(sID==null){
					doc.removeItem(sFieldname);					
				}else{
					doc.replaceItemValue(sFieldname, sID);							
				}//sID == null
				bReturn = true;
			}catch(NotesException ne){
				ExceptionZZZ ez = new ExceptionZZZ(ne.text, iERROR_RUNTIME, "CarrierVIA", ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
		}//END main
		return bReturn;
	}
	
	public static boolean isValidCarrierId(String sCarrierId){
		boolean bReturn =  false;
		main:{
			try{
				bReturn = CarrierVIA.isValidCarrierIdFeedback(sCarrierId);				
			}catch(ExceptionZZZ ez){
				bReturn = false;
			}
		}
		return bReturn;
	}
	
	public static boolean isValidCarrierIdFeedback(String sCarrierId) throws ExceptionZZZ{
		boolean bReturn =  false;
		main:{
			final String sValidExample = " (A valid carrier id contains 6 numbers (as a data abbreviation) with the maximum values shown in the example, the '#' charakter, followed by one or more other numbers. Example:    991231#9";
			String sString = "the string '" + sCarrierId + "' ";
			
			if(StringZZZ.isEmpty(sCarrierId)){
				ExceptionZZZ ez = new ExceptionZZZ("is an empty string" + sValidExample, iERROR_PARAMETER_VALUE, "CarrierVIA", ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			int itemp = StringZZZ.count(sCarrierId, "#");
			if (itemp <= 0 ){
				ExceptionZZZ ez = new ExceptionZZZ(sString + "does not contain the '#' character."  + sValidExample, iERROR_PARAMETER_VALUE, "CarrierVIA", ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}else if (itemp >= 2){
				ExceptionZZZ ez = new ExceptionZZZ(sString + "does contain the '#' character " + itemp + " times."  + sValidExample, iERROR_PARAMETER_VALUE, "CarrierVIA", ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			String sLeft = StringZZZ.left(sCarrierId, "#");
			if(sLeft.length()<6){
				ExceptionZZZ ez = new ExceptionZZZ(sString + "has not enought characters, left to the '#' character " + sValidExample, iERROR_PARAMETER_VALUE, "CarrierVIA", ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}else if(sLeft.length()>6){
				ExceptionZZZ ez = new ExceptionZZZ(sString + "has more than allowed characters, left to the '#' character " + sValidExample, iERROR_PARAMETER_VALUE, "CarrierVIA", ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			String sYear = StringZZZ.left(sLeft, 2);
			if(! StringZZZ.isNumeric(sYear)){
				ExceptionZZZ ez = new ExceptionZZZ(sString + " is not numeric, left to the '#' character " + sValidExample, iERROR_PARAMETER_VALUE, "CarrierVIA", ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			String sMonth = StringZZZ.mid(sLeft, 2, 2);
			if(! StringZZZ.isNumeric(sMonth)){
				ExceptionZZZ ez = new ExceptionZZZ(sString + " is not numeric, left to the '#' character " + sValidExample, iERROR_PARAMETER_VALUE, "CarrierVIA", ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			String sDay = StringZZZ.mid(sLeft, 4, 2);
			if(! StringZZZ.isNumeric(sDay)){
				ExceptionZZZ ez = new ExceptionZZZ(sString + " is not numeric, left to the '#' character " + sValidExample, iERROR_PARAMETER_VALUE, "CarrierVIA", ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			String sRight = StringZZZ.right(sCarrierId, "#");
			if(! StringZZZ.isNumeric(sRight)){
				ExceptionZZZ ez = new ExceptionZZZ(sString + " is not numeric, right to the '#' character " + sValidExample, iERROR_PARAMETER_VALUE, "CarrierVIA", ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			//Nun den Wertebereich prüfen
			Integer objInteger = new Integer(sYear);
			if(objInteger.intValue() < 0){
				ExceptionZZZ ez = new ExceptionZZZ(sString + " the leftmost 2 characters (Year) are negative " + sValidExample, iERROR_PARAMETER_VALUE, "CarrierVIA", ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			objInteger = new Integer(sMonth);
			if(objInteger.intValue() <= 0){
				ExceptionZZZ ez = new ExceptionZZZ(sString + " the  'Month' characters are negative or 0" + sValidExample, iERROR_PARAMETER_VALUE, "CarrierVIA", ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}else if(objInteger.intValue() >= 13){
				ExceptionZZZ ez = new ExceptionZZZ(sString + " the  'Month' characters are more than 12" + sValidExample, iERROR_PARAMETER_VALUE, "CarrierVIA", ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			objInteger = new Integer(sDay);
			if(objInteger.intValue() <= 0){
				ExceptionZZZ ez = new ExceptionZZZ(sString + " the  'Day' characters are negative or 0" + sValidExample, iERROR_PARAMETER_VALUE, "CarrierVIA", ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			

			  SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd");
			   dateFormat.setLenient(false);
			   try {
			            Date date = dateFormat.parse(sLeft);
			    } catch (ParseException e) {
				    	ExceptionZZZ ez = new ExceptionZZZ(sString + " is not a valid date('" + e.getMessage() + "')" + sValidExample, iERROR_PARAMETER_VALUE, "CarrierVIA", ReflectCodeZZZ.getMethodCurrentName());
						throw ez;
			   }
		}
		bReturn = true;
		return bReturn;
	}
	
	
	
	
	public String getDataStoreAliasUsed(){
		return "Carrier";
	}
	public static String getFormUsed(){
		return "frmCarrierVIA";
	}
	public String getLookupViewCarrierIdUsed(){
		return "viwCarrierIDLookupVIA";
	}

	
	public Document createDocument() throws ExceptionZZZ{
		Document objReturn = null;
		main:{
			//1. Den MapperStore mit einer neuen Carrier ID befüllen
			KernelNotesZZZ objKernelNotes = this.getKernelNotesObject();				
			Session session = objKernelNotes.getSession();
		
			
			Database db = objKernelNotes.getDBApplicationCurrent();
			if (db ==null ) break main;
			
			MapperStoreServerVIA objMapper = (MapperStoreServerVIA) this.getMapperStore();
			DataStoreZZZ objData = objMapper.getDataStoreCarrier();	
			
			//Eine neue CarrierID berechnen und in den DataStore hängen
			//!!! Aber nur, wenn es die nicht schon gibt:
			String sCarrierIDPassed = objData.getValueString("Number", 0);
			if(StringZZZ.isEmpty(sCarrierIDPassed)){		
			
				//Date objDate = CarrierVIA.readDateCarrierCreated(session, objReturn, objData);
				Vector vec = objData.getValueVector("Created");  //Merke: Beim Setzen der Werte in den DataStore wird schon der Korrekte Objekt-Datentyp erzeugt. 
				Date objDate = (Date) vec.firstElement();
				
				//Ggfs. wurde die CarrierSequenzeNr übergeben. Dann ist diese für die Berechnung heranzuiehen
				String sCarrierIDNew = null;
				String sCarrierSequenzeNr = objData.getValueString("Sequence", 0);
				if(!StringZZZ.isEmpty(sCarrierSequenzeNr)){
					sCarrierIDNew = CarrierVIA.generateCarrierId(objDate, sCarrierSequenzeNr);
				}else{
					sCarrierIDNew = CarrierVIA.generateCarrierIdNew(session, db, this.getFormUsed(), objData, objDate);
				} 
				if(StringZZZ.isEmpty(sCarrierIDNew)){
					NotesReportLogZZZ.write(NotesReportLogZZZ.ERROR, "CarrierID: Unable to create a new one.");							
					ExceptionZZZ ez = new ExceptionZZZ("CarrierID: Unable to create a new one.", iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName() );
					throw ez;
				}
				objData.replaceValue("Number", sCarrierIDNew); 	//NEIN: CarrierVIA.writeCarrierID(session, objReturn, objData, sCarrierIDNew);  //ALSO: Hier den Wert noch nicht setzen, sondern nur in den DataStore übergeben	
				NotesReportLogZZZ.write(NotesReportLogZZZ.INFO, "CarrierID: New one ('" + sCarrierIDNew + "' successfully created and passed to datastore-objekt.");
			}else{
				NotesReportLogZZZ.write(NotesReportLogZZZ.INFO, "CarrierID: By the datastore-object Provided one ('" + sCarrierIDPassed+ "' will be used.");
			}
			
			//2. nun kann die Elternklasse damit weiterarbeiten
			objReturn = super.createDocument();
		}//end main:
		return objReturn;
	}
	
	/* (non-Javadoc)
	 * @see use.via.server.ICategorizableZZZ#getCategoryAllUsed()
	 */
	public Vector getCategoryAllUsed() {
			Vector vecReturn = new Vector();
			vecReturn.add("CarrierTitle");
			vecReturn.add("IDCarrier");
			return vecReturn;
	}
	/* (non-Javadoc)
	 * @see use.via.server.DocumentCreatorZZZ#getViewnameEmbeddedAllUsed()
	 */
	public static Vector getViewnameEmbeddedAllUsed() {
		Vector vecReturn = new Vector();
		vecReturn.add("viwAllByCarrierIdSingleCategoryVIA");
		return vecReturn;
	}
}//ENd class
