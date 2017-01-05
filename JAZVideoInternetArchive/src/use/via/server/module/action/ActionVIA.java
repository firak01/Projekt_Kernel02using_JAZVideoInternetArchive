package use.via.server.module.action;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Vector;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.View;
import use.via.server.DocumentCreatorZZZ;
import use.via.server.IActionConstantZZZ;
import use.via.server.ICategorizableZZZ;
import use.via.server.ICategoryConstantZZZ;
import use.via.server.module.create.CarrierVIA;
import use.via.server.module.create.MovieVIA;
import use.via.server.module.create.SerieVIA;
import use.via.server.module.create.FileVIA;
import custom.zNotes.kernel.KernelNotesZZZ;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zNotes.document.DocumentZZZ;
import basic.zNotes.kernel.KernelNotesUseObjectZZZ;
import basic.zNotes.use.log4j.NotesReportLogZZZ;

public abstract class ActionVIA extends KernelNotesUseObjectZZZ implements IActionConstantZZZ{
	public ActionVIA(KernelNotesZZZ objKernelNotes, String sFlag){
		super(objKernelNotes, sFlag);
	}
	public ActionVIA(KernelNotesZZZ objKernelNotes, String[] saFlag){
		super(objKernelNotes, saFlag);
	}
	/** Die URL (konfigurierbar in der Kernel ini-Datei), die im Erfolgsfall geöffnet werden soll (d.h. als Umleitung des Requests.)
	* @param iResultCase
	* @return
	* @throws ExceptionZZZ
	* 
	* lindhauer; 25.04.2008 14:02:48
	 */
	//abstract String getURLOnResult(int iResultCase) throws ExceptionZZZ;
			
	/**Führt die Hauptaktion aus
	* @return
	* @throws ExceptionZZZ
	* 
	* lindhauer; 25.04.2008 14:03:53
	 */
	abstract int start() throws ExceptionZZZ;
	
