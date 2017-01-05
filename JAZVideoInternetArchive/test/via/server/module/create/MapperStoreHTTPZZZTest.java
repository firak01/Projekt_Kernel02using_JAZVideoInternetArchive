package via.server.module.create;

import java.util.Date;
import java.util.HashMap;

import use.via.MapperStoreHttpZZZ;
import use.via.server.NotesDataStoreZZZ;
import use.via.server.module.create.CarrierVIA;
import use.via.server.module.create.MapperStoreServerVIA;
import use.via.server.module.create.ServletDocumentCreateVIA;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IConstantZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.data.DataFieldZZZ;
import basic.zBasic.util.data.DataStoreZZZ;
import basic.zNotes.basic.DJAgentContext;
import basic.zNotes.kernel.NotesContextProviderZZZ;
import basic.zKernel.KernelZZZ;
import custom.zKernel.LogZZZ;
import custom.zNotes.kernel.KernelNotesLogZZZ;
import custom.zNotes.kernel.KernelNotesZZZ;
import junit.framework.TestCase;
import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.NotesThread;
import lotus.domino.Session;

public class MapperStoreHTTPZZZTest extends TestCase implements IConstantZZZ {
//	+++ Test setup
	private static boolean doCleanup = true;		//default = true      false -> kein Aufr�umen um tearDown().
	
	//Kernelobjekte
	private KernelZZZ objKernel = null;
	private NotesContextProviderZZZ objContext = null; //Damit der im tearDown wieder recycled werden kann
	private KernelNotesZZZ objKernelNotes=null;
	
	//Das zu testende Objekt
	private NotesDataStoreZZZ objData = null;
	private MapperStoreServerVIA objMapperTest = null;
	
	//private CreatorDocumentCarrierVIA objCreatorTest = null;

	/*
	//Ergebnisse der Tests. Hier global deklariert, damit sie im tearDown recycled werden k�nnen
	Document docTemp1 = null;
	Document docTemp2 = null;
	Document docCarrier = null;
	Document docMovie = null;
	Document docSerie = null;
	Document docFile = null;
	*/
	
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
			objData = new NotesDataStoreZZZ(objKernelNotes, "frmTest"); //Das Objekt wird zu beginn jedes Tests mit Werten gef�llt, die dann in das Dokument geschrieben werden.
			
			//NUN DIE EINZELNEN FIELD OBJEKTE DEFINIEREN, die f�r die Tests gebraucht werden
			DataFieldZZZ objField = new DataFieldZZZ("1");
			objField.Datatype=DataFieldZZZ.sSTRING;
			objField.Fieldname="MyFieldname1";
			objData.setField(objField);
			
			objField = new DataFieldZZZ("2");
			objField.Datatype=DataFieldZZZ.sSTRING;
			objField.Fieldname="MyFieldname2";
			objData.setField(objField);
			
			objField = new DataFieldZZZ("Remark");
			objField.Datatype=DataFieldZZZ.sNOTESRICHTEXT;
			objField.Fieldname="MyRTF1";
			objData.setField(objField);
			
			objField = new DataFieldZZZ("Number");
			objField.Datatype = DataFieldZZZ.sINTEGER;
			objField.Fieldname = "MyNumber1";
		    objData.setField(objField);
		    
		    objField = new DataFieldZZZ("NumberDouble");
		    objField.Datatype = DataFieldZZZ.sDOUBLE;
		    objField.Fieldname = "MyNumberDouble1";
		    objData.setField(objField);
		    
		    objField = new DataFieldZZZ("Date");
		    objField.Datatype = DataFieldZZZ.sDATE;
		    objField.Fieldname = "MyDate1";
		    objField.Format = "dd.MM.yyyy";
		    objData.setField(objField);
			
						
			objMapperTest = new MapperStoreServerVIA(objKernelNotes);

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
		//try{
			/*
		DJAgentContext objContext = this.objKernelNotes.getAgentContextCurrent();
		if (objContext != null){			
			objContext.recycle();  //erledigt implizit das nlDoc.recycle(); laut Buch
		}
		*/
			
/* keine Dokumenterstellung im Mapper
		if(docTemp1 != null) docTemp1.recycle();
		if(docTemp2 != null) docTemp2.recycle();
		if(docCarrier!=null) docCarrier.recycle();
		if(docMovie!=null) docMovie.recycle();
		if(docFile!=null) docFile.recycle();
	    if(docSerie!=null) docSerie.recycle();
*/			
			
