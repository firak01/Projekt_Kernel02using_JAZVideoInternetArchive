package via.server;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import use.via.server.DocumentCategorizerZZZ;
import use.via.server.DocumentSearcherZZZ;
import custom.zNotes.kernel.KernelNotesZZZ;
import junit.framework.TestCase;
import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.NotesException;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IConstantZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zKernel.KernelZZZ;
import basic.zNotes.kernel.NotesContextProviderZZZ;

public class DocumentSearcherZZZTest extends TestCase implements IConstantZZZ {
//	+++ Test setup
	private static boolean doCleanup = true;		//default = true      false -> kein Aufr�umen um tearDown().

//	Kernelobjekte
	private KernelZZZ objKernel = null;
	private NotesContextProviderZZZ objContext = null; //Damit der im tearDown wieder recycled werden kann
	private KernelNotesZZZ objKernelNotes=null;
	
	//Ergebnisse der Tests. Hier global deklariert, damit sie im tearDown recycled werden k�nnen. 
	//Durch initDocument(...) sollen diese initialisiert werden vor dem jeweiligen Test.
	Document docTemp1 = null;
	Document docTemp2 = null;
	Document docTemp3 = null;
	
	//!!! hier gibt es nur statische Methoden zu testen !!! Darum gibt es kein eigenes TestObjekt.
	
	
	protected void setUp(){
		try {		
			//Kernel + Log - Object dem TestFixture hinzuf�gen. Siehe test.zzzKernel.KernelZZZTest
			objKernel = new KernelZZZ("TEST", "01", "", "ZKernelConfigVideoArchiveServlet_test.ini",(String)null);
			
			//Der ContextProvider simuliert einen vorhandenen NotesContext (d.h. z.B. eine Datenbank, in der der Code l�uft, einen Agentennamen, etc.)
			//Hier wird der Datenbankname aus dem Kernel-ini-File ausgelesen und der AgentenName wird �bergeben.
			objContext = new NotesContextProviderZZZ(objKernel, this.getClass().getName(), this.getClass().getName());
			
			//Damit das funktioniert muss in der Datenbank (s. Kernel-ini-File) eine Application mit dem Alias 'VIA' konfiguriert sein (d.h. f�r den Benutzer stehen entsprechende Profildokumente zur Verf�gung)
			objKernelNotes= new KernelNotesZZZ(objContext ,"JAZTest", "01", null);
			 
	}catch(ExceptionZZZ ez){
		fail("Method throws an exception." + ez.getMessageLast());
	}
	
	}//END setup
	
	public void tearDown() throws Exception {
		if(doCleanup){
			cleanUp();
		}
	}
	
