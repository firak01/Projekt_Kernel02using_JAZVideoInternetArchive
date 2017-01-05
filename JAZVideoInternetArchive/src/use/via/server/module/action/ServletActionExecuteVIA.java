package use.via.server.module.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.NotesFactory;
import lotus.domino.NotesThread;
import lotus.domino.Session;
import sun.servlet.http.HttpResponse;
import use.via.server.IActionConstantZZZ;
import use.via.server.module.create.ResponseGeneratorVIA;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IConstantZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zKernel.KernelZZZ;
import basic.zNotes.document.DocumentZZZ;
import basic.zNotes.kernel.NotesContextProviderZZZ;
import basic.zNotes.use.log4j.NotesReportContextProviderZZZ;
import basic.zNotes.use.log4j.NotesReportLogZZZ;
import custom.zKernel.LogZZZ;
import custom.zNotes.kernel.KernelNotesLogZZZ;
import custom.zNotes.kernel.KernelNotesZZZ;

/** Klasse für ein Servlet, das die durchzuführende Aufgabe und eine UniversalId aus dem per GET übergebenen QueryString holt.
 *   Die angegebenen Aufgabe wird dann durchgefürht.
 *   Ebenfalls im QueryString enthalten sind die Angaben zur zu verwendenden Umgebung (Applikationkey, SystemNr, etc.) 
 * 
 */
public class ServletActionExecuteVIA  extends HttpServlet implements IConstantZZZ{
	private KernelZZZ objKernel = null;
	private KernelNotesZZZ objKernelNotes = null;

