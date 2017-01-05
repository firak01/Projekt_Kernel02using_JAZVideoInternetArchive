import junit.framework.Test;
import junit.framework.TestSuite;
import via.ExpressionTranslatorZZZTest;
import via.client.DlgAboutVIATest;
import via.client.DlgIPExternalVIATest;
import via.client.FrmServerIPExternalVIATest;
import via.client.FrmMainVIATest;
import via.client.MapperStoreClientVIATest;
import via.client.module.export.CommonUtilVIATest;
import via.server.DocumentCategorizerZZZTest;
import via.server.DocumentCreatorZZZTest;
import via.server.DocumentSearcherZZZTest;
import via.server.ServletDocumentCreateVIATest;
import via.server.module.action.ActionFieldUpdateVIATest;
import via.server.module.create.CarrierVIATest;
import via.server.module.create.MapperStoreHTTPZZZTest;
import via.server.module.create.ServletResponseGeneratorVIATest;


public class VideoInternetArchiveAllTest {
	public static Test suite(){
		TestSuite objReturn = new TestSuite();
		//Merke: Die Tests bilden in ihrer Reihenfolge in etwa die Hierarchie im Framework ab. 
		//            Dies beim Einf�gen weiterer Tests bitte beachten.         
		
		
		objReturn.addTestSuite(ActionFieldUpdateVIATest.class);
		objReturn.addTestSuite(ExpressionTranslatorZZZTest.class);
		
		objReturn.addTestSuite(DlgAboutVIATest.class);
		objReturn.addTestSuite(DlgIPExternalVIATest.class);
		objReturn.addTestSuite(FrmMainVIATest.class);
		objReturn.addTestSuite(FrmServerIPExternalVIATest.class);
		objReturn.addTestSuite(MapperStoreClientVIATest.class);
		
		objReturn.addTestSuite(CommonUtilVIATest.class);
		
		objReturn.addTestSuite(DocumentCategorizerZZZTest.class);
		objReturn.addTestSuite(DocumentCreatorZZZTest.class);
		objReturn.addTestSuite(DocumentSearcherZZZTest.class);
		objReturn.addTestSuite(ServletDocumentCreateVIATest.class); //Scheint aber dann nicht wirklich gebraucht zu werden
		
		
		
		objReturn.addTestSuite(CarrierVIATest.class);
		objReturn.addTestSuite(MapperStoreHTTPZZZTest.class);
		objReturn.addTestSuite(ServletResponseGeneratorVIATest.class);
		
		
		return objReturn;
	}
	/**
	 * Hiermit eine Swing-Gui starten.
	 * Das ist bei eclipse aber nicht notwendig, au�er man will alle hier eingebundenen Tests durchf�hren.
	 * @param args
	 */
	public static void main(String[] args) {
		//Ab Eclipse 4.4 ist junit.swingui sogar nicht mehr Bestandteil des Bundles
		//also auch nicht mehr unter der Eclipse Variablen JUNIT_HOME/junit.jar zu finden
		//junit.swingui.TestRunner.run(VideoInternetArchiveAllTest.class);
	}
}
