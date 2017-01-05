package via.server;

import java.util.Vector;

import org.apache.commons.collections.map.MultiValueMap;

import use.via.server.DocumentCategorizerZZZ;
import use.via.server.ICategoryConstantZZZ;
import junit.framework.TestCase;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IConstantZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zKernel.KernelZZZ;
import basic.zNotes.kernel.NotesContextProviderZZZ;
import custom.zNotes.kernel.KernelNotesZZZ;
import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.Session;

public class DocumentCategorizerZZZTest extends TestCase implements IConstantZZZ {
//	+++ Test setup
	private static boolean doCleanup = true;		//default = true      false -> kein Aufr�umen um tearDown().

//	Kernelobjekte
	private KernelZZZ objKernel = null;
	private NotesContextProviderZZZ objContext = null; //Damit der im tearDown wieder recycled werden kann
	private KernelNotesZZZ objKernelNotes=null;
	
	//Ergebnisse der Tests. Hier global deklariert, damit sie im tearDown recycled werden k�nnen. 
	//Durch initDocument(...) sollen diese initialisiert werden vor dem jeweiligen Test.
	Document docTemp1 = null;
	Document docTemp2 = null;
	Document docTemp3 = null;
	
	//!!! hier gibt es nur statische Methoden zu testen !!! Darum gibt es kein eigenes TestObjekt.
	
	
	protected void setUp(){
		try {		
			//Kernel + Log - Object dem TestFixture hinzuf�gen. Siehe test.zzzKernel.KernelZZZTest
			objKernel = new KernelZZZ("TEST", "01", "", "ZKernelConfigVideoArchiveServlet_test.ini",(String)null);
			
			//Der ContextProvider simuliert einen vorhandenen NotesContext (d.h. z.B. eine Datenbank, in der der Code l�uft, einen Agentennamen, etc.)
			//Hier wird der Datenbankname aus dem Kernel-ini-File ausgelesen und der AgentenName wird �bergeben.
			objContext = new NotesContextProviderZZZ(objKernel, this.getClass().getName(), this.getClass().getName());
			
			//Damit das funktioniert muss in der Datenbank (s. Kernel-ini-File) eine Application mit dem Alias 'VIA' konfiguriert sein (d.h. f�r den Benutzer stehen entsprechende Profildokumente zur Verf�gung)
			objKernelNotes= new KernelNotesZZZ(objContext ,"JAZTest", "01", null);
						
	}catch(ExceptionZZZ ez){
		fail("Method throws an exception." + ez.getMessageLast());
	}
	
	}//END setup
	
	public void tearDown() throws Exception {
		if(doCleanup){
			cleanUp();
		}
	}
	
