package use.via.server.module.create;

import java.util.Vector;

import use.via.server.DocumentCreatorZZZ;
import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.Item;
import lotus.domino.NotesException;
import lotus.domino.Session;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.abstractList.VectorZZZ;
import basic.zBasic.util.data.DataFieldZZZ;
import basic.zBasic.util.data.DataStoreZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zNotes.document.DocumentZZZ;
import basic.zNotes.use.log4j.NotesReportLogZZZ;
import custom.zNotes.kernel.KernelNotesZZZ;

public class FileVIA  extends DocumentCreatorZZZ{
		
	public FileVIA(){
		//Wird benötigt, um einfach so per ReflectionAPI ein Dokument zu erzeugen.
	}
	public FileVIA(KernelNotesZZZ objKernelNotes){
		super(objKernelNotes);
	}
	public FileVIA(KernelNotesZZZ objKernelNotes, MapperStoreServerVIA objStoreHTTPMapper){
		super(objKernelNotes);
		this.setMapperStore(objStoreHTTPMapper);
	}
	
	public boolean validateMapperStore() throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			//try{
				KernelNotesZZZ objKernelNotes = this.getKernelNotesObject();			
				Database db = objKernelNotes.getDBApplicationCurrent();
				if (db ==null ) break main;
				
				MapperStoreServerVIA objMapper = (MapperStoreServerVIA) this.getMapperStore();
				DataStoreZZZ objData = objMapper.getDataStoreFile();	
				
				
				//################################################
				//Die Validierungen
								
