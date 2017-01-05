/**
 * 
 */
package via.server.module.create;

import java.util.Date;
import java.util.Vector;

import use.via.server.DocumentCategorizerZZZ;
import use.via.server.module.create.CarrierVIA;
import use.via.server.module.create.MapperStoreServerVIA;
import junit.framework.TestCase;
import lotus.domino.Database;
import lotus.domino.DateTime;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.Session;
import basic.zKernel.KernelZZZ;
import custom.zNotes.kernel.KernelNotesLogZZZ;
import custom.zNotes.kernel.KernelNotesZZZ;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IConstantZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.data.DataFieldZZZ;
import basic.zBasic.util.data.DataStoreZZZ;
import basic.zNotes.kernel.NotesContextProviderZZZ;

/**
 * @author 0823
 *
 */
public class CarrierVIATest extends TestCase implements IConstantZZZ{
//	+++ Test setup
	private static boolean doCleanup = true;		//default = true      false -> kein Aufr�umen um tearDown().
	
	//Kernelobjekte
	private KernelZZZ objKernel = null;
	private NotesContextProviderZZZ objContext = null; //Damit der im tearDown wieder recycled werden kann
	private KernelNotesZZZ objKernelNotes=null;
	
	//Das zu testende Objekt
	private CarrierVIA objCreatorTest = null;
	private MapperStoreServerVIA objMapper = null;
		
	//Ergebnisse der Tests. Hier global deklariert, damit sie im tearDown recycled werden k�nnen
	Document docTemp1 = null;
	Document docTemp2 = null;
	Document docCarrier = null;
	//Document docMovie = null;
	//Document docSerie = null; 
	//Document docFile = null;
	
	protected void setUp(){
		try {		
			//Kernel + Log - Object dem TestFixture hinzuf�gen. Siehe test.zzzKernel.KernelZZZTest
			objKernel = new KernelZZZ("TEST", "01", "", "ZKernelConfigVideoArchiveServlet_test.ini",(String)null);
			
			//Der ContextProvider simuliert einen vorhandenen NotesContext (d.h. z.B. eine Datenbank, in der der Code l�uft, einen Agentennamen, etc.)
			//Hier wird der Datenbankname aus dem Kernel-ini-File ausgelesen und der AgentenName wird �bergeben.
			objContext = new NotesContextProviderZZZ(objKernel, this.getClass().getName(), this.getClass().getName());
			
			//Damit das funktioniert muss in der Datenbank (s. Kernel-ini-File) eine Application mit dem Alias 'VIA' konfiguriert sein (d.h. f�r den Benutzer stehen entsprechende Profildokumente zur Verf�gung)
			objKernelNotes= new KernelNotesZZZ(objContext ,"JAZTest", "01", null);
						
			//NUN das zu testende Objekt					
			objCreatorTest = new CarrierVIA(objKernelNotes);

	}catch(ExceptionZZZ ez){
		fail("Method throws an exception." + ez.getMessageLast());
	}
	
	}//END setup
	
	public void tearDown() throws Exception {
		if(doCleanup){
			cleanUp();
		}
	}
	
	/**************************************************************************/
	/**** diese Aufr�um-Methode muss mit Leben gef�llt werden *****************/
	/**************************************************************************/
	private void cleanUp() {
		try{
			/*
		DJAgentContext objContext = this.objKernelNotes.getAgentContextCurrent();
		if (objContext != null){			
			objContext.recycle();  //erledigt implizit das nlDoc.recycle(); laut Buch
		}
		*/
		if(docTemp1 != null) docTemp1.recycle();
		if(docTemp2 != null) docTemp2.recycle();
		if(docCarrier!=null) docCarrier.recycle();
	//	if(docMovie!=null) docMovie.recycle();
	//	if(docFile!=null) docFile.recycle();
	 //   if(docSerie!=null) docSerie.recycle();
			
		objContext.recycle();
		//NotesThread.stermThread();
		this.objKernelNotes=null;
		this.objKernel=null;
		this.objMapper=null;
		this.objCreatorTest=null;
		
	
		//}catch(ExceptionZZZ ez){
		//	System.out.println(ez.getDetailAllLast());
		} catch (NotesException ne) {
			ne.printStackTrace();
		}
	}
	
	
	//###################################################
	//Die Tests
	
