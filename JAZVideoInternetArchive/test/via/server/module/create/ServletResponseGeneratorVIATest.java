package via.server.module.create;

import java.io.File;

import junit.framework.TestCase;
import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import use.via.server.module.create.ResponseGeneratorVIA;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IConstantZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zKernel.IKernelContextZZZ;
import basic.zKernel.KernelContextZZZ;
import basic.zNotes.kernel.NotesContextProviderZZZ;
import basic.zKernel.KernelZZZ;
import custom.zNotes.kernel.KernelNotesLogZZZ;
import custom.zNotes.kernel.KernelNotesZZZ;

public class ServletResponseGeneratorVIATest extends TestCase implements IConstantZZZ{
//	+++ Test setup
	private static boolean doCleanup = true;		//default = true      false -> kein Aufr�umen um tearDown().
	
	//Kernelobjekte
	private KernelZZZ objKernel = null;
	private NotesContextProviderZZZ objContext = null; //Damit der im tearDown wieder recycled werden kann
	private KernelNotesZZZ objKernelNotes=null;
	private KernelContextZZZ objKernelSection = null;
	
	//Das zu testende Objekt
	private ResponseGeneratorVIA objGeneratorTest = null;
			
	//TestDokumente. Hier global deklariert, damit sie im tearDown recycled werden k�nnen
	Document docCarrier = null;
	Document docFile = null;
	Document docSerie = null;
	Document docMovie = null;
	
