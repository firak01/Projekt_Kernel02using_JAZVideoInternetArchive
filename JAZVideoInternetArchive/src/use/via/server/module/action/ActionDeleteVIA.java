package use.via.server.module.action;

import java.util.Iterator;
import java.util.Vector;
 

import javax.servlet.http.HttpServletRequest;

import use.via.server.DocumentCategorizerZZZ;
import use.via.server.IActionConstantZZZ;
import use.via.server.ICategoryConstantZZZ;
import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.NotesException;
import lotus.domino.View;
 
import custom.zKernel.LogZZZ;
import custom.zNotes.kernel.KernelNotesLogZZZ;
import custom.zNotes.kernel.KernelNotesZZZ;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.web.cgi.UrlLogicEeZZZ;
import basic.zBasic.util.web.cgi.UrlLogicZZZ;
import basic.zKernel.IKernelConfigSectionEntryZZZ;
import basic.zNotes.basic.util.web.cgi.NotesCgiAnalyserZZZ;
import basic.zNotes.document.DocumentZZZ;
import basic.zNotes.use.log4j.NotesReportLogZZZ;

/** Klasse mit der �ber ein Servlet die als Parameter �bergebene DocId 
 * @author lindhaueradmin
 *
 */
public class ActionDeleteVIA extends ActionHttpVIA implements IActionResult{
	private String sDocIdCurrent = null;  //Die docId des zu l�schenden Dokuments
	
	
	public ActionDeleteVIA(KernelNotesZZZ objKernelNotes, HttpServletRequest req, String sFlagControl) throws ExceptionZZZ{
		super(objKernelNotes, req,  sFlagControl);
		main:{
			if(this.getFlag("int")==true) break main;
			if(req==null){
				ExceptionZZZ ez = new ExceptionZZZ("No HttpServletRequest-Object provided", iERROR_PARAMETER_MISSING, DocumentZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
		
			//TODO: Eigenltich sollte hier ein Mapper Store Objekt �bergeben werden. Das kann man dann f�r einen Test als Eingabe-Objekt verwenden !!!
	
			//1. Parameter DocId entgegennehmen
			String sDocId = req.getParameter(IActionConstantZZZ.sPARAMETER_DOCID);
			if(StringZZZ.isEmpty(sDocId)){
				ExceptionZZZ ez = new ExceptionZZZ("No parameter docid in HttpServletRequest-Object provided.", iERROR_PARAMETER_MISSING, DocumentZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			this.setDocIdCurrent(sDocId); //Merke: pr�ft gleichzeitig auf eine "valide" docId
		}//END MAIN:
	}
	
	/** F�hrt die L�schung des Dokuments durch.
	 *   Allerdings muss ggf. noch die Kategorisierung in verschiedenen anderen Dokumenten ge�ndert (bzw. entfernt werden)
	 *   
	 *   MERKE: Diese Methode wirft keinen Fehler. 
	 *                Statt dessen wird eine ExceptionZZZ im Protokoll vermerkt
	* @return true / false 
	* 
	* 
	* lindhauer; 14.04.2008 10:01:59
	 */
	public int start(){
		int iReturn = IActionConstantZZZ.iFALSE_CASE;
		main:{
			KernelNotesZZZ objKernelNotes = null;
			try{
				try{
					//1. Holen der aktuellen DocId und das zu l�schende Dokument
					String sDocId = this.getDocIdCurrent();
					if(StringZZZ.isEmpty(sDocId)){
						ExceptionZZZ ez = new ExceptionZZZ("No parameter docid in HttpServletRequest-Object provided.", iERROR_PROPERTY_MISSING, this.getClass().getName(), ReflectCodeZZZ.getMethodCurrentName());
						throw ez;
					}
					
					objKernelNotes = this.getKernelNotesObject();
					if(objKernelNotes==null){
						ExceptionZZZ ez = new ExceptionZZZ("No KernelNotes-Object provided.", iERROR_PROPERTY_MISSING, this.getClass().getName(), ReflectCodeZZZ.getMethodCurrentName());
						throw ez;
					}
					
					Database db = objKernelNotes.getDBApplicationCurrent();
					if(db==null){
						ExceptionZZZ ez = new ExceptionZZZ("No ApplicationDatabase provided.", iERROR_PROPERTY_MISSING, this.getClass().getName(), ReflectCodeZZZ.getMethodCurrentName());
						throw ez;
					}
								 			
					//Document docDelete = db.getDocumentByUNID(sDocId);  //ggf. doch besser auf folgende View zugreifen  sVIEW_LOOKUP_REFERENCE
					View viwRef = db.getView(ICategoryConstantZZZ.sVIEW_LOOKUP_REFERENCE + objKernelNotes.getApplicationKeyCurrent());
					if(viwRef == null){
						ExceptionZZZ ez = new ExceptionZZZ("No view found with the name '" + ICategoryConstantZZZ.sVIEW_LOOKUP_REFERENCE + "'", iERROR_ZFRAME_DESIGN, DocumentZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
						throw ez; 
					}
					
					DocumentCollection colRef = viwRef.getAllDocumentsByKey(sDocId);
					 if(colRef.getCount()==0){
							ExceptionZZZ ez = new ExceptionZZZ("No document found with the docId '" + sDocId + "' in the view '" + ICategoryConstantZZZ.sVIEW_LOOKUP_REFERENCE + "'", iERROR_ZFRAME_DESIGN, DocumentZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
							throw ez; 
					 }
					 if(colRef.getCount()>=2){
							ExceptionZZZ ez = new ExceptionZZZ("Unexpected ! More than one document found with the objRef" + objKernelNotes.getApplicationKeyCurrent() + "=" + sDocId + "' in the view '" + ICategoryConstantZZZ.sVIEW_LOOKUP_REFERENCE + "'", iERROR_ZFRAME_DESIGN, DocumentZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
							throw ez;
					 }
					
					Document doc2delete = colRef.getFirstDocument();
					if(doc2delete==null){
						ExceptionZZZ ez = new ExceptionZZZ("Unexpected ! No document found with the objRef" + objKernelNotes.getApplicationKeyCurrent() + "=" + sDocId + "' in the view '" + ICategoryConstantZZZ.sVIEW_LOOKUP_REFERENCE + "'", iERROR_ZFRAME_DESIGN, DocumentZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
						throw ez;
					}
					
					///TODO dies durch die Methode objCat.searchDocumentCollectionDepending(); ersetzen
					 DocumentCategorizerZZZ objCat = new DocumentCategorizerZZZ(objKernelNotes, doc2delete);
					DocumentCollection colFound = objCat.searchDocumentDependingAll(db);
					
					/*
					//2. Dokumente finden, die dieses Dokument "als Kategorie Referenzieren" (d.h. im feld 'objCatRefSourceVIA' an der entsprechenden Postion die DocId stehen haben)
					//Beispielsweise folgende Zeile Movie#A5521DE7C0E65193C125741D002B2D6E#MovieTitle"
					View viwCatRef = db.getView(ICategoryConstantZZZ.sVIEW_LOOKUP_CATEGORY_REFERENCE + objKernelNotes.getApplicationKeyCurrent());
					if(viwCatRef == null){
						ExceptionZZZ ez = new ExceptionZZZ("No view found with the name '" + ICategoryConstantZZZ.sVIEW_LOOKUP_CATEGORY_REFERENCE + "'", iERROR_ZFRAME_DESIGN, DocumentZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
						throw ez; 
					}
					
					//+++ Keys zusammenbauen					
					String sAlias = doc2delete.getItemValueString( ICategoryConstantZZZ.sFIELD_PREFIX_ALIAS + objKernelNotes.getApplicationKeyCurrent());
					Vector vecCatValue = doc2delete.getItemValue( ICategoryConstantZZZ.sFIELD_PREFIX_CATEGORY_META + objKernelNotes.getApplicationKeyCurrent()); //das sind alle Feldnamen, die zur Kategorsisierung herangezogen werden sollen
					
					// Es reicht der erste Eintrag ! (wahrscheinlich)
					String sFieldFirst = (String) vecCatValue.get(0);
					if(StringZZZ.isEmpty(sFieldFirst)) break main;	
					
					
					//@IsMember("File#A9A859D9D4ED1769C125741D002B2D62#FileName"; objCatRefSourceVIA)
					//Das w�re der Suchstring f�r einen dbSearch: String sSearch = "@IsMember(\"" +  sAlias + "#" + sDocId + "#" + sFieldFirst + "\"; " + ICategoryConstantZZZ.sFIELD_PREFIX_CATEGORYSOURCE_META + objKernelNotes.getApplicationKeyCurrent() +")";
					String sSearch = sAlias + "#" + sDocId + "#" + sFieldFirst;
					if(this.getFlag("Debug")) System.out.println(ReflectCodeZZZ.getMethodCurrentName() + " - Suchstring: '" + sSearch + "'");
					if(this.getFlag("Debug")) System.out.println(ReflectCodeZZZ.getMethodCurrentName() + " - LogLevel: '" + this.getKernelNotesLogObject().getLogLevelGlobal() + "'");
					this.getKernelNotesLogObject().writeLog("Suchstring: '"+ sSearch + "'", 3);
					
					//+++ Alle Dokument mit diesem Key suchen										
					DocumentCollection colFound = viwCatRef.getAllDocumentsByKey(sSearch);
					 if(colFound.getCount()==0){
						 this.getKernelNotesLogObject().writeLog("Kein Dokument gefunden.", 3);
						 break main;
					 }else{
						 this.getKernelNotesLogObject().writeLog("Anzahl gefundener Dokumente: " + colFound.getCount(), 3);
					 }
						/////######################
						 * */
					
					//3. Aus den gefundenen Dokumenten die Zeilen entfernen in denen die DocId des zu l�schenden Dokumentes auftaucht					
					for (int icount = 1; icount <= colFound.getCount(); icount++ ){
						Document doctemp = colFound.getNthDocument(icount);
						if(doctemp != null){
							boolean bAnyRemoved = objCat.removeDocumentFromCategory(doctemp);											
							
							if(bAnyRemoved==true){						
								//Gefundene und ver�nderte Dokumente speichern
								if( doctemp.save(true,false, false)==false){
									ExceptionZZZ ez = new ExceptionZZZ("Unable to save document, which uses the documents categories: '" + doctemp.getUniversalID() +"'", iERROR_RUNTIME, this.getClass().getName(), ReflectCodeZZZ.getMethodCurrentName());
									throw ez;
								}
							}							
						}//end if doctemp != null						
					}//end for
					 										
					//4. Aktuelles dokument l�schen
					if(doc2delete.remove(true)==false){
						ExceptionZZZ ez = new ExceptionZZZ("Unable to delete document: '" + doc2delete.getUniversalID() +"'", iERROR_RUNTIME, this.getClass().getName(), ReflectCodeZZZ.getMethodCurrentName());
						throw ez;
					}
					iReturn = IActionConstantZZZ.iSUCCESS_CASE;
										 						 			
				}catch(NotesException ne){
					ExceptionZZZ ez = new ExceptionZZZ("NotesException: '" + ne.text +"'", iERROR_RUNTIME, this.getClass().getName(), ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
			}catch(ExceptionZZZ ez){
				try{
					if (objKernelNotes != null){
						KernelNotesLogZZZ objLogNotes = objKernelNotes.getKernelNotesLogObject();
						if(objLogNotes!= null){
							objLogNotes.writeLog(ez.getDetailAllLast());
						}
						LogZZZ objLog = objKernelNotes.getLogObject();
						if(objLog!=null){
							objLog.WriteLineDate(ez.getDetailAllLast());
						}
						NotesReportLogZZZ.writeException(ez);								
					}else{
						System.out.println(this.getClass().getName() + ".start() - Error (No KernelObject available case): '" + ez.getDetailAllLast() + "'");
					}
				}catch(Throwable t){
					System.out.println(this.getClass().getName() + ".start() - Error in ExceptionHandling: '" + t.getMessage() + "'");
				}
			}			
		}//end main:
		return iReturn;
	}
	
	
	//######### GETTER / SETTER
	/** Merke: Muss eine als "valide" geltende DocId sein (nur Hex Werte).  Nur null ist erlaubt um den bisherigen Wert quasi zu l�schen.
	 *   Ansonsten wird ein Fehler geworfen. 
	* @param sDocId
	* @throws ExceptionZZZ
	* 
	* lindhauer; 14.04.2008 09:48:44
	 */
	public void setDocIdCurrent(String sDocId) throws ExceptionZZZ{
		if(sDocId!=null){
			boolean btemp = DocumentZZZ.isValidDocId(sDocId);  //Damit darf nix falsches gesetz werden
			if(btemp==false){
				ExceptionZZZ ez = new ExceptionZZZ("Provided DocId '" + sDocId + "' seems not to be valid. You can only set a valid docId here !", iERROR_PARAMETER_VALUE, this.getClass().getName(), ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
		}
		this.sDocIdCurrent = sDocId;
	}
	public String getDocIdCurrent(){
		return this.sDocIdCurrent;
	}
	


	public String getURLOnResult(int iCase) throws ExceptionZZZ {
		String sReturn = null;
		main:{
			//try{
				if(iCase == ActionVIA.iSUCCESS_CASE){					
					IKernelConfigSectionEntryZZZ objEntry = this.getKernelObject().getParameterByProgramAlias(ServletActionExecuteVIA.class.getName(), this.getClass().getName(), "PageOnCaseSuccess");
					String sPage = objEntry.getValue();
					if(sPage.startsWith("/")){ //Zugriff auf Ressource in der lokalen Datenbank
						
						//FGL 20090404 - Hier den Pfad mit dem "richtigen" Servernamen ersetzen (der sich unterscheidet je nachdem, ob der Aufruf aus dem Internet oder Intranet stammt....
						//deshalb kann man den db.getHttpURL()-Pfad nicht verwenden
						HttpServletRequest objReq = this.getHttpServletRequest();
						sReturn = NotesCgiAnalyserZZZ.getHttpUrl(objReq, this.getKernelNotesObject().getDBApplicationCurrent());
						sReturn = sReturn + sPage;
						
					}else{
						sReturn = sPage;
					}
				}else{
					break main;
				}
			//}catch(NotesException ne){
			//	ExceptionZZZ ez = new ExceptionZZZ("NotesException: " + ne.text, iERROR_RUNTIME, this.getClass().getName(), ReflectCodeZZZ.getMethodCurrentName());
			//	throw ez;
			//}	
		}//end main:
		return sReturn;
	}
}