	public void testContructor(){
		
		try{
				//+++ Hier wird ein Fehler erwarte
				
				//+++ This is not correct when using the test object
				boolean btemp = objCreatorTest.getFlag("init");
				assertFalse("Unexpected: The init flag was expected NOT to be set", btemp);
				
				//+++ Nun eine Log-Ausgabe (Notes-Log)
				KernelNotesLogZZZ objKernelNotesLog = objCreatorTest.getKernelNotesLogObject();
				assertNotNull(objKernelNotesLog);				
				objKernelNotesLog.writeLog("succesfully created", this, ReflectCodeZZZ.getMethodCurrentName(), 3);
					
		}catch(ExceptionZZZ ez){
			fail("Method throws an exception." + ez.getMessageLast());
		//}catch(NotesException ne){
		//	fail("Method throws a NotesException." + ne.text);
		}
	}//END testConstructor
	 
	public void testValidateMapperStore(){
		try{
			//1. Versuch: Setzen der Nummer auf leer
			 //+++ In diesem Test wird ein "rudiment�res" Dokument erzeugt, dass lediglich die Anforderungen f�r die zu testenden Methoden beinhaltet.				
			 objMapper = new MapperStoreServerVIA(objKernelNotes);  //Hier ggf. den Alias und den objData zus�tzlich �bergeben.
			 objMapper.setValue(objCreatorTest.getDataStoreAliasUsed(), "Number", ""); //Merke: Das dient als Beispiel f�r eine statische CustomFunktion im PostInsert 	
			 //der wird so nicht gesetzt objMapper.setValue(CreatorDocumentCarrierVIA.getDataStoreAliasUsed(), "Title", "Das ist ein JUnit Test");
			 objMapper.setValue(objCreatorTest.getDataStoreAliasUsed(), "Created", "19.12.2006");    //Wird im weiteren Verlauf des Tests gebraucht			 			 
			 objCreatorTest.setMapperStore(objMapper);
			
		    //Ohne Titel, Type des Carriers gibt es einen Fehler
			 try{
				 objCreatorTest.validateMapperStore();
				 fail("No title of the carrier provided. An error was expected.");	
			 }catch(ExceptionZZZ ez){					
			 }
			 
			 //Nun einen Titel und den Typ f�r den Carrier hinzuf�gen. Dann soll es klappen
			 objMapper.setValue(objCreatorTest.getDataStoreAliasUsed(), "Title", "MyTest");    //Wird im weiteren Verlauf des Tests gebraucht
			 objMapper.setValue(objCreatorTest.getDataStoreAliasUsed(), "Type", "DVD");
			 boolean btemp1 = objCreatorTest.validateMapperStore();
			 assertTrue(btemp1);
			 
			 DataStoreZZZ objData = objMapper.getDataStoreCarrier();	
			 String stemp = objData.getValueString("Number", 0);	             //Auf jeden Fall muss der Mapper Store gesetzt sein.
			 assertNotNull(stemp);
			 assertTrue(stemp.equals(""));
			 
			 
			 
			 
			 //2. Versuch: Die Nummer sollte nun im MapperStore gesetzt sein, d.h. es wird ein anderer Zweig durchlaufen.
			 //objData.appendValue("Number","080206#1");  //!!! Das h�ngt hinter den ersten Wert (als Leerzeichen) einen Wert an !!!
			 objData.replaceValue("Number", "080206#1");
			// stemp = objData.getValueString("Number", 0);	             //Auf jeden Fall muss der Mapper Store gesetzt sein.
			 Vector vectemp = objData.getValueVectorString("Number");
			 assertNotNull(vectemp);
			 assertEquals(1, vectemp.size());
			 
			 stemp = (String) vectemp.firstElement();
			 assertTrue(stemp.equals("080206#1"));
			 
			 boolean btemp2 = objCreatorTest.validateMapperStore();
			 assertTrue(btemp2);
			 
			 
			
		}catch(ExceptionZZZ ez){
			fail("Method throws an exception." + ez.getMessageLast());	
		}
	}
	public void testValidCarrierId(){
		boolean btemp = CarrierVIA.isValidCarrierId("080229#1");  //ja, 2008 war ein Schaltjahr, darum Februar mit 29 Tagen	
		assertTrue(btemp);
		
		btemp = CarrierVIA.isValidCarrierId("080230#1");  		
		assertFalse(btemp);
		
		btemp = CarrierVIA.isValidCarrierId("080317#1");  		
		assertTrue(btemp);
		
		btemp = CarrierVIA.isValidCarrierId("080431#1");  		
		assertFalse(btemp);
	} 
	
