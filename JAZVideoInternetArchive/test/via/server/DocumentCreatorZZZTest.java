package via.server;

import java.util.Vector;

import org.apache.commons.collections.map.MultiValueMap;

import junit.framework.TestCase;
import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.RichTextItem;
import lotus.domino.Session;
import use.via.server.DocumentCategorizerZZZ;
import use.via.server.DocumentCreatorZZZ;
import use.via.server.NotesDataStoreZZZ;
import use.via.server.module.create.CarrierVIA;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IConstantZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.data.DataFieldZZZ;
import basic.zNotes.kernel.NotesContextProviderZZZ;
import basic.zKernel.KernelZZZ;
import custom.zNotes.kernel.KernelNotesZZZ;

public class DocumentCreatorZZZTest extends TestCase implements IConstantZZZ{
//	+++ Test setup
	private static boolean doCleanup = true;		//default = true      false -> kein Aufr�umen um tearDown().
	
	//Kernelobjekte
	private KernelZZZ objKernel = null;
	private NotesContextProviderZZZ objContext = null; //Damit der im tearDown wieder recycled werden kann
	private KernelNotesZZZ objKernelNotes=null;
	
	//Ergebnisse der Tests. Hier global deklariert, damit sie im tearDown recycled werden k�nnen
	Document docTemp1 = null;
	Document docTemp2 = null;
	
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
	
	/**************************************************************************/
	/**** diese Aufr�um-Methode muss mit Leben gef�llt werden *****************/
	/**************************************************************************/
	private void cleanUp() {
		try{
			if(docTemp1 != null) docTemp1.recycle();
			if(docTemp2 != null) docTemp2.recycle();
				
			objContext.recycle();
			this.objKernelNotes=null;
			this.objKernel=null;

		} catch (NotesException ne) {
			ne.printStackTrace();
		}
	}
	
	
	//###################################################
	//Die Tests
	public void testAppendFieldByStore(){
		//!!! neu 20080127: Dokument erstellen und Felder hineinsetzen, bzw. anh�ngen !!! 
		
		
		try{
		//+++  Erst die Feldstruktur definieren, f�r eine bestimmte Maske
		NotesDataStoreZZZ objData = new NotesDataStoreZZZ(objKernelNotes, "frmTest"); //Das Objekt wird zu beginn jedes Tests mit Werten gef�llt, die dann in das Dokument geschrieben werden.

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
		
		
		
		//+++ Nun die Werte setzen
		objData.appendValue("2", "Hallo Welt im 2. Feld");
		objData.appendValue("1", "Das ist ein Test");
		objData.appendValue("1", "Das ist ein Mehrfachwert");
		objData.appendValue("Remark", "Das ist die erste Zeile im RTF-Feld");
		objData.appendValue("Remark", "Das ist die zweite Zeile im RTF-Feld");
		
		objData.appendValue("Number", 100);
		objData.appendValue("Number", 101);
		
		objData.appendValue("Date", "28.11.2006");
		
		objData.appendValue("NumberDouble", 2.5);
					 
		Session session = this.objKernelNotes.getSession();
		Database db = this.objKernelNotes.getDBApplicationCurrent();
		//String sApplicationKey = this.objKernelNotes.getApplicationKeyCurrent();
		
		//+++ Testdokument erstellen und dort einen Wert setzen.
		try{
			Document doctemp = db.createDocument();
			doctemp.appendItemValue("Form", "frmTest"); //Damit man es sich als Notesmaske ansehen kann
			
			doctemp.appendItemValue("MyFieldname1", "TestWert 1");
			
			RichTextItem rtitem = doctemp.createRichTextItem("MyRTF1");
			rtitem.appendText("Zeile 0");
			
			//Nun f�r dieses Item den Wert setzen, basierend auf den DataStore
			DocumentCreatorZZZ.appendFieldByStore(session, doctemp, objData, "1");
			DocumentCreatorZZZ.appendFieldByStore(session, doctemp, objData, "Remark");
			
			
			//+++ Das TestDokument am Ende speichern
			doctemp.save();
			System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": New document (" + doctemp.getUniversalID() + ") saved in database: '" + doctemp.getParentDatabase().getFilePath() + "' on server '" + doctemp.getParentDatabase().getServer() + "'");
			
		}catch(NotesException ne){
			ExceptionZZZ ez = new ExceptionZZZ(ne.text, iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}
		
		}catch(ExceptionZZZ ez){
			fail("Method throws an exception." + ez.getMessageLast());	
		}
	}
	
	
	