		objContext.recycle();
		//NotesThread.stermThread();
		this.objKernelNotes=null;
		this.objKernel=null;
		//this.objData=null;
		this.objMapperTest = null;
		//this.objCreatorTest=null;
		
	
		//}catch(ExceptionZZZ ez){
		//	System.out.println(ez.getDetailAllLast());
		//} catch (NotesException ne) {
		//	ne.printStackTrace();
		//}
	}
	
	
	//###################################################
	//Die Tests
	
	public void testContructor(){
		
		try{
				//+++ Hier wird ein Fehler erwarte
				
				//+++ This is not correct when using the test object
				boolean btemp = objMapperTest.getFlag("init");
				assertFalse("Unexpected: The init flag was expected NOT to be set", btemp);
				
				//+++ Nun eine Log-Ausgabe (Notes-Log)
				KernelNotesLogZZZ objKernelNotesLog = objMapperTest.getKernelNotesLogObject();
				assertNotNull(objKernelNotesLog);				
				objKernelNotesLog.writeLog("succesfully created", this, ReflectCodeZZZ.getMethodCurrentName(), 3);
					
		}catch(ExceptionZZZ ez){
			fail("Method throws an exception." + ez.getMessageLast());
		//}catch(NotesException ne){
		//	fail("Method throws a NotesException." + ne.text);
		}
	}//END testConstructor
	 

	/** Das setzen von diversen Datenstrukturen, die dann im Servlet genutzt werden.
	* lindhaueradmin; 03.12.2006 12:13:15
	 */
	public void testStoreSet(){
		try{
			HashMap hmMapping = this.objMapperTest.loadFieldMapAll();
			assertNotNull(hmMapping);
			
			HashMap hmValue = new HashMap();  //Diese HashMap hat die Struktur liste(HTTPRequestParameter)=Wert, der per HTTP transportiert wird.
			hmValue.put("carrierid", "123");
			
			boolean btemp = this.objMapperTest.storeHttpParam(hmMapping, hmValue);
			assertTrue(btemp);
			
			DataStoreZZZ objStore = this.objMapperTest.getDataStoreCarrier();
			String stemp = objStore.getValueString("Number", 0);
			assertEquals("123", stemp);
			
//			##############################################################
			//+++ Nun wieder auslesen
			String sFieldnameCarrierID = objStore.getMetadata("Number", DataFieldZZZ.FIELDNAME);
			assertEquals("IDCarrier", sFieldnameCarrierID);
			
		}catch(ExceptionZZZ ez){
			fail("Method throws an exception." + ez.getMessageLast());
		}
	}//END testStoreSet

		public void testSetValue_AllType(){
			try{
				//Erst die Struktur laden
				objMapperTest.loadFieldMapAll();
				
				//Nun die Data-Strukturen f�llen
				objMapperTest.setValue("Carrier", "Number", "testIDCarrier");
				objMapperTest.setValue("Carrier", "Title", "testTitle");
				objMapperTest.setValue("Carrier", "Type", "testType DVD");
				objMapperTest.setValue("Carrier", "Created", "27.11.2006");
				objMapperTest.setValue("Carrier", "Remark", "Das ist ein RTF-Feld");
 
				objMapperTest.setValue("File", "Number", "testIDNumber");
				objMapperTest.setValue("File", "Name", "testName");
				objMapperTest.setValue("File", "Size", 1235689);
				objMapperTest.setValue("File", "Date", "28.11.2006");
				objMapperTest.setValue("File", "CompressionType", "DIVX 5.11");
				objMapperTest.setValue("File", "Remark", "");
				
				objMapperTest.setValue("Movie", "Title", "ein neuer Film");
				objMapperTest.setValue("Movie", "Remark", "URL ....");
				
				objMapperTest.setValue("Serie", "Title", "Bonanza");
				objMapperTest.setValue("Serie", "Remark", "....");
											
				//Das ist keine eigene Maske !!!
				objMapperTest.setValue("Creator", "CN=Fritz Lindhauer/O=fgl/C=DE"); //TODO: Ggf. nur 2 Parameter anbieten, was dann bedeutet, das dieser Wert in alle anderen HashMaps eingetragen wird.
				objMapperTest.setValue("Reader", "CN=Fritz Lindhauer/O=fgl/C=DE");
				objMapperTest.setValue("Reader", "[VIAReader]");
				objMapperTest.setValue("Author", "[ZZZAdmin]");
				Date objDate = new Date(); //Das ist damit das aktuelle Datum !!!
				objMapperTest.setValue("ImportDate", objDate);
				
				//##############################################################
				//+++ Nun wieder auslesen
				DataStoreZZZ objStore = objMapperTest.getDataStoreCarrier();
				String sFieldnameCarrierID = objStore.getMetadata("Number", DataFieldZZZ.FIELDNAME);
				assertEquals("IDCarrier", sFieldnameCarrierID);
				
				
				//+++ Eine L�sung, die dieses "Auslesen" verwendet
				Session session = objKernelNotes.getSession();
				Database db = objKernelNotes.getDBApplicationCurrent();
				CarrierVIA.existsCarrierIdInDb(session, db, "frmCarrierVIA", objStore, "123");
			}catch(ExceptionZZZ ez){
				fail("Method throws an exception." + ez.getMessageLast());	
			}
		}

}//end class