	public void testCreateCarrierID(){
		try{
			try{
			  //+++ In diesem Test wird ein "rudiment�res" Dokument erzeugt, dass lediglich die Anforderungen f�r die zu testenden Methoden beinhaltet.				
			 objMapper = new MapperStoreServerVIA(objKernelNotes);  //Hier ggf. den Alias und den objData zus�tzlich �bergeben.
			 objMapper.setValue(objCreatorTest.getDataStoreAliasUsed(), "Number", ""); //Merke: Das dient als Beispiel f�r eine statische CustomFunktion im PostInsert 	
			 //der wird so nicht gesetzt objMapper.setValue(CreatorDocumentCarrierVIA.getDataStoreAliasUsed(), "Title", "Das ist ein JUnit Test");
			 objMapper.setValue(objCreatorTest.getDataStoreAliasUsed(), "Created", "19.12.2006");    //Wird im weiteren Verlauf des Tests gebraucht			 			 
			 objCreatorTest.setMapperStore(objMapper);
			
			 Database db = objKernelNotes.getDBApplicationCurrent();
			 docTemp1 = db.createDocument();
			 docTemp1.replaceItemValue("Form", objCreatorTest.getFormUsed());
			 
			 DataStoreZZZ objData = objMapper.getDataStoreCarrier();		
			 String sFieldnameTitle = objData.getMetadata("Title", DataFieldZZZ.FIELDNAME);
			 docTemp1.replaceItemValue(sFieldnameTitle, "Das ist ein JUnit Test, " + ReflectCodeZZZ.getMethodCurrentName());
			 
			 String sFieldnameCreated = objData.getMetadata("Created", DataFieldZZZ.FIELDNAME);
			 
			 Session session = objKernelNotes.getSession();			
			 DateTime ndate = session.createDateTime("19.12.2006");
			 docTemp1.replaceItemValue(sFieldnameCreated, ndate);
			
			 
			 //#####################################################################
	//		+++ Nun die ID ermitteln	
			Date objDate = CarrierVIA.readDateCarrierCreated(session, docTemp1, objData);
			assertNotNull(objDate);  //Das sollte der 19.12.2006 sein
			
			String sID = CarrierVIA.generateCarrierIdNew(session, db, objCreatorTest.getFormUsed(), objData, objDate);
			assertNotNull(sID);
						
//			+++ Die Methode, welche pr�ft, ob eine ID existiert, sollte nun in der Datenbank diese ID noch nicht finden.
			boolean bExists = CarrierVIA.existsCarrierIdInDb(session, db, objCreatorTest.getFormUsed(), objData, sID);
			assertFalse(bExists);
			
			//+++ Die CarrierID ins Dokument schreiben und speichern
			boolean btemp = CarrierVIA.writeCarrierId(session, docTemp1, objData, sID);
			assertTrue(btemp);
						
			docTemp1.save();
			System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": New document (" + docTemp1.getUniversalID() + ") saved in database: '" + docTemp1.getParentDatabase().getFilePath() + "' on server '" + docTemp1.getParentDatabase().getServer() + "'");
									
			//+++ Die Methode, welche pr�ft, ob eine ID existiert, sollte nun in der Datenbank etwas finden.
			bExists = CarrierVIA.existsCarrierIdInDb(session, db, objCreatorTest.getFormUsed(), objData, sID);
			assertTrue(bExists);
			
			}catch(NotesException ne){
				ExceptionZZZ ez = new ExceptionZZZ(ne.text, iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}		
		}catch(ExceptionZZZ ez){
			fail("Method throws an exception." + ez.getMessageLast());	
		}
	}
	
