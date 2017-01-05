package use.via.server;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.commons.collections.map.MultiValueMap;

import custom.zNotes.kernel.KernelNotesZZZ;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.Item;
import lotus.domino.NotesException;
import lotus.domino.View;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.abstractList.VectorZZZ;
import basic.zBasic.util.datatype.string.StringArrayZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zNotes.document.DocumentZZZ;
import basic.zNotes.kernel.KernelNotesUseObjectZZZ;

public class DocumentCategorizerZZZ extends KernelNotesUseObjectZZZ implements ICategoryConstantZZZ{
	Document doc2beProcessed=null;
	MultiValueMap hmCategoryData = null;
	/*  hat z.B. das Format
 				MultiValueMap hmCategories = new MultiValueMap();
				hmCategories.put("Category1", "Cat1Value1");
				hmCategories.put("Category2", "Cat2Value1");
				hmCategories.put("Category1", "Cat1Value2");
	 */

	public DocumentCategorizerZZZ(){
		//Wird benötigt, um einfach so per ReflectionAPI mal ein Objekt zu erzeugen.
	}
	public DocumentCategorizerZZZ(KernelNotesZZZ objKernelNotes){
		super(objKernelNotes);
	}
	public DocumentCategorizerZZZ(KernelNotesZZZ objKernelNotes, Document doc2beProcessed){
		super(objKernelNotes);
		this.setDocument(doc2beProcessed);
	}
	