				//### Der Dateiname ist ein ganz wichtiger Parameter, der übergeben worden sein MUSS
				String sFile = objData.getValueString("Name", 0);
				if(StringZZZ.isEmpty(sFile)){
					ExceptionZZZ ez = new ExceptionZZZ("No 'Name' of the file was passed by http-request-parameter.", iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				String sSize = objData.getValueString("Size", 0);
				if(StringZZZ.isEmpty(sSize)){
					ExceptionZZZ ez = new ExceptionZZZ("No 'Size' of the file was passed by http-request-parameter.", iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				String sDate = objData.getValueString("Date", 0);
				if(StringZZZ.isEmpty(sDate)){
					ExceptionZZZ ez = new ExceptionZZZ("No 'Date' of the file was passed by http-request-parameter.", iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				String sCompression = objData.getValueString("CompressionType", 0);
				if(StringZZZ.isEmpty(sCompression)){
					ExceptionZZZ ez = new ExceptionZZZ("No 'CompressionType' of the file was passed by http-request-parameter.", iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				
				
				
				
								
			/*
			}catch(NotesException ne){
				ExceptionZZZ ez = new ExceptionZZZ(ne.text, iERROR_RUNTIME, this, ReflectionZZZ.getMethodCurrentName());
				throw ez;
			}
			*/
			bReturn = true; // wenn bis hierhin alles erfolgreich validiert wurde, o.k.
		}
		return bReturn;
	}
	
	public Document createDocument(Document docCarrier) throws ExceptionZZZ{
		Document objReturn = null;
		main:{	
			try{
				if(docCarrier==null){
					ExceptionZZZ ez = new ExceptionZZZ("No carrierdocument provided", iERROR_PARAMETER_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				//### Erst einmal überhaupt ein Dokument erzeugen
				objReturn = this.createDocument();
				
				
				KernelNotesZZZ objKernelNotes = this.getKernelNotesObject();			
				Session session = objKernelNotes.getSession();
				
//				### Die CarrierID aus dem CarrierDokument auslesen
				DataStoreZZZ objDataCarrier = this.getMapperStore().getDataStoreByAlias("Carrier");			
				String sIDCarrierFieldname = objDataCarrier.getMetadata("Number", DataFieldZZZ.FIELDNAME);
				if(StringZZZ.isEmpty(sIDCarrierFieldname)){
					ExceptionZZZ ez = new ExceptionZZZ("Fieldname for the carrierID available", iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				String sIDCarrier = CarrierVIA.readCarrierId(session, docCarrier, objDataCarrier);
				if(StringZZZ.isEmpty(sIDCarrier)){
					ExceptionZZZ ez = new ExceptionZZZ("No carrierID available", iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
//		### CarrierID als Schlüssel des Parentdokuments. Merke 1 Movie kann sich auf n Carriern befinden
		Item item = objReturn.appendItemValue(sIDCarrierFieldname, sIDCarrier);
		item.setSummary(true);
			}catch(NotesException ne){
				ExceptionZZZ ez = new ExceptionZZZ(ne.text, iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
		}//End main:
		
		return objReturn;
	}
	
	public boolean updateDocument(Document doc2update, Document docCarrier) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			try{
				if(doc2update==null){
					ExceptionZZZ ez = new ExceptionZZZ("No document to update provided", iERROR_PARAMETER_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				if(docCarrier==null){
					ExceptionZZZ ez = new ExceptionZZZ("No carrier document provided", iERROR_PARAMETER_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				KernelNotesZZZ objKernelNotes = this.getKernelNotesObject();			
				Session session = objKernelNotes.getSession();
				
				
				//##########################################
				//### Die CarrierID wird unabhängig von den Kategoriesierungsfeldern in jedem Dokument gespeichert.
				//### Eine Datei kann auf mehreren Carrier sein. Darum wird beim Update das entsprechende Feld ggf. um den Wert erweitert.
				
				//1 Die CarrierID aus dem CarrierDokument auslesen
				DataStoreZZZ objDataCarrier = this.getMapperStore().getDataStoreByAlias("Carrier");			
				String sIdCarrierFieldname = objDataCarrier.getMetadata("Number", DataFieldZZZ.FIELDNAME);
				if(StringZZZ.isEmpty(sIdCarrierFieldname)){
					ExceptionZZZ ez = new ExceptionZZZ("Fieldname for the carrierID available", iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				String sIdCarrier = CarrierVIA.readCarrierId(session, docCarrier, objDataCarrier);
				if(StringZZZ.isEmpty(sIdCarrier)){
					ExceptionZZZ ez = new ExceptionZZZ("No carrierID available", iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				
				//2. den alten CarrierID Wert des upzudatenden Dokuments lesen
				Vector vecIdOld = doc2update.getItemValue(sIdCarrierFieldname);
				vecIdOld.add(sIdCarrier);
				Vector vecIdNew = VectorZZZ.unique(vecIdOld);  //Falls doch wieder der gleich Carrier gemeint ist.
				
				//3. Item ersetzen
				DocumentZZZ.itemInstanceAllRemove(session, doc2update, sIdCarrierFieldname);
				Item item = doc2update.replaceItemValue(sIdCarrierFieldname, vecIdNew);
				item.setSummary(true);
				
				super.updateDocument(session, doc2update);
			}catch(NotesException ne){
				ExceptionZZZ ez = new ExceptionZZZ(ne.text, iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
		}
		return bReturn;
	}

	
	public String getDataStoreAliasUsed(){
		return "File";
	}
	public static String getFormUsed(){
		return "frmFileVIA";
	}

	public Document searchDocumentExisting() throws ExceptionZZZ {
		Document docReturn = null;
		main:{
			try{
				KernelNotesZZZ objKernelNotes = this.getKernelNotesObject();			
				Database db = objKernelNotes.getDBApplicationCurrent();
				if (db ==null ) break main;
				
				MapperStoreServerVIA objMapper = (MapperStoreServerVIA) this.getMapperStore();
				DataStoreZZZ objData = objMapper.getDataStoreFile();	
				
				
				//################################################
				//Das Ermitteln der Felder/Felwerte, die als Key herangezogen werden
				String sForm = this.getFormUsed();
				
				//### Falls die CarrierID übergeben worden ist:
				String sFileIDPassed = objData.getValueString("Number", 0);
				if(! StringZZZ.isEmpty(sFileIDPassed)){					
					NotesReportLogZZZ.write(NotesReportLogZZZ.INFO, "A FileID '" + sFileIDPassed + "' was passed by http-request-parameter AND this exists in the database: '" + db.getTitle() + "'");
					String sFieldnameFileID = objData.getMetadata("Number", DataFieldZZZ.FIELDNAME);
					if(StringZZZ.isEmpty(sFieldnameFileID)){
						ExceptionZZZ ez = new ExceptionZZZ("Unable to read the NotesFieldname for the alias 'Number' (e.g ='FileID') from the DataStoreZZZ-Object. May you this Field is not configured there or you passed a wrong DataStoreZZZ-Object to this method.", iERROR_PARAMETER_MISSING, this,ReflectCodeZZZ.getMethodCurrentName());
						throw ez;
					}
					
					String sSearch = "@Lowercase(Form)=\""+ sForm.toLowerCase() + "\" & " + sFieldnameFileID + "=\"" + sFileIDPassed + "\"";
					NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "Searching for document in database '"+ db.getTitle() + "' with '" + sSearch + "'");
					
					DocumentCollection col = db.search(sSearch);
					if(col.getCount() >= 1){
						docReturn = col.getFirstDocument();
						NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "A File document was found");	
						break main;
					}
				}else{
					NotesReportLogZZZ.write(NotesReportLogZZZ.INFO, "NO FileID '" + sFileIDPassed + "' was passed by http-request-parameter. Search for document by more complex search statement: '" + db.getTitle() + "'");
				}

				//###############################################################################
				//### Nun nach einem anderen, zusammengesetzten Schlüssel suchen
			
				
				//### Name
				String sName = objData.getValueString("Name", 0);
				if(StringZZZ.isEmpty(sName)){
					ExceptionZZZ ez = new ExceptionZZZ("No 'name' of the file was passed by http-request-parameter.", iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				//### Size
				String sSize = objData.getValueString("Size", 0);
				if(StringZZZ.isEmpty(sSize)){
					ExceptionZZZ ez = new ExceptionZZZ("No 'size' of the file was passed by http-request-parameter.", iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				//!!! Bei Size ist in Notes der Datentyp "Number"  !!!
				
				
				//### Das "Letzte Änderungsdatum" der Datei
				String sDate = objData.getValueString("Date", 0);
				if(StringZZZ.isEmpty(sDate)){
					ExceptionZZZ ez = new ExceptionZZZ("No 'date' of the file was passed by http-request-parameter.", iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				//### Der Typ
				String sCompressionType = objData.getValueString("CompressionType", 0);
				if(StringZZZ.isEmpty(sCompressionType)){
					ExceptionZZZ ez = new ExceptionZZZ("No 'Compression Type' of the file was passed by http-request-parameter.", iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				//++++++++ die Suche
				String sNameField = objData.getMetadata("Name", DataFieldZZZ.FIELDNAME);
				String sSizeField = objData.getMetadata("Size", DataFieldZZZ.FIELDNAME);
				String sDateField = objData.getMetadata("Date", DataFieldZZZ.FIELDNAME);				
				String sCompressionTypeField = objData.getMetadata("CompressionType", DataFieldZZZ.FIELDNAME);
				String sSearch = "@Lowercase(Form)=\""+ sForm.toLowerCase() + "\" & " + sNameField + "=\"" + sName + "\" & " + sCompressionTypeField + "=\"" + sCompressionType + "\" & @ToTime(" + sDateField + ")=@ToTime(\"" + sDate + "\")"; //kann ich irgendwie nicht vergleichen   ....& @ToNumber(@Text(" + sSizeField + "))=@ToNumber(" + sSize + ")";//
				NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "Searching for document in database '"+ db.getTitle() + "' with '" + sSearch + "'");
				
				DocumentCollection col = db.search(sSearch);
				if(col.getCount() >= 1){
					NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "A file document was found");		
					docReturn = col.getFirstDocument();						
					break main;
				}else{
					NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "NO file document was found");								
				}				

			}catch(NotesException ne){
				ExceptionZZZ ez = new ExceptionZZZ(ne.text, iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}		
		}
		return docReturn;
	}
	 
	
	/* (non-Javadoc)
	 * @see use.via.server.DocumentCreatorZZZ#getCategoryAllUsed()
	 */
	public Vector getCategoryAllUsed() {
		Vector vecReturn = new Vector();
		vecReturn.add("FileName");
		return vecReturn;
	}
	
	/* (non-Javadoc)
	 * @see use.via.server.DocumentCreatorZZZ#getViewnameEmbeddedAllUsed()
	 */
	public static Vector getViewnameEmbeddedAllUsed() {
		//In diesem Dokument sind keine embedded Views enthalten
			Vector vecReturn = new Vector();
			return vecReturn;
	}
}//END class
