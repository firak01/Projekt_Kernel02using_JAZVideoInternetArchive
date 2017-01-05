package via.server.module.action;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Vector;

import junit.framework.TestCase;
import lotus.domino.Database;
import lotus.domino.DateTime;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.Session;
import use.via.server.DocumentCategorizerZZZ;
import use.via.server.module.action.ActionFieldUpdateVIA;
import use.via.server.module.action.ActionVIA;
import use.via.server.module.create.CarrierVIA;
import use.via.server.module.create.MapperStoreServerVIA;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IConstantZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.data.DataFieldZZZ;
import basic.zBasic.util.abstractList.VectorZZZ;
import basic.zBasic.util.data.DataStoreZZZ;
import basic.zKernel.KernelZZZ;
import basic.zNotes.kernel.NotesContextProviderZZZ;
import custom.zNotes.kernel.KernelNotesLogZZZ;
import custom.zNotes.kernel.KernelNotesZZZ;
 
public class ActionFieldUpdateVIATest  extends TestCase implements IConstantZZZ{
//	+++ Test setup
	private static boolean doCleanup = true;		//default = true      false -> kein Aufr�umen um tearDown().
	
	//Kernelobjekte
	private KernelZZZ objKernel = null;
	private NotesContextProviderZZZ objContext = null; //Damit der im tearDown wieder recycled werden kann
	private KernelNotesZZZ objKernelNotes=null;
	
	//Das zu testende Objekt
	private ActionVIA objActionTest = null;

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
			objActionTest = new ActionFieldUpdateVIA(objKernelNotes, "");

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
		this.objActionTest=null;
		
	
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
				boolean btemp = objActionTest.getFlag("init");
				assertFalse("Unexpected: The init flag was expected NOT to be set", btemp);
				
				//+++ Nun eine Log-Ausgabe (Notes-Log)
				KernelNotesLogZZZ objKernelNotesLog = objActionTest.getKernelNotesLogObject();
				assertNotNull(objKernelNotesLog);				
				objKernelNotesLog.writeLog("succesfully created", this, ReflectCodeZZZ.getMethodCurrentName(), 3);
					
		}catch(ExceptionZZZ ez){
			fail("Method throws an exception." + ez.getMessageLast());
		//}catch(NotesException ne){
		//	fail("Method throws a NotesException." + ne.text);
		}
	}//END testConstructor
	 
	public void testAccessStaticValue(){

		try{
			try{
				//Beispielsweise f�r den Carrier-Dokumenttyp auf die static Methode zugreifen
				String sClassname = CarrierVIA.class.getName();
				
				Class cls = Class.forName(sClassname);
				Method method = cls.getMethod("getViewnameEmbeddedAllUsed", null);
				Vector vecViewname = (Vector) method.invoke(null, null);
				assertNotNull(vecViewname);
				assertTrue(vecViewname.contains("viwAllByCarrierIdSingleCategoryVIA"));
				
			}catch(ClassNotFoundException cnfe){
				ExceptionZZZ ez = new ExceptionZZZ("ClassNotFoundException: " + cnfe.getMessage(), iERROR_RUNTIME, this.getClass().getName(), ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}catch(NoSuchMethodException nsme){
				ExceptionZZZ ez = new ExceptionZZZ("NoSuchMethodException: " + nsme.getMessage(), iERROR_RUNTIME, this.getClass().getName(), ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}catch(IllegalAccessException iae){
				ExceptionZZZ ez = new ExceptionZZZ("IllegalAccessException: " + iae.getMessage(), iERROR_RUNTIME, this.getClass().getName(), ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}catch(InvocationTargetException ite){
				ExceptionZZZ ez = new ExceptionZZZ("InvocationTargetException: " + ite.getMessage(), iERROR_RUNTIME, this.getClass().getName(), ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
		}catch(ExceptionZZZ ez){
			fail("Method throws an exception." + ez.getMessageLast());	
		}
	}
}//END class