	public Document initDocument(Document doc, String sMethodCalling) throws ExceptionZZZ{
		Document docReturn = null;
		main:{
		try{
			//!!! Falls doc noch null ist, soll es erstellt werden
			if(doc!=null){				
				break main;
			}
			
			Database db = this.objKernelNotes.getDBApplicationCurrent();
			doc = db.createDocument();   												
			doc.replaceItemValue("Form", "frmTest");
			
			String sMethod = null;
			if(StringZZZ.isEmpty(sMethodCalling)){
				sMethod = ReflectCodeZZZ.getMethodCurrentName();
			}else{
				sMethod = sMethodCalling;
			}
			doc.replaceItemValue("Subject", sMethod);  //Name der aufrufenden Methode
			
			//Alias f�r den Dokumenttyp "Test" setzen
			doc.replaceItemValue(DocumentCategorizerZZZ.sFIELD_PREFIX_ALIAS + this.objKernelNotes.getApplicationKeyCurrent(), "Test");
			
			//Ref f�r den Dokumenttyp Test setzen
			doc.replaceItemValue(DocumentCategorizerZZZ.sFIELD_PREFIX_REFERENCE + this.objKernelNotes.getApplicationKeyCurrent(), doc.getUniversalID());
			
			
		}catch(NotesException ne){
			ExceptionZZZ ez = new ExceptionZZZ(ne.text, iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
			throw ez;	
		}			
		}//end main
		docReturn = doc;
		return docReturn;
	}
	
	/**************************************************************************/
	/**** diese Aufr�um-Methode muss mit Leben gef�llt werden *****************/
	/**************************************************************************/
	private void cleanUp() {
		try{
			if(docTemp1 != null) docTemp1.recycle();
			if(docTemp2 != null) docTemp2.recycle();
			if(docTemp3 != null) docTemp3.recycle();
				
			objContext.recycle();
			this.objKernelNotes=null;
			this.objKernel=null;

		} catch (NotesException ne) {
			ne.printStackTrace();
		}
	}
	
	public void testSearchCategorySource(){
		try{
			try{
//				++++++++++++++++++++++++++++++++++++++++++++++++++++
				//!!! Neue Dokumente erstellen und Kategorisieren
				String sCode = ReflectCodeZZZ.getMethodCurrentName();
				
				//Damit der Wert "einzigartig ist" mit dem aktuellen Datum/Uhrzeit versehen
				String sDateTime = null;
				Date objDate = new Date();
				
				Calendar cal = Calendar.getInstance();
				cal.setTime(objDate);
				int iYear = cal.get(Calendar.YEAR);
				if(iYear == 1900){ 
					//!!! aus irgendeinem Grund muss wohl ein Leerwert als 1.1.1900 zur�ckgegeben werden. Darauf pr�fen
					sDateTime = "#1.1.1900 0:0:0";
				}else{
					//Merke: Das Servlet setzt das �bergebene "Brenndatum/Erstelldatum" in das format yymmdd um.    String sFormat = "yyMMdd"; //das ist das normale Datumsformat "dd.MM.yyyy";
					String sFormat = "dd.MM.yyyy H:m:s"; 
					SimpleDateFormat objFormat = new SimpleDateFormat(sFormat);
					sDateTime = "#" +objFormat.format(objDate);
				}							
				
				//+++ doc1: Merke der Alias dieses Dokumenttyps ist "Test"
				docTemp1 = initDocument(docTemp1, sCode);
				
//				Nun manuell die Kategoriewerte f�llen
				//a) Feld mit den Namen der Kategoefelder
				Vector vecCategory = new Vector();
				vecCategory.add("CarrierTitle");
				vecCategory.add("CarrierId");
				vecCategory.add("BeispielF�rMehrfachwert");
				docTemp1.replaceItemValue(DocumentCategorizerZZZ.sFIELD_PREFIX_CATEGORY_META + objKernelNotes.getApplicationKeyCurrent(), vecCategory);  //Category + ApplikationKey als Itemname
														
				//b) Inhalt der Kategoriefelder
				Vector vecTemp = new Vector();
				vecTemp.add("a" + sDateTime);
				vecTemp.add("b" + sDateTime);
				docTemp1.replaceItemValue("BeispielF�rMehrfachwert", vecTemp);
				docTemp1.replaceItemValue("catValBeispielF�rMehrfachwert", vecTemp);
				
				docTemp1.replaceItemValue("CarrierID", "080206#1" + sDateTime);
				docTemp1.replaceItemValue("catValCarrierID", "080206#1" + sDateTime);
				
				docTemp1.replaceItemValue("CarrierTitle", "Filme 1" + sDateTime);
				docTemp1.replaceItemValue("catValCarrierTitle","Filme 1" + sDateTime);
									
				DocumentCategorizerZZZ objCat = new DocumentCategorizerZZZ(objKernelNotes, docTemp1);
				
				//+++ doc2
				docTemp2 = initDocument(docTemp2,ReflectCodeZZZ.getMethodCurrentName());
				
				//Nun docTemp2 mit den Kategoriewerten von docTemp1 versehen
				objCat.addDocumentAsCategory(docTemp2);
				
				
				//!!! Speichern: Wichtig f�r die Suche !!!
				docTemp1.save();
				System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": New (1.) document (" + docTemp1.getUniversalID() + ") saved in database: '" + docTemp1.getParentDatabase().getFilePath() + "' on server '" + docTemp1.getParentDatabase().getServer() + "'");
				
				docTemp2.save();
				System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": New (2.) document (" + docTemp2.getUniversalID() + ") saved in database: '" + docTemp1.getParentDatabase().getFilePath() + "' on server '" + docTemp2.getParentDatabase().getServer() + "'");
								
								
				//##########################################	
				Database db = this.objKernelNotes.getDBApplicationCurrent();
				DocumentSearcherZZZ objSearch = new DocumentSearcherZZZ(objKernelNotes,db );
				assertFalse(objSearch.getFlag("init"));
				
				String sValue = "080206#1" + sDateTime;
				String sAlias = "Test"; //Alias des Dokumenttyps
				DocumentCollection col = objSearch.searchCategorySource(sValue, sAlias);
				assertNotNull(col);
				assertEquals(col.getCount(),1);
								
			}catch(NotesException ne){
				ExceptionZZZ ez = new ExceptionZZZ(ne.text, iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;	
			}
		}catch(ExceptionZZZ ez){
			fail("Method throws an exception." + ez.getMessageLast());	
		}
	}
	
	
}//END Class
