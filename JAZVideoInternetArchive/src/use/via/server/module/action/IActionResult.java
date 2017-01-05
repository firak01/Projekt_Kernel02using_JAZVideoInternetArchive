package use.via.server.module.action;

import javax.servlet.http.HttpServletRequest;

import basic.zBasic.ExceptionZZZ;

public interface IActionResult {
	
	/** Die URL (konfigurierbar in der Kernel ini-Datei), die im Erfolgsfall geöffnet werden soll (d.h. als Umleitung des Requests.)
	* @param iResultCase
	* @return
	* @throws ExceptionZZZ
	* 
	* lindhauer; 25.04.2008 14:02:48
	 */
	public String getURLOnResult(int iResultCase) throws ExceptionZZZ;
	public HttpServletRequest getHttpServletRequest();
	public void setHttpServletRequest(HttpServletRequest objReq);
	
}
