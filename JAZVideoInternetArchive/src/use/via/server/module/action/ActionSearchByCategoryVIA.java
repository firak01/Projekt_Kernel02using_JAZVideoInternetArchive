package use.via.server.module.action;

import javax.servlet.http.HttpServletRequest;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.NotesException;

import use.via.server.DocumentSearcherZZZ;
import use.via.server.IActionConstantZZZ;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zNotes.basic.util.web.cgi.NotesCgiAnalyserZZZ;
import basic.zNotes.document.DocumentZZZ;
import basic.zNotes.use.log4j.NotesReportLogZZZ;
import custom.zNotes.kernel.KernelNotesZZZ;

/** Suche anhand der übergebenen Kategory und des objAliasVIA - Werts nach dem Dokument.
 * @author lindhauer
 *
 */
public class ActionSearchByCategoryVIA  extends ActionHttpVIA implements IActionResult{ 
String sAlias = null;
String sValue = null;
String sUrlReturn = null;

public ActionSearchByCategoryVIA(KernelNotesZZZ objKernelNotes, HttpServletRequest req, String sFlagControl)throws ExceptionZZZ{
	super(objKernelNotes, req, sFlagControl);
	main:{
			if(this.getFlag("int")==true) break main;
			if(req==null){
				ExceptionZZZ ez = new ExceptionZZZ("No HttpServletRequest-Object provided", iERROR_PARAMETER_MISSING, DocumentZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			//TODO: Eigenltich sollte hier ein Mapper Store Objekt übergeben werden. Das kann man dann für einen Test als Eingabe-Objekt verwenden !!!
	
			//1. Parameter  Alias der zu suchenden Dokuementart  entgegennehmen
			String sAlias = req.getParameter(IActionConstantZZZ.sPARAMETER_ALIAS);
			if(StringZZZ.isEmpty(sAlias)){
				ExceptionZZZ ez = new ExceptionZZZ("No parameter 'alias' in HttpServletRequest-Object provided.", iERROR_PARAMETER_MISSING, this.getClass().getName(), ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "alias=" + sAlias, true);
			this.setAlias(sAlias); 
			
			
			//2. Parameter Feldwert entgegennehmen
			String sValue = req.getParameter(IActionConstantZZZ.sPARAMETER_FIELDVALUE);			
			if(StringZZZ.isEmpty(sValue)){
				ExceptionZZZ ez = new ExceptionZZZ("No parameter 'value' in HttpServletRequest-Object provided.", iERROR_PARAMETER_MISSING, this.getClass().getName(), ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "value=" + sValue, true);			
			this.setCategoryValue(sValue);
				
	}//END main:

}



int start() throws ExceptionZZZ {
	int iReturn = IActionConstantZZZ.iFALSE_CASE;
	main:{
	try{
		//1. Die Applikationsdatenbank soll durchsucht werden //TODO: Auch eine Aktion anbieten, die eine beliebige Datenbank auf einem beliebigen Server durchsucht !!!
		Database db = this.getKernelNotesObject().getDBApplicationCurrent();
		if(! db.isOpen()){
			ExceptionZZZ ez = new ExceptionZZZ("Cannot opn application database: '" + db.getTitle() + "'", iERROR_RUNTIME, this.getClass().getName(), ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}

		//2. Suche durchführen
		String sValue = this.getCategoryValue();
		String sAlias = this.getAlias();
		
		DocumentSearcherZZZ objSearch = new DocumentSearcherZZZ(this.getKernelNotesObject(), db);
		DocumentCollection col = objSearch.searchCategorySource(sValue, sAlias);
		if(col.getCount()>=2){
			NotesReportLogZZZ.write(NotesReportLogZZZ.ERROR, "More than one document found with the alias='" + sAlias + "' and the categoryvalue='" + sValue + "'", true);
			ExceptionZZZ ez = new ExceptionZZZ("Unexpected - There are more than one document with the alias='" + sAlias + "' and the categoryvalue='" + sValue + "'", iERROR_RUNTIME, this.getClass().getName(), ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}else if(col.getCount() == 1){
		
			Document doc = col.getFirstDocument();
			
			//FLi 20090407 Internet Zugriff unterscheidet sich vom Intranet Zugriff --> URLs umrechnen
			//String sUrl = doc.getHttpURL();			
			String sUrl = NotesCgiAnalyserZZZ.getHttpUrl(this.getHttpServletRequest(), doc);
			NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "Found document with URL='" + sUrl + "'", true);	
											
			this.setURLOnResult(IActionConstantZZZ.iSUCCESS_CASE, sUrl);  //Diese URL wird nicht in der ini-DAtei konfigureirt
			iReturn = IActionConstantZZZ.iSUCCESS_CASE;
		}else{
			//Im false Fall wird die Rückgabemeldung aus der konfiguration geholt
			iReturn = IActionConstantZZZ.iFALSE_CASE;
		}
		
		
		}catch(NotesException ne){
			NotesReportLogZZZ.write(NotesReportLogZZZ.ERROR, "NotesException: '" + ne.text +"'", true);
			ExceptionZZZ ez = new ExceptionZZZ("NotesException: " + ne.text, iERROR_RUNTIME, this.getClass().getName(), ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}	
	}//End main:
	return iReturn;
}


//##### GETTER // SETTER 
public String getURLOnResult(int iResultCase) throws ExceptionZZZ {
	String sReturn = null;
	if(iResultCase==IActionConstantZZZ.iSUCCESS_CASE){
		sReturn = this.sUrlReturn;
	}
	return sReturn; 
}
private void setURLOnResult(int iResultCase, String sUrl){
	if(iResultCase==IActionConstantZZZ.iSUCCESS_CASE){
		this.sUrlReturn = sUrl;
	}
}

public void setCategoryValue(String sCatVal){
	this.sValue=sCatVal;
}
public String getCategoryValue(){
	return this.sValue;
}

public void setAlias(String sAlias){
	this.sAlias = sAlias;
}
public String getAlias(){
	return this.sAlias;
}



}//END Class
