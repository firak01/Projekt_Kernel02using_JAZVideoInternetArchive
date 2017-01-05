package use.via.server.module.status;

import org.apache.ecs.HtmlColor;
import org.apache.ecs.StringElement;
import org.apache.ecs.html.BR;
import org.apache.ecs.html.Font;
import org.apache.ecs.html.H1;
import org.apache.ecs.html.HR;
import org.apache.ecs.xhtml.title;

import lotus.domino.Database;
import lotus.domino.NotesException;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zKernel.markup.content.ContentEcsZZZ;
import basic.zKernel.KernelZZZ;
import custom.zKernel.LogZZZ;
import custom.zNotes.kernel.KernelNotesZZZ;

/**Aufgabe dieser Klasse ist die Erstellung einer Webseite, die - z.B. nach einer erfolgreichen Verarbeitung - den Benutzer  
 * darüber informiert, das ein Dokument erstellt worden ist.
 * 
 * Dazu wird KernelContentZZZ erweitert und demnach wird JakartaECS genutzt.
 * 
 * TODO:
 * Auf dieser Seite soll ein Links auf das gerade erstellte Dokument stehen.
 * @author lindhaueradmin
 *
 */
public class ContentDocumentCreatedPageZZZ extends ContentEcsZZZ {
	private KernelNotesZZZ objKernelNotes = null ;
	
	public ContentDocumentCreatedPageZZZ(KernelZZZ objKernel, KernelNotesZZZ objKernelNotes) throws ExceptionZZZ{	
		super(objKernel);
		this.objKernelNotes = objKernelNotes;
	}
	
	public KernelNotesZZZ getKernelNotesObject(){
		return this.objKernelNotes;
	}
	public boolean computeKernelDetail(){
		boolean bReturn = false;
		try{
			try{
			main:{
				Database objDB = this.getKernelNotesObject().getDBLogCurrent();
				String stemp = objDB.getTitle();			
				this.setVar("DbLogTitle", stemp);
			
				bReturn = true;
			}//END main
			
			
			//	Ziel soll es sein:
			//Über den false Rückgabewert der MEthode weiss die Aufrufende Methode, dass ein Fehler passiert ist.
			//Dieser Fehler wird ins log-protokolliert.
			//Es wird ein neutraler Fehlertext im Browser angezeigt.
			}catch(NotesException ne){
				ExceptionZZZ ez = new ExceptionZZZ(ne.text, iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
		}catch(ExceptionZZZ ez){
			LogZZZ objLog = this.getLogObject();
			objLog.WriteLineDate(ez.getDetailAllLast());
			bReturn = false;
		}
		return bReturn;
	}//END computeKernelDetail


public void recycle(){ 
	if(this.objKernelNotes!=null) this.objKernelNotes.recycle();
}
/* 
 * this method overwrites the compute-Method. 
 * Possible Variable-Placeholders can be replaced
 * The ECS-Elements will be added to the internal HashMap.
 * 
 * @see basic.zzzKernel.markup.content.KernelContentZZZ#compute()
 */
public boolean compute() throws ExceptionZZZ{
	
	boolean bReturn=false;
	main:{
	/*
	 * //TODO Was hier erstellt wird per jakarta.ecs erstellen. Das Erstellen soll in der Klasse DebugKernelInformation durchgeführt werden.
			out.println("<html><head>\n");			
			out.println("<title> Domino Kernel Information Servlet</title>");
			
			out.println("</head><body>");			
			out.println("FGL rulez" + "<br></br>" 
			+ "Request Method: " + req.getMethod() + "<br/>");
			
	 */
	try{
	//den title-tag
		title objECS_0 = new title("Domino Kernel Information Servlet");
		this.addElement("Title", objECS_0);
		
		
		
	//Baue eine einfache Seite, noch ohne Variablenersetzung
	H1 objECS_1 = new H1("Informationen über den Kernel:");
	this.addElement("Headerline", objECS_1);
	
	HR objECS_2 = new HR();
	this.addElement("HeaderlineUnderline", objECS_2);
	
	//KernelDetails	
	String sDBLogTitle = this.getVarString("DBLogTitle");
	StringElement objECS_3b = new StringElement("Titel der Log Datenbank: " + sDBLogTitle);
	//this.addElement("StringDBLogTitle", objECS_3b); //Merke: Die Variablen nicht sofort einsetzen, sondern erst die Schriftart ändern.
	
	//org.apache.ecs.html.BR
	BR objECS_4 = new BR();
	//this.addElement("BRDateTime", objECS_4);

	//Füge nun die Variable ein. Setze gleichzeitig die Schriftart, etc.				
	Font objECS_3a = new Font().setSize("-2").setColor(HtmlColor.RED);
	objECS_3a.addElement(objECS_3b);
	objECS_3a.addElement(objECS_4);
	this.addElement("KernelDetail", objECS_3a);

	
	bReturn = true;
	}catch(ExceptionZZZ ez){
		this.objException=ez;
		throw ez;
	}
	}//end main:
	return bReturn;
}


}