	boolean refreshViewEmbedded(String sDocIdIn) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			KernelNotesZZZ objKernelNotes = null;
			try{
				//1. Holen der aktuellen DocId und das upzudatende Dokument
				String sDocId;
				if(StringZZZ.isEmpty(sDocIdIn)){
						ExceptionZZZ ez = new ExceptionZZZ("Missing parameter docid '", iERROR_PARAMETER_MISSING, this.getClass().getName(), ReflectCodeZZZ.getMethodCurrentName());
						throw ez;
				}else{
					sDocId = sDocIdIn;
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
				
				View viwRef = db.getView(ICategoryConstantZZZ.sVIEW_LOOKUP_REFERENCE + objKernelNotes.getApplicationKeyCurrent());
				if(viwRef==null){
					ExceptionZZZ ez = new ExceptionZZZ("No View '" + ICategoryConstantZZZ.sVIEW_LOOKUP_REFERENCE + objKernelNotes.getApplicationKeyCurrent() + " available.", iERROR_ZFRAME_DESIGN, this.getClass().getName(), ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
//				FALSCH: Document docCur = db.getDocumentByUNID(sDocId); //Diese Id ist ggf. schon nicht mehr in der Datenbank vorhanden
				NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "Suche Dokument in der Ansicht '" + ICategoryConstantZZZ.sVIEW_LOOKUP_REFERENCE + objKernelNotes.getApplicationKeyCurrent() + " per objRefId: '"+ sDocId +"'.", true);
				Document docCur = viwRef.getDocumentByKey(sDocId);
				if(docCur==null){
					ExceptionZZZ ez = new ExceptionZZZ("No document found in the application database with the referenceid: '" + sDocId + "'", iERROR_PROPERTY_MISSING, this.getClass().getName(), ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				//+++ Für jede Dokumentenart ist ein DocumentCreator vorhanden. Dieser wird ermittelt und die Notwendigen Informationen werden geholt.
				String sClassname = this.getCreatorClassnameByDocument(docCur);
				if(StringZZZ.isEmpty(sClassname)){
					ExceptionZZZ ez = new ExceptionZZZ("No DocumentCreatorClassname found for the document with the referenceid: '" + sDocId + "' and Form='" + docCur.getItemValueString("Form") + "'", iERROR_PROPERTY_VALUE, this.getClass().getName(), ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				//+++ Nun per ReflectionAPI die static Methode ausführen, die einen Vektor mit den ganzen Viewnamen zurückliefert.
				Class cls = Class.forName(sClassname);
				Method method = cls.getMethod("getViewnameEmbeddedAllUsed", null);
				Vector vecViewname = (Vector) method.invoke(null, null);
				if(vecViewname==null) break main;
				if(vecViewname.isEmpty()) break main;
				
				//+++ Vector durchlaufen und alle views aktualisieren
				Iterator it = vecViewname.iterator();
				while(it.hasNext()){
					String sViewname = (String) it.next();
					if(! StringZZZ.isEmpty(sViewname)){
						View viwEmbedded = db.getView(sViewname);
						if(viwEmbedded == null){
							ExceptionZZZ ez = new ExceptionZZZ("No view found with the name '" + sViewname + "'", iERROR_ZFRAME_DESIGN, DocumentZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
							throw ez; 
						}
						
						//+++ Das eigentliche Refreshen der View
						try{
							NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "Refreshen der View '"+ sViewname +"'.", true);
							viwEmbedded.refresh();
							bReturn = true;
						}catch(NotesException ne){
							System.out.println("Refreshen der View '"+ sViewname +"' wirft Fehler. Views können nicht per IIOR refresht werden.");
							NotesReportLogZZZ.write(NotesReportLogZZZ.ERROR, "Refreshen der View '"+ sViewname +"' wirft Fehler. Views können nicht per IIOR refresht werden.", true);
						}
					}
					
				}//End while
				
				
			}catch(NotesException ne){
				ExceptionZZZ ez = new ExceptionZZZ("NotesException: " + ne.text, iERROR_RUNTIME, this.getClass().getName(), ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}catch(ClassNotFoundException cnfe){
				ExceptionZZZ ez = new ExceptionZZZ("ClassNotFoundException: " + cnfe.getMessage(), iERROR_RUNTIME, this.getClass().getName(), ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}catch(NoSuchMethodException nsme){
				ExceptionZZZ ez = new ExceptionZZZ("NoSuchMethodException: " + nsme.getMessage(), iERROR_RUNTIME, this.getClass().getName(), ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}catch(IllegalAccessException iae){
				ExceptionZZZ ez = new ExceptionZZZ("IllegalAccessException: " + iae.getMessage(), iERROR_RUNTIME, this.getClass().getName(), ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}catch(InvocationTargetException ite){
				ExceptionZZZ ez = new ExceptionZZZ("InvocationTargetException: " + ite.getMessage(), iERROR_RUNTIME, this.getClass().getName(), ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
		}//end main
		return bReturn;
	}
	
	String getCreatorClassnameByDocument(Document docIn) throws ExceptionZZZ{
		String sReturn = "";
		main:{
			try{
				if(docIn == null){
					ExceptionZZZ ez = new ExceptionZZZ("No handle on document provided", iERROR_PARAMETER_MISSING, this.getClass().getName(), ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				String sForm = docIn.getItemValueString("Form");
				if(StringZZZ.isEmpty(sForm)){
					ExceptionZZZ ez = new ExceptionZZZ("Document has empty formname", iERROR_PROPERTY_MISSING, this.getClass().getName(), ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				//Nun alle möglichen DocumentCreator prüfen
				String stemp = CarrierVIA.getFormUsed();
				if (stemp.equalsIgnoreCase(sForm)){
					sReturn = CarrierVIA.class.getName();
					break main;
				}
				
				stemp = MovieVIA.getFormUsed();
				if (stemp.equalsIgnoreCase(sForm)){
					sReturn = MovieVIA.class.getName();
					break main;
				}
				
				stemp = SerieVIA.getFormUsed();
				if (stemp.equalsIgnoreCase(sForm)){
					sReturn = SerieVIA.class.getName();
					break main;
				}
				
				stemp = FileVIA.getFormUsed();
				if (stemp.equalsIgnoreCase(sForm)){
					sReturn = FileVIA.class.getName();
					break main;
				}
				
				
			}catch(NotesException ne){
				ExceptionZZZ ez = new ExceptionZZZ("NotesException: " + ne.text, iERROR_RUNTIME, this.getClass().getName(), ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}	
		}//end mian
		return sReturn;
	}
}
