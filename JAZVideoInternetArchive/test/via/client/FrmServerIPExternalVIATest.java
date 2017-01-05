package via.client;

import junit.framework.TestCase;

import use.via.client.FrmExportDataHttpSingletonVIA;
import use.via.client.PanelFrmExportDataHttpVIA;
import basic.zBasic.ExceptionZZZ;
import basic.zKernel.KernelZZZ;

public class FrmServerIPExternalVIATest   extends TestCase{
	private FrmExportDataHttpSingletonVIA frmTest = null;
	
	protected void setUp(){
		try {			
			KernelZZZ objKernel = new KernelZZZ("TEST", "01", "", "ZKernelConfigVideoArchiveClient_test.ini",(String)null);
			frmTest= FrmExportDataHttpSingletonVIA.getInstance(objKernel, null);
			frmTest.launch("TEST - Client (Server IP External)");
									
		} catch (ExceptionZZZ ez) {
			fail("Method throws an exception." + ez.getMessageLast());
		}		
	}//END setup
	
	
	protected void tearDown(){
		//Den gestarteten Frame wieder schliessen, sonst �ffnet man permanent Fenster
		frmTest.dispose();
	}
	
	
	
	public void testdlgServerIPExternalVIACreation(){					
		//Test auf Sichtbarkeit
		assertTrue("The configuration frame is not showing.", frmTest.isShowing());
		
		//Test auf Titel
		assertEquals("TEST - Client (Server IP External)", frmTest.getTitle());
		
	}
	
	public void testPanelServerIPExternalVIA_ContentPaneCreation(){		
		//Test auf das Panel, das diesem Frame hinzugef�gt wurde
		PanelFrmExportDataHttpVIA panelIP = (PanelFrmExportDataHttpVIA) frmTest.getPanelSub("ContentPane");
		assertNotNull("The panel 'ContentPane' of the frame was not found", panelIP);	
		
		//Test auf Sichtbarkeit
		//assertTrue("The 'ContentPane' panel is not showing", panelConfig.isShowing());				
	}
}//END class
