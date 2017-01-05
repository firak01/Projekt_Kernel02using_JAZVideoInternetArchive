package via.client;

import javax.swing.JFrame;

import junit.framework.TestCase;
import use.via.client.DlgAboutVIA;

import use.via.client.FrmMainVIA;
import basic.zBasic.ExceptionZZZ;
import basic.zKernel.KernelZZZ;

public class DlgAboutVIATest   extends TestCase{
	private FrmMainVIA frmParentTest;
	private DlgAboutVIA dlgTest; 
	
	protected void setUp(){
		try {			

			KernelZZZ objKernel = new KernelZZZ("TEST", "01", "", "ZKernelConfigVideoArchiveClient_test.ini",(String)null);
			frmParentTest = new FrmMainVIA(objKernel, null);
			frmParentTest.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frmParentTest.setSize(200, 200);
			frmParentTest.setVisible(true);
			
			dlgTest = new DlgAboutVIA(objKernel, frmParentTest);

					
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
			boolean btemp = dlgTest.showDialog(frmParentTest, "Bitte Wegclicken im Test. Modales Fenster. (testShowDialog)");
			assertTrue("unable to execute 'showDialog()' as expected", btemp);		
		} catch (ExceptionZZZ ez) {
			fail("Method throws an exception." + ez.getMessageLast());
		}		
	}
	
}//END class
