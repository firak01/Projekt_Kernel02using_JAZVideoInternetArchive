/**
 * 
 */
package use.via.server;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.collections.map.MultiValueMap;

import use.via.MapperStoreHttpZZZ;
import use.via.server.module.create.MapperStoreServerVIA;

import lotus.domino.Database;
import lotus.domino.DateTime;
import lotus.domino.Document;
import lotus.domino.Item;
import lotus.domino.NotesException;
import lotus.domino.RichTextItem;
import lotus.domino.Session;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.abstractList.VectorZZZ;
import basic.zBasic.util.data.DataFieldZZZ;
import basic.zBasic.util.data.DataStoreZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zNotes.kernel.KernelNotesUseObjectZZZ;
import basic.zNotes.use.util.KernelNumberGeneratorZZZ;
import custom.zNotes.kernel.KernelNotesZZZ;

/**TODO: Diese Klasse sollte in das Notes-Package verschoben werden, da sie allgemein die Erstellung von Notesdocumenten behandelt
 * @author 0823
 *
 */
public abstract class DocumentCreatorZZZ extends KernelNotesUseObjectZZZ implements ICategorizableZZZ{
	private Document document = null;          //Das Notesdokument, das gerade erzeugt werden soll.
	private MapperStoreServerVIA objStoreHTTPMapper = null;  //Der Mapper für alle Paremter, die per HTTP übermittelt wurden.
	private DataStoreZZZ  objStoreCommon = null; //der Datastore für $All, ergo: Der Datastore, der für alle Dokumente unabhängig der Maske/Form gelten soll.
	private NotesDataStoreZZZ objStore = null;  //Der spezielle Store, für eine NotesDokumentart.
	
	
	
	public DocumentCreatorZZZ(){
		//Wird benötigt, um einfach so per ReflectionAPI mal ein Objekt zu erzeugen.
	}
	public DocumentCreatorZZZ(KernelNotesZZZ objKernelNotes){
		super(objKernelNotes);
	}
	
