/**
 * 
 */
package via.client;

import java.util.HashMap;

import javax.swing.JFrame;

import junit.framework.TestCase;
import use.via.client.DlgIPExternalVIA;
import use.via.client.FrmMainVIA;
import basic.zBasic.ExceptionZZZ;
import basic.zKernel.KernelZZZ;

/**
 * @author 0823
 *
 */
public class DlgIPExternalVIATest extends TestCase{
private FrmMainVIA frmParentTest;
private DlgIPExternalVIA dlgTest; 

protected void setUp(){
	try {			

		KernelZZZ objKernel = new KernelZZZ("TEST", "01", "", "ZKernelConfigVideoArchiveClient_test.ini",(String)null);
		frmParentTest = new FrmMainVIA(objKernel, null);
		frmParentTest.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmParentTest.setSize(200, 200);
		frmParentTest.setVisible(true);
		
		HashMap<String, Boolean> hmFlag = new HashMap<String, Boolean>();
		hmFlag.put("isKernelProgram", true);
		dlgTest = new DlgIPExternalVIA(objKernel, frmParentTest, hmFlag);

				
	} catch (ExceptionZZZ ez) {
		fail("Method throws an exception." + ez.getMessageLast());
	}		
}//END setup


protected void tearDown(){
	//Den gestarteten Frame wieder schliessen, sonst �ffnet man permanent Fenster
	frmParentTest.dispose();
	
	//dlgTest.dispose();
}



public void testShowDialog(){		
	try{ 			
		//den testdialog nun �ber den frame �ffnen
		boolean btemp = dlgTest.showDialog(frmParentTest, "Das soll kein modales Fenster sein. Soll sich also sebst beenden im Test. (testShowDialog)");
		assertTrue("unable to execute 'showDialog()' as expected", btemp);		
	} catch (ExceptionZZZ ez) {
		fail("Method throws an exception." + ez.getMessageLast());
	}		
}

}//END class