	protected void setUp(){
		try {	
			try{
				//Kernel + Log - Object dem TestFixture hinzuf�gen. Siehe test.zzzKernel.KernelZZZTest
				objKernel = new KernelZZZ("TEST", "01", "", "ZKernelConfigVideoArchiveServlet_test.ini",(String)null);
				objKernelSection = new KernelContextZZZ(this.getClass().getName(), this.getClass().getName());
				
				//Der ContextProvider simuliert einen vorhandenen NotesContext (d.h. z.B. eine Datenbank, in der der Code l�uft, einen Agentennamen, etc.)
				//Hier wird der Datenbankname aus dem Kernel-ini-File ausgelesen und der AgentenName wird �bergeben.
				objContext = new NotesContextProviderZZZ(objKernel, this.getClass().getName(), this.getClass().getName());
				
				//Damit das funktioniert muss in der Datenbank (s. Kernel-ini-File) eine Application mit dem Alias 'VIA' konfiguriert sein (d.h. f�r den Benutzer stehen entsprechende Profildokumente zur Verf�gung)
				objKernelNotes= new KernelNotesZZZ(objContext ,"VIA", "01", null);
				
				//#### DIE DOKUMENTE HOLEN, DEREN INHALT ZUR�CKGEGEBEN WERDEN SOLL. MERKE: SIE WERDEN KOMPLETT NUR IM SPEICHER ERSTELLT.
				Database dbAppl = objKernelNotes.getDBApplicationCurrent();			
				docCarrier = dbAppl.createDocument();
				docCarrier.appendItemValue("IDCarrier", "070412#1");
				
				
				docFile = dbAppl.createDocument();
				docFile.appendItemValue("IDCarrier", "070412#1");
				
				docMovie = dbAppl.createDocument();
				docMovie.appendItemValue("IDCarrier", "070412#1");
				
				docSerie = dbAppl.createDocument();
				docSerie.appendItemValue("IDCarrier", "070412#1");
				
				/* FALSCHER ANSATZ: DIE DOKUMENTE M�SSEN "GEFAKED WERDEN" UND NUR IM SPEICHER EXISTIEREN F�R DIESEN TEST !!!
				 * 	View viwAll = dbAppl.getView("$All");  //Merke: Ich gehe davon aus, das die zuletzt erstellten Dokumente oben stehen !!!
				ViewEntryCollection colviw = viwAll.getAllEntries();
				
				
//				+++ Auf der Suche nach dem CarrierDokument
				  ViewEntry ve = colviw.getFirstEntry();
			      while (ve != null  && docCarrier == null) {
			        //System.out.println("Entry is at position " + ve.getPosition('.'));
			    	  			    	  
			    	if(ve.isDocument()){
			    		Document doctemp = ve.getDocument();
			    		String stemp = doctemp.getItemValueString("Alias" + objKernelNotes.getApplicationKeyCurrent());
			    		if(stemp!=null && stemp.equalsIgnoreCase("carrier")){
			    			docCarrier = doctemp;
			    		}
			    	}
			      }//END while
			      if(docCarrier==null){
			    	  fail("there is no carrier document available");			    	  
			      }
			      String sCarrierID = docCarrier.getItemValueString("CarrierID");
			      
			      //+++ Auf der Suche nach dem file-Dokument
			      ve = colviw.getFirstEntry();
			      while (ve != null  && docFile == null) {
			        //System.out.println("Entry is at position " + ve.getPosition('.'));
			    	  			    	  
			    	if(ve.isDocument()){
			    		Document doctemp = ve.getDocument();
			    		String stemp = doctemp.getItemValueString("Alias" + objKernelNotes.getApplicationKeyCurrent());
			    		String sCarrierIDProof = doctemp.getItemValueString("CarrierID");
			    		if(stemp!=null && stemp.equalsIgnoreCase("file") && sCarrierID.equalsIgnoreCase(sCarrierIDProof)){
			    			docFile = doctemp;
			    		}else{
			    			
			    		}
			    	}
			      }//END while
			      if(docFile==null){
			    	  fail("there is no file document available");			    	  
			      }
			    */
			      
	
				//NUN das zu testende Objekt					
				objGeneratorTest = new ResponseGeneratorVIA(objKernelNotes, objKernelSection, null);

			} catch (NotesException e) {
				ExceptionZZZ ez = new ExceptionZZZ(e.getMessage(), iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
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

		if(docCarrier!=null) docCarrier.recycle();
		if(docMovie!=null) docMovie.recycle();
		if(docFile!=null) docFile.recycle();
		if(docSerie!=null) docSerie.recycle();
			
		objContext.recycle();
		//NotesThread.stermThread();
		this.objKernelNotes=null;
		this.objKernel=null;
		this.objGeneratorTest=null;
		
	
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
				boolean btemp = objGeneratorTest.getFlag("init");
				assertFalse("Unexpected: The init flag was expected NOT to be set", btemp);
				
				//+++ Nun eine Log-Ausgabe (Notes-Log)
				KernelNotesLogZZZ objKernelNotesLog = objGeneratorTest.getKernelNotesLogObject();
				assertNotNull(objKernelNotesLog);				
				objKernelNotesLog.writeLog("succesfully created", this, ReflectCodeZZZ.getMethodCurrentName(), 3);
					
		}catch(ExceptionZZZ ez){
			fail("Method throws an exception." + ez.getMessageLast());
		//}catch(NotesException ne){
		//	fail("Method throws a NotesException." + ne.text);
		}
	}//END testConstructor
	 
	public void testGenerateResponseHtml(){
		///* TODO GOON Die Dokumente holen, und basierend darauf eine HTML-Seite erzeugen
		try{
			File filePattern =objKernel.getParameterFileByProgramAlias(this.getClass().getName(), this.getClass().getName(), "FilePageSuccessPatternPath");
			assertNotNull("the pattern file was not configured", filePattern);
			assertTrue("the pattern file does not exist", filePattern.exists());
			
			IKernelContextZZZ objContext = objGeneratorTest.getContextUsed();
			String sModule = objContext.getModuleName();
			String sProgram = objContext.getProgramName();
			String sFileResponse = objKernel.getParameterByProgramAlias(sModule, sProgram, "FilePageSuccessResponsePath").getValue(); 
			assertNotNull("the response file was not configured", sFileResponse);
			assertFalse("the response file was not configured", sFileResponse.equals(""));
			
			String sContent = objGeneratorTest.generateContent4Success(docCarrier, docFile, docSerie, docMovie, "text/html");
			assertNotNull(sContent);
			
			System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "# HTML als String: '" + sContent + "'");
			
		}catch(ExceptionZZZ ez){
			fail("Method throws an exception." + ez.getMessageLast());	
		}
	
	}
	
	
	public void testGenerateResponseXML(){
		try{
			String sContent = objGeneratorTest.generateContent4Success(docCarrier, docFile, docSerie, docMovie, "text/xml");
			assertNotNull(sContent);
			
			//TODO: Den String in die Datei ausgeben
			System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "# XML als String: '" + sContent + "'");
		}catch(ExceptionZZZ ez){
			fail("Method throws an exception." + ez.getMessageLast());	
		}
	}

		
}//END class