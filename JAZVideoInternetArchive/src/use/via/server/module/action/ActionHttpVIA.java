package use.via.server.module.action;

import javax.servlet.http.HttpServletRequest;

import use.via.server.IActionConstantZZZ;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zNotes.document.DocumentZZZ;
import custom.zNotes.kernel.KernelNotesZZZ;

public abstract class ActionHttpVIA extends ActionVIA{
	private HttpServletRequest objReq = null; //der HttpServletRequest. Aus ihm kann man viel untersuchen.
	 
	public ActionHttpVIA(KernelNotesZZZ objKernelNotes, HttpServletRequest req, String sFlag) throws ExceptionZZZ{
		super(objKernelNotes, sFlag);
		main:{
			if(this.getFlag("int")==true) break main;
			if(req==null){
				ExceptionZZZ ez = new ExceptionZZZ("No HttpServletRequest-Object provided", iERROR_PARAMETER_MISSING, DocumentZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			this.setHttpServletRequest(req);
		}//end main:		
	}
	
	//Konstruktor für Tests ohne HttpRequest Objekt
	public ActionHttpVIA(KernelNotesZZZ objKernelNotes, String sFlag){
		super(objKernelNotes, sFlag);
	}
	public ActionHttpVIA(KernelNotesZZZ objKernelNotes, HttpServletRequest req, String[] saFlag){
		super(objKernelNotes, saFlag);
	}
	
	//Konstruktor für Tests ohne HttpRequest Objekt
	public ActionHttpVIA(KernelNotesZZZ objKernelNotes, String[] saFlag){
		super(objKernelNotes, saFlag);
	}
	
	
	
	//#### Getter / Setter ####################
	public void setHttpServletRequest(HttpServletRequest req){
		this.objReq = req;
	}
	public HttpServletRequest getHttpServletRequest(){
		return this.objReq;
	}
}