	public void testCreateDocumentCarrier(){
		try{
			
			/*das funktioniert zwar, hat dann aber aber mit dem Carrier Dokument nix zu tun
			objData.setValue("2", "Hallo Welt im 2. Feld");
			objData.setValue("1", "Das ist ein Test");
			objData.setValue("1", "Das ist ein Mehrfachwert");
			objData.setValue("Remark", "Das ist die erste Zeile im RTF-Feld");
			objData.setValue("Remark", "Das ist die zweite Zeile im RTF-Feld");
			
			objData.setValue("Number", 100);
			objData.setValue("Number", 101);
			
			objData.setValue("Date", "28.11.2006");
			
			objData.setValue("NumberDouble", 2.5);
			*/
						 
			//Session session = this.objKernelNotes.getSession();
			//Database db = this.objKernelNotes.getDBApplicationCurrent();
			
			//Das erste Document
			//Das ist aber nicht an die Struktur des VIA-Projekts angelehnt.    this.objMapper.setDataStoreByAlias("Carrier", objData);
			
			
			
			//### Mit dem MapperStore bekommt man die notwendigen Informationen
			//Merke: Beim Initialisieren des MapperStoreHTTPZZZ-Dokuments wird mit loadDataStructureAllDefault() die Datenstruktur definiert.
			//           Dort bekommt man die Aliasnamen her
			 objMapper = new MapperStoreServerVIA(objKernelNotes);  //Hier ggf. den Alias und den objData zus�tzlich �bergeben.
			 objMapper.setValue(objCreatorTest.getDataStoreAliasUsed(), "Number", ""); //Merke: Das dient als Beispiel f�r eine statische CustomFunktion im PostInsert 	
			 objMapper.setValue(objCreatorTest.getDataStoreAliasUsed(), "Title", "Das ist ein JUnit Test, " + ReflectCodeZZZ.getMethodCurrentName());
			 objMapper.setValue(objCreatorTest.getDataStoreAliasUsed(), "Created", "19.12.2006");    //Wird im weiteren Verlauf des Tests gebraucht
			 			 
			 objCreatorTest.setMapperStore(objMapper);
			docTemp1 = objCreatorTest.createDocument();				
			assertNotNull(docTemp1);
			
			//### Nun ein Dokument darunter kategorisieren
			try{
				docTemp2 = objKernelNotes.getDBApplicationCurrent().createDocument();
				docTemp2.replaceItemValue("Form", "frmTest");
				docTemp2.replaceItemValue("Subject", ReflectCodeZZZ.getMethodCurrentName());
				
				//!!!! FAKEN: das neu erstellte Dokument soll kategoriesiert werden
				//objCreatorTest.setDocumentCurrent(docTemp2);
				
				
				//++++++++++++++  !!! Achtung die Dokumente wurden absichtlich vertauscht
				DocumentCategorizerZZZ objCat = new DocumentCategorizerZZZ(objKernelNotes, docTemp2);
				objCat.addDocumentAsCategory(docTemp1);     //d.h. unter dem CarrierDokument sollen dann dieses Dokument vorhanden sein, bzw. die Kategorisierungsfelder sollen in dem dokument sein..
				
				docTemp1.save();
				System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": New document (" + docTemp1.getUniversalID() + ") saved in database: '" + docTemp1.getParentDatabase().getFilePath() + "' on server '" + docTemp1.getParentDatabase().getServer() + "'");
				
				docTemp2.save();
				System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": New document (" + docTemp2.getUniversalID() + ") saved in database: '" + docTemp2.getParentDatabase().getFilePath() + "' on server '" + docTemp2.getParentDatabase().getServer() + "'");
				
			}catch(NotesException ne){
				ExceptionZZZ ez = new ExceptionZZZ(ne.text, iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}					
		}catch(ExceptionZZZ ez){
			fail("Method throws an exception." + ez.getMessageLast());	
		}
	}	
}//END class