	public static void appendFieldAllToBecomeCategorizable(String sKeyApplication, String sDocumentAlias, Vector vecCategoryAllUsed, Document doc2beProcessed) throws ExceptionZZZ{
		
		main:{
		try{
		if(StringZZZ.isEmpty(sKeyApplication)){
			ExceptionZZZ ez = new ExceptionZZZ("Application-String", iERROR_PARAMETER_MISSING,DocumentCategorizerZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}
		if(StringZZZ.isEmpty(sDocumentAlias)){
			ExceptionZZZ ez = new ExceptionZZZ("DocumentAlias-String", iERROR_PARAMETER_MISSING,DocumentCategorizerZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}
		if(doc2beProcessed==null){
			ExceptionZZZ ez = new ExceptionZZZ("Document to be processed", iERROR_PARAMETER_MISSING,DocumentCategorizerZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}
		if(vecCategoryAllUsed==null){
			ExceptionZZZ ez = new ExceptionZZZ("Vector with category-Strings", iERROR_PARAMETER_MISSING,DocumentCategorizerZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}
		if(vecCategoryAllUsed.isEmpty()) break main; //Es gibt keine CategorisierungsFelder
		
		
		//####################################################
		//1. Alias 			
		doc2beProcessed.removeItem(DocumentCategorizerZZZ.sFIELD_PREFIX_ALIAS + sKeyApplication);

		//Erst muss der Wert mal gesetzt werden, dann kann er mit dieser Methode ausgelesen werden    String sAlias = DocumentCategorizerZZZ.readAlias(sKeyApplication, doc2beProcessed);
		doc2beProcessed.appendItemValue(DocumentCategorizerZZZ.sFIELD_PREFIX_ALIAS + sKeyApplication, sDocumentAlias);
		
		
		//2. Category - MetaDaten
		doc2beProcessed.removeItem(DocumentCategorizerZZZ.sFIELD_PREFIX_CATEGORY_META + sKeyApplication);
		
		doc2beProcessed.appendItemValue(DocumentCategorizerZZZ.sFIELD_PREFIX_CATEGORY_META + sKeyApplication, vecCategoryAllUsed);
				
		//3. CatVal setzen, d.h. aus den Feldern die Werte lesen und in das CatVal-Feld setzen
		Iterator itCategory = vecCategoryAllUsed.iterator();
		while(itCategory.hasNext()){
			String sCategory = (String) itCategory.next();
			if(!StringZZZ.isEmpty(sCategory)){
				Vector vecValue = doc2beProcessed.getItemValue(sCategory);
				if(!vecValue.isEmpty()){
					doc2beProcessed.replaceItemValue(DocumentCategorizerZZZ.sFIELD_PREFIX_CATEGORY_VALUE + sCategory, vecValue);
				}
			}
		}
		
		//4. objRef setzen
		doc2beProcessed.removeItem(DocumentCategorizerZZZ.sFIELD_PREFIX_REFERENCE + sKeyApplication);
		
		doc2beProcessed.appendItemValue(DocumentCategorizerZZZ.sFIELD_PREFIX_REFERENCE + sKeyApplication, doc2beProcessed.getUniversalID());
	}catch(NotesException ne){
		ExceptionZZZ ez = new ExceptionZZZ("NotesException: " + ne.text, iERROR_RUNTIME, DocumentCategorizerZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
		throw ez;
	}		
	}//end main:
	}
	

	/** Trage in dem Hauptdokument in Feld, das als Kategoriefeld für diesen Dokumenttyp vorgesehen ist, 
	*   den Wert ein, der in dem Dokument des Dokumenttyps steht im Feld .sFIELD_PREFIX_CATEGORY + ApplicationKey (also z.B. CategoryVIA)
	*   
	*    Ziel ist es, in einer Ansicht die erste Spalte kategoriesiert zu haben, und zwar z.B. nach der folgenden Formel:
	*    
	*    					@If(@LowerCase(AliasVIA) ="carrier"; CatValCarrierTitleVIA;CategoryCarrierTitleVIA + "\\" + CatValVIA)
	*    
	*    
	*    Was bewirkt, das in der Ansicht oben das 'carrier' dokument steht
	*    und darunter z.B. die Dateien-Dokumente,
	*    in denen das Feld CategoryCarrierTitleVIA den Eintrag enthält, der im Feld CatValCarrierTitelVIA des Carrier-Dokuments steht. 
	*    
	* @param doc2AddAsCategory
	* @throws ExceptionZZZ
	* 
	* Rückgabewert true, wenn eine kategorisierung vorliegt. D.h. Felder geändert wurden.
	* 
	* lindhaueradmin; 16.02.2007 09:50:37
	*/
	public  boolean addDocumentAsCategory(Document doc2becomeCategorized ) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
		try{
			
			if(doc2becomeCategorized==null){
				ExceptionZZZ ez = new ExceptionZZZ("Document as target of the category values ", iERROR_PARAMETER_MISSING,DocumentCreatorZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			Document docCategorySource = this.getDocument();
			if(docCategorySource==null){
				ExceptionZZZ ez = new ExceptionZZZ("Document as source of the category values (.getDocumentCurrent()", iERROR_PROPERTY_MISSING,DocumentCreatorZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			MultiValueMap objHmData = this.getCategoryMap();
			if(objHmData==null){
				KernelNotesZZZ objKernelNotes = this.getKernelNotesObject();
				objHmData = DocumentCategorizerZZZ.computeDocumentCategoryMap(objKernelNotes, docCategorySource);				
			}
			if(objHmData.totalSize()<= 0) break main;
			
						
			//20080326 In dem Dokument, das kategorisiert wird, die Quelle der Kategorisierung festhalten
			String sAlias = docCategorySource.getItemValueString(DocumentCategorizerZZZ.sFIELD_PREFIX_ALIAS + this.getKernelNotesObject().getApplicationKeyCurrent());
			String sRef = docCategorySource.getItemValueString(DocumentCategorizerZZZ.sFIELD_PREFIX_REFERENCE + this.getKernelNotesObject().getApplicationKeyCurrent());
			Vector vecCategorySource = new Vector();
		
			
			Set objKeySet = objHmData.keySet();
			Iterator it = objKeySet.iterator();
			while(it.hasNext()){  //Äußerste Schleife, alle Feldnamen durchlaufen
				String sKey =(String)  it.next();  //Der Notes-Item-Name
				if(!StringZZZ.isEmpty(sKey)){
					Collection colValue = objHmData.getCollection(sKey);
					Iterator itCol = colValue.iterator();					
					Vector vecVal = new Vector();
					while(itCol.hasNext()){ //Innere Schleife, alle Feldwerte durchlaufen
						String sValue = (String)itCol.next();
						vecVal.add(sValue);							
					}
					
					//Den gefüllten Vector nun an das Dokument hängen. 
					if(vecVal.size()>= 1){
						
						Item item = doc2becomeCategorized.getFirstItem(DocumentCategorizerZZZ.sFIELD_PREFIX_CATEGORY + sKey);			
						if(item==null){
							Vector vecValueUnique = VectorZZZ.unique(vecVal);    //!!! Kategorieen brauchen nur einzigartig zu sein
							
							item = doc2becomeCategorized.replaceItemValue(DocumentCategorizerZZZ.sFIELD_PREFIX_CATEGORY + sKey, vecValueUnique);
							item.setSummary(true);
						}else{
							Vector vecValueOld = item.getValues();
							Vector vecValueNew = VectorZZZ.append(vecValueOld,vecVal);
							Vector vecValueUnique = VectorZZZ.unique(vecValueNew);    //!!! Kategorieen brauchen nur einzigartig zu sein
							
							doc2becomeCategorized.replaceItemValue(DocumentCategorizerZZZ.sFIELD_PREFIX_CATEGORY + sKey,vecValueUnique);
						}
					
						//Die Kategorisierungsquelle im dokument vermerken. (z.B. damit eine Wertänderung übernommen werden kann. Aber nur, wenn auch objAlias ... gefüllt ist.
						if(!StringZZZ.isEmpty(sAlias)){ 
							vecCategorySource.add(sAlias + "#" + sRef + "#" + sKey);
						}
					
					}// end if vecVal.size() >= 1
						
				}// end if(!StringZZZ.isempty(sKey))

		}//end while it.hastNext()
		
		
		//Falls objRef gefüllt ist, die Kategorisierungsquellen erweitern
		if(!StringZZZ.isEmpty(sRef)){ 
			
			String sCategorySource = DocumentCategorizerZZZ.sFIELD_PREFIX_CATEGORYSOURCE_META + this.getKernelNotesObject().getApplicationKeyCurrent(); //Name des Feldes in dem die KategoryQuellen enthalten sind.				
			Item item = doc2becomeCategorized.getFirstItem(sCategorySource);
			if(item==null){							
				Vector vecValue = VectorZZZ.unique(vecCategorySource);					
				item = doc2becomeCategorized.replaceItemValue(sCategorySource, vecValue);
				item.setSummary(true);									
			}else{
				Vector vecValueOld = item.getValues();
				Vector vecValueNew = VectorZZZ.append(vecValueOld,vecCategorySource);
				Vector vecValue = VectorZZZ.unique(vecValueNew);					
				doc2becomeCategorized.replaceItemValue(sCategorySource, vecValue);																
			}
		}//end if !StringZZZ.isEmpty(sAlias)
		
		//Returnwert setzen
		bReturn = true;
			
		}catch(NotesException ne){
			ExceptionZZZ ez = new ExceptionZZZ("NotesException: " + ne.text, iERROR_RUNTIME, DocumentCreatorZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}
		
	}//end main
		return bReturn;
		
	}
	
	/** Entferne das intern gehaltene Dokument aus der Kategorisierung des Dokuments das an diese Methode übergeben wird. 
	 *   D.h. entferne die Kategorisierungswerte (s. übergebenes Dokument) aus den Kategorisierungsfeldern.
	 *  
	* @param docCategorySource
	* @return
	* @throws ExceptionZZZ
	* 
	* lindhaueradmin; 29.03.2008 13:59:08
	 */
	public boolean removeDocumentFromCategory(Document doc2DeCategorize) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			try{
				if(doc2DeCategorize==null){
					ExceptionZZZ ez = new ExceptionZZZ("Document as target of the category values (.getDocumentCurrent()", iERROR_PROPERTY_MISSING,DocumentCategorizerZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
			
				Document docCategorySource = this.getDocument();
				if(docCategorySource==null){
					ExceptionZZZ ez = new ExceptionZZZ("Document as source of the category values", iERROR_PARAMETER_MISSING,DocumentCategorizerZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
					
				//##################################
				//Alias und objRef aus der Kategorisierungsquelle holen
				String sAlias = docCategorySource.getItemValueString(DocumentCategorizerZZZ.sFIELD_PREFIX_ALIAS + this.getKernelNotesObject().getApplicationKeyCurrent());
				String sRef = docCategorySource.getItemValueString(DocumentCategorizerZZZ.sFIELD_PREFIX_REFERENCE + this.getKernelNotesObject().getApplicationKeyCurrent());
				
				//Wert aus dem Kategegorisierungsquellen-Referenzfeld holen
				Vector vecSource = doc2DeCategorize.getItemValue(DocumentCategorizerZZZ.sFIELD_PREFIX_CATEGORYSOURCE_META + this.getKernelNotesObject().getApplicationKeyCurrent());
				if(vecSource.isEmpty()) break main;
				
				//Nur die Werte mit dem sAlias # sRef berücksichtigen
				Vector vecRef = new Vector();
				Iterator it = vecSource.iterator();
				while(it.hasNext()){
					String stemp = (String) it.next();
					if(stemp.startsWith(sAlias + "#" + sRef)){
						vecRef.add(stemp);
					}
				}
				
				//Vectorinhalt in ein Stringarray überführen
				String[] sa = new String[vecRef.size()];
				vecRef.toArray(sa);
				
				boolean bDoneDoc = false;
				for(int icount =  0; icount <= sa.length-1; icount ++){
					String sSource = sa[icount];
					String sField = StringZZZ.word(sSource, "#", 3);
					if(!StringZZZ.isEmpty(sField)){
						//Hat das zu dekategorisierende Dokument überhaupt dieses Kategorisierungsfeld
						boolean bGoonField = false;
						Vector vecValueCat = null;
						String sFieldCat = DocumentCategorizerZZZ.sFIELD_PREFIX_CATEGORY + sField;
						if(doc2DeCategorize.hasItem(sFieldCat)){
							vecValueCat = doc2DeCategorize.getItemValue(sFieldCat);
							if(vecValueCat.isEmpty()){
								bGoonField = false;
							}else{
								bGoonField = true;
							}
						}else{
							bGoonField = false;
						}
						
						boolean bDoneField = false;
						if(bGoonField){
							//Feldwert aus dem übergebenem Dokument auslesen
							Vector vecValueRef = docCategorySource.getItemValue(sField);						
								Iterator itValueRef = vecValueRef.iterator();
								while(itValueRef.hasNext()){
									String s2Remove = (String) itValueRef.next();
									if(vecValueCat.contains(s2Remove)){
										vecValueCat.remove(s2Remove);
										bDoneField = true;
									}
								}						
						}//end if bGoon
						
						
						if(bDoneField){
							//Falls ein Wert verändert wurde dies zurückschreiben, bzw. sogar das Feld entfernen.
							if(vecValueCat.isEmpty()){
								doc2DeCategorize.removeItem(sFieldCat);
							}else{
								doc2DeCategorize.replaceItemValue(sFieldCat, vecValueCat);
							}
							
							//Auch die Zeile mit dem Alias # UniversalId # Kategorisierungsfeld entfernen
							vecSource.remove(sSource);
							if(vecSource.isEmpty()){
								doc2DeCategorize.removeItem(DocumentCategorizerZZZ.sFIELD_PREFIX_CATEGORYSOURCE_META + this.getKernelNotesObject().getApplicationKeyCurrent());
							}else{
								doc2DeCategorize.replaceItemValue(DocumentCategorizerZZZ.sFIELD_PREFIX_CATEGORYSOURCE_META + this.getKernelNotesObject().getApplicationKeyCurrent(), vecSource);
							}
							
							 //!!! Rückgabewert, es wurde etwas an dem Dokument verändert
							bReturn = true;
						}
					}//end if !isempty(sField)
				}//end for
				
			}catch(NotesException ne){
				ExceptionZZZ ez = new ExceptionZZZ("NotesException: " + ne.text, iERROR_RUNTIME, DocumentCategorizerZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
		}//end main
		return bReturn;
	}
	
	
	
	/** Aktualisiere den Kategorisierungswert des Dokuments mit dem Wert, welches das intern gespeicherte Dokument hat.
	* @param doc2beChanged
	* @param sFieldname
	* @return
	* @throws ExceptionZZZ
	* 
	* lindhauer; 22.04.2008 09:26:12
	 */
	public boolean updateDocumentCategory(Document doc2beChanged, String sFieldname) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			try{
				if(doc2beChanged==null){
					ExceptionZZZ ez = new ExceptionZZZ("Document as target of the category values (.getDocumentCurrent()", iERROR_PROPERTY_MISSING,DocumentCategorizerZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
			
				Document docCategorySource = this.getDocument();
				if(docCategorySource==null){
					ExceptionZZZ ez = new ExceptionZZZ("Document as source of the category values", iERROR_PARAMETER_MISSING,DocumentCategorizerZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}				
				if(!this.isCategoryField(sFieldname)){
					ExceptionZZZ ez = new ExceptionZZZ("Fieldname seems to be no relevant category field.", iERROR_PARAMETER_VALUE,DocumentCategorizerZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				//##################################				
				//Wert des Feldes aus dem intern gespeicherten Dokument lesen: CatVal.....				
				Vector vecCategoryValueNew = docCategorySource.getItemValue(ICategoryConstantZZZ.sFIELD_PREFIX_CATEGORY_VALUE+ sFieldname);
												
				//Diesen ausgelesenen Wert an das upzudatende Dokument übergeben
				bReturn = this.changeDocumentCategory(doc2beChanged, sFieldname, vecCategoryValueNew);
				
				/*//DEBUG: Ausgeben der neuen Werte
				Vector vecValueTest = doc2beChanged.getItemValue(DocumentCategorizerZZZ.sFIELD_PREFIX_CATEGORY + "BeispielFürMehrfachwert");
				Iterator it = vecValueTest.iterator();
				while(it.hasNext()){
					System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "#" + (String) it.next());
				}
				*/
				
				
			}catch(NotesException ne){
				ExceptionZZZ ez = new ExceptionZZZ("NotesException: " + ne.text, iERROR_RUNTIME, DocumentCategorizerZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
		}//End main
		return bReturn;
	}
	
	
	/** Ändere die Kategory des Dokuments auf den übergebenen Wert ab.
	 *   Dabei wird der übergebene Wert berücksichtigt. Der im intern gehaltene Kategroiseirungswert ist für diese Methode nicht wichtig.
	 *   
	 *   
	* @param doc2beChanged
	* @return
	* 
	* lindhaueradmin; 22.04.2008 06:29:53
	 */
	private boolean changeDocumentCategory(Document doc2beChanged, String sFieldname, Vector vecCategoryValueNew) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			try{
				if(doc2beChanged==null){
					ExceptionZZZ ez = new ExceptionZZZ("Document as target of the category values (.getDocumentCurrent()", iERROR_PROPERTY_MISSING,DocumentCategorizerZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
			
				Document docCategorySource = this.getDocument();
				if(docCategorySource==null){
					ExceptionZZZ ez = new ExceptionZZZ("Document as source of the category values", iERROR_PARAMETER_MISSING,DocumentCategorizerZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				if(!this.isCategoryField(sFieldname)){
					ExceptionZZZ ez = new ExceptionZZZ("Fieldname seems to be no relevant category field.", iERROR_PARAMETER_VALUE,DocumentCategorizerZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}

				//Merke: Wenn der Wert ein Leerstring/Null ist, dann wird diese Kategorisierung entfernt
				
				
				
//				##################################
				//Alias und objRef aus der Kategorisierungsquelle holen
				String sAlias = docCategorySource.getItemValueString(DocumentCategorizerZZZ.sFIELD_PREFIX_ALIAS + this.getKernelNotesObject().getApplicationKeyCurrent());
				String sRef = docCategorySource.getItemValueString(DocumentCategorizerZZZ.sFIELD_PREFIX_REFERENCE + this.getKernelNotesObject().getApplicationKeyCurrent());
				
				//Wert aus dem Kategegorisierungsquellen-Referenzfeld holen
				Vector vecSource = doc2beChanged.getItemValue(DocumentCategorizerZZZ.sFIELD_PREFIX_CATEGORYSOURCE_META + this.getKernelNotesObject().getApplicationKeyCurrent());
				if(vecSource.isEmpty()) break main;
				
				//Nur die Werte mit dem sAlias # sRef berücksichtigen
				Vector vecRef = new Vector();
				Iterator it = vecSource.iterator();
				while(it.hasNext()){
					String stemp = (String) it.next();
					if(stemp.startsWith(sAlias + "#" + sRef)){
						vecRef.add(stemp);
					}
				}
				
				//Vectorinhalt in ein Stringarray überführen
				String[] sa = new String[vecRef.size()];
				vecRef.toArray(sa);
					
				//+++ Das passende Feld herausfinden
				for(int icount =  0; icount <= sa.length-1; icount ++){
					String sSource = sa[icount];
					String sField = StringZZZ.word(sSource, "#", 3);
					if(!StringZZZ.isEmpty(sField)){
						if(sField.equalsIgnoreCase(sFieldname)){
							if(vecCategoryValueNew==null){
								DocumentZZZ.itemInstanceAllRemove(this.getKernelNotesObject().getSession(), doc2beChanged, DocumentCategorizerZZZ.sFIELD_PREFIX_CATEGORY + sFieldname);
							}else if(vecCategoryValueNew.isEmpty()){
								DocumentZZZ.itemInstanceAllRemove(this.getKernelNotesObject().getSession(), doc2beChanged, DocumentCategorizerZZZ.sFIELD_PREFIX_CATEGORY + sFieldname);
							}else{					
								DocumentZZZ.itemInstanceAllRemove(this.getKernelNotesObject().getSession(), doc2beChanged, DocumentCategorizerZZZ.sFIELD_PREFIX_CATEGORY + sFieldname);
								doc2beChanged.replaceItemValue(DocumentCategorizerZZZ.sFIELD_PREFIX_CATEGORY + sFieldname, vecCategoryValueNew);
							}			
							
							 //!!! Rückgabewert, es wurde etwas an dem Dokument verändert
							bReturn = true;
						}
					}//end if !isempty(sField)
				}//end for
				
				/*//DEBUG: Ausgeben der neuen Werte
				Vector vecValueTest = doc2beChanged.getItemValue(DocumentCategorizerZZZ.sFIELD_PREFIX_CATEGORY + sFieldname);
				Iterator itTest = vecValueTest.iterator();
				while(itTest.hasNext()){
					System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "#" + (String) itTest.next());
				}
				*/
								
			}catch(NotesException ne){
				ExceptionZZZ ez = new ExceptionZZZ("NotesException: " + ne.text, iERROR_RUNTIME, DocumentCreatorZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
				
		}//End main
		return bReturn;
	}
	
	
	/** Ändere den Kategorisierungswert für das intern gehaltene Dokument. 
	 *   Merke: Dieser Wert wird noch nicht an die damit categoriesierten Dokumente weitergegeben.
	 *               Es wird lediglich zusätzlich zu dem eigentlichen Feld das Feld "catVal + Feldname" aktualisiert. 
	* @param sFieldname
	* @param vecValueNew
	* @return
	* 
	* lindhauer; 22.04.2008 09:49:48
	 * @throws ExceptionZZZ 
	 */
	public boolean updateCategory(String sFieldname, Vector vecValueNew) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{		
			try{
				Document docCategorySource = this.getDocument();
				if(docCategorySource==null){
					ExceptionZZZ ez = new ExceptionZZZ("Document as source of the category values", iERROR_PROPERTY_MISSING,DocumentCategorizerZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				if(!this.isCategoryField(sFieldname)){
					ExceptionZZZ ez = new ExceptionZZZ("Fieldname seems to be no relevant category field.", iERROR_PARAMETER_VALUE,DocumentCategorizerZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				
				
				//######################################################
				boolean bRemove = false;
				if(vecValueNew==null){
					bRemove =true;
				}else if(vecValueNew.isEmpty()){
					bRemove = true;
				}
				
				if(bRemove){
					DocumentZZZ.itemInstanceAllRemove(this.getKernelNotesObject().getSession(), docCategorySource, sFieldname);
					bReturn = DocumentZZZ.itemInstanceAllRemove(this.getKernelNotesObject().getSession(), docCategorySource, ICategoryConstantZZZ.sFIELD_PREFIX_CATEGORY_VALUE + sFieldname);
				}else{
					DocumentZZZ.itemInstanceAllRemove(this.getKernelNotesObject().getSession(), docCategorySource, sFieldname);					
					docCategorySource.replaceItemValue(sFieldname, vecValueNew);
					
					DocumentZZZ.itemInstanceAllRemove(this.getKernelNotesObject().getSession(), docCategorySource, ICategoryConstantZZZ.sFIELD_PREFIX_CATEGORY_VALUE + sFieldname);					
					docCategorySource.replaceItemValue(ICategoryConstantZZZ.sFIELD_PREFIX_CATEGORY_VALUE + sFieldname, vecValueNew);
					bReturn = true;
				}											
			}catch(NotesException ne){
				ExceptionZZZ ez = new ExceptionZZZ("NotesException: " + ne.text, iERROR_RUNTIME, DocumentCreatorZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
		}//end main
		return bReturn;		
	}




	/* (non-Javadoc)
	 * @see use.via.server.ICategorizableZZZ#readDocumentAlias(lotus.domino.Document)
	 */
	public static String readAlias(String sKeyApplication, Document docToReadFrom) throws ExceptionZZZ{
		String sReturn = null;
		main:{
			try{
				if(docToReadFrom==null){
					ExceptionZZZ ez = new ExceptionZZZ("DocumentToReadFrom", iERROR_PARAMETER_MISSING, DocumentCreatorZZZ.class.getName());
					throw ez;
				}
				if(StringZZZ.isEmpty(sKeyApplication)){
					sReturn = docToReadFrom.getItemValueString(DocumentCategorizerZZZ.sFIELD_PREFIX_ALIAS);					
				}else{
					sReturn = docToReadFrom.getItemValueString(DocumentCategorizerZZZ.sFIELD_PREFIX_ALIAS + sKeyApplication);
				}
				
			
			}catch(NotesException ne){
				ExceptionZZZ ez = new ExceptionZZZ("NotesException: " + ne.text, iERROR_RUNTIME, DocumentCreatorZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
		}
		return sReturn;
	}

		/**Gib den Feldinhalt mit den Metainformationen zurück, der alle Feldnamen, die zur Kategorisierung verwendet werden beinhaltet.
		* @param docToReadFrom
		* @return
		* @throws ExceptionZZZ
		* 
		* lindhauer; 27.04.2008 07:03:50
		 */
		public Vector readDocumentCategory(Document docToReadFrom) throws ExceptionZZZ{
			Vector  vecReturn = null;
			main:{
				try{
					if(docToReadFrom==null){
						ExceptionZZZ ez = new ExceptionZZZ("DocumentToReadFrom", iERROR_PARAMETER_MISSING, DocumentCreatorZZZ.class.getName());
						throw ez;
					}
					KernelNotesZZZ objKernelNotes = this.getKernelNotesObject();
					String sKeyApplication = objKernelNotes.getApplicationKeyCurrent();	
					vecReturn = docToReadFrom.getItemValue(DocumentCategorizerZZZ.sFIELD_PREFIX_CATEGORY_META + sKeyApplication);					
				}catch(NotesException ne){
					ExceptionZZZ ez = new ExceptionZZZ("NotesException: " + ne.text, iERROR_RUNTIME, DocumentCategorizerZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
			}
			return vecReturn;
		}
		
/** Sammle alle Dokumente zusammen, die das interne Dokument als Quelle für die Kategorie verwenden.
* @param db2SearchIn
* @return
* @throws ExceptionZZZ
* 
* lindhauer; 27.04.2008 07:05:46
 */
public DocumentCollection searchDocumentDependingAll(Database db2SearchIn) throws ExceptionZZZ {
	DocumentCollection colReturn = null;
	main:{		
		try{	
			KernelNotesZZZ objKernelNotes = this.getKernelNotesObject();
			if (objKernelNotes==null){
				ExceptionZZZ ez = new ExceptionZZZ("No KernelNotes-object available", iERROR_PROPERTY_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez; 				
			}
			
			//1. Holen des aktuellen Dokuments und der zu verwendenden Refenz-DocId
			Document docCurrent = this.getDocument();
			if (docCurrent==null){
				ExceptionZZZ ez = new ExceptionZZZ("No internal document-object available", iERROR_PROPERTY_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez; 				
			}
			String sDocId = this.getDocReferenceIdCurrent();
			
			Database db2Search=null;
			if(db2SearchIn==null){
				db2Search=objKernelNotes.getDBApplicationCurrent();
				if(db2Search==null){
					ExceptionZZZ ez = new ExceptionZZZ("No database provided", iERROR_PARAMETER_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez; 		
				}
			}else{
				db2Search = db2SearchIn;
			}
			if(!db2Search.isOpen()){
				ExceptionZZZ ez = new ExceptionZZZ("Unable to open database '" + db2Search.getTitle() + "'", iERROR_PARAMETER_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez; 	
			}
			
			
			
			
			
//			2. Dokumente finden, die dieses Dokument "als Kategorie Referenzieren" (d.h. im feld 'objCatRefSourceVIA' an der entsprechenden Postion die DocId stehen haben)
			//Beispielsweise folgende Zeile Movie#A5521DE7C0E65193C125741D002B2D6E#MovieTitle"
			View viwCatRef = db2Search.getView(ICategoryConstantZZZ.sVIEW_LOOKUP_CATEGORY_REFERENCE + objKernelNotes.getApplicationKeyCurrent());
			if(viwCatRef == null){
				ExceptionZZZ ez = new ExceptionZZZ("No view found with the name '" + ICategoryConstantZZZ.sVIEW_LOOKUP_CATEGORY_REFERENCE + "'", iERROR_ZFRAME_DESIGN, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez; 
			}
			
			
			//+++ Hat das Dokument überhaupt etwas zum kategoriesieren ?
			Vector vecCatValue = docCurrent.getItemValue( ICategoryConstantZZZ.sFIELD_PREFIX_CATEGORY_META + objKernelNotes.getApplicationKeyCurrent()); //das sind alle Feldnamen, die zur Kategorsisierung herangezogen werden sollen
			
//			 Es reicht der erste Eintrag ! (wahrscheinlich)
			String sFieldFirst = (String) vecCatValue.get(0);
			if(StringZZZ.isEmpty(sFieldFirst)) {
				this.getKernelNotesLogObject().writeLog("Internal Document has no fields which are used for categorizing.", 3);
				break main;	
			}
			
			
			//+++ Keys zusammenbauen					
			String sAlias = docCurrent.getItemValueString( ICategoryConstantZZZ.sFIELD_PREFIX_ALIAS + objKernelNotes.getApplicationKeyCurrent());
			if(StringZZZ.isEmpty(sAlias)){
				sAlias = docCurrent.getItemValueString("Form");
				if(StringZZZ.isEmpty(sAlias)){
					ExceptionZZZ ez = new ExceptionZZZ("Internal document has no field  '" + ICategoryConstantZZZ.sFIELD_PREFIX_ALIAS + objKernelNotes.getApplicationKeyCurrent() + "' and no field 'form'", iERROR_ZFRAME_DESIGN, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez; 
				}
			}
			
		
			//@IsMember("File#A9A859D9D4ED1769C125741D002B2D62#FileName"; objCatRefSourceVIA)
			//Das wäre der Suchstring für einen dbSearch: String sSearch = "@IsMember(\"" +  sAlias + "#" + sDocId + "#" + sFieldFirst + "\"; " + ICategoryConstantZZZ.sFIELD_PREFIX_CATEGORYSOURCE_META + objKernelNotes.getApplicationKeyCurrent() +")";
			String sSearch = sAlias + "#" + sDocId + "#" + sFieldFirst;
			if(this.getFlag("Debug")) System.out.println(ReflectCodeZZZ.getMethodCurrentName() + " - Suchstring: '" + sSearch + "'");
			if(this.getFlag("Debug")) System.out.println(ReflectCodeZZZ.getMethodCurrentName() + " - LogLevel: '" + this.getKernelNotesLogObject().getLogLevelGlobal() + "'");
			this.getKernelNotesLogObject().writeLog("Suchstring: '"+ sSearch + "'", 3);
			
			//+++ Alle Dokument mit diesem Key suchen										
			colReturn = viwCatRef.getAllDocumentsByKey(sSearch);
			 if(colReturn.getCount()==0){
				 this.getKernelNotesLogObject().writeLog("Kein Dokument gefunden.", 3);
				 break main;
			 }else{
				 this.getKernelNotesLogObject().writeLog("Anzahl gefundener Dokumente: " + colReturn.getCount(), 3);
			 }
		}catch(NotesException ne){
			ExceptionZZZ ez = new ExceptionZZZ("NotesException: " + ne.text, iERROR_RUNTIME, DocumentCategorizerZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}
	}//end main:
	return colReturn;
}

	public static MultiValueMap computeDocumentCategoryMap(KernelNotesZZZ objKernelNotes, Document docToReadFrom) throws ExceptionZZZ{
		MultiValueMap objReturn = new MultiValueMap();
		main:{
			try{
				if(objKernelNotes == null){
					ExceptionZZZ ez = new ExceptionZZZ("Notes-Kernel-Object", iERROR_PARAMETER_MISSING, DocumentCreatorZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				if(docToReadFrom==null){
					ExceptionZZZ ez = new ExceptionZZZ("Document to read category values from", iERROR_PARAMETER_MISSING, DocumentCreatorZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				//1. Schritt: Lies das Feld objCategoryVIA aus
				Vector vecCategory = docToReadFrom.getItemValue(DocumentCategorizerZZZ.sFIELD_PREFIX_CATEGORY_META + objKernelNotes.getApplicationKeyCurrent());
				if(vecCategory.isEmpty()) break main;
				
				//2. Schritt: Hole die CategorisierungsFelder - Werte und fülle damit die MultiValueMap
				//Merke: In CatVal + Feldname steht der Wert
				Iterator itCategory = vecCategory.iterator();
				while(itCategory.hasNext()){
					String sCategory = (String)itCategory.next();
					
					Vector vecVal = docToReadFrom.getItemValue(DocumentCategorizerZZZ.sFIELD_PREFIX_CATEGORY_VALUE + sCategory);
					Iterator itVal = vecVal.iterator();
					while(itVal.hasNext()){
						String stemp = (String) itVal.next();
						objReturn.put(sCategory, stemp);
					}//end while itVal
				}//end while itCategory
				
				
			}catch(NotesException ne){
				ExceptionZZZ ez = new ExceptionZZZ("NotesException: " + ne.text, iERROR_RUNTIME, DocumentCategorizerZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
		}//end main:
		return objReturn;
	}

	/** Prüft, ob der übergebenen Feldname in der Liste der Kategorisierungsfelder des internen Dokuments ist
	* @param sFieldname
	* @return
	* @throws ExceptionZZZ
	* 
	* lindhaueradmin; 22.04.2008 06:39:34
	 */
	public boolean isCategoryField(String sFieldname) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			try{
				if(StringZZZ.isEmpty(sFieldname)){
					ExceptionZZZ ez = new ExceptionZZZ("Fieldname", iERROR_PARAMETER_MISSING,DocumentCategorizerZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				
				//##################################
				//+++ Prüfe das intern gespeicherte Dokument: Enthält es dieses Feld als Kategorie ?
				Document docCategorySource = this.getDocument();
				if(docCategorySource==null){
					ExceptionZZZ ez = new ExceptionZZZ("internal Document", iERROR_PROPERTY_MISSING,DocumentCategorizerZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				String sFieldCategoryAll = ICategoryConstantZZZ.sFIELD_PREFIX_CATEGORY_META + this.getKernelNotesObject().getApplicationKeyCurrent();
				if(! docCategorySource.hasItem(sFieldCategoryAll)){
					this.getKernelNotesLogObject().writeLog("Dokument (" + docCategorySource.getUniversalID() + ") enthält keine Metadaten für die Kategorsierung", 1); 
					break main;
				}
				
				Vector vecCatAll = docCategorySource.getItemValue(sFieldCategoryAll);
				if(vecCatAll.isEmpty()){
					this.getKernelNotesLogObject().writeLog("Dokument (" + docCategorySource.getUniversalID() + ") ist keine Kategorisierungsquelle für das Feld '" + sFieldname + "' (Metadaten sind leer)", 1); 
					break main;
				}
				
				String[] saFieldnameCatAll = new String[vecCatAll.size()];
				vecCatAll.toArray(saFieldnameCatAll);
				
				bReturn = StringArrayZZZ.contains(saFieldnameCatAll, sFieldname);
				
			}catch(NotesException ne){
				ExceptionZZZ ez = new ExceptionZZZ("NotesException: " + ne.text, iERROR_RUNTIME, DocumentCategorizerZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
		}//End main
		return bReturn;
	}
	
	public String getDocReferenceIdCurrent() throws ExceptionZZZ{
		String sReturn = null;
		main:{
			try{
				Document docCur = this.getDocument();
				if(docCur==null){
					ExceptionZZZ ez = new ExceptionZZZ("internal Document", iERROR_PROPERTY_MISSING,this,  ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				KernelNotesZZZ objKernelNotes = this.getKernelNotesObject();
				if(objKernelNotes==null){
					ExceptionZZZ ez = new ExceptionZZZ("KernelNotes-Object", iERROR_PROPERTY_MISSING,this,  ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				sReturn = docCur.getItemValueString(ICategoryConstantZZZ.sFIELD_PREFIX_REFERENCE + objKernelNotes.getApplicationKeyCurrent());
				
			}catch(NotesException ne){
				ExceptionZZZ ez = new ExceptionZZZ("NotesException: " + ne.text, iERROR_RUNTIME, DocumentCategorizerZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
		}//end main
		return sReturn;
	}

	
	
	
	//#### Getter / setter
	public void setDocument(Document doc2beProcessed){
		this.doc2beProcessed = doc2beProcessed;
	}
	public Document getDocument(){
		return this.doc2beProcessed;
	}
	/**
 @param hmData
	 hat z.B. das Format
		MultiValueMap hmCategories = new MultiValueMap();
		hmCategories.put("Category1", "Cat1Value1");
		hmCategories.put("Category2", "Cat2Value1");
		hmCategories.put("Category1", "Cat1Value2");
	* 
	* lindhaueradmin; 29.03.2008 12:32:55
	 */
	public void setCategoryMap(MultiValueMap hmData){
		this.hmCategoryData = hmData;
	}
	public MultiValueMap getCategoryMap(){
		return this.hmCategoryData;
	}
}
