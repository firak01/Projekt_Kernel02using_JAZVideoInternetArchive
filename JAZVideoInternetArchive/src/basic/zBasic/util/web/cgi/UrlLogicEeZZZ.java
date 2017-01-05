package basic.zBasic.util.web.cgi;

import javax.servlet.http.HttpServletRequest;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;

/**MERKE: Da HttpServletRequest ein Bestandteil von J2EE ist, und dieses .jar File in der ZKernel-Bibliothek nicht genutzt werden darf, 
 *               wird es hier eingebunden.
 * @author lindhaueradmin
 *
 */
public class UrlLogicEeZZZ extends UrlLogicZZZ {
	
//	Default Konstruktor
	public UrlLogicEeZZZ(){
		super();
	}
	public UrlLogicEeZZZ(String sUrl){
		super(sUrl);
	}
	
	/** HttpServletRequest.getProtocol()  liefert z.B. HTTP/1.1 zurück !!!
	 *  Diese Methode redudiert dies auf z.B. "http"
	 *  ABER: Da HttpServletRequest ein Bestandteil von J2EE ist, und dieses .jar File in der ZKernel-Bibliothek nicht genutzt werden darf,  
	 *             siehe: UrlLogicZZZ
	* @param objReq
	* @return
	* 
	* lindhaueradmin; 05.04.2009 11:57:51
	 * @throws ExceptionZZZ 
	 */
	public static String getProtocolSimple(HttpServletRequest objReq) throws ExceptionZZZ{
		String sReturn = "";
		main:{
			if(objReq==null){
					ExceptionZZZ ez = new ExceptionZZZ("No HttpServletRequest-Object  provided", iERROR_PARAMETER_MISSING,  UrlLogicEeZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName()); 
					throw ez;
			}
			
			String sProtocol = objReq.getProtocol();
			if(StringZZZ.isEmpty(sProtocol)) break main;
			
			sReturn = StringZZZ.left(sProtocol+" ", " ");
			
			sReturn = StringZZZ.left(sReturn+"/", "/");
			
			sReturn = sReturn.toLowerCase();
		}//End main:
		return sReturn;
		
	}
	
}
