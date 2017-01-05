package via.client;

import use.via.client.FrmMainVIA;
import use.via.client.PanelMainVIA;
import junit.framework.TestCase;
import basic.zBasic.ExceptionZZZ;
import basic.zKernel.KernelZZZ;

public class FrmMainVIATest  extends TestCase{
	private FrmMainVIA frmMainTest;
	
	protected void setUp(){
		try {			
			KernelZZZ objKernel = new KernelZZZ("TEST", "01", "", "ZKernelConfigVideoArchiveClient_test.ini",(String)null);
			frmMainTest = new FrmMainVIA(objKernel, null);
			frmMainTest.launch(objKernel.getApplicationKey() + " - Client (Status, Information & Dataimport)");
									
		} catch (ExceptionZZZ ez) {
			fail("Method throws an exception." + ez.getMessageLast());
		}		
	}//END setup
	
	
	protected void tearDown(){
		//Den gestarteten Frame wieder schliessen, sonst �ffnet man permanent Fenster
		frmMainTest.dispose();
	}
	
	
	
	public void testFrmMainVIACreation(){					
		//Test auf Sichtbarkeit
		assertTrue("The configuration frame is not showing.", frmMainTest.isShowing());
		
		//Test auf Titel
		assertEquals("TEST - Client (Status, Information & Dataimport)", frmMainTest.getTitle());
		
	}
	
	public void testPanelMainVIA_ContentPaneCreation(){		
		//Test auf das Panel, das diesem Frame hinzugef�gt wurde
		PanelMainVIA panelMain = (PanelMainVIA) frmMainTest.getPanelSub("ContentPane");
		assertNotNull("The panel 'ContentPane' of the frame was not found", panelMain);	
		
		//Test auf Sichtbarkeit
		//assertTrue("The 'ContentPane' panel is not showing", panelConfig.isShowing());				
	}
	
}