	/** Holt sich aus dem übergebenen DataStoreZZZ - Objekt alle Feldnamen und geht diese in einer Schleife durch. Es wird dann gemäß dem Definierten DataFieldZZZ.TARGET_VALUE_HANDLING eine Funktion zum Anhängen, Ersetzen, etc. des ursprünglichen Feldwertes aufgerufen.
	* @param session
	* @param objDocument
	* @param objData
	* @throws ExceptionZZZ
	* @throws NotesException
	* 
	* lindhauer; 27.01.2008 11:57:46
	 */
	public static void processFieldAllByStore(Session session, Document objDocument, DataStoreZZZ objData) throws ExceptionZZZ, NotesException{
		//Neu: 20080127
		main:{
		ArrayList alsField = objData.getValueKeyStringAlll();
		if(alsField.isEmpty()) break main;
		
			//Felder ins Dokument setzen
			for(int icount = 0; icount <= alsField.size()-1; icount++){
				//Der Alias
				String sAlias = (String) alsField.get(icount);
				String sValueHandling = objData.getMetadata(sAlias, DataFieldZZZ.TARGETVALUEHANDLING);
				if(StringZZZ.isEmpty(sValueHandling)){
					sValueHandling = "r";    //removen des alten Wertes ist also default
				}
			
				//Merke: Mit einer Switch - Anweisung kann man keine Strings testen
				if(sValueHandling.equalsIgnoreCase(DataFieldZZZ.sTARGET_VALUE_REPLACE)){
					DocumentCreatorZZZ.replaceFieldByStore(session, objDocument, objData, sAlias);
				}else if(sValueHandling.equalsIgnoreCase(DataFieldZZZ.sTARGET_VALUE_REPLACE_UNIQUE)){
					DocumentCreatorZZZ.replaceFieldByStore(session, objDocument, objData, sAlias, true);
					
				}else if(sValueHandling.equalsIgnoreCase(DataFieldZZZ.sTARGET_VALUE_KEEP)){
					DocumentCreatorZZZ.keepFieldByStore(session, objDocument, objData, sAlias);
				}else if(sValueHandling.equalsIgnoreCase(DataFieldZZZ.sTARGET_VALUE_KEEP_UNIQUE)){					
					DocumentCreatorZZZ.keepFieldByStore(session, objDocument, objData, sAlias, true);
					
				}else if(sValueHandling.equalsIgnoreCase(DataFieldZZZ.sTARGET_VALUE_APPEND)){
					DocumentCreatorZZZ.appendFieldByStore(session, objDocument, objData, sAlias);
				} else if(sValueHandling.equalsIgnoreCase(DataFieldZZZ.sTARGET_VALUE_APPEND_UNIQUE)){
					DocumentCreatorZZZ.appendFieldByStore(session, objDocument, objData, sAlias, true);
					
				}else if(sValueHandling.equalsIgnoreCase(DataFieldZZZ.sTARGET_VALUE_PREPEND)){
					DocumentCreatorZZZ.prependFieldByStore(session, objDocument, objData, sAlias);					
				}else if(sValueHandling.equalsIgnoreCase(DataFieldZZZ.sTARGET_VALUE_PREPEND_UNIQUE)){
					DocumentCreatorZZZ.prependFieldByStore(session, objDocument, objData, sAlias, true);
					
				}else{
					ExceptionZZZ ez = new ExceptionZZZ("Value handling of the type '" + sValueHandling + "' is not available. Error using alias '"+ sAlias + "'", iERROR_CONFIGURATION_VALUE, "DocumentCreatorZZZ", ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
			}//end for
		}//end main:
	
	}
	public static void keepFieldByStore(Session session, Document objDocument, DataStoreZZZ objData, String sAlias) throws ExceptionZZZ, NotesException{
//		Neu: 20080127
		main:{
			if(StringZZZ.isEmpty(sAlias)){
				ExceptionZZZ ez = new ExceptionZZZ("Alias-String", iERROR_PARAMETER_MISSING, "DocumentCreatorZZZ", ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			if(objData == null){
				ExceptionZZZ ez = new ExceptionZZZ("DataStoreZZZ - Object", iERROR_PARAMETER_MISSING, "DocumentCreatorZZZ", ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			String sFieldname = objData.getMetadata(sAlias, DataFieldZZZ.FIELDNAME);
			if(StringZZZ.isEmpty(sFieldname)) break main;  //ohne Feldnamen ist auch nichts durchzuführen
			
			if(objDocument == null){
				ExceptionZZZ ez = new ExceptionZZZ("Notesdocument", iERROR_PARAMETER_MISSING, "DocumentCreatorZZZ", ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
		
			
			//++++++++++++++++++++++++++++++++++++++++++++++++++++++
			String sDatatype = objData.getMetadata(sAlias, DataFieldZZZ.DATATYPE);
			if(StringZZZ.isEmpty(sDatatype)) sDatatype = DataFieldZZZ.sSTRING;   //d.h. String ist default
			
			//++++++++++++++++++++++++++++++++++++++++++++++++++++++
			
			//1. Prüfen, ob das Item vorhanden ist.
			if(objDocument.hasItem(sFieldname)){
//				2. Prüfen, ob der Wert des Items leer ist
				String stemp = objDocument.getItemValueString(sFieldname);
				if(! StringZZZ.isEmpty(stemp)) break main; //Falls ein Wert drin ist, darf nix ersetzt werden
				
			}
			
			//3. Falls der bisherige Wert leer ist, oder das Item ist noch nicht vorhanden, die Replace-Methode aufrufen
			DocumentCreatorZZZ.replaceFieldByStore(session, objDocument, objData, sAlias);
		}//end main:
	}
	
	public static void keepFieldByStore(Session session, Document objDocument, DataStoreZZZ objData, String sAlias, boolean bUnique) throws ExceptionZZZ, NotesException{
		//Neu: 20080513: Fall bUnique = true ===> am Schluss den Feldwert noch Unique machen
		main:{
		

		//1. Aufrufen der grundlegenden Funktion
		DocumentCreatorZZZ.keepFieldByStore(session, objDocument, objData, sAlias);
				
		//2. Wert holen, Vector unique machen und anschliessend damit den Wert ersetzen (wenn die Anzahl der Vektoreinträge sich geändert hat.)
		String sDatatype = objData.getMetadata(sAlias, DataFieldZZZ.DATATYPE);
		if(sDatatype.equals(DataFieldZZZ.sNOTESRICHTEXT)) break main; //Richtext wird also nicht unique gemacht		
		if(StringZZZ.isEmpty(sDatatype)) sDatatype = DataFieldZZZ.sSTRING;   //d.h. String ist default
				
		String sFieldname = objData.getMetadata(sAlias, DataFieldZZZ.FIELDNAME);
		if(StringZZZ.isEmpty(sFieldname)) break main;  //ohne Feldnamen ist auch nichts durchzuführen
		
		if(objDocument.hasItem(sFieldname)){
			Vector vecValue = objDocument.getItemValue(sFieldname);
			if(vecValue.isEmpty()) break main;
			
			Vector vecReturn = VectorZZZ.unique(vecValue);
			if(vecReturn.size()<vecValue.size()){
				objDocument.replaceItemValue(sFieldname, vecReturn);				 //Annahme: Item-Properties bleiben gleich	
			}
		}
	}//end main:
	}
	
	
	public static void replaceFieldByStore(Session session, Document objDocument, DataStoreZZZ objData, String sAlias) throws ExceptionZZZ, NotesException{
//		Neu: 20080127
		main:{
			if(session == null){
				ExceptionZZZ ez = new ExceptionZZZ("Session", iERROR_PARAMETER_MISSING, "DocumentCreatorZZZ", ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			if(StringZZZ.isEmpty(sAlias)){
				ExceptionZZZ ez = new ExceptionZZZ("Alias-String", iERROR_PARAMETER_MISSING, "DocumentCreatorZZZ", ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			if(objData == null){
				ExceptionZZZ ez = new ExceptionZZZ("DataStoreZZZ - Object", iERROR_PARAMETER_MISSING, "DocumentCreatorZZZ", ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			String sFieldname = objData.getMetadata(sAlias, DataFieldZZZ.FIELDNAME);
			if(StringZZZ.isEmpty(sFieldname)) break main;  //ohne Feldnamen ist auch nichts durchzuführen
			
			if(objDocument == null){
				ExceptionZZZ ez = new ExceptionZZZ("Notesdocument", iERROR_PARAMETER_MISSING, "DocumentCreatorZZZ", ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
		
			
			//++++++++++++++++++++++++++++++++++++++++++++++++++++++
			String sDatatype = objData.getMetadata(sAlias, DataFieldZZZ.DATATYPE);
			if(StringZZZ.isEmpty(sDatatype)) sDatatype = DataFieldZZZ.sSTRING;   //d.h. String ist default
			
			//++++++++++++++++++++++++++++++++++++++++++++++++++++++
			
			//Wert holen
			Vector objVectorValue = objData.getValueVector(sAlias);
			
			//Diesen Wert nun in Abhängigkeit des Datentyps in Notesitems packen
			if(sDatatype.equals(DataFieldZZZ.sSTRING) | sDatatype.equals(DataFieldZZZ.sINTEGER) | sDatatype.equals(DataFieldZZZ.sDOUBLE)){
				Item objItem = objDocument.replaceItemValue(sFieldname, objVectorValue);		
				objItem.setSummary(true);
			}else if(sDatatype.equals(DataFieldZZZ.sNOTESRICHTEXT)){
				RichTextItem objRTItem = null;
				if(objDocument.hasItem(sFieldname)){
					objDocument.removeItem(sFieldname);   //TODO: ggf. alle Instancen des Itmes Löschen (s. LotusScript Z-Kernel)
				}
				
				objRTItem = objDocument.createRichTextItem(sFieldname);
				
				for(int icountline=0; icountline<= objVectorValue.size()-1; icountline++){
					String stemp = (String) objVectorValue.get(icountline);
					objRTItem.appendText(stemp);
					objRTItem.addNewLine();
				}
				
			}else if(sDatatype.equals(DataFieldZZZ.sNOTESAUTHOR)){
				Item objItem = objDocument.replaceItemValue(sFieldname, objVectorValue);		
				objItem.setAuthors(true);
				objItem.setReaders(false);
				objItem.setNames(false);
				objItem.setSummary(true);
			}else if(sDatatype.equals(DataFieldZZZ.sNOTESREADER)){
				Item objItem = objDocument.replaceItemValue(sFieldname, objVectorValue);	
				objItem.setAuthors(false);
				objItem.setReaders(true);
				objItem.setNames(false);
				objItem.setSummary(true);
			}else if(sDatatype.equals(DataFieldZZZ.sNOTESNAME)){
				Item objItem = objDocument.replaceItemValue(sFieldname, objVectorValue);	
				objItem.setAuthors(false);
				objItem.setReaders(false);
				objItem.setNames(true);
				objItem.setSummary(true);
			}else if(sDatatype.equals(DataFieldZZZ.sDATE)){
				boolean bReplaced = false;
				Vector vNotesDate = new Vector(objVectorValue.size());
				for(int icountline=0; icountline <= objVectorValue.size()-1; icountline++){
					Date objDate = (Date) objVectorValue.get(icountline);
					if(objDate != null){
						DateTime objNotesDate = session.createDateTime(objDate);
						vNotesDate.add(objNotesDate);
						bReplaced = true;
					}
				}
				if (bReplaced == true) {
					Item objItem = objDocument.replaceItemValue(sFieldname, vNotesDate);
					objItem.setSummary(true);
				}
				
			}else{
				ExceptionZZZ ez = new ExceptionZZZ("Values of datatype '" + sDatatype + "', can´t be assigned yet. This has to be developed.", iERROR_ZFRAME_METHOD, "DocumentCreatorZZZ", ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}			
			
			//Nun ggf. eine Konfigurierte Nachbereitungsmethode aufrufen
			//TODO: Für Arbeiten an dem Dokument muß immer das Session und Dokument Objekt übergeben werden
			DocumentCreatorZZZ.invokeMethodConfiguredByStore(objData, sAlias);
			
		}//end main:
	}
	
	public static void replaceFieldByStore(Session session, Document objDocument, DataStoreZZZ objData, String sAlias, boolean bUnique) throws ExceptionZZZ, NotesException{
//Neu: 20080513: Fall bUnique = true ===> am Schluss den Feldwert noch Unique machen
		
		main:{
		//1. Aufrufen der grundlegenden Funktion
		DocumentCreatorZZZ.replaceFieldByStore(session, objDocument, objData, sAlias);
		
		//2. Wert holen, Vector unique machen und anschliessend damit den Wert ersetzen (wenn die Anzahl der Vektoreinträge sich geändert hat.)
		String sDatatype = objData.getMetadata(sAlias, DataFieldZZZ.DATATYPE);
		if(sDatatype.equals(DataFieldZZZ.sNOTESRICHTEXT)) break main; //Richtext wird also nicht unique gemacht		
		if(StringZZZ.isEmpty(sDatatype)) sDatatype = DataFieldZZZ.sSTRING;   //d.h. String ist default
				
		String sFieldname = objData.getMetadata(sAlias, DataFieldZZZ.FIELDNAME);
		if(StringZZZ.isEmpty(sFieldname)) break main;  //ohne Feldnamen ist auch nichts durchzuführen
		
		if(objDocument.hasItem(sFieldname)){
			Vector vecValue = objDocument.getItemValue(sFieldname);
			if(vecValue.isEmpty()) break main;
			
			Vector vecReturn = VectorZZZ.unique(vecValue);
			if(vecReturn.size()<vecValue.size()){
				objDocument.replaceItemValue(sFieldname, vecReturn);			 //Annahme: Item-Properties bleiben gleich	
			}
		}
	}//END main:		
	}
		
	
	public static void appendFieldByStore(Session session, Document objDocument, DataStoreZZZ objData, String sAlias) throws ExceptionZZZ, NotesException{
		//Neu: 20080127
		main:{
			if(StringZZZ.isEmpty(sAlias)){
				ExceptionZZZ ez = new ExceptionZZZ("Alias-String", iERROR_PARAMETER_MISSING, "DocumentCreatorZZZ", ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			if(objData == null){
				ExceptionZZZ ez = new ExceptionZZZ("DataStoreZZZ - Object", iERROR_PARAMETER_MISSING, "DocumentCreatorZZZ", ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			String sFieldname = objData.getMetadata(sAlias, DataFieldZZZ.FIELDNAME);
			if(StringZZZ.isEmpty(sFieldname)) break main;  //ohne Feldnamen ist auch nichts durchzuführen
			
			if(objDocument == null){
				ExceptionZZZ ez = new ExceptionZZZ("Notesdocument", iERROR_PARAMETER_MISSING, "DocumentCreatorZZZ", ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
		
			
			//++++++++++++++++++++++++++++++++++++++++++++++++++++++
			String sDatatype = objData.getMetadata(sAlias, DataFieldZZZ.DATATYPE);
			if(StringZZZ.isEmpty(sDatatype)) sDatatype = DataFieldZZZ.sSTRING;   //d.h. String ist default
			
			//++++++++++++++++++++++++++++++++++++++++++++++++++++++
			
			//Wert holen
			Vector objVectorValue = objData.getValueVector(sAlias);
			
			//Diesen Wert nun in Abhängigkeit des Datentyps in Notesitems packen
			if(sDatatype.equals(DataFieldZZZ.sSTRING) | sDatatype.equals(DataFieldZZZ.sINTEGER) | sDatatype.equals(DataFieldZZZ.sDOUBLE)){
				//!!! Item objItem = objDocument.appendItemValue(sFieldname, objVectorValue); //Hiermit wird ein gänzlich neues Item angehängt. Wir wollen aber eine Erweiterung des bestehenden Items. Ergo muss der Vector erweitert werden
				Vector objVectorValueOld = objDocument.getItemValue(sFieldname);
				Vector objVectorValueNew = VectorZZZ.append(objVectorValueOld, objVectorValue);
				Item objItem = objDocument.replaceItemValue(sFieldname, objVectorValueNew);		
				objItem.setSummary(true);
			}else if(sDatatype.equals(DataFieldZZZ.sNOTESRICHTEXT)){
				RichTextItem objRTItem = null;
				if(objDocument.hasItem(sFieldname)){
					objRTItem = (RichTextItem) objDocument.getFirstItem(sFieldname);
					
					//Neu 20080127 Nun abfragen, ob es schon Inhalt darin gibt. Falls ja, dann einen Zeilenumbruch einfügen, damit die neuen Werte in einer neuen Zeile anfangen
					String stemp = objRTItem.getText();
					if(! StringZZZ.isEmpty(stemp)){
						objRTItem.addNewLine();
					}
					
				}else{
					objRTItem = objDocument.createRichTextItem(sFieldname);
				}
				for(int icountline=0; icountline<= objVectorValue.size()-1; icountline++){
					String stemp = (String) objVectorValue.get(icountline);
					objRTItem.appendText(stemp);
					objRTItem.addNewLine();
				}
				
			}else if(sDatatype.equals(DataFieldZZZ.sNOTESAUTHOR)){
				Vector objVectorValueOld = objDocument.getItemValue(sFieldname);
				Vector objVectorValueNew = VectorZZZ.append(objVectorValueOld, objVectorValue);
				Item objItem = objDocument.replaceItemValue(sFieldname, objVectorValueNew);		
				objItem.setAuthors(true);
				objItem.setReaders(false);
				objItem.setNames(false);
				objItem.setSummary(true);
			}else if(sDatatype.equals(DataFieldZZZ.sNOTESREADER)){
				Vector objVectorValueOld = objDocument.getItemValue(sFieldname);
				Vector objVectorValueNew = VectorZZZ.append(objVectorValueOld, objVectorValue);
				Item objItem = objDocument.replaceItemValue(sFieldname, objVectorValueNew);	
				objItem.setAuthors(false);
				objItem.setReaders(true);
				objItem.setNames(false);
				objItem.setSummary(true);
			}else if(sDatatype.equals(DataFieldZZZ.sNOTESNAME)){
				Vector objVectorValueOld = objDocument.getItemValue(sFieldname);
				Vector objVectorValueNew = VectorZZZ.append(objVectorValueOld, objVectorValue);
				Item objItem = objDocument.replaceItemValue(sFieldname, objVectorValueNew);	
				objItem.setAuthors(false);
				objItem.setReaders(false);
				objItem.setNames(true);
				objItem.setSummary(true);
			}else if(sDatatype.equals(DataFieldZZZ.sDATE)){
				Vector vNotesDate = new Vector(objVectorValue.size());
				for(int icountline=0; icountline <= objVectorValue.size()-1; icountline++){
					Date objDate = (Date) objVectorValue.get(icountline);
					DateTime objNotesDate = session.createDateTime(objDate);
					vNotesDate.add(objNotesDate);
				}
				
				
				Vector objVectorValueOld = objDocument.getItemValue(sFieldname);
				Vector objVectorValueNew = VectorZZZ.append(objVectorValueOld, vNotesDate);
				Item objItem = objDocument.replaceItemValue(sFieldname, objVectorValueNew);	
				
				//Item objItem = objDocument.appendItemValue(sFieldname, vNotesDate);
				objItem.setSummary(true);
			}else{
				ExceptionZZZ ez = new ExceptionZZZ("Values of datatype '" + sDatatype + "', can´t be assigned yet. This has to be developed.", iERROR_ZFRAME_METHOD, "DocumentCreatorZZZ", ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}			
			
			//Nun ggf. eine Konfigurierte Nachbereitungsmethode aufrufen
			//TODO: Für Arbeiten an dem Dokument muß immer das Session und Dokument Objekt übergeben werden
			DocumentCreatorZZZ.invokeMethodConfiguredByStore(objData, sAlias);
		
		}//end main:
	}
	
	public static void appendFieldByStore(Session session, Document objDocument, DataStoreZZZ objData, String sAlias, boolean bUnique) throws ExceptionZZZ, NotesException{
//Neu: 20080513: Fall bUnique = true ===> am Schluss den Feldwert noch Unique machen
		main:{
			//1. Aufrufen der grundlegenden Funktion
			DocumentCreatorZZZ.appendFieldByStore(session, objDocument, objData, sAlias);
			
			//2. Wert holen, Vector unique machen und anschliessend damit den Wert ersetzen (wenn die Anzahl der Vektoreinträge sich geändert hat.)
			String sDatatype = objData.getMetadata(sAlias, DataFieldZZZ.DATATYPE);
			if(sDatatype.equals(DataFieldZZZ.sNOTESRICHTEXT)) break main; //Richtext wird also nicht unique gemacht		
			if(StringZZZ.isEmpty(sDatatype)) sDatatype = DataFieldZZZ.sSTRING;   //d.h. String ist default
					
			String sFieldname = objData.getMetadata(sAlias, DataFieldZZZ.FIELDNAME);
			if(StringZZZ.isEmpty(sFieldname)) break main;  //ohne Feldnamen ist auch nichts durchzuführen
			
			if(objDocument.hasItem(sFieldname)){
				Vector vecValue = objDocument.getItemValue(sFieldname);
				if(vecValue.isEmpty()) break main;
				
				Vector vecReturn = VectorZZZ.unique(vecValue);
				if(vecReturn.size()<vecValue.size()){
					objDocument.replaceItemValue(sFieldname, vecReturn);			 //Annahme: Item-Properties bleiben gleich	
				}
			}
		}//END main:		
				
	}
	
	public static void prependFieldByStore(Session session, Document objDocument, DataStoreZZZ objData, String sAlias) throws ExceptionZZZ, NotesException{
		//Neu: 20080127
		main:{
			if(StringZZZ.isEmpty(sAlias)){
				ExceptionZZZ ez = new ExceptionZZZ("Alias-String", iERROR_PARAMETER_MISSING, "DocumentCreatorZZZ", ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			if(objData == null){
				ExceptionZZZ ez = new ExceptionZZZ("DataStoreZZZ - Object", iERROR_PARAMETER_MISSING, "DocumentCreatorZZZ", ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			String sFieldname = objData.getMetadata(sAlias, DataFieldZZZ.FIELDNAME);
			if(StringZZZ.isEmpty(sFieldname)) break main;  //ohne Feldnamen ist auch nichts durchzuführen
			
			if(objDocument == null){
				ExceptionZZZ ez = new ExceptionZZZ("Notesdocument", iERROR_PARAMETER_MISSING, "DocumentCreatorZZZ", ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
		
			
			//++++++++++++++++++++++++++++++++++++++++++++++++++++++
			String sDatatype = objData.getMetadata(sAlias, DataFieldZZZ.DATATYPE);
			if(StringZZZ.isEmpty(sDatatype)) sDatatype = DataFieldZZZ.sSTRING;   //d.h. String ist default
			
			//++++++++++++++++++++++++++++++++++++++++++++++++++++++
			
			//Wert holen
			Vector objVectorValue = objData.getValueVector(sAlias);
			
			//Diesen Wert nun in Abhängigkeit des Datentyps in Notesitems packen
			if(sDatatype.equals(DataFieldZZZ.sSTRING) | sDatatype.equals(DataFieldZZZ.sINTEGER) | sDatatype.equals(DataFieldZZZ.sDOUBLE)){
				//!!! Item objItem = objDocument.appendItemValue(sFieldname, objVectorValue); //Hiermit wird ein gänzlich neues Item angehängt. Wir wollen aber eine Erweiterung des bestehenden Items. Ergo muss der Vector erweitert werden
				Vector objVectorValueOld = objDocument.getItemValue(sFieldname);
				Vector objVectorValueNew = VectorZZZ.append(objVectorValue, objVectorValueOld);  //!!! In der Reihenfolge der Parameter unterscheidet sich die Methode vom "append..."
				Item objItem = objDocument.replaceItemValue(sFieldname, objVectorValueNew);		
				objItem.setSummary(true);
			}else if(sDatatype.equals(DataFieldZZZ.sNOTESRICHTEXT)){
				RichTextItem objRTItem = null;
				if(objDocument.hasItem(sFieldname)){
					objRTItem = (RichTextItem) objDocument.getFirstItem(sFieldname);
					
					//Neu 20080127 Nun abfragen, ob es schon Inhalt darin gibt. Falls ja, dann einen Zeilenumbruch einfügen, damit die neuen Werte in einer neuen Zeile anfangen
					//                        Auch muß nur in diesem Fall die RTF-Fusion durchgeführt werden
					String stemp = objRTItem.getText();
					if(! StringZZZ.isEmpty(stemp)){
						//Fall 1a) altes RTItem an neues RTItem anhängen
						RichTextItem objRTItemTemp = objDocument.createRichTextItem(sFieldname + "_");
						for(int icountline=0; icountline<= objVectorValue.size()-1; icountline++){
							stemp = (String) objVectorValue.get(icountline);
							objRTItemTemp.appendText(stemp);
							objRTItemTemp.addNewLine();
						}
						
						//+++ Die neue Zeile nicht vergessen
						objRTItemTemp.addNewLine();
						
						//+++ Altes RTItem anhängen
						objRTItemTemp.appendRTItem(objRTItem);
						objRTItem.recycle();
						objDocument.removeItem(sFieldname);
						
						objRTItem = objDocument.createRichTextItem(sFieldname);
						objRTItem.appendRTItem(objRTItemTemp);
						
						objRTItemTemp.recycle();
						
						
						
					}else{
						//Fall 1b) Leeres RTItem füllen
						for(int icountline=0; icountline<= objVectorValue.size()-1; icountline++){
							stemp = (String) objVectorValue.get(icountline);
							objRTItem.appendText(stemp);
							objRTItem.addNewLine();
						}
					}					
				}else{
					//Fall 2: Neues RTItem
					objRTItem = objDocument.createRichTextItem(sFieldname);
					for(int icountline=0; icountline<= objVectorValue.size()-1; icountline++){
						String stemp = (String) objVectorValue.get(icountline);
						objRTItem.appendText(stemp);
						objRTItem.addNewLine();
					}
				}
				
				
			}else if(sDatatype.equals(DataFieldZZZ.sNOTESAUTHOR)){
				Vector objVectorValueOld = objDocument.getItemValue(sFieldname);
				Vector objVectorValueNew = VectorZZZ.append(objVectorValue, objVectorValueOld); //!!! In der Reihenfolge der Parameter unterscheidet sich die Methode vom "append..."
				Item objItem = objDocument.replaceItemValue(sFieldname, objVectorValueNew);		
				objItem.setAuthors(true);
				objItem.setReaders(false);
				objItem.setNames(false);
				objItem.setSummary(true);
			}else if(sDatatype.equals(DataFieldZZZ.sNOTESREADER)){
				Vector objVectorValueOld = objDocument.getItemValue(sFieldname);
				Vector objVectorValueNew = VectorZZZ.append(objVectorValue, objVectorValueOld); //!!! In der Reihenfolge der Parameter unterscheidet sich die Methode vom "append..."
				Item objItem = objDocument.replaceItemValue(sFieldname, objVectorValueNew);	
				objItem.setAuthors(false);
				objItem.setReaders(true);
				objItem.setNames(false);
				objItem.setSummary(true);
			}else if(sDatatype.equals(DataFieldZZZ.sNOTESNAME)){
				Vector objVectorValueOld = objDocument.getItemValue(sFieldname);
				Vector objVectorValueNew = VectorZZZ.append(objVectorValue, objVectorValueOld);//!!! In der Reihenfolge der Parameter unterscheidet sich die Methode vom "append..."
				Item objItem = objDocument.replaceItemValue(sFieldname, objVectorValueNew);	
				objItem.setAuthors(false);
				objItem.setReaders(false);
				objItem.setNames(true);
				objItem.setSummary(true);
			}else if(sDatatype.equals(DataFieldZZZ.sDATE)){
				Vector vNotesDate = new Vector(objVectorValue.size());
				for(int icountline=0; icountline <= objVectorValue.size()-1; icountline++){
					Date objDate = (Date) objVectorValue.get(icountline);
					DateTime objNotesDate = session.createDateTime(objDate);
					vNotesDate.add(objNotesDate);
				}
				
				
				Vector objVectorValueOld = objDocument.getItemValue(sFieldname);
				Vector objVectorValueNew = VectorZZZ.append( vNotesDate, objVectorValueOld); //!!! In der Reihenfolge der Parameter unterscheidet sich die Methode vom "append..."
				Item objItem = objDocument.replaceItemValue(sFieldname, objVectorValueNew);	
				
				//Item objItem = objDocument.appendItemValue(sFieldname, vNotesDate);
				objItem.setSummary(true);
			}else{
				ExceptionZZZ ez = new ExceptionZZZ("Values of datatype '" + sDatatype + "', can´t be assigned yet. This has to be developed.", iERROR_ZFRAME_METHOD, "DocumentCreatorZZZ", ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}			
			
			//Nun ggf. eine Konfigurierte Nachbereitungsmethode aufrufen
			//TODO: Für Arbeiten an dem Dokument muß immer das Session und Dokument Objekt übergeben werden
			DocumentCreatorZZZ.invokeMethodConfiguredByStore(objData, sAlias);
			
		}//end main:
	}
	
public static void prependFieldByStore(Session session, Document objDocument, DataStoreZZZ objData, String sAlias, boolean bUnique) throws ExceptionZZZ, NotesException{
//	Neu: 20080513: Fall bUnique = true ===> am Schluss den Feldwert noch Unique machen
	main:{
	//1. Aufrufen der grundlegenden Funktion
			DocumentCreatorZZZ.prependFieldByStore(session, objDocument, objData, sAlias);
			
			//2. Wert holen, Vector unique machen und anschliessend damit den Wert ersetzen (wenn die Anzahl der Vektoreinträge sich geändert hat.)
			String sDatatype = objData.getMetadata(sAlias, DataFieldZZZ.DATATYPE);
			if(sDatatype.equals(DataFieldZZZ.sNOTESRICHTEXT)) break main; //Richtext wird also nicht unique gemacht		
			if(StringZZZ.isEmpty(sDatatype)) sDatatype = DataFieldZZZ.sSTRING;   //d.h. String ist default
					
			String sFieldname = objData.getMetadata(sAlias, DataFieldZZZ.FIELDNAME);
			if(StringZZZ.isEmpty(sFieldname)) break main;  //ohne Feldnamen ist auch nichts durchzuführen
			
			if(objDocument.hasItem(sFieldname)){
				Vector vecValue = objDocument.getItemValue(sFieldname);
				if(vecValue.isEmpty()) break main;
				
				Vector vecReturn = VectorZZZ.unique(vecValue);
				if(vecReturn.size()<vecValue.size()){
					objDocument.replaceItemValue(sFieldname, vecReturn);			 //Annahme: Item-Properties bleiben gleich	
				}
			}
		}//END main:					
	}
	
	
	/** In der Konfiguration kann man angeben, dass nach dem Setzen des Wertes noch eine Klasse mit einer Methode zur Nachbearbeitung aufgerufen werden soll. Diese Verarbeitung geschieht hier mittels der Reflection-API.
	 * Merke: Das ist aber ungeeignet, um Berechnungen auf das aktuelle Dokument durchzuführen !!!
		//TODO: Es sei denn, diese Methoden bekommen immer als Parameter ein NotesDocument / NotesItem übergeben !!!
	* lindhauer; 27.01.2008 12:48:53
	 */
	public static void invokeMethodConfiguredByStore(DataStoreZZZ objData, String sAlias) throws ExceptionZZZ{
		main:{
			if(objData==null){
				ExceptionZZZ ez = new ExceptionZZZ("DataStoreZZZ - Object", iERROR_PARAMETER_MISSING, "DocumentCreatorZZZ", ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			if(StringZZZ.isEmpty(sAlias)){
				ExceptionZZZ ez = new ExceptionZZZ("Alias-String", iERROR_PARAMETER_MISSING, "DocumentCreatorZZZ", ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}

		String sMethodname = objData.getMetadata(sAlias, "CustomMethodPostTargetInsert");
		if(StringZZZ.isEmpty(sMethodname)) break main;

		//++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		String sClassname = objData.getMetadata(sAlias, "CustomClassPostTargetInsert");
		if(StringZZZ.isEmpty(sClassname)){
			ExceptionZZZ ez = new ExceptionZZZ("Classname was not provided, which contains the method '" + sMethodname + "', for the field with the alias: '" + sAlias + "'", iERROR_CONFIGURATION_MISSING, "DocumentCreatorZZZ", ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}
		
		
			Class clX = null;
			try{
				clX = Class.forName(sClassname);
			}catch(ClassNotFoundException ce){
				ExceptionZZZ ez = new ExceptionZZZ("Class was not found '" + sClassname + "', for the field with the alias: '" + sAlias + "'", iERROR_RUNTIME, "DocumentCreatorZZZ", ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
				
			Method meX = null;
			try{
				Class[] argTypes={};  //Wenn man hier ein NotesDokument/Item übergeben würde, wäre eine richtige Nachbearbeitung möglich
				meX = clX.getMethod(sMethodname, argTypes);
			}catch(NoSuchMethodException me){
				ExceptionZZZ ez = new ExceptionZZZ("Method was not found '" + sMethodname + "' which should be found in the class: '" +  sClassname + "', for the field with the alias: '" + sAlias + "'", iERROR_RUNTIME, "DocumentCreatorZZZ", ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
				
			Object objX;
			try {
				objX = clX.newInstance();
			} catch (InstantiationException e) {
				ExceptionZZZ ez = new ExceptionZZZ("InstanciationException on class: '" +  sClassname + "', for the field with the alias: '" + sAlias + "'", iERROR_RUNTIME, "DocumentCreatorZZZ", ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			} catch (IllegalAccessException e) {
				ExceptionZZZ ez = new ExceptionZZZ("IllegalAccessException on class: '" +  sClassname + "', for the field with the alias: '" + sAlias + "'", iERROR_RUNTIME, "DocumentCreatorZZZ", ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
				
			try {
				meX.invoke(objX, null);
			} catch (IllegalArgumentException e) {
				ExceptionZZZ ez = new ExceptionZZZ("IllegalArgumentException on invoking method: '" + sMethodname + "' of class: '" +  sClassname + "', for the field with the alias: '" + sAlias + "'", iERROR_RUNTIME, "DocumentCreatorZZZ", ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			} catch (IllegalAccessException e) {
				ExceptionZZZ ez = new ExceptionZZZ("IllegalAccessException on invoking method: '" + sMethodname + "' of class: '" +  sClassname + "', for the field with the alias: '" + sAlias + "'", iERROR_RUNTIME, "DocumentCreatorZZZ", ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			} catch (InvocationTargetException e) {
				ExceptionZZZ ez = new ExceptionZZZ("InvocationTargetException on invoking method: '" + sMethodname + "' of class: '" +  sClassname + "', for the field with the alias: '" + sAlias + "'", iERROR_RUNTIME, "DocumentCreatorZZZ", ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}		
			
		}//end main:
	}

	


	
	/**Erstelle ein Notesdokument, basierend auf dem aktuell verwendeten NotesDataStore.
	 *   
	* @return
	* @throws ExceptionZZZ
	* 
	* lindhauer; 10.02.2008 08:07:45
	 */
	public Document createDocument() throws ExceptionZZZ{
		Document objReturn = null;
		main:{	
			try{

				String sAlias = this.getDataStoreAliasUsed();				
				if(StringZZZ.isEmpty(sAlias)){
					ExceptionZZZ ez = new ExceptionZZZ("Alias for the current Documenttype", iERROR_PROPERTY_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
						
				
				//##############################
				KernelNotesZZZ objKernelNotes = this.getKernelNotesObject();			
				Session session = objKernelNotes.getSession();
			
												
				//###########################################
				//### die Werte aus dem DataStore für den Aliasnamen setzen
				//Ziel ist es, dies auf eine static - Funktion umzuleiten

				//### Dokument in  der Applikationsdatenbank erstellen
				Database db = objKernelNotes.getDBApplicationCurrent();
				if (db ==null ) break main;
				
				NotesDataStoreZZZ objDataNotes = this.getNotesDataStoreUsed();
				if (objDataNotes == null ) break main;
				String sApplicationKey = objKernelNotes.getApplicationKeyCurrent();
				objReturn = DocumentCreatorZZZ.createDocumentOnly(session, db, sApplicationKey, objDataNotes);
				this.setDocumentCurrent(objReturn);  //Damit anschliessend auch alle Methoden, die nicht static sind darauf zugreifen können.			
				
				//### nun die Werte aus dem DataStore $ALL setzen
				DataStoreZZZ objStore = this.getDataStoreCommonUsed();          //this.getDataStoreByAlias("$ALL");
				if(objStore.sizeValuedata()>0){
					//DocumentCreatorZZZ.appendFieldByStore(session, objReturn, objStore);
					//200801287 NEU:
					DocumentCreatorZZZ.processFieldAllByStore(session, objReturn, objStore);
				}
					
				
				//### nun eine Nummer aus dem ApplicationStore-Dokument setzen
				KernelNumberGeneratorZZZ objGenerator = new KernelNumberGeneratorZZZ(objKernelNotes);
				String sFieldname = objGenerator.getFieldnameTargetUsed(sAlias);
				String sNumber = objGenerator.generateNumberNew(sAlias);
				objReturn.replaceItemValue(sFieldname, sNumber);   //Merke: Die Nummer ist immer noch nicht gespeichert !!!
				
				//### Default Autorenfelder/Leserfelder setzen
				DocumentCreatorZZZ.appendFieldAccessDefault(session , objReturn, sApplicationKey);				
				
				//### Default Kategorisierungsfelder setzen
				this.appendFieldAllToBecomeCategorizable();
				
			}catch(NotesException ne){
				ExceptionZZZ ez = new ExceptionZZZ(ne.text, iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
		}//END main:
		return objReturn;
	}
	
	/** Merke: Setzt keine Felder aus dem DataStore $ALL, der ja für alle Dokument gesetzt werden soll.
	 * !!! Es werden hier auch keine Nummern (sieh ApplicationStore) gesetzt.
	 * 
	 * Es werden aber die Default Leser/Autorenfelder gesetzt
	* @param session
	* @param db
	* @param objData
	* @return
	* @throws ExceptionZZZ
	* 
	* lindhaueradmin; 28.11.2006 10:42:47
	 */
	public static Document createDocumentOnly(Session  session, Database db, String sApplicationKey, NotesDataStoreZZZ objData) throws ExceptionZZZ{
		Document objReturn = null;
		main:{
			try{
				if (objData==null){
					ExceptionZZZ ez = new ExceptionZZZ("NotesDataStoreZZZ-Object", iERROR_PARAMETER_MISSING, "DocumentCreatorZZZ", ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}			
				if(db==null){
					ExceptionZZZ ez = new ExceptionZZZ("Application DB not configured by Notes-Kernel", iERROR_PROPERTY_MISSING, "DocumentCreatorZZZ", ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				if(db.isOpen()==false){
					ExceptionZZZ ez = new ExceptionZZZ("Application DB configured by Notes-Kernel is not open. Access ?", iERROR_PROPERTY_VALUE, "DocumentCreatorZZZ", ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				objReturn = db.createDocument();
				
				//Nun alle Felder durchgehen, für die ein Wert gesetzt worden ist
				//MERKE: HIER IST DER MASKENNAME BESTANDTEIL VON OBJDATA !!!				
				//DocumentCreatorZZZ.appendFieldByStore(session, objReturn, (DataStoreZZZ)objData);
				//NEU: 20080127 
				DocumentCreatorZZZ.processFieldAllByStore(session, objReturn, objData);
				
				
				//###########################################
				//Wichtige Felder Setzen				
				//MErke: Da die Form in den FieldStore gepackt wird, ist das nicht notwendig  objReturn.replaceItemValue("Form", sForm);
							
				
				//Default Autorenfelder/Leserfelder setzen
				DocumentCreatorZZZ.appendFieldAccessDefault(session , objReturn, sApplicationKey);
				
				//Default Category Felder setzen
				//TODO: hierfür eine Static-Methode anbieten
				
				
			} catch (NotesException ne) {
				ExceptionZZZ ez = new ExceptionZZZ(ne.text, iERROR_RUNTIME, "DocumentCreatorZZZ", ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
				
			
		}//END main:
		return objReturn;		
	}
	
	
	/**Erstellt ein Dokument in der ApplicationDB des Kernels.
	 *  Das Dokument wird mit allen Werten, der diversen Hashmaps des objData-Objekts, gefüllt.
	 *
	 * Merke: Setzt keine Felder aus dem DataStore $ALL, der ja für alle Dokument gesetzt werden soll
	 * Merke: Setzt keine Nummern (s. Application Store)
	 *  !!! Das Dokument wird noch nicht gespeichert !!!
	 *  
	 *  Es werden aber die Default Leser/Autorenfelder gesetzt
	 *  
	 *  Merke: Das soll für die verschiedenen DataStore-Objekte gleich funktionieren.
	 *  
	* @param session
	* @param db
	* @param sForm
	* @param objData
	* @return Document
	* @throws ExceptionZZZ
	* 
	* lindhaueradmin; 24.11.2006 09:13:11
	 */
	public static Document createDocumentOnly(Session session, Database db, String sApplicationKey, String sForm, DataStoreZZZ objData) throws ExceptionZZZ{
		Document objReturn = null;
		main:{
			try{
				if (objData==null){
					ExceptionZZZ ez = new ExceptionZZZ("DataStoreZZZ-Object", iERROR_PARAMETER_MISSING, "DocumentCreatorZZZ", ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}			
				if(db==null){
					ExceptionZZZ ez = new ExceptionZZZ("Application DB not configured by Notes-Kernel", iERROR_PROPERTY_MISSING, "DocumentCreatorZZZ", ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				if(db.isOpen()==false){
					ExceptionZZZ ez = new ExceptionZZZ("Application DB configured by Notes-Kernel is not open. Access ?", iERROR_PROPERTY_VALUE, "DocumentCreatorZZZ", ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				if(StringZZZ.isEmpty(sForm)){
					ExceptionZZZ ez = new ExceptionZZZ("Form name", iERROR_PARAMETER_MISSING, "DocumentCreatorZZZ", ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				objReturn = db.createDocument();
				
				//DocumentCreatorZZZ.appendFieldByStore(session, objReturn, objData);
				//NEU: 20080127
				DocumentCreatorZZZ.processFieldAllByStore(session, objReturn, objData);
				
				//###########################################
				//Wichtige Felder Setzen
				objReturn.replaceItemValue("Form", sForm);
				
				//Default Autorenfelder/Leserfelder setzen
				DocumentCreatorZZZ.appendFieldAccessDefault(session , objReturn, sApplicationKey);
				
				
				//Default Category Felder setzen
				//TODO: hierfür eine Static-Methode anbieten
				
				
			} catch (NotesException ne) {
				ExceptionZZZ ez = new ExceptionZZZ(ne.text, iERROR_RUNTIME, "DocumentCreatorZZZ", ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
		}//END main:
		return objReturn;		
	}
	
	public void appendFieldAllToBecomeCategorizable() throws ExceptionZZZ {
		main:{
		try{
			Document doc2beProcessed = this.getDocumentCurrent();
			if(doc2beProcessed==null){
				ExceptionZZZ ez = new ExceptionZZZ("Doument to be processed", iERROR_PARAMETER_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			
			KernelNotesZZZ objKernelNotes = this.getKernelNotesObject();			
			String sKeyApplication = objKernelNotes.getApplicationKeyCurrent();
			
			
			Vector vecCategory = this.getCategoryAllUsed();
			if (vecCategory == null){
				//this.getKernelNotesLogObject().writeLog("Keine Kategorien konfiguriert für diesen Dokumenttyp: " + this.getDataStoreAliasUsed() + " | Form: " + this.getFormUsed() );
				this.getKernelNotesLogObject().writeLog("Keine Kategorien konfiguriert für diesen Dokumenttyp: " + DocumentCategorizerZZZ.readAlias(sKeyApplication, doc2beProcessed) + " | Form: " + doc2beProcessed.getItemValueString("Form") );
				break main;
			}
			
			DocumentCategorizerZZZ.appendFieldAllToBecomeCategorizable(sKeyApplication, this.getDataStoreAliasUsed(), vecCategory, doc2beProcessed);
			
		}catch(NotesException ne){
			ExceptionZZZ ez = new ExceptionZZZ("NotesException: " + ne.text, iERROR_RUNTIME, DocumentCreatorZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}			
		}//end main:
	}
	
	
	/** Aktualisiere das übergebene Notesdokument mit den Daten des aktuell verwendeten NotesDataStore.
	* @param docCur
	* @return
	* @throws ExceptionZZZ
	* 
	* lindhauer; 10.02.2008 08:10:44
	 */
	public boolean updateDocument(Session session, Document doc2update) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{	
			try{
				if(session == null){
					session = this.getKernelNotesObject().getSession();
					if (session == null){
						ExceptionZZZ ez = new ExceptionZZZ("Session", iERROR_PARAMETER_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
						throw ez;
					}
				}
				if(doc2update == null){
					ExceptionZZZ ez = new ExceptionZZZ("Document 2 Update", iERROR_PARAMETER_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}else{
					this.setDocumentCurrent(doc2update);
				}
				String sAlias = this.getDataStoreAliasUsed();				
				if(StringZZZ.isEmpty(sAlias)){
					ExceptionZZZ ez = new ExceptionZZZ("Alias for the current Documenttype", iERROR_PROPERTY_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				
				//###########################################
				//### die Werte aus dem DataStore für den Aliasnamen setzen
				//Ziel ist es, dies auf eine static - Funktion umzuleiten
				
				NotesDataStoreZZZ objDataNotes = this.getNotesDataStoreUsed();
				if (objDataNotes == null ) break main;
				
				KernelNotesZZZ objKernelNotes = this.getKernelNotesObject();
				String sApplicationKey = objKernelNotes.getApplicationKeyCurrent();
				
				
				bReturn = DocumentCreatorZZZ.updateDocument(session, doc2update, sApplicationKey, objDataNotes);
				
				//### nun die Werte aus dem DataStore $ALL setzen
				DataStoreZZZ objStore = this.getDataStoreCommonUsed();          //this.getDataStoreByAlias("$ALL");
				if(objStore.sizeValuedata()>0){
					//DocumentCreatorZZZ.appendFieldByStore(session, objReturn, objStore);
					//20080127 NEU:
					DocumentCreatorZZZ.processFieldAllByStore(session, doc2update, objStore);
				}
					
				//### nun eine Nummer aus dem ApplicationStore-Dokument setzen. NEIIN
				//KernelNumberGeneratorZZZ objGenerator = new KernelNumberGeneratorZZZ(objKernelNotes);
				//String sFieldname = objGenerator.getFieldnameTargetUsed(sAlias);
				//String sNumber = objGenerator.generateNumberNew(sAlias);
				//doc2update.replaceItemValue(sFieldname, sNumber);   //Merke: Die Nummer ist immer noch nicht gespeichert !!!
				
				//### Default Autorenfelder/Leserfelder setzen, JA, aber nur aus Sicherheitsgründen
				//Wird schon in der static-MEthode gemacht     DocumentCreatorZZZ.appendFieldAccessDefault(session , doc2update, sApplicationKey);
				
				//### Default Kategorisierungsfelder setzen, JA, aber nur aus Sicherheitsgründen
				this.appendFieldAllToBecomeCategorizable();
				
				//### Rückgabewert setzen
				bReturn = true;
			}catch(NotesException ne){
				ExceptionZZZ ez = new ExceptionZZZ(ne.text, iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
		}//END main:
		return	bReturn;
	}
	
	/** Merke: Setzt keine Felder aus dem DataStore $ALL, der ja für alle Dokument gesetzt werden soll.
	 * !!! Es werden hier auch keine Nummern (sieh ApplicationStore) gesetzt
	* @param session
	* @param db
	* @param objData
	* @return
	* @throws ExceptionZZZ
	* 
	* lindhaueradmin; 28.11.2006 10:42:47
	 */
	public static boolean updateDocument(Session  session, Document doc2update, String sApplicationKey, NotesDataStoreZZZ objData) throws ExceptionZZZ{
		boolean bReturn = false;;
		main:{
			try{
				if (objData==null){
					ExceptionZZZ ez = new ExceptionZZZ("NotesDataStoreZZZ-Object", iERROR_PARAMETER_MISSING, "DocumentCreatorZZZ", ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}			
				if(doc2update==null){
					ExceptionZZZ ez = new ExceptionZZZ("Document 2 update", iERROR_PROPERTY_MISSING, "DocumentCreatorZZZ", ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				//Nun alle Felder durchgehen, für die ein Wert gesetzt worden ist
				//MERKE: HIER IST DER MASKENNAME BESTANDTEIL VON OBJDATA !!!				
				//DocumentCreatorZZZ.appendFieldByStore(session, objReturn, (DataStoreZZZ)objData);
				//NEU: 20080127 
				DocumentCreatorZZZ.processFieldAllByStore(session,doc2update, objData);
				
				
				//###########################################
				//Wichtige Felder Setzen				
				//MErke: Da die Form in den FieldStore $ALL gepackt wird, ist das nicht notwendig  objReturn.replaceItemValue("Form", sForm);
							
				//Default Autorenfelder/Leserfelder setzen
				DocumentCreatorZZZ.appendFieldAccessDefault(session ,doc2update, sApplicationKey);
			
				
//				Default Category Felder setzen
				//TODO: hierfür eine Static-Methode anbieten
				
				//### Rückgabewert
				bReturn = true;
			} catch (NotesException ne) {
				ExceptionZZZ ez = new ExceptionZZZ(ne.text, iERROR_RUNTIME, "DocumentCreatorZZZ", ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
				
			
		}//END main:
		return bReturn;		
	}
	
	
	//#################################
//	+++ Getter / Setter
	public Document getDocumentCurrent(){
		return this.document;
	}
	public void setDocumentCurrent(Document doc){
		this.document = doc;
	}
	public void setMapperStore(MapperStoreServerVIA objStoreHTTPMapper){
		this.objStoreHTTPMapper=objStoreHTTPMapper;
		this.objStore=null;  //Wenn sich der Mapper Store ändert, dann sollte der objStore neu geholt werden
	}
	
	public NotesDataStoreZZZ getNotesDataStoreUsed() throws ExceptionZZZ{
		if(this.objStore==null){
			MapperStoreHttpZZZ objMapStore = this.getMapperStore();
			if(objMapStore==null){
				ExceptionZZZ ez = new ExceptionZZZ("No MapperStore available", iERROR_PROPERTY_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			String sAlias = this.getDataStoreAliasUsed(); //CreatorDocumentCarrierVIA.getDataStoreAliasUsed();
			NotesDataStoreZZZ objStore = (NotesDataStoreZZZ) objMapStore.getDataStoreByAlias(sAlias);
			this.objStore = objStore;
		}
		return this.objStore;
	}
	
	public DataStoreZZZ getDataStoreCommonUsed() throws ExceptionZZZ{
		if(this.objStoreCommon == null){
			MapperStoreHttpZZZ objMapStore = this.getMapperStore();
			if(objMapStore==null){
				ExceptionZZZ ez = new ExceptionZZZ("No MapperStore available", iERROR_PROPERTY_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			String sAlias = "$ALL";
			DataStoreZZZ objStore = (DataStoreZZZ) objMapStore.getDataStoreByAlias(sAlias);
			this.objStoreCommon = objStore;
		}
		return this.objStoreCommon;
	}
	public MapperStoreHttpZZZ getMapperStore(){
		return this.objStoreHTTPMapper;
	}
		
	public abstract Document searchDocumentExisting() throws ExceptionZZZ;
	public abstract Vector getCategoryAllUsed();
	
	//### zu customizende Getter//Setter
	public String getDataStoreAliasUsed() throws ExceptionZZZ{
		ExceptionZZZ ez = new ExceptionZZZ("This method has to be overwritten by a custom class", iERROR_ZFRAME_METHOD, this, ReflectCodeZZZ.getMethodCurrentName());
		throw ez;
	}
	public static String getFormUsed() throws ExceptionZZZ{
		ExceptionZZZ ez = new ExceptionZZZ("This method has to be overwritten by a custom class", iERROR_ZFRAME_METHOD, DocumentCreatorZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
		throw ez;
	}
	 
	/**Gibt an, welche Ansichten verwendet werden, die in der Maske eingebunden sind.
	 *  Diese Ansichten sollten aktualisiert werden, nach dem Aktualisieren der Kategorie (z.B. durch das Servlet).
	 *  
	* @return Vector mit den Namen der Ansichten
	* 
	* lindhaueradmin; 11.07.2008 10:17:51
	 */
	public static Vector getViewnameEmbeddedAllUsed() throws ExceptionZZZ{
		ExceptionZZZ ez = new ExceptionZZZ("This method has to be overwritten by a custom class", iERROR_ZFRAME_METHOD, DocumentCreatorZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
		throw ez;
	}
	
	
	
	//#################################
	//	+++ Beispiel
	public void customExampleForPostTargetInsert(){
		System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "#Das ist eine Möglichkeit eine statische Aktion durchzuführen.");
	}
	

		
	public  static boolean appendFieldAccessDefault(Session session, Document doc, String sApplicationKey) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			try{
			if(doc==null){
				ExceptionZZZ ez = new ExceptionZZZ("Document", iERROR_PARAMETER_MISSING, "DocumentCreatorZZZ", ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			if(StringZZZ.isEmpty(sApplicationKey)){
				ExceptionZZZ ez = new ExceptionZZZ("ApplicationKey", iERROR_PARAMETER_MISSING, "DocumentCreatorZZZ", ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			Vector vecValue = null;
			Item item = null;
			
			//+++ Leserfelder
			vecValue = new Vector();
			vecValue.addElement("[" + sApplicationKey + "Reader]");
			vecValue.addElement("[ZZZReader]");
						
			item = doc.getFirstItem("Reader" + sApplicationKey);			
			if(item==null){
				item = doc.replaceItemValue("Reader" + sApplicationKey, vecValue);
				item.setAuthors(false);
				item.setReaders(true);
				item.setNames(false);
				item.setSummary(true);
			}else{
				Vector vecValueOld = item.getValues();
				vecValueOld.addAll(vecValue);
				
				Vector vecValueNew = VectorZZZ.unique(vecValueOld);
				
				item = doc.replaceItemValue("Reader" + sApplicationKey, vecValueNew);
			}
			
			//+++ Autorenfelder
			vecValue = new Vector();
			vecValue.addElement("[" + sApplicationKey + "Author]");
			vecValue.addElement("[" + sApplicationKey + "Admin]");			
			vecValue.addElement("[ZZZAuthor]");
			vecValue.addElement("[ZZZAdmin]");
			vecValue.addElement("[ZZZServer]");
						
			item = doc.getFirstItem("Author" + sApplicationKey);
			if(item == null){
				item = doc.replaceItemValue("Author" + sApplicationKey, vecValue);
				item.setAuthors(true);
				item.setReaders(false);
				item.setNames(false);
				item.setSummary(true);
			}else{
				Vector vecValueOld = item.getValues();
				vecValueOld.addAll(vecValue);
				
				Vector vecValueNew = VectorZZZ.unique(vecValueOld);
				
				item = doc.replaceItemValue("Author" + sApplicationKey, vecValueNew);
			}
			
			
			/*
			 * zzzReader
			NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "Trying to set: '[VIAReader]' as reader.");
			objMapper.setValue("Reader", "[VIAReader]");
			NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, " '[VIAReader]', successfully set as reader.");
			
			zzzAuthor
			NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "Trying to set: '[ZZZAdmin]' as author.");
					objMapper.setValue("Author", "[ZZZAdmin]");
					NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, " '[ZZZAdmin]', successfully set as author.");
					
					NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "Trying to set: '[ZZZServer]' as author.");
					objMapper.setValue("Author", "[ZZZServer]");
					NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, " '[ZZZServer]', successfully set as author.");
					
					NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "Trying to set: '[VIAAuthor]' as author.");
					objMapper.setValue("Author", "[VIAAuthor]");
					NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, " '[VIAAuthor]', successfully set as author.");
					
					NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "Trying to set: '[VIAAdmin]' as author.");
					objMapper.setValue("Author", "[VIAAdmin]");
					NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, " '[VIAAdmin]', successfully set as author.");
					
			
			*/
			}catch(NotesException ne){
				ExceptionZZZ ez = new ExceptionZZZ(ne.text, iERROR_RUNTIME, "DocumentCreatorZZZ", ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
		}
		return bReturn;
	}
	
}//END Class
