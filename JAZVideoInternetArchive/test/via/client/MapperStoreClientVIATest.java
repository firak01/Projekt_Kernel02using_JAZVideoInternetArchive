package via.client;

import java.util.ArrayList;

import javax.swing.JFrame;

import junit.framework.TestCase;
import use.via.client.MapperStoreClientVIA;
import basic.zBasic.ExceptionZZZ;
import basic.zKernel.KernelZZZ;

public class MapperStoreClientVIATest extends TestCase{
	JFrame frmParentTest;
	MapperStoreClientVIA mapperClient;
	
	
	protected void setUp(){
		try {			

			KernelZZZ objKernel = new KernelZZZ("TEST", "01", "", "ZKernelConfigVideoArchiveClient_test.ini",(String)null);
			frmParentTest = new JFrame();
			frmParentTest.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frmParentTest.setSize(200, 200);
			frmParentTest.setVisible(true); 
			
			//TODO Nun einige Komponenten in die Form bauen.
			//Wichtig: Diese global deklarieren, dann kann man in den Testfunktionen darauf zugreifen.
			
			mapperClient = new MapperStoreClientVIA(objKernel);
			
						
					
		} catch (ExceptionZZZ ez) {
			fail("Method throws an exception." + ez.getMessageLast());
		}		
	}//END setup
	
	
	protected void tearDown(){
		try{
			//Den gestarteten Frame wieder schliessen, sonst �ffnet man permanent Fenster
			frmParentTest.dispose();
			mapperClient.getDataStoreExportPanel().clear();
		} catch (ExceptionZZZ ez) {
		fail("Method throws an exception." + ez.getMessageLast());
		}		
	}
	
	public void testGetAlias(){
		try{
			//Pr�fen, ob es den Alias �berhaupt gibt
			boolean bAliasExists = this.mapperClient.isAliasMappedAvailable("ExportPanel", "CarrierTitle");
			assertTrue(bAliasExists);
			
			
			//Bestimmten Http-Paremter Wert auslesen, der durch den Alias definiert wurde
			String stemp = this.mapperClient.getParameterNameHttpByAlias("ExportPanel", "CarrierTitle");
			assertEquals("carriertitle", stemp);		
			
			//Alle Aliaswerte f�r den ExportPanel auslesen
			ArrayList listaAlias = this.mapperClient.getAliasMappedAll("ExportPanel");
			assertNotNull(listaAlias);
			assertTrue(listaAlias.size()>= 1);
			assertTrue(listaAlias.contains("CarrierTitle"));
			
			
			
		} catch (ExceptionZZZ ez) {
			fail("Method throws an exception." + ez.getMessageLast());
		}	
	}
	
}