	private String sContentTypeUsed = null;
	private HttpServletRequest req;
	private HttpServletResponse res;
	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 * 
	 * 
	 * Diese 'Umleitung' ist wichtig, damit der Server nicht bei einem POST - AUfruf folgende Fehlermeldung schickt:
	 * POST is not supported by this URL
	 */
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException{
//Starten und den Get-String entgegennehmen.....
		
		Session session = null;
		HttpSession httpSession = null;
		this.setRequestObject(req);
		this.setResponseObject(res);
		
		PrintWriter out = res.getWriter();
		
		Document docCarrier=null;
		Document docFile = null;
		Document docSerie = null;
		Document docMovie = null;
		
		NotesContextProviderZZZ  objContext = null;
		KernelNotesLogZZZ objLogNotes = null;
		LogZZZ objLog = null;
		
	
		try{			
			main:{
				try{
					String sClassname = this.getClass().getName();
					
					//### Parameter aus der URL (s. GET-Methode) entgegennehmen
					//Systemnumber als Parameter entgegennehmen. Ggf default Werte setzen
					String sSystemnr = req.getParameter("Systemnumber");
					if(StringZZZ.isEmpty(sSystemnr))	sSystemnr = "01";
								
					String sFileConfig = req.getParameter("Configurationfile");
					if(StringZZZ.isEmpty(sFileConfig)) sFileConfig = "ZKernelConfigVideoArchiveServlet.ini";
					
					String sPathConfig = req.getParameter("Configurationpath");
					if(StringZZZ.isEmpty(sPathConfig)) sPathConfig = "";
					
					//Es wird vom Client angegeben, ob er es ggf. im Format des zurückzuliefernden Ergebnisses text/xml haben will (text/html", "text/xml")
					String sContentType = req.getParameter("ResultContentType");
					
					//### Debug - Ausgabe VOR dem Initialisieren des Kernels und Schreiben ins Log
					System.out.println("classname of servlet: '" + sClassname + "'");
					System.out.println("ResultContentType: '" + sContentType + "'");
					this.setContentTypeUsed(sContentType);
					
					//### Kernel-Objekt initialisieren: Merke: Wenn man das hier macht, wird auch jedesmall beim "ButtonClick" ein neues Log erzeugt.					
					System.out.println("Initializing KernelObject (ServletActionExecuteVIA)");
					this.objKernel =  new KernelZZZ(sClassname, sSystemnr, sPathConfig, sFileConfig,"DEBUG");  //durch das flag debug bekommt man mehr informationen
					objLog = objKernel.getLogObject();
					
//					Nun das Log-Schreiben, sowohl im Kernel-Log, als auch im KernelNotesLog, als auch im NotesReportLog. Danach wird nur noch im NotesReportLog weitergeschreiben.
					String sLog = "ServletActionExecuteVIA V02: Start";
					objLog.WriteLineDate(sLog);
					objLog.WriteLineDate("KernelObject initialized: " + sClassname + ", Systemnr: " + sSystemnr + ", ConfigFile: " + sFileConfig);
					
					
					//#####################################################
					//### Authentifizierung
					//Wenn die Session Authentifizierung Disabled ist, dann soll hiermit durch das Servlet eine Anmeldung erzwungen werden.
					//+++ Weiterer Parameter entgegennehmen
					//Es wird vom Client angegeben, ob er es ggf. im Format text/xml haben will (text/html", "text/xml")
					this.setContentTypeUsed(req.getParameter("ResultContentType"));
					
					
					//+++ Authentifizierung
					//Wenn die Session Authentifizierung Disabled ist, dann soll hiermit durch das Servlet eine Anmeldung erzwungen werden.
					if(req.getHeader("Authorization")==null){				
						objLog.WriteLineDate("Authorization check: No authorization-header available in HttpServletRequest.");
						
						httpSession = req.getSession(true);
						if(httpSession==null){
							objLog.WriteLineDate("Authorization check: HttpSession IS NULL !!!.");
							
						
							//Versuch über Cookies die Session zu erzeugen !!!
							objLog.WriteLineDate("Authorization check: Versuch über Cookies !!!.");				
							Cookie[] cookiea = null;
							String sSessionToken = null;
							String sSessionTokenName = null;
							
							cookiea = req.getCookies();
							if(cookiea == null){
								objLog.WriteLineDate("Authorization check: KEINE COOKIES VORHANDEN  !!!.");				
							}else{
								objLog.WriteLineDate("Authorization check: '" + cookiea.length + "' COOKIES VORHANDEN !!!.");
								if(cookiea.length >= 2){
									for(int i = 0; i < cookiea.length; i++){									
										if(cookiea[i].getName().equals("DomAuthSessId")){
												sSessionToken = cookiea[i].getValue();
												sSessionTokenName = "DomAuthSessId";
										}else if (cookiea[i].getName().equals("LtpaToken")){  //Merke: Das ist das Coookie, das Beim SSO über mehrere Server (s. Einstellung Serverdokument) verschickt wird.
											sSessionToken = cookiea[i].getValue();			
											sSessionTokenName = "LtpaToken";
										}
									}
								}else{
									objLog.WriteLineDate("Authorization check: Cookie vorhanden mit dem Namen '" + cookiea[0].getName() + "' und dem Wert: '" + cookiea[0].getValue() + "'");
									sSessionToken = cookiea[0].getValue();				
									sSessionTokenName = cookiea[0].getName();
								}
								
								if(StringZZZ.isEmpty(sSessionToken)){
									sLog = "VIADocumentCreateServletZZZ: Kann keinen IOR String holen von '192.168.3.103:63148'";
									objLog.WriteLineDate(sLog);
									
									//??? GEHT NICHT, obwohl ich ein Cookie bekommen habe. Fehlermeldung vom DominoServer 'not implemented'.   
									sLog = "VIADocumentCreateServletZZZ: Versuche NotesThread statisch zu initialisieren (1).";
									objLog.WriteLineDate(sLog);
									NotesThread.sinitThread();
									sLog = "VIADocumentCreateServletZZZ: NotesThread statisch initialisiert (1).";
									objLog.WriteLineDate(sLog);
									session = NotesFactory.createSession("192.168.3.103", req);

							        sLog = "";
							        sLog += "\n" + "Server:           " + session.getServerName();
							        sLog += "\n" + "IsOnServer:       " + session.isOnServer();
							        sLog += "\n" + "CommonUserName:   " + session.getCommonUserName();
							        sLog += "\n" + "UserName:         " + session.getUserName();
							        sLog += "\n" + "NotesVersion:     " + session.getNotesVersion();
							        sLog += "\n" + "Platform:         " + session.getPlatform();
							        objLog.WriteLineDate(sLog);
								       
							        //NotesThread.stermThread();					
								}else{
									if(sSessionTokenName.equalsIgnoreCase("DomAuthSessId")){
										sLog = "VIADocumentCreateServletZZZ: Behandel DomAuthSessId. Lies HttpRequest Header aus.";
										objLog.WriteLineDate(sLog);
										
										Enumeration enumHeader = req.getHeaderNames();
										sLog = "";																				
										while(enumHeader.hasMoreElements()){
											Object obj = enumHeader.nextElement();
											sLog += "\n" + obj.toString();
										}
										objLog.WriteLineDate(sLog);
									    
										 NotesThread.sinitThread();
										 sLog = "VIADocumentCreateServletZZZ: NotesThread statisch initialisiert (2).";
										 objLog.WriteLineDate(sLog);
								         session = NotesFactory.createSession("", req);
								         //NotesThread.stermThread();
									}else{
										sLog = "VIADocumentCreateServletZZZ: Versuche LtpaToken an NotesFactoy.createSession() zu uebergeben.";
										objLog.WriteLineDate(sLog);
									       
////										??? GEHT NICHT, obwohl ich ein Cookie bekommen habe. Fehlermeldung vom DominoServer 'not implemented'.   
//										sLog = "VIADocumentCreateServletZZZ: Versuche NotesThread statisch zu initialisieren (3).";
//										objLog.WriteLineDate(sLog);
//								          NotesThread.sinitThread();
//								          sLog = "VIADocumentCreateServletZZZ: NotesThread statisch initialisiert (3).";
//										  objLog.WriteLineDate(sLog);
								          session = NotesFactory.createSession(null, sSessionToken);
								          sLog = "";
								          sLog += "\n" + "Server:           " + session.getServerName();
								          sLog += "\n" + "IsOnServer:       " + session.isOnServer();
								          sLog += "\n" + "CommonUserName:   " + session.getCommonUserName();
								          sLog += "\n" + "UserName:         " + session.getUserName();
								          sLog += "\n" + "NotesVersion:     " + session.getNotesVersion();
								          sLog += "\n" + "Platform:         " + session.getPlatform();
								          objLog.WriteLineDate(sLog);
									       
								          //NotesThread.stermThread();
									}
								    }
							}// if cookiea == null
							
						}else if(httpSession.isNew()){
							//FGL 20061116: Das ist der Normalfall, wenn man die Test-HTML-Page und den Button "zum ersten Mal" benutzt.
							//							Damit öffnet sich beim Benutzer eine Anmeldedialogbox
							objLog.WriteLineDate("Authorization check: HttpSession available in HttpServletRequest is new (4)!!!.");					
							res.setStatus(HttpResponse.SC_UNAUTHORIZED);
							res.setHeader("WWW-Authentificate", "Basic");
							
							//FGL 20061116: Auf gar keinen Fall ein BREAK durchführen. Keine Ahnung warum nicht. Der Code wird auf jeden Fall nicht weiter ausgeführt.
							//                      Verwendet man untenstehende Befehle, so kommen blöde Meldungen im Browser. 
							//res.sendError(401); //SC_UNAUTHORIZED
							//break main;
							
//							??? GEHT NICHT, obwohl ich ein Cookie bekommen habe. Fehlermeldung vom DominoServer 'not implemented'.   
							sLog = "VIADocumentCreateServletZZZ: Versuche NotesThread statisch zu initialisieren (4).";
							objLog.WriteLineDate(sLog);
							NotesThread.sinitThread();		
							sLog = "VIADocumentCreateServletZZZ: NotesThread statisch initialisiert (4).";
							objLog.WriteLineDate(sLog);
							session = NotesFactory.createSession("", req);
						}else{
							//FGL 20061116: Das ist dann der Fall, wenn man die Test-HTML-Page und den Button "zum zweiten Mall" benutzt.
							//Merke: Ein erneuter Button-Click bewirkt, dass ein weiteres Log geschreiben wird.
							objLog.WriteLineDate("Authorization check: HttpSession available in HttpServletRequest is NOT new (5). Previous Authorization ?");	
							
//							Damit öffnet sich beim Benutzer eine Anmeldedialogbox
							//res.setStatus(HttpResponse.SC_UNAUTHORIZED);
							//res.setHeader("WWW-Authentificate", "Basic");
//							??? GEHT NICHT, obwohl ich ein Cookie bekommen habe. Fehlermeldung vom DominoServer 'not implemented'.   
							sLog = "VIADocumentCreateServletZZZ: Versuche NotesThread statisch zu initialisieren (5).";
							objLog.WriteLineDate(sLog);
							NotesThread.sinitThread();
							sLog = "VIADocumentCreateServletZZZ: NotesThread statisch initialisiert (5).";
							objLog.WriteLineDate(sLog);
							session = NotesFactory.createSession("", req);
						}
					}else{				
						objLog.WriteLineDate("Authorization check: Authorization available in HttpServletRequest.");
						
						
//						Damit öffnet sich beim Benutzer eine Anmeldedialogbox
						res.setStatus(HttpResponse.SC_UNAUTHORIZED);
						res.setHeader("WWW-Authentificate", "Basic");
						
					}		
					objLog.WriteLineDate("Authorization check: Authorization successfull");
					
					
					//TODO: In einem ersten Schritt muss nun der request geprüft werden, ob darin nicht Parameter enthalten sind, die den Kernel auf eine andere Konfiguration "lenken."
					//   Also: 1. Es muss einen MapperStoreKernelServerZZZ geben
					//              Dieser muss nicht validiert werden. Falls keine entsprechenden Werte übergeben worden sind, wird einfach der Wert aus dem aktuellen Kernel-Objekt übernommen.
					//              Übergebbar sind: ApplikationKey,  Pfad, iniDateiname, SystemNumber, FlagControl  (Merke: letzteres ggf. mal als Mehrfachwert per JSON versuchen zu übergeben.
					//           2. Diese Werte müssen dann für einen Konstruktor eines neuen KernelObjekts verwendet werden, mit dem im Folgenden weitergearbeitet wird.
					                 
					
					
					
					//NotesContext
					//Merke: Anders als für einen Agenten gibt es für ein Servlet keinen "Context". Dieser muss durch eine spezielle Zwischenschicht "gefaked" werden. Das Fake basiert darauf, dass die konfigurierte Applikationsdatenbank quasi als "CurrentDatabase" verwendet wird.
				
			/* so gibt es immer einen unerklärlichen absturz beim erzeugen des Context-Objekts, obwohl session.username funktioniert !!!
					//20080127 Für die Session wird nun nicht mehr die lokale session verwendet, sondern es wird eine Session aus dem HttpReques-Objekt generiert.
					NotesThread.sinitThread();
					sLog = "VIADocumentCreateServletZZZ: NotesThread initialisiert.";
					objLog.WriteLineDate(sLog);
					
					//
					session = NotesFactory.createSession(null, req);
					
					//Session session = NotesFactory.createSession("fgl02/server/fgl/DE", req);
					//Session session = NotesFactory.createSession("172.16.0.102", req);
					//Session session = NotesFactory.createSession("fgl02", req);
			//		*/
					
					if(session == null){
						sLog = "ServletActionExecuteVIA: Unable to create session for user. Using local session.";
						objLog.WriteLineDate(sLog);
						
						objContext = new NotesContextProviderZZZ(this.objKernel, this.getClass().getName(), this.getClass().getName());
					}else{
						sLog = "ServletActionExecuteVIA: Session created for user '" + session.getCommonUserName() + "'.";
						objLog.WriteLineDate(sLog);
						
						objContext = new NotesContextProviderZZZ(this.objKernel, this.getClass().getName(), this.getClass().getName(), session);
					}
					

					if (objContext == null) 
					{
						objLog.WriteLineDate("Unable to receive a valid NotesContextProviderZZZ - Object.");//TODO EXCEPTION WERFEN
					}else{
						objLog.WriteLineDate("NotesContextProviderZZZ - Object created.");
						if (objContext.getAgentContext() == null){
							objLog.WriteLineDate("Unable to receive a valid AgentContext from the  NotesContextProviderZZZ - Object.");							
						}else{
							objLog.WriteLineDate("AgentContext from  NotesContextProviderZZZ - Object received.");
						}
						if (objContext.getSession() == null){
							objLog.WriteLineDate("Unable to receive a Notessession from the  NotesContextProviderZZZ - Object.");
						}else{
							objLog.WriteLineDate("Notessession from  NotesContextProviderZZZ - Object received.");
						}
					}
					
					//NotesKernelObjekt
					this.objKernelNotes = new KernelNotesZZZ(objContext, "VIA", sSystemnr, null);
					objLog.WriteLineDate("KernelNotesObject initialized.");
					Database dbLog = this.objKernelNotes.getDBLogCurrent();	
					if(dbLog==null){
						sLog = "dbLog==null";
						System.out.println(sLog);
					}else{
						if(dbLog.isOpen()==false){
							sLog = "Unable to open dbLog";
							System.out.println(sLog);
						}else{
							sLog = "dbLog found and able to open: " + dbLog.getServer() + "!!" + dbLog.getFilePath();
							System.out.println(sLog);
						}
					}
					
					
					//NotesKernelLogObjekt - Als die nächste Ebene der Logs
					objLogNotes = this.objKernelNotes.getKernelNotesLogObject();
					objLogNotes.setKernelNotesObject(this.objKernelNotes);                 //!!!! Das müßte eigentlich schon im Konstruktor des KernelNotes-Objekts und im Konstruktor des KernelNotesLog-Objekts gemacht worden sein.
					if(objLogNotes !=null){
						objLog.WriteLineDate("KernelNotesLogObject available");
						if(objLogNotes.getKernelNotesObject()!=null){
							objLog.WriteLineDate("KernelNotesLogObjekt enthält das KernelNotesObjekt.");
						}else{
							objLog.WriteLineDate("KernelNotesLogObjekt enthält KEIN KernelNotesObjekt.");
						}
						objLog.WriteLineDate("KernelNotesLogObject LogLevel: '" + objLogNotes.getLogLevelGlobal() + "'. More logs will be written to the database: '" + dbLog.getFilePath() + "'");
						
						objLogNotes.writeLog("ServletActionExecuteVIA: Started");
						objLogNotes.writeLog(sLog);						
					}else{
						objLog.WriteLineDate("KernelNotesLogObject NOT available");
					}									
					
					//NotesReportLogObjekt - Als die nächste Ebene der Logs
					NotesReportContextProviderZZZ objContextReport = new NotesReportContextProviderZZZ(objContext, this.objKernelNotes);
					NotesReportLogZZZ.loadKernelContext(objContextReport, true);
					NotesReportLogZZZ.write(NotesReportLogZZZ.INFO, "ServletActionExecuteVIA: Started");
					NotesReportLogZZZ.write(NotesReportLogZZZ.INFO, sLog);
					
					Enumeration reqEnum = req.getParameterNames();
					String sParamAll = "";
					while(reqEnum.hasMoreElements()){
						sParamAll = sParamAll + (String) reqEnum.nextElement() + "; ";
					}
					
					String sQuery = req.getQueryString();
					
					sLog = "Request Method: " + req.getMethod() + "\n" +
								"Name des NotesSession-Users: '" + this.objKernelNotes.getSession().getUserName() + "'\n" +
								"Name des HttpServletRequest-RemoteUsers: '" + req.getRemoteUser() + "\n" +
								"Protokoll des Requests: '" + req.getProtocol() + "'\n" +
								"Parameter des Requests: '" + sParamAll +"'\n" +
								"QueryString des Requests: '" + sQuery + "'";
					NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, sLog);
					
					
					//#################################################################################
					//### Die eigentliche Aufgabe erledigen
					
					System.out.println("+++++++++ ACTION START +++++++++++++++++++++++++++++++++++++");
					
					
					//1. Parameter einlesen
					String sAction = req.getParameter("action");
					int iResultCase = IActionConstantZZZ.iFALSE_CASE;
					String sUrl = null;
					if(StringZZZ.isEmpty(sAction)){
						ExceptionZZZ ez = new ExceptionZZZ("No 'action' as parameter provided", iERROR_PARAMETER_MISSING, this.getClass().getName(), ReflectCodeZZZ.getMethodCurrentName());
						throw ez;
					}
					if(! sAction.equalsIgnoreCase(IActionConstantZZZ.sACTION_DELETE) & ! sAction.equalsIgnoreCase(IActionConstantZZZ.sACTION_UPDATE) & ! sAction.equalsIgnoreCase(IActionConstantZZZ.sACTION_SEARCH_BY_CATEGORY)){
						ExceptionZZZ ez = new ExceptionZZZ("Unexpecte 'action='" + sAction + " as parameter provided. Expected '" + IActionConstantZZZ.sACTION_DELETE + "', '" + IActionConstantZZZ.sACTION_UPDATE + "', '" + IActionConstantZZZ.sACTION_SEARCH_BY_CATEGORY + "'", iERROR_PARAMETER_MISSING, DocumentZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
						throw ez;
					}
					if (sAction.equalsIgnoreCase(IActionConstantZZZ.sACTION_DELETE)){
						//+++ ACTION: DELETE
						ActionDeleteVIA objAction = new ActionDeleteVIA(objKernelNotes, req, "DEBUG");
						iResultCase= objAction.start();
						if(iResultCase==IActionConstantZZZ.iSUCCESS_CASE) sUrl = objAction.getURLOnResult(IActionConstantZZZ.iSUCCESS_CASE);
					}else if(sAction.equalsIgnoreCase(IActionConstantZZZ.sACTION_UPDATE)){
						//+++ ACTION: UPDATE
						String[] saFlag = {"DEBUG","dont_save_intital_document"};
						ActionFieldUpdateVIA objAction = new ActionFieldUpdateVIA(objKernelNotes, req, saFlag);
						iResultCase = objAction.start();
						if(iResultCase==IActionConstantZZZ.iSUCCESS_CASE) sUrl = objAction.getURLOnResult(IActionConstantZZZ.iSUCCESS_CASE);
					}else if(sAction.equalsIgnoreCase(IActionConstantZZZ.sACTION_SEARCH_BY_CATEGORY)){
						//+++ ACTION: SEARCH BY CATEGORY
						ActionSearchByCategoryVIA objAction = new ActionSearchByCategoryVIA(objKernelNotes, req, "DEBUG");
						iResultCase = objAction.start();
						if(iResultCase==IActionConstantZZZ.iSUCCESS_CASE) sUrl = objAction.getURLOnResult(IActionConstantZZZ.iSUCCESS_CASE);
					}
					if(iResultCase == IActionConstantZZZ.iSUCCESS_CASE){
						//TODO Goon: einen response-Generator verwenden, oder eine feste Page in der Applikation
						objKernelNotes.getKernelNotesLogObject().writeLog("Found Url for redirect: '" + sUrl + "'", 3);
						
					   res.sendRedirect(sUrl);
					   
						/*
						out.println("<html><head>\n");	
						out.println("<meta http-equiv=\"content-type\" content=\"text/html; charset=ISO-8859-1\">");
						out.println("<title> Domino Servlet KernelZZZ - Action Document Delete</title>");
						out.println("</head><body>");		
						out.println("Document deletion was successful.");
						out.println("</body></html>");
						*/
					}else{
						out.println("<html><head>\n");	
						out.println("<meta http-equiv=\"content-type\" content=\"text/html; charset=ISO-8859-1\">");
						out.println("<title> Domino Servlet KernelZZZ - Action " + sAction + "</title>");
						out.println("</head><body>");		
						out.println("NO document was processed by the action '" + sAction + "'.<BR>Either the document was not available or an error has happend.");
						out.println("</body></html>");
					}
				}catch(ExceptionZZZ ez){
					if(objLog!=null)	objLog.WriteLineDate(ez.getDetailAllLast());
					System.out.println("An error happend. Message from exceptionzzz V02: " + ez.getMessage());	
					ez.printStackTrace();
					String sOutput = "";
					try{
						if(this.getContentTypeUsed().equals("text/xml")){
							//System.out.println("Error handling for xml");
							sOutput = ResponseGeneratorVIA.generateContent4ErrorXml(ez);
						}else{
							//System.out.println("Error handling for html");
							sOutput = ResponseGeneratorVIA.generateContent4ErrorHtml(ez);
						}
					}catch(Throwable t){
						sOutput = "An error in the error handling happened: '" + t.getMessage() + "'";
						System.out.println(sOutput);
						t.printStackTrace();					
					}
					out.println(sOutput);
				}catch(NotesException ne){
					System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "#" + ne.text);
					if (objLog != null) objLog.WriteLineDate(ReflectCodeZZZ.getMethodCurrentName() + "#" + ne.text);
					ExceptionZZZ ez = new ExceptionZZZ(ne.text, iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}finally{		 	
						//+++ Das letze Protokoll wieder überall schreiben. Merke: Ins finally, damit auch im Fehlerfall das log4j-Protokoll geschrieben wird.
						String sLog = "ServletActionExecuteVIA: End";
						System.out.println(sLog);
					
						if (objLog != null) objLog.WriteLineDate(sLog);
						if (objLogNotes!=null) objLogNotes.writeLog(sLog);
						
						if(objContext!=null){
							NotesReportLogZZZ.write(NotesReportLogZZZ.INFO, sLog);
							NotesReportLogZZZ.endit(true);
						}
						
					try{					
						//+++ Versuche die Dokumente, Session, etc. wieder freizugeben
						if(docCarrier != null) docCarrier.recycle();
						if(docMovie != null) docMovie.recycle();
						if(docFile != null) docFile.recycle();
						if(docSerie != null) docSerie.recycle();
										
						if(objContext != null) objContext.recycle();
					}catch(NotesException ne){
						ExceptionZZZ ez = new ExceptionZZZ(ne.text, iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
						throw ez;
					}
				}//END finally
			}//END main:

			
			
			}catch( ExceptionZZZ ez){
				//TODO 20080318 Wenn ein Fehler geworfen wird, dann darf nun nicht mehr pauschal html zurückgegeben werden. Falls der Client XML angefordert hat, muss XML zurückgegeben werden.
				//                             Die Fehlermeldung gehört dann in das Statusmessage-Tag.
				//                             Merke: 
				
				
				if(objLog!=null)	objLog.WriteLineDate(ez.getDetailAllLast());
				System.out.println("An error happend. Message from exceptionzzz: " + ez.getMessage());	
				ez.printStackTrace();
				String sOutput = "";
				try{
					if(this.getContentTypeUsed().equals("text/xml")){
						//System.out.println("Error handling for xml");
						sOutput = ResponseGeneratorVIA.generateContent4ErrorXml(ez);
					}else{
						//System.out.println("Error handling for html");
						sOutput = ResponseGeneratorVIA.generateContent4ErrorHtml(ez);
					}
				}catch(Throwable t){
					sOutput = "An error in the error handling happened: '" + t.getMessage() + "'";
					System.out.println(sOutput);
					t.printStackTrace();					
				}
				out.println(sOutput);
			}catch (Throwable t){
				if(objLog!=null) objLog.WriteLineDate("An error happend. Message from exception: " + t.getMessage());	
				
				out.println("<html><head>\n");	
				out.println("<meta http-equiv=\"content-type\" content=\"text/html; charset=ISO-8859-1\">");
				out.println("<title> Domino Servlet KernelZZZ</title>");
				out.println("</head><body>");		
				out.println("An error happend. Message from exception: " + t.getMessage() + "<br>" + ExceptionZZZ.computeHtmlFromStackTrace(t));
				out.println("</body></html>");
				
				System.out.println("An error happend. Message from exception V02b: " + t.getMessage());	
				t.printStackTrace();							
			}finally{
				
			}
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException{	
		Enumeration reqEnum = req.getParameterNames();
		String sParamAll = "";
		while(reqEnum.hasMoreElements()){
			sParamAll = sParamAll + (String) reqEnum.nextElement() + "; ";
		}
		
		String sQuery = req.getQueryString();
		
		String sLog = "Request Method: " + req.getMethod() + "\n" +
					"Name des HttpServletRequest-RemoteUsers: '" + req.getRemoteUser() + "\n" +
					"Protokoll des Requests: '" + req.getProtocol() + "'\n" +
					"Parameter des Requests: '" + sParamAll +"'\n" +
					"QueryString des Requests: '" + sQuery + "'";
		System.out.println(sLog);
		System.out.println("LEITE AUF doPOST() um V02");
		doPost(req, res);
	}
	
	public void setContentTypeUsed(String sContentType) throws ExceptionZZZ{
		if(StringZZZ.isEmpty(sContentType)){
			this.sContentTypeUsed = "text/html";
		}else if(! sContentType.equals("text/html") & ! sContentType.equals("text/xml")){
			ExceptionZZZ ez = new ExceptionZZZ("ContentType='" + sContentType + ", but expected 'text/html' or 'text/xml'", iERROR_PARAMETER_VALUE, this, ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}else{
			this.sContentTypeUsed = sContentType;
			this.getResponseObject().setContentType(this.getContentTypeUsed());
		}
	}
		public String getContentTypeUsed(){
			return this.sContentTypeUsed;
		}    
		public void setRequestObject(HttpServletRequest objReq){
			this.req=objReq;
		}
		public HttpServletRequest getRequestObject(HttpServletRequest objReq){
			return this.req;
		}
		public void setResponseObject(HttpServletResponse objRes){
			this.res=objRes;
		}
		public HttpServletResponse getResponseObject(){
			return this.res;
		}	
		public String getServletInfo(){
			return "Ajax wird damit ausgeführt.";
		}

}//End class
