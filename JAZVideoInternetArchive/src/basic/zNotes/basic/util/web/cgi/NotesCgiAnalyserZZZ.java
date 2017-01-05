package basic.zNotes.basic.util.web.cgi;

import javax.servlet.http.HttpServletRequest;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IConstantZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.web.cgi.UrlLogicEeZZZ;
import basic.zBasic.util.web.cgi.UrlLogicZZZ;

/**MERKE: Da HttpServletRequest ein Bestandteil von J2EE ist, und dieses .jar File in der ZKernel-Bibliothek nicht genutzt werden darf, 
 *               wird es hier eingebunden.
 * @author lindhaueradmin
 *
 */
public class NotesCgiAnalyserZZZ   implements IConstantZZZ {

	private NotesCgiAnalyserZZZ(){
		//Zum "Verstecken" des Konstruktors
	}
	
	public static String getHttpUrl(HttpServletRequest objReq, Database db) throws ExceptionZZZ{
		String sReturn = "";
		main:{
			try{
				if(db==null){
					ExceptionZZZ ez = new ExceptionZZZ("No Notesdatabase-Object  provided", iERROR_PARAMETER_MISSING,  NotesCgiAnalyserZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName()); 
					throw ez;
				}
				
				sReturn = db.getHttpURL();
				if(StringZZZ.isEmpty(sReturn)) break main;
				
				String sHost = objReq.getServerName();
				String sProtocol = UrlLogicEeZZZ.getProtocolSimple(objReq); //liefert z.B. HTTP/1.1 zurück !!!  objReq.getProtocol();
				
				String sPath = db.getHttpURL();
				sPath = UrlLogicZZZ.getPath(sPath); //Merke: führender Slash !!!
				
				sReturn = sProtocol + UrlLogicZZZ.sURL_SEPARATOR_PROTOCOL + sHost +  sPath;
		
			}catch(NotesException ne){
				ExceptionZZZ ez = new ExceptionZZZ("NotesException: '" + ne.text +"'", iERROR_RUNTIME, NotesCgiAnalyserZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
		}//ENd main:
		return sReturn;
	}
	
	public static String getHttpUrl(HttpServletRequest objReq, Document doc) throws ExceptionZZZ{
		String sReturn = "";
		main:{
			try{
				if(doc==null){
					ExceptionZZZ ez = new ExceptionZZZ("No Notesdocument-Object  provided", iERROR_PARAMETER_MISSING,  NotesCgiAnalyserZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName()); 
					throw ez;
				}
				
				sReturn = doc.getHttpURL();
				if(StringZZZ.isEmpty(sReturn)) break main;
				
				String sHost = objReq.getServerName();
				String sProtocol = UrlLogicEeZZZ.getProtocolSimple(objReq); //liefert z.B. HTTP/1.1 zurück !!!  objReq.getProtocol();
				
				String sPath = doc.getHttpURL();
				sPath = UrlLogicZZZ.getPath(sPath); //Merke: führender Slash !!!
				
				sReturn = sProtocol + UrlLogicZZZ.sURL_SEPARATOR_PROTOCOL + sHost +  sPath;
		
			}catch(NotesException ne){
				ExceptionZZZ ez = new ExceptionZZZ("NotesException: '" + ne.text +"'", iERROR_RUNTIME, NotesCgiAnalyserZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
		}//ENd main:
		return sReturn;
	}
	
}