	public void testCreateDocument_Static(){
		try{
			//+++  Erst die Feldstruktur definieren, f�r eine bestimmte Maske
			NotesDataStoreZZZ objData = new NotesDataStoreZZZ(objKernelNotes, "frmTest"); //Das Objekt wird zu beginn jedes Tests mit Werten gef�llt, die dann in das Dokument geschrieben werden.

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
			
			
			
			//+++ Nun die Werte setzen
			objData.appendValue("2", "Hallo Welt im 2. Feld");
			objData.appendValue("1", "Das ist ein Test");
			objData.appendValue("1", "Das ist ein Mehrfachwert");
			objData.appendValue("Remark", "Das ist die erste Zeile im RTF-Feld");
			objData.appendValue("Remark", "Das ist die zweite Zeile im RTF-Feld");
			
			objData.appendValue("Number", 100);
			objData.appendValue("Number", 101);
			
			objData.appendValue("Date", "28.11.2006");
			
			objData.appendValue("NumberDouble", 2.5);
						 
			Session session = this.objKernelNotes.getSession();
			Database db = this.objKernelNotes.getDBApplicationCurrent();
			String sApplicationKey = this.objKernelNotes.getApplicationKeyCurrent();
			
			//Das erste Document
			docTemp1 = DocumentCreatorZZZ.createDocumentOnly(session, db, sApplicationKey, "frmTest", objData);  //Merke: die Angabe von frmTest ist eigentlich �berfl�ssig, da wir ein NotesDataStore-Objekt �bergenben, worin der Maskenname schon enthalten ist.
			assertNotNull(docTemp1);
								
			
			//Das zweite Document 
			docTemp2 = DocumentCreatorZZZ.createDocumentOnly(session, db, sApplicationKey, objData);
			assertNotNull(docTemp2);
			
			
			try{
				docTemp1.save();
				System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": New document (" + docTemp1.getUniversalID() + ") saved in database: '" + docTemp1.getParentDatabase().getFilePath() + "' on server '" + docTemp1.getParentDatabase().getServer() + "'");
								
				docTemp2.save();
				System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": New (2.) document (" + docTemp2.getUniversalID() + ") saved in database: '" + docTemp2.getParentDatabase().getFilePath() + "' on server '" + docTemp2.getParentDatabase().getServer() + "'");
				
			}catch(NotesException ne){
				ExceptionZZZ ez = new ExceptionZZZ(ne.text, iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			
		}catch(ExceptionZZZ ez){
			fail("Method throws an exception." + ez.getMessageLast());	
		}
	}
	
	
	public void testAppendFieldAccessDefault_Static(){
		try{
			try{
				//zuerst ein dokument erstellen
				Session session = this.objKernelNotes.getSession();
				Database db = this.objKernelNotes.getDBApplicationCurrent();
				docTemp1 = db.createDocument();
				docTemp1.replaceItemValue("Form", "frmTest");
				
				//Nun einen dummyWert einf�gen, der soll anschliessend nicht doppelt sein
				Vector vec = new Vector();
				vec.addElement("[ZZZReader]");
				
				String sApplicationKey = this.objKernelNotes.getApplicationKeyCurrent();
				docTemp1.replaceItemValue("Reader" + sApplicationKey, vec);
				
				
				//###############################
				DocumentCreatorZZZ.appendFieldAccessDefault(session, docTemp1, sApplicationKey);
				
				docTemp1.save();
				System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": New (1.) document (" + docTemp1.getUniversalID() + ") saved in database: '" + docTemp1.getParentDatabase().getFilePath() + "' on server '" + docTemp1.getParentDatabase().getServer() + "'");
				
			
			}catch(NotesException ne){
				ExceptionZZZ ez = new ExceptionZZZ(ne.text, iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}			
		}catch(ExceptionZZZ ez){
			fail("Method throws an exception." + ez.getMessageLast());	
		}
	}
	
	

}
