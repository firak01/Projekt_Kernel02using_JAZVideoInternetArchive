package use.via.server.module.action;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.NotesException;
import lotus.domino.View;
import use.via.server.DocumentCategorizerZZZ;
import use.via.server.IActionConstantZZZ;
import use.via.server.ICategoryConstantZZZ;
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

public class ActionFieldUpdateVIA extends ActionHttpVIA implements IActionResult{
	private String sDocIdCurrent = null;
	private String sFieldname = null;
	private Vector vecValueNew = new Vector();
	private boolean bFlagDontSaveInitialDocument = false;
	
	/** Dieser Konstruktor wird verwendet in diversen Tests. Paramer, die sonst per HttpServletRequest �bergeben werden m�ssen dann manuell gesetz werden
	* lindhaueradmin; 11.07.2008 11:41:05
	 * @param objKernelNotes
	 * @param sFlagControl
	 * @throws ExceptionZZZ
	 */
	public ActionFieldUpdateVIA(KernelNotesZZZ objKernelNotes, String sFlagControl) throws ExceptionZZZ{
		super(objKernelNotes, sFlagControl);
	}
	
	
	/** Konstruktor, wie er im Servlet verwendet wird. Diverse �bergabeparameter werden aus dem HttpServletRequest ausgelesen
	* lindhaueradmin; 11.07.2008 11:41:57
	 * @param objKernelNotes
	 * @param req  
	 * @param sFlagControl:   "dont_save_initial_document  -- bewirkt, dass der neuen Wert in dem eigentlichen Ausgangsdokument nicht gespeichert wird, sondern nur in den abh�ngingen Dokumente 
	 * @throws ExceptionZZZ
	 */
	public ActionFieldUpdateVIA(KernelNotesZZZ objKernelNotes, HttpServletRequest req, String[] saFlagControl) throws ExceptionZZZ{
		super(objKernelNotes, req, saFlagControl);
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
				ExceptionZZZ ez = new ExceptionZZZ("No parameter docid in HttpServletRequest-Object provided.", iERROR_PARAMETER_MISSING, ActionFieldUpdateVIA.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "DocID=" + sDocId, true);
			this.setDocIdCurrent(sDocId); //Merke: pr�ft gleichzeitig auf eine "valide" docId
			
			
			//2. Parameter Feldname entgegennehmen
			String sFieldname = req.getParameter(IActionConstantZZZ.sPARAMETER_FIELDNAME);			
			if(StringZZZ.isEmpty(sFieldname)){
				ExceptionZZZ ez = new ExceptionZZZ("No parameter docid in HttpServletRequest-Object provided.", iERROR_PARAMETER_MISSING, ActionFieldUpdateVIA.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "field=" + sFieldname, true);			
			this.setFieldname(sFieldname);
			
			//3. Parameter neuer Wert entgegennehmen
			//TODO: �BERGABE VON MEHERFACHWERTEN _ JASON SYNTAX
			String sFieldValue = req.getParameter(IActionConstantZZZ.sPARAMETER_FIELDVALUE);
			NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "value=" + sFieldValue, true);
			
			//!!! Ein leerwet bedeutet Feld l�schen !!!
			
			Vector vecValue = this.getValueNew();
			vecValue.add(sFieldValue); //TODO Das m�ssen bei Mehrfachwerten die unterschiedlichen Werte sein !!!
			
		}//END MAIN:
	}
	
	


	/* F�hrt die Aktualsierung des Felds im Dokument durch
	 * 
	 *  (non-Javadoc)
	 * @see use.via.server.module.action.ActionVIA#start()
	 */
	public int start() throws ExceptionZZZ {
		int iReturn = IActionConstantZZZ.iFALSE_CASE;
		main:{
		KernelNotesZZZ objKernelNotes = null;
		try{
			try{
				//1. Holen der aktuellen DocId und das upzudatende Dokument
				String sDocId = this.getDocIdCurrent();
				if(StringZZZ.isEmpty(sDocId)){
					ExceptionZZZ ez = new ExceptionZZZ("No property of the parameter '" + IActionConstantZZZ.sPARAMETER_DOCID + "' in HttpServletRequest-Object provided.", iERROR_PROPERTY_MISSING, this.getClass().getName(), ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				String sFieldname = this.getFieldname();
				if(StringZZZ.isEmpty(sDocId)){
					ExceptionZZZ ez = new ExceptionZZZ("No property of the parameter  '" + IActionConstantZZZ.sPARAMETER_FIELDNAME + "'  in HttpServletRequest-Object provided.", iERROR_PROPERTY_MISSING, this.getClass().getName(), ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				Vector vecValueNew = this.getValueNew();
				//Merke: Entfernen des Felds w�re bei leerem Vector m�glich
				
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
						ExceptionZZZ ez = new ExceptionZZZ("Unexpected ! More than one document found with the objRef" + ICategoryConstantZZZ.sFIELD_PREFIX_REFERENCE + objKernelNotes.getApplicationKeyCurrent() + "=" + sDocId + "' in the view '" + ICategoryConstantZZZ.sVIEW_LOOKUP_REFERENCE + "'", iERROR_ZFRAME_DESIGN, DocumentZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
						throw ez;
				 }
				
				Document doc2update = colRef.getFirstDocument();
				if(doc2update==null){
					ExceptionZZZ ez = new ExceptionZZZ("Unexpected ! No document found with the objRef" + ICategoryConstantZZZ.sFIELD_PREFIX_REFERENCE + objKernelNotes.getApplicationKeyCurrent() + "=" + sDocId + "' in the view '" + ICategoryConstantZZZ.sVIEW_LOOKUP_REFERENCE + "'", iERROR_ZFRAME_DESIGN, DocumentZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				
				//TODO: VALIDIERUNG DES NEUEN FELDWERTS
				
				 DocumentCategorizerZZZ objCat = new DocumentCategorizerZZZ(objKernelNotes, doc2update);
				
				//3a Neuen Feldwert in das Dokument setzen, damit f�r die abh�ngigen Dokumente auch schon etwas zu aktualiseren ist
				objCat.updateCategory(sFieldname, vecValueNew);
				boolean bSaveInitialDoc = this.getFlag("dont_save_initial_document"); //Damit soll ggf. die Entstehung von Replzierkonflikten vermieden werden
				if(bSaveInitialDoc==true){
					boolean btemp = objCat.getDocument().save();
					 if(btemp==false){
						 ExceptionZZZ ez = new ExceptionZZZ("Unable to save selected document with the objRef" + ICategoryConstantZZZ.sFIELD_PREFIX_REFERENCE + objKernelNotes.getApplicationKeyCurrent() + "=" + sDocId + "'" , iERROR_ZFRAME_DESIGN, DocumentZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
						throw ez; 
					 }
				}else{
					NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "Flag 'Dont_Save_inital_document' was set. Document was not saved. Will only save depending documents.", true);
				}
				 
				//3b  Die Dokumente in der Kategorsierung dieses Felds �ndern
				//+++"abh�ngige"  Dokumente zusammensuchen
				 DocumentCollection colFound = objCat.searchDocumentDependingAll(db);
				 if(colFound.getCount()==0){
					 NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "NO depending document found", true);
					 break main;
				 }else{
					 NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, colFound.getCount() + " depending document(s) found", true);
				 }
				for (int icount = 1; icount <= colFound.getCount(); icount++ ){
					Document doctemp = colFound.getNthDocument(icount);
					if(doctemp != null){
						boolean bSuccess = objCat.updateDocumentCategory(doctemp, sFieldname);											
						
						if(bSuccess==true){						
							//Gefundene und ver�nderte Dokumente speichern
							if( doctemp.save(true,false, false)==false){
								ExceptionZZZ ez = new ExceptionZZZ("Unable to save document, which uses the documents categories: '" + doctemp.getUniversalID() +"'", iERROR_RUNTIME, this.getClass().getName(), ReflectCodeZZZ.getMethodCurrentName());
								throw ez;
							}
						}							
					}//end if doctemp != null						
				}//end for
				 										
				iReturn = IActionConstantZZZ.iSUCCESS_CASE;
				
				//20080710: TODO Alle als embedded View genutzten Ansichten (im Ausgangsdokument) aktualisieren, damit diese auch die abh�ngigen Dokumente enth�lt
				boolean bResult = this.refreshViewEmbedded(sDocId);
				if(bResult==true){
					 NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "Refreshing Embedded View was successful.", true);
					 try {
						 NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "Warte.....", true);
						Thread.sleep(1000); //Dem Server Zeit geben
						 NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "Gewartet.....", true);
					} catch (InterruptedException e) {
						ExceptionZZZ ez = new ExceptionZZZ("InterruptedException: '" + e.getMessage() +"'", iERROR_RUNTIME, this.getClass().getName(), ReflectCodeZZZ.getMethodCurrentName());
						throw ez;
					}
				}else{
					 NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "Refreshing Embedded View was NOT successful", true);
				}
				
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


	
	//########   GETTER / SETTER
	/** DocId wird per Parameter aus dem HttpRequest geholt und hier intern gesetzt. Dabei findet eine Pr�fung auf Validit�t statt.
	* @param sDocId
	* @throws ExceptionZZZ
	* 
	* lindhauer; 25.04.2008 13:57:45
	 */
	public void setDocIdCurrent(String sDocId) throws ExceptionZZZ {
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
	
	public void setFieldname(String sFieldname) throws ExceptionZZZ{
		if(sFieldname!=null){
			boolean btemp = DocumentZZZ.isValidFieldname(sFieldname);
			if(btemp==false){
				ExceptionZZZ ez = new ExceptionZZZ("ProvidedFieldname '" + sFieldname + "' seems not to be valid. You can only set a valid fieldname here !", iERROR_PARAMETER_VALUE, this.getClass().getName(), ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
		}
		this.sFieldname = sFieldname;
	}
	public String getFieldname(){
		return this.sFieldname;
	}
	
	public void setValueNew(Vector vecValueNew){
		this.vecValueNew = vecValueNew;
	}
	public Vector getValueNew(){
		return this.vecValueNew;
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
		//		ExceptionZZZ ez = new ExceptionZZZ("NotesException: " + ne.text, iERROR_RUNTIME, this.getClass().getName(), ReflectCodeZZZ.getMethodCurrentName());
		//		throw ez;
		//	}	
		}//end main:
		return sReturn;
	}
	
	
	//##################
	/**
	 * @see zzzKernel.basic.KernelUseObjectZZZ#setFlag(java.lang.String, boolean)
	 * @param sFlagName
	 * 			  source_rename: after copying the source_files will be renamed.
	 * 			  source_remove: after copying the source_files will be removed.
	 */
	public boolean setFlag(String sFlagName, boolean bFlagValue){
		boolean bFunction = false;
		main:{
			if(StringZZZ.isEmpty(sFlagName)) break main;
			bFunction = super.setFlag(sFlagName, bFlagValue);
		if(bFunction==true) break main;
	
		//setting the flags of this object
		String stemp = sFlagName.toLowerCase();
		if(stemp.equals("dont_save_initial_document")){
			this.bFlagDontSaveInitialDocument = bFlagValue;
			bFunction = true;
			break main;
		}
		}//end main:
		return bFunction;
	}
	
	public boolean getFlag(String sFlagName){
		boolean bFunction = false;
		main:{
			if(StringZZZ.isEmpty(sFlagName)) break main;
			bFunction = super.getFlag(sFlagName);
			if(bFunction==true) break main;
			
			//getting the flags of this object
			String stemp = sFlagName.toLowerCase();
			if(stemp.equals("dont_save_initial_document")){
				bFunction = this.bFlagDontSaveInitialDocument;
				break main;
			}
		}//end main:
		return bFunction;
	}
	
	
}//end class
