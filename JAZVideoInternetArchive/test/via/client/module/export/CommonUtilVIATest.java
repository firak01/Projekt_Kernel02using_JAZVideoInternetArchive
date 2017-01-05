package via.client.module.export;

import java.io.File;
import java.util.Vector;

import junit.framework.TestCase;
import use.via.client.module.export.CommonUtilVIA;
import basic.zBasic.ExceptionZZZ;
import basic.zKernel.KernelZZZ;

public class CommonUtilVIATest extends TestCase{
	KernelZZZ objKernel;
	protected void setUp(){		
		try {			

			//Das KErel Objekt dient dazu relevante Infos nachzuschlagen. Z.B. den Pfad zur Seriennamen - Katalogdatei
			objKernel = new KernelZZZ("TEST", "01", "", "ZKernelConfigVideoArchiveClient_test.ini",(String)null);			
			
			//Merke: Ein eigentliches Test-Objekt ist nicht notwendig, weil die Methoden alle static sein sollen
			
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
	
	public void testComputeMovieDetailByFile(){
		try{
			//MEKE: DAMIT DIE SERIENNAMEN KORREKT ERFASST  WERDEN MÜSSEN SIE IN DER ENTSPRECHNDEN KATALOGDATEI HINTERLEGT WERDEN !!!
			
			
			String sModule = this.getClass().getName();
			String sProgram = this.getClass().getName();			
			File objFileCatalog = objKernel.getParameterFileByProgramAlias(sModule, sProgram, "CatalogSerieTitleFilename");	
			assertTrue("File-Katalog Datei existiert nicht '" + objFileCatalog.getPath() + "'", objFileCatalog.exists());
			
			//File objFileDummy = new File("e:\\Historische Ereignisse - die Landung auf dem Mond_die Nacht in der niemand schlafen wollte");
			File objFileDummy = new File("testdata\\Historische Ereignisse - die Landung auf dem Mond_die Nacht in der niemand schlafen wollte --- Dummy.avi");
			assertTrue("Dummy Datei existiert nicht '" + objFileDummy.getPath() + "'", objFileDummy.exists());
			
			
			Vector vec = CommonUtilVIA.computeMovieDetailByFile(objFileDummy, objFileCatalog);
			assertEquals(vec.size(), 3);
			
			String s1 = (String) vec.get(0);
			assertEquals("Historische Ereignisse", s1);
			
			String s2 = (String) vec.get(1);
			assertEquals("Die Landung auf dem Mond, die Nacht in der niemand schlafen wollte", s2);
			
			//##########################################################			.
			objFileDummy = new File("testdata\\Die_großen_Rätsel_-_Der_Sohn_Gottes_-1---Dummy.avi");
			assertTrue("Dummy Datei existiert nicht '" + objFileDummy.getPath() + "'", objFileDummy.exists());
			
			vec = CommonUtilVIA.computeMovieDetailByFile(objFileDummy, objFileCatalog);
			assertEquals(vec.size(), 3);
			
			s1 = (String) vec.get(0);
			assertEquals("Die großen Rätsel", s1);
			
			s2 = (String) vec.get(1);
			assertEquals("Der Sohn Gottes -1", s2);
			
			//###########################################################
			//Hier wurde am Anfang einer neuen Sektion ein Leerzeichen verwendet. Daher würde der Unterstrich in ein Komma umgewandelt. 
			//Das ist aber nicht korrekt. Darum muss um die Sektionen, die zur Analyse der Unterstrichersetzung herangezogen werden ein Trim gemacht werden.
			objFileDummy = new File("testdata\\Film - Der_Schninderhannes---Dummy.avi");
			assertTrue("Dummy Datei existiert nicht '" + objFileDummy.getPath() + "'", objFileDummy.exists());
			
			vec = CommonUtilVIA.computeMovieDetailByFile(objFileDummy, objFileCatalog);
			assertEquals(vec.size(), 3);
			
			s1 = (String) vec.get(0);
			assertEquals("Film", s1);
			
			s2 = (String) vec.get(1);
			assertEquals("Der Schninderhannes", s2);
			
		} catch (ExceptionZZZ ez) {
			fail("Method throws an exception." + ez.getMessageLast());
		}	
	}
	
}
