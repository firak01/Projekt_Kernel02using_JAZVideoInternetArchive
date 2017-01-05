/**
 * 
 */
package via.server;


import basic.zBasic.IConstantZZZ;
import junit.framework.TestCase;



/**
 * @author 0823
 *
 */
public class ServletDocumentCreateVIATest extends TestCase implements IConstantZZZ {
//	+++ Test setup
	private static boolean doCleanup = true;		//default = true      false -> kein Aufr�umen um tearDown().
	
	//Das zu testende Objekt
	//private ServletDocumentCreateVIA objServletTest;
	//private HttpServletRequestMockZZZ objRequestMock = null;
	
//	Kernelobjekte
	//private KernelZZZ objKernel = null;
	//private NotesContextProviderZZZ objContext = null; //Damit der im tearDown wieder recycled werden kann
	//private KernelNotesZZZ objKernelNotes=null;
	
	
	
	protected void setUp(){
		//try {		
//			Kernel + Log - Object dem TestFixture hinzuf�gen. Siehe test.zzzKernel.KernelZZZTest
			//objKernel = new KernelZZZ("TEST", "01", "", "ZKernelConfigVideoArchiveServlet_test.ini",(String)null);
			
			//Der ContextProvider simuliert einen vorhandenen NotesContext (d.h. z.B. eine Datenbank, in der der Code l�uft, einen Agentennamen, etc.)
			//Hier wird der Datenbankname aus dem Kernel-ini-File ausgelesen und der AgentenName wird �bergeben.
			//objContext = new NotesContextProviderZZZ(objKernel, this.getClass().getName(), this.getClass().getName());
			
			//Damit das funktioniert muss in der Datenbank (s. Kernel-ini-File) eine Application mit dem Alias 'VIA' konfiguriert sein (d.h. f�r den Benutzer stehen entsprechende Profildokumente zur Verf�gung)
			//objKernelNotes= new KernelNotesZZZ(objContext ,"JAZTest", "01", null);
			
			//objServletTest = new ServletDocumentCreateVIA();
			//objRequestMock = new HttpServletRequestMockZZZ();  //Aber: HttpServletRequestWrapper kann z.B. nicht gefunden werden !!!
	

	//}catch(ExceptionZZZ ez){
	//	fail("Method throws an exception." + ez.getMessageLast());
	//}

	
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
		
	}
	
	
	//###################################################
	//Die Tests
	
	public void testxy(){
		
	}//END xy
	 



}//END Class