	public Document initDocument(Document doc, String sMethodCalling) throws ExceptionZZZ{
		Document docReturn = null;
		main:{
		try{
			//!!! Falls doc noch null ist, soll es erstellt werden
			if(doc!=null){				
				break main;
			}
			
			Database db = this.objKernelNotes.getDBApplicationCurrent();
			doc = db.createDocument();   												
			doc.replaceItemValue("Form", "frmTest");
			
			String sMethod = null;
			if(StringZZZ.isEmpty(sMethodCalling)){
				sMethod = ReflectCodeZZZ.getMethodCurrentName();
			}else{
				sMethod = sMethodCalling;
			}
			doc.replaceItemValue("Subject", sMethod);  //Name der aufrufenden Methode
			
			//Alias f�r den Dokumenttyp "Test" setzen
			doc.replaceItemValue(DocumentCategorizerZZZ.sFIELD_PREFIX_ALIAS + this.objKernelNotes.getApplicationKeyCurrent(), "Test");
			
			//Ref f�r den Dokumenttyp Test setzen
			doc.replaceItemValue(DocumentCategorizerZZZ.sFIELD_PREFIX_REFERENCE + this.objKernelNotes.getApplicationKeyCurrent(), doc.getUniversalID());
			
			
		}catch(NotesException ne){
			ExceptionZZZ ez = new ExceptionZZZ(ne.text, iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
			throw ez;	
		}			
		}//end main
		docReturn = doc;
		return docReturn;
	}
	
	/**************************************************************************/
	/**** diese Aufr�um-Methode muss mit Leben gef�llt werden *****************/
	/**************************************************************************/
	private void cleanUp() {
		try{
			if(docTemp1 != null) docTemp1.recycle();
			if(docTemp2 != null) docTemp2.recycle();
			if(docTemp3 != null) docTemp3.recycle();
				
			objContext.recycle();
			this.objKernelNotes=null;
			this.objKernel=null;

		} catch (NotesException ne) {
			ne.printStackTrace();
		}
	}
	
	public void testComputeCategoryMap(){
		try{
			try{
//				zuerst ggf. das dokument erstellen
				docTemp1 = initDocument(docTemp1, ReflectCodeZZZ.getMethodCurrentName());
				
				//Nun manuell die Kategoriewerte f�llen				
				Vector vecCategory = new Vector();
				vecCategory.add("CarrierTitle");
				vecCategory.add("CarrierId");
				vecCategory.add("BeispielF�rMehrfachwert");
				docTemp1.replaceItemValue(DocumentCategorizerZZZ.sFIELD_PREFIX_CATEGORY_META + objKernelNotes.getApplicationKeyCurrent(), vecCategory);  //Category + ApplikationKey als Itemname
				
				Vector vecTemp = new Vector();
				vecTemp.add("a");
				vecTemp.add("b");
				docTemp1.replaceItemValue("BeispielF�rMehrfachwert", vecTemp);
				docTemp1.replaceItemValue("catValBeispielF�rMehrfachwert", vecTemp);
				
				docTemp1.replaceItemValue("CarrierID", "080206#1");
				docTemp1.replaceItemValue("catValCarrierID", "080206#1");
				
				docTemp1.replaceItemValue("CarrierTitle", "Filme 1");
				docTemp1.replaceItemValue("catValCarrierTitle","Filme 1");
				
				
				//##########################################				
				//Per Algorithmus die Werte auslesen und in eine Hashmap stellen
				MultiValueMap hmCategories = new MultiValueMap();			
				hmCategories = DocumentCategorizerZZZ.computeDocumentCategoryMap(objKernelNotes, docTemp1);
				
				assertFalse(hmCategories.isEmpty()); //da muss auf jeden Fall etwas drinstehen
				
				
			}catch(NotesException ne){
				ExceptionZZZ ez = new ExceptionZZZ(ne.text, iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;	
			}
		}catch(ExceptionZZZ ez){
			fail("Method throws an exception." + ez.getMessageLast());	
		}
	}
	
	
	public void testAddDocumentAsCategory(){
//		zuerst ein dokument erstellen
		try{
			try{
//				zuerst ggf. das Ausgangsdokument erstellen
				docTemp1 = initDocument(docTemp1,ReflectCodeZZZ.getMethodCurrentName());
				DocumentCategorizerZZZ objCat = new DocumentCategorizerZZZ(objKernelNotes, docTemp1);
								
				//Dann eine Hashmap mit Werten erstellen. Merke: Ohne diese Hashmap w�rde in der Methode versucht aus den Default - Kategorisierungsfeldern solch eine Map aufzubauen.
				MultiValueMap hmCategories = new MultiValueMap();
				hmCategories.put("Category1", "Cat1Value1");
				hmCategories.put("Category2", "Cat2Value1");
				hmCategories.put("Category1", "Cat1Value2");
				objCat.setCategoryMap(hmCategories);
					
			
				//nun die kategoriesierung durchf�hren, d.h. Das neue Dokument wird mit Kategoriseirungswerten aus dem vorherigen Dokument versehen
				docTemp2 = initDocument(docTemp2,ReflectCodeZZZ.getMethodCurrentName());
				boolean btemp = objCat.addDocumentAsCategory(docTemp2);
				assertTrue(btemp);
				
				//Speichern
				docTemp1.save();
				System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": New (1.) document (" + docTemp1.getUniversalID() + ") saved in database: '" + docTemp1.getParentDatabase().getFilePath() + "' on server '" + docTemp1.getParentDatabase().getServer() + "'");
				
				docTemp2.save();
				System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": New (2.) document (" + docTemp2.getUniversalID() + ") saved in database: '" + docTemp1.getParentDatabase().getFilePath() + "' on server '" + docTemp2.getParentDatabase().getServer() + "'");
				
				
				docTemp1.recycle();
				docTemp2.recycle();
				docTemp1 = null;
				docTemp2 = null;
			}catch(NotesException ne){
				ExceptionZZZ ez = new ExceptionZZZ(ne.text, iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;	
			}
		}catch(ExceptionZZZ ez){
			fail("Method throws an exception." + ez.getMessageLast());	
		}
	}

	public void testRemoveDocumentFromCategory(){
		try{
			try{
				
				//#################  ERSTE KATEGORISIERUNG
		//		zuerst ggf. das dokument erstellen
				docTemp1 = initDocument(docTemp1, ReflectCodeZZZ.getMethodCurrentName());
				
				//Nun manuell die Kategoriewerte f�llen				
				Vector vecCategory = new Vector();
				vecCategory.add("CarrierTitle");
				vecCategory.add("CarrierId");
				vecCategory.add("BeispielF�rMehrfachwert");
				String stemp = DocumentCategorizerZZZ.sFIELD_PREFIX_CATEGORY_META + objKernelNotes.getApplicationKeyCurrent();
				docTemp1.replaceItemValue(stemp, vecCategory);  //Category + ApplikationKey als Itemname
				
				Vector vecTemp = new Vector();
				vecTemp.add("a");
				vecTemp.add("b");
				docTemp1.replaceItemValue("BeispielF�rMehrfachwert", vecTemp);
				docTemp1.replaceItemValue("catValBeispielF�rMehrfachwert", vecTemp);
				
				docTemp1.replaceItemValue("CarrierID", "080206#1");
				docTemp1.replaceItemValue("catValCarrierID", "080206#1");
				
				docTemp1.replaceItemValue("CarrierTitle", "Filme 1");
				docTemp1.replaceItemValue("catValCarrierTitle","Filme 1");
				
				
				//ERSTES Kategoriserungsobjekt erstellen
				DocumentCategorizerZZZ objCat1 = new DocumentCategorizerZZZ(objKernelNotes, docTemp1);
				
				//Dokument, das kategorisiert werde soll erstellen und kategorisierung durchf�hren
				docTemp2 = initDocument(docTemp2, ReflectCodeZZZ.getMethodCurrentName());
				boolean bIsCategorized = objCat1.addDocumentAsCategory(docTemp2);
				assertTrue(bIsCategorized);
				
				//#####################  ZWEITE KATEGORISIERUNG
//				zuerst ggf. das dokument erstellen
				docTemp3 = initDocument(docTemp3, ReflectCodeZZZ.getMethodCurrentName());
				
				//Nun manuell die Kategoriewerte f�llen				
				Vector vecCategory3 = new Vector();
				vecCategory3.add("MyCat1");
				vecCategory3.add("MyCat2");
				vecCategory3.add("MyCat3");
				stemp = DocumentCategorizerZZZ.sFIELD_PREFIX_CATEGORY_META + objKernelNotes.getApplicationKeyCurrent();
				docTemp3.replaceItemValue(stemp, vecCategory3);  //Category + ApplikationKey als Itemname
				
				Vector vecTemp2 = new Vector();
				vecTemp2.add("a");
				vecTemp2.add("b");
				docTemp3.replaceItemValue("MyCat1", vecTemp2);
				docTemp3.replaceItemValue("catValMyCat1", vecTemp2);
				
				docTemp3.replaceItemValue("MyCat2", "Wert von MyCat2");
				docTemp3.replaceItemValue("catValMyCat2", "Wert von MyCat2");
				
				docTemp3.replaceItemValue("MyCat3", "Wert von MyCat3");
				docTemp3.replaceItemValue("catValMyCat3","Wert von MyCat3");
				
				
				//ZWEITES Kategorisierungsobjekt erstellen
				DocumentCategorizerZZZ objCat3 = new DocumentCategorizerZZZ(objKernelNotes, docTemp3);
				
				//Dokument, das kategorisiert werde soll erstellen und kategorisierung durchf�hren
				bIsCategorized = objCat3.addDocumentAsCategory(docTemp2);
				assertTrue(bIsCategorized);
				
				
				
				//##########  Speichern
				docTemp1.save();
				System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": New (1.) document (" + docTemp1.getUniversalID() + ") saved in database: '" + docTemp1.getParentDatabase().getFilePath() + "' on server '" + docTemp1.getParentDatabase().getServer() + "'");
				
				docTemp2.save();
				System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": New (2.) document (" + docTemp2.getUniversalID() + ") saved in database: '" + docTemp2.getParentDatabase().getFilePath() + "' on server '" + docTemp2.getParentDatabase().getServer() + "'");
			
				docTemp3.save();
				System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": New (3.) document (" + docTemp3.getUniversalID() + ") saved in database: '" + docTemp3.getParentDatabase().getFilePath() + "' on server '" + docTemp3.getParentDatabase().getServer() + "'");
		

				//#############################################
				//#### Dokument 2 aus der ERSTEN Kategorisierung rausnehmen
				boolean bDoneDoc = objCat1.removeDocumentFromCategory(docTemp2);
				assertTrue(bDoneDoc);
				
				docTemp2.save();
				System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": Ge�ndertes (2.) document (" + docTemp2.getUniversalID() + ") saved in database: '" + docTemp2.getParentDatabase().getFilePath() + "' on server '" + docTemp2.getParentDatabase().getServer() + "'");

					
				
			}catch(NotesException ne){
				ExceptionZZZ ez = new ExceptionZZZ(ne.text, iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;	
			}
		}catch(ExceptionZZZ ez){
			fail("Method throws an exception." + ez.getMessageLast());	
		}
	}
	
	public void testChangeDocumentCategory(){
		try{
			try{
						//#################  ERSTE KATEGORISIERUNG
						//		zuerst ggf. das dokument erstellen
						docTemp1 = initDocument(docTemp1, ReflectCodeZZZ.getMethodCurrentName());
						
						//Nun manuell die Kategoriewerte f�llen				
						Vector vecCategory = new Vector();
						vecCategory.add("CarrierTitle");
						vecCategory.add("CarrierId");
						vecCategory.add("BeispielF�rMehrfachwert");
						String stemp = DocumentCategorizerZZZ.sFIELD_PREFIX_CATEGORY_META + objKernelNotes.getApplicationKeyCurrent();
						docTemp1.replaceItemValue(stemp, vecCategory);  //Category + ApplikationKey als Itemname
						
						Vector vecTemp = new Vector();
						vecTemp.add("a");
						vecTemp.add("b");
						docTemp1.replaceItemValue("BeispielF�rMehrfachwert", vecTemp);
						docTemp1.replaceItemValue("catValBeispielF�rMehrfachwert", vecTemp);
						
						docTemp1.replaceItemValue("CarrierID", "080206#1");
						docTemp1.replaceItemValue("catValCarrierID", "080206#1");
						
						docTemp1.replaceItemValue("CarrierTitle", "Filme 1");
						docTemp1.replaceItemValue("catValCarrierTitle","Filme 1");
						
						docTemp1.save();
						System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": New (1.) document (" + docTemp1.getUniversalID() + ") saved in database: '" + docTemp1.getParentDatabase().getFilePath() + "' on server '" + docTemp1.getParentDatabase().getServer() + "'");
						
						
						//ERSTES Kategoriserungsobjekt erstellen
						DocumentCategorizerZZZ objCat1 = new DocumentCategorizerZZZ(objKernelNotes, docTemp1);
						
						//Dokument, das kategorisiert werde soll erstellen und kategorisierung durchf�hren
						docTemp2 = initDocument(docTemp2, ReflectCodeZZZ.getMethodCurrentName());
						boolean bIsCategorized = objCat1.addDocumentAsCategory(docTemp2);
						assertTrue(bIsCategorized);
						
						
						//++++++++++++++++++++++++++++++++++++++
						//Neuer Wert 
						Vector vecValueNew = new Vector();
						vecValueNew.add("newA");						
						objCat1.updateCategory("BeispielF�rMehrfachwert", vecValueNew);
						 
						Document docTest = objCat1.getDocument();
						Vector vecValueUpdateTest1 = docTest.getItemValue(ICategoryConstantZZZ.sFIELD_PREFIX_CATEGORY_VALUE +"BeispielF�rMehrfachwert");
						assertTrue(vecValueNew.equals(vecValueUpdateTest1));
						Vector vecValueUpdateTest2 = docTest.getItemValue("BeispielF�rMehrfachwert");
						assertTrue(vecValueNew.equals(vecValueUpdateTest2));
						
						
							
						boolean bIsUpdated = objCat1.updateDocumentCategory(docTemp2, "BeispielF�rMehrfachwert");
						assertTrue(bIsUpdated);
						
						Vector vecValueTest = docTemp2.getItemValue(DocumentCategorizerZZZ.sFIELD_PREFIX_CATEGORY + "BeispielF�rMehrfachwert");
						assertTrue(vecValueNew.equals(vecValueTest));
						
						
//						##########  Speichern
						docTemp1.save();
						docTemp2.save();
						System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": New (2.) document (" + docTemp2.getUniversalID() + ") saved in database: '" + docTemp2.getParentDatabase().getFilePath() + "' on server '" + docTemp2.getParentDatabase().getServer() + "'");
					
						
			}catch(NotesException ne){
				ExceptionZZZ ez = new ExceptionZZZ(ne.text, iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;	
			}
		}catch(ExceptionZZZ ez){
			fail("Method throws an exception." + ez.getMessageLast());	
		}
	}
	
}
