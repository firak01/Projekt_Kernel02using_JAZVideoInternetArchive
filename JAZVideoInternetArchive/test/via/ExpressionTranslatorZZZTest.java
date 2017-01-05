package via;


import junit.framework.TestCase;
import use.via.ExpressionTranslatorZZZ;
import use.via.client.MapperStoreClientVIA;
import basic.zBasic.ExceptionZZZ;
import basic.zKernel.KernelZZZ;

public class ExpressionTranslatorZZZTest extends TestCase{
	private ExpressionTranslatorZZZ exprTest = null;
	protected void setUp(){
		try {			

			KernelZZZ objKernel = new KernelZZZ("TEST", "01", "", "ZKernelConfigVideoArchiveClient_test.ini",(String)null);			
			MapperStoreClientVIA mapperClient = new MapperStoreClientVIA(objKernel);
			
			//Das eigentliche Test-Objekt	
			exprTest = new ExpressionTranslatorZZZ(objKernel, mapperClient, "ExportPanel");
					
		} catch (ExceptionZZZ ez) {
			fail("Method throws an exception." + ez.getMessageLast());
		}		
	}//END setup
	
	
	protected void tearDown(){
		/*
		try{		
			mapperClient.getDataStoreExportPanel().clear();
		} catch (ExceptionZZZ ez) {
			fail("Method throws an exception." + ez.getMessageLast());
		}	
		*/	
	}
	
	public void testTranslate(){
		try{
			//Merke: Die Ausdr�cke sind nicht frei w�hlbar, sondern m�ssen sich schon an den im MapperStoreClientVIA vorhandenen Feldern orientieren
			//String sExpressionAlias = "CarrierSequenze<?/>CarrierCreated<+/>'#'<+/>CarrierSequenze<:/>''";
			String sExpressionAlias = "CarrierID<?/>CarrierID<:/>CarrierCreated<+/>'#'<+/>CarrierSequenze";
			String sExpressionTranslated = exprTest.translate(sExpressionAlias, MapperStoreClientVIA.iPARAMETER_FIELDNAME);
			//assertEquals("textCarrierSequenze.getText<?/>textCarrierCreated.getText<+/>'#'<+/>textCarrierSequenze.getText<:/>''", sExpressionTranslated);
			assertEquals("textCarrierId.getText<?/>textCarrierId.getText<:/>textCarrierCreated.getText<+/>'#'<+/>textCarrierSequenze.getText", sExpressionTranslated);
		} catch (ExceptionZZZ ez) {
			fail("Method throws an exception." + ez.getMessageLast());
		}	
		
		/*
		
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
			
			
			
	
		*/
	}
	
}
