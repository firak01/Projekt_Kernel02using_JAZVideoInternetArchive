package use.via.server.module.create;


import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;

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

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IConstantZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zKernel.KernelContextZZZ;
import basic.zKernel.html.writer.KernelWriterHtmlByEcsZZZ;
import basic.zKernel.html.writer.KernelWriterHtmlByFileZZZ;
import basic.zKernel.markup.content.ContentFileZZZ;
import basic.zNotes.kernel.NotesContextProviderZZZ;
import basic.zNotes.use.log4j.NotesReportContextProviderZZZ;
import basic.zNotes.use.log4j.NotesReportLogZZZ;

import basic.zKernel.KernelZZZ;
import custom.zKernel.LogZZZ;
import custom.zNotes.kernel.KernelNotesLogZZZ;
import custom.zNotes.kernel.KernelNotesZZZ;

import sun.servlet.http.HttpResponse;
import use.via.MapperStoreHttpZZZ;
import use.via.server.DocumentCategorizerZZZ;
import use.via.server.module.status.ContentDocumentCreatedPageZZZ;


/**Diese Klasse führt die (verschiedenen ?) Klassen zur Page Erstellung aus.
 *   Die erstellte Page wird in eine Datei gespeichert.
 * 
 * @author lindhaueradmin
 *
 */
public class ServletDocumentCreateVIA extends HttpServlet implements IConstantZZZ{
	private KernelZZZ objKernel = null;
	private KernelNotesZZZ objKernelNotes = null;
	private ContentDocumentCreatedPageZZZ objInfo= null;
	private KernelWriterHtmlByEcsZZZ objWriterHTML = null;
	private String sContentTypeUsed = "text/html";
	private HttpServletRequest req;
	private HttpServletResponse res;
	
	public void init(){
//Kernel-Objekt initialisieren
/*
	/try{
			NotesContextProviderZZZ objContext = new NotesContextProviderZZZ(this.objKernel, "KernelInformationServlet");
		    this.objKernelNotes = new KernelNotesZZZ(objContext, this.objKernel, "JAZTest", "01", null);
			
//			1. Objekt, das dann später an einen ContentWriter übergeben werden kann
			this.objInfo =  new ContentKernelInformationPageZZZ(objKernel);
			//In jedem doGet() erneut ausrechnen   objContentStore.compute();
			
	
			//In jedem doGet() erneut schreiben  objWriterHTML.addContent(objContentStore);

			
		}catch(ExceptionZZZ ez){
			if(this.objKernel!=null){
				LogZZZ objLog = this.objKernel.getLogObject();
				if(objLog!=null){
					objLog.WriteLineDate(ez.getDetailAllLast());
					ez.printStackTrace();
				}else{
					System.out.println(ez.getDetailAllLast()+"\n");
					ez.printStackTrace();
				}
			}else{
				System.out.println(ez.getDetailAllLast()+"\n");
				ez.printStackTrace();
			}
		}
					*/
	}
	
	public void destroy(){
		this.objInfo.recycle();
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
		System.out.println("LEITE AUF doPOST() um");
		doPost(req, res);
	}//END doGet
	
	
	public void doPost(HttpServletRequest req, HttpServletResponse res)throws IOException{
//		System.out.println("Leite auf doGET(...) um");
//		doGet(req, res);
		
		//Starten und den GET-String entgegennehmen
		Session session = null;
		HttpSession httpSession = null;
		this.setRequestObject(req);
		this.setResponseObject(res);
		
		PrintWriter out = res.getWriter();
		
		NotesContextProviderZZZ  objContext = null;
		KernelNotesLogZZZ objLogNotes = null;
		LogZZZ objLog = null;
		
		
		Document docCarrier=null;
		Document docFile = null;
		Document docSerie = null;
		Document docMovie = null;
		String sLog = null;
		
	
		try{			
			main:{
				try{								
					
					//### Parameter aus der URL (s. GET - Methode) entgegennehmen
					String sClassname = this.getClass().getName();					
					String sContentType = req.getParameter("ResultContentType"); //Es wird vom Client angegeben, ob er es ggf. im Format text/xml haben will (text/html", "text/xml")
					
					//### Debug - Ausgabe VOR dem Initialisieren des Kernels und Schreiben ins Log
					System.out.println("classname of servlet: '" + sClassname + "'");
					System.out.println("ResultContentType: '" + sContentType + "'");
					this.setContentTypeUsed(sContentType);
					
					//++++ Kernel-Objekt initialisieren: Merke: Wenn man das hier macht, wird auch jedes Mal beim "ButtonClick" ein neues Log erzeugt.
					String sSystemnr = req.getParameter("Systemnumber");
					if(StringZZZ.isEmpty(sSystemnr))	sSystemnr = "01";
								
					String sFileConfig = req.getParameter("Configurationfile");
					if(StringZZZ.isEmpty(sFileConfig)) sFileConfig = "ZKernelConfigVideoArchiveServlet.ini";
					
					String sPathConfig = req.getParameter("Configurationpath");
					if(StringZZZ.isEmpty(sPathConfig)) sPathConfig = "";
					
					System.out.println("Try to initialize KernelObject: " + this.getClass().getName() + ", Systemnr: " + sSystemnr + ", ConfigFile: " + sFileConfig);
					this.objKernel =  new KernelZZZ(this.getClass().getName(), sSystemnr, sPathConfig, sFileConfig,"DEBUG"); 
					if(this.objKernel!=null){
						sLog = "KernelObject initialized: " + this.getClass().getName() + ", Systemnr: " + sSystemnr + ", ConfigFile: " + sFileConfig;
						System.out.println(sLog);
						
						objLog = objKernel.getLogObject();
						objLog.WriteLineDate(sLog);
					}else{
						System.out.println("UNABLE TO INITIALIZE KernelObject: " + this.getClass().getName() + ", Systemnr: " + sSystemnr + ", ConfigFile: " + sFileConfig);						
					}
					//Erst nach der Kernel-Objekt initialisierung macht es Sinn irgendwelche Exceptions zu werfen....
					//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
					
					
//					#####################################################
					//### Authentifizierung
					//Wenn die Session Authentifizierung Disabled ist, dann soll hiermit durch das Servlet eine Anmeldung erzwungen werden.
					sLog = "Behandlung der Authentifizierung: Start.";
					objLog.WriteLineDate(sLog);
					System.out.println(sLog);	
					if(req.getHeader("Authorization")==null){			
						sLog = "Authorization check: No authorization-header available in HttpServletRequest.";
						objLog.WriteLineDate(sLog);
						System.out.println(sLog);
						
						httpSession = req.getSession(true);
						if(httpSession==null){
							sLog = "Authorization check: HttpSession IS NULL !!!.";
							objLog.WriteLineDate(sLog);
							System.out.println(sLog);
							
							//Versuch über Cookies die Session zu erzeugen !!!
							sLog = "Authorization check: Versuch über Cookies !!!.";		
							objLog.WriteLineDate(sLog);
							System.out.println(sLog);
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
//									FGL TODO GOON, Das muss konfigurierbar sein
									sLog = "VIADocumentCreateServletZZZ: Kann keinen IOR String holen von '192.168.3.102:63148'";
									objLog.WriteLineDate(sLog);
									
									//??? GEHT NICHT, obwohl ich ein Cookie bekommen habe. Fehlermeldung vom DominoServer 'not implemented'.   
									sLog = "VIADocumentCreateServletZZZ: Versuche NotesThread statisch zu initialisieren (1).";
									System.out.println(sLog);
									objLog.WriteLineDate(sLog);
									NotesThread.sinitThread();
									sLog = "VIADocumentCreateServletZZZ: NotesThread statisch initialisiert (1).";
									System.out.println(sLog);
									objLog.WriteLineDate(sLog);
									
									//FGL TODO GOON, Das muss konfigurierbar sein
									session = NotesFactory.createSession("192.168.3.102", req);

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
								         session = NotesFactory.createSession("", req);
								         //NotesThread.stermThread();
									}else{
										sLog = "VIADocumentCreateServletZZZ: Versuche LtpaToken an NotesFactoy.createSession() zu uebergeben.";
										objLog.WriteLineDate(sLog);
									       
								          NotesThread.sinitThread();
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
							objLog.WriteLineDate("Authorization check: HttpSession available in HttpServletRequest is new !!!.");		
							
							//FGL 20141127: Auch hier den Thread statisch initialiseren, sonst gibt es die Fehlermeldung
							//                       doGet#Cannot service method call on uninitialized thread
							sLog = "VIADocumentCreateServletZZZ: Versuche NotesThread statisch zu initialisieren (2).";
							System.out.println(sLog);
							objLog.WriteLineDate(sLog);
							NotesThread.sinitThread();
							sLog = "VIADocumentCreateServletZZZ: NotesThread statisch initialisiert (2).";
							System.out.println(sLog);
							objLog.WriteLineDate(sLog);
							
//							20141208: Fallls Benutzername und Kennwort vorhanden sind anwenden
							String sUsername = "lindhauer";
							String sPassword = "1fgl2fgl";
							if(!sUsername.equals("")){
								sLog = "VIADocumentCreateServletZZZ: Username provided: " + sUsername;
								System.out.println(sLog);
								objLog.WriteLineDate(sLog);	
								
							  session = NotesFactory.createSession((String)null, sUsername, sPassword);
							}else{
								sLog = "VIADocumentCreateServletZZZ: NO Username provided.";
								System.out.println(sLog);
								objLog.WriteLineDate(sLog);
								
								//FGL 20061116: Das ist der Normalfall, wenn man die Test-HTML-Page und den Button "zum ersten Mal" benutzt.
								//							Damit öffnet sich beim Benutzer eine Anmeldedialogbox										
								res.setStatus(HttpResponse.SC_UNAUTHORIZED);
								res.setHeader("WWW-Authentificate", "Basic");
								
								//FGL 20061116: Auf gar keinen Fall ein BREAK durchführen. Keine Ahnung warum nicht. Der Code wird auf jeden Fall nicht weiter ausgeführt.
								//                      Verwendet man untenstehende Befehle, so kommen blöde Meldungen im Browser. 
								//res.sendError(401); //SC_UNAUTHORIZED
								//break main;
								
								sLog = "VIADocumentCreateServletZZZ: Versuche NotesSession lokal zu erstellen (2).";
								System.out.println(sLog);
								objLog.WriteLineDate(sLog);							
								session = NotesFactory.createSession("", req);
							}
						}else{														
							//FGL 20061116: Das ist dann der Fall, wenn man die Test-HTML-Page und den Button "zum zweiten Mall" benutzt.
							//Merke: Ein erneuter Button-Click bewirkt, dass ein weiteres Log geschreiben wird.
							objLog.WriteLineDate("Authorization check: HttpSession available in HttpServletRequest is NOT new. Previous Authorization ?");	
							
							sLog = "VIADocumentCreateServletZZZ: Versuche NotesSession lokal zu erstellen (2).";
							System.out.println(sLog);
							objLog.WriteLineDate(sLog);
//							Damit öffnet sich beim Benutzer eine Anmeldedialogbox
							//res.setStatus(HttpResponse.SC_UNAUTHORIZED);
							//res.setHeader("WWW-Authentificate", "Basic");
							
							session = NotesFactory.createSession("", req);
						}
					}else{				
						objLog.WriteLineDate("Authorization check: Authorization available in HttpServletRequest.");
						
						
//						Damit öffnet sich beim Benutzer eine Anmeldedialogbox
						res.setStatus(HttpResponse.SC_UNAUTHORIZED);
						res.setHeader("WWW-Authentificate", "Basic");						
					}		
					objLog.WriteLineDate("Authorization check: Authorization successfull");
					
					sLog = "Behandlung der Authentifizierung: Ende.";
					objLog.WriteLineDate(sLog);
					System.out.println(sLog);	
					
					//#######################################################################
					//Nun das Log-Schreiben, sowohl im Kernel-Log, als auch im KernelNotesLog, als auch im NotesReportLog. Danach wird nur noch im NotesReportLog weitergeschreiben.
					sLog = "VIADocumentCreateServletZZZ: Start";
					objLog.WriteLineDate(sLog);
					
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
						sLog = "VIADocumentCreateServletZZZ: Unable to create session for user. Using local session.";
						System.out.println(sLog);
						objLog.WriteLineDate(sLog);
						
						objContext = new NotesContextProviderZZZ(this.objKernel, this.getClass().getName(), this.getClass().getName());
					}else{						;
						sLog = "VIADocumentCreateServletZZZ: Session created for user '" + session.getCommonUserName() + "'.";
						System.out.println(sLog);
						objLog.WriteLineDate(sLog);
						
						objContext = new NotesContextProviderZZZ(this.objKernel, this.getClass().getName(), this.getClass().getName(), session);
					}
					

					if (objContext == null) 
					{
						System.out.println("Unable to receive a valid NotesContextProviderZZZ - Object.");
						objLog.WriteLineDate("Unable to receive a valid NotesContextProviderZZZ - Object.");//TODO EXCEPTION WERFEN
					}else{
						System.out.println("NotesContextProviderZZZ - Object created.");
						objLog.WriteLineDate("NotesContextProviderZZZ - Object created.");
						if (objContext.getAgentContext() == null){
							System.out.println("Unable to receive a valid AgentContext from the  NotesContextProviderZZZ - Object.");
							objLog.WriteLineDate("Unable to receive a valid AgentContext from the  NotesContextProviderZZZ - Object.");							
						}else{
							System.out.println("AgentContext from  NotesContextProviderZZZ - Object received.");
							objLog.WriteLineDate("AgentContext from  NotesContextProviderZZZ - Object received.");
						}
						if (objContext.getSession() == null){
							System.out.println("Unable to receive a Notessession from the  NotesContextProviderZZZ - Object.");
							objLog.WriteLineDate("Unable to receive a Notessession from the  NotesContextProviderZZZ - Object.");
						}else{
							System.out.println("Notessession from  NotesContextProviderZZZ - Object received.");
							objLog.WriteLineDate("Notessession from  NotesContextProviderZZZ - Object received.");
						}
					}
					
					//NotesKernelObjekt
					System.out.println("Trying to initialize KernelNotesObject: KernelKey 'VIA', Systemnr: " + sSystemnr);
					this.objKernelNotes = new KernelNotesZZZ(objContext, "VIA", sSystemnr, null);
					if(this.objKernelNotes!=null){
						System.out.println("KernelNotesObject initialized: KernelKey 'VIA', Systemnr: " + sSystemnr);
					}else{
						System.out.println("UNABLE TO INITIALIZE KernelNotesObject: KernelKey 'VIA', Systemnr: " + sSystemnr); //TODO : ERROR Werfen
					}
				
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
					objLog.WriteLineDate("KernelNotesObject initialized. More logs will be written to the database: '" + dbLog.getFilePath() + "'");
					
					
					objLogNotes = this.objKernelNotes.getKernelNotesLogObject();
					objLogNotes.writeLog(sLog);
					
					NotesReportContextProviderZZZ objContextReport = new NotesReportContextProviderZZZ(objContext, this.objKernelNotes);
					NotesReportLogZZZ.loadKernelContext(objContextReport, true);
					NotesReportLogZZZ.write(NotesReportLogZZZ.INFO, sLog);
					
					sLog = "Request Method: " + req.getMethod() + "\n" +
								"Name des NotesSession-Users: '" + this.objKernelNotes.getSession().getUserName() + "\n" +
								"Name des HttpServletRequest-RemoteUsers: '" + req.getRemoteUser();								
					NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, sLog);
					
					//### NUN SIND DIE WERTE IN EIN DATASTOREZZZ-Objekt zu packen
					
					//TODO: Theoretisch wäre es möglich die Informationen aus der Notes-Kernel-Konfigurationsdatenbank, aus einem Activity-Dokument zu holen.					
					//Solange das aber nicht realisiert ist, und um schneller eine praktikable Lösung zu erreichen:
					//1. Parameter der .setValue(...) Methode ist der Alias der Maske/des DataStore
					//2.                                    "                          Alias des Felds, in dem DataStore.
					//3.                                    "                          der entgegengenommene Wert. Er wird in den für das Feld definierten Datentyp umgewandelt.
														
//					Nun die Data-Strukturen füllen
					//HashMap objHmMapping = MapperStoreHttpZZZ.loadFieldMapAllDefault();
					
					//Damit das zuweisen der HTTP-Parameter zu den DataStore-Objkten testbar wird, die HTTP-Parameter-Namen und die Werte in eine HashMap packen.
					//HashMap objHmHTTP = MapperStoreHttpZZZ.loadHTTPRequestValue(req);
															
					//Die HTTP-Parameter nun in das Mapper-Objekt packen. Intern werden verschiedene Hashmaps angelegt 
					MapperStoreServerVIA objMapper = new MapperStoreServerVIA(objKernelNotes);   // ServletDocumentCreateVIA.storeHTTPParam(this.objKernelNotes, objHmMapping, objHmHTTP);
					objLogNotes.writeLog("MapperStoreServerVIA - Objekt erzeugt");
					//HashMap objHmMapping = objMapper.getFieldMapAllDefault();//objMapper.loadFieldMapAllDefault();					
					//objMapper.storeHTTPParam(objHmMapping, objHmHTTP);
					
					objMapper.storeHttpParam(req); 
					objLogNotes.writeLog("MapperStoreServerVIA - ObjektMethode .storeHttpParam(req) ausgeführt.");
					
					
					/* Darin wird erst die statische Methode MapperStoreHttpZZZ.loadHttpRequestValue(req) ausgeführt und anschliessend
					 * die HashMap 
					 */ 
														
				    //### Werte, die in jedes Dokument kommen sollen und die aus den Umgebungsvariablen stammen bzw. eine Eigenschaft des Request-Objekts sind.
					//Merke: Es werden sonstige default-Leser/Autorenfelder automatisch beim erstellen mit derDocumentCreatorZZZ-Klasse eingefügt. 
					//Leserfelder
					String sValue = req.getRemoteUser();					
					if(sValue!=null){						
						NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "HTTP-Request-Property 'RemoteUser', trying to set: '" + sValue + "' as reader.");
						objMapper.setValue("Reader", sValue);
						NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "HTTP-Request-Property 'RemoteUser', successfully set: '" + sValue + "' as reader.");
					}
										
					//Autorenfelder
					sValue = req.getRemoteUser();		
					if(sValue!=null){
						NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "HTTP-Request-Property 'RemoteUser', trying to set: '" + sValue + "' as creator / author.");
						objMapper.setValue("Creator", sValue);
						NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "HTTP-Request-Property 'RemoteUser', successfully set: '" + sValue + "' as creator / author.");
					}
					
					sValue = req.getRemoteUser();		
					if(sValue!=null){
						NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "HTTP-Request-Property 'RemoteUser', trying to set: '" + sValue + "' as modifier / author.");
						objMapper.setValue("Modifier", sValue);
						NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "HTTP-Request-Property 'RemoteUser', successfully set: '" + sValue + "' as modifier / author.");
					}
					
					
					sValue = this.objKernelNotes.getSession().getUserName();   //Den Ersteller auch als Autor eintragen
					if(sValue!=null){
						NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "HTTP-Request-Property 'NotesSessionUser', trying to set: '" + sValue + "' as author.");
						objMapper.setValue("Author", sValue);
						NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "HTTP-Request-Property 'NotesSessionUser', successfully set: '" + sValue + "' as author.");
					}
												        
					Date objDate = new Date(); //Das ist damit das aktuelle Datum !!!
					NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "Server-Environment 'CurrentDate', trying to set: '" + sValue + "'");
					objMapper.setValue("ImportDate", objDate);
					NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "Server-Environment 'CurrentDate', successfully set: '" + sValue + "'");
					
					
					//#########################################################################
//					Spezialfälle
					boolean bIsSerie = false;
					if(!StringZZZ.isEmpty(req.getParameter("serietitle"))) bIsSerie = true;
					
					
					//### Objekte für die Verschiedenen zu erstellenden Dokumenttypen
					CarrierVIA objCreatorCarrier = new CarrierVIA(objKernelNotes, objMapper );
					FileVIA objCreatorFile = new FileVIA(objKernelNotes, objMapper);
					MovieVIA objCreatorMovie = new MovieVIA(objKernelNotes, objMapper);
					SerieVIA objCreatorSerie = new SerieVIA(objKernelNotes, objMapper);
					
					//### Validierung bzgl. der übergebenen Parameter
					objCreatorCarrier.validateMapperStore();
					objCreatorFile.validateMapperStore();
					objCreatorMovie.validateMapperStore();
					
					if(bIsSerie){
						objCreatorSerie.validateMapperStore();
					}
					
					
					//### Nun die Dokumente einzeln erstellen
					//TODO: Folgender Aspekt ist noch unberücksichtigt:
					//          Die Dokumente sollen ggf. zusätzlich noch als "Anhangsdokumente (Enclosure)" behandelt werden.
					//          D.h. es muss das Feld UniversalIdZZZ geben (mit der universalid des Dokuments in dem Feld),
					//          Der Wert dieses Feldes muss in ein Feld mit dem Aufbau Aliasname + "ID" + ApplikationKey kommen.
					//
					//           Dies entspricht dann einer Verknüpfung "anders herum" als die Kategoriesierung.
					
					
																	
					//+++ Carrier
					docCarrier = objCreatorCarrier.searchDocumentExisting();
					if(docCarrier==null){
						//!!! Bei der nun folgenden "Dokumenterzeugung" muss ggf. eine schon übergebenen CarrierID berücksichtigt werden
						 
						docCarrier = objCreatorCarrier.createDocument();		
					}else{
						objCreatorCarrier.updateDocument(session, docCarrier);						
					}
					DocumentCategorizerZZZ objCatCarrier = new DocumentCategorizerZZZ(objKernelNotes, docCarrier);
					
					//+++ File
					docFile = objCreatorFile.searchDocumentExisting();
					if(docFile==null){
						docFile = objCreatorFile.createDocument(docCarrier);  //Die IDCarrier wird zusätzlich zur Kategoriesierung noch übernommen
					}else{
						objCreatorFile.updateDocument(docFile, docCarrier);						
					}
					
					DocumentCategorizerZZZ objCatFile = new DocumentCategorizerZZZ(objKernelNotes, docFile);
					objCatFile.addDocumentAsCategory(docCarrier);     //d.h. unter dem CarrierDokument sollen dann alle File-Dokumente vorhanden sein.
																										//Es wird der Wert des FileDokuments von CatValFileName in das Carrier Dokument eingetragen 
					 objCatCarrier.addDocumentAsCategory(docFile);     //d.h. in den File Dokumenten wird es ein Feld objCatRefIdCarrier und ein Feld objCatRefCarrierTitle geben (werte des Carrier Dokuments: CatValIdCarrier und CatValCarrierTitle)
					
					//+++ Movie
					 if(bIsSerie){
						 docMovie = objCreatorMovie.searchDocumentExisting(objCreatorSerie); 
					 }else{
						 docMovie = objCreatorMovie.searchDocumentExisting(); 
					 }
					if(docMovie == null){
						docMovie = objCreatorMovie.createDocument(docCarrier);
					}else{ 
						objCreatorMovie.updateDocument(docMovie, docCarrier);						
						
						//TODO: FGL 200809 Falls ein weiterer Movie-Kommentar eingegeben worden ist, so muss er an das bestehende Bemerkungsfeld des Movies angehängt (ggf. vorneweg gestellt) werden
						//             Dies ist konfigurierbar zu machen, d.h. es muss schon bei der Definition des Feldnamens eine Steuerung geben, ob die bestehenden Werte a) ersetzt werden durch die neuen, b) angehängt, c) vornagestellt, d) unverändert bleiben
						//             Es muss ein Feedback geben, ob Werte auf diese Art verändert wurden.
						//             Aber: Merke, dass ein ggf. vorhandenes Movie Document aufgrund der erzeugten neuen Referenzen zu anderen Dokumenten eh abgespeichert werden wird.
						
					}
					//objCreatorMovie.setDocumentCurrent(docMovie);
					DocumentCategorizerZZZ objCatMovie = new DocumentCategorizerZZZ(objKernelNotes, docMovie);
                    objCatMovie.addDocumentAsCategory(docCarrier);     //d.h. unter dem CarrierDokument sollen dann alle Film-Dokumente vorhanden sein. Es wird der Kategorisierungswert des docMovie in das CarrierDokument eingetragen
                    objCatMovie.addDocumentAsCategory(docFile);
					
                    //Eintragen der Carrier und File Kategorisierungswerte in das MovieDokument
                    objCatCarrier.addDocumentAsCategory(docMovie);     //aber auch unter den Film Dokumenten sollen alle Carrier Dokumente stehen.
                    objCatFile.addDocumentAsCategory(docMovie);         //unter den Film Dokumenten sollen alle File-Dokumente stehen.
					
					
					//+++ Ggf. Serie.
					DocumentCategorizerZZZ objCatSerie = null;
					if(bIsSerie)	{
						docSerie = objCreatorSerie.searchDocumentExisting();
						if(docSerie==null){
							docSerie = objCreatorSerie.createDocument(docCarrier);
						}else{  
							objCreatorSerie.updateDocument(docSerie, docCarrier);
						}
						
						objCatSerie = new DocumentCategorizerZZZ(objKernelNotes, docSerie);
						objCatSerie.addDocumentAsCategory(docCarrier);//Eintragen der Serienkategorisierungswerte in das Carrierdocument  
						objCatSerie.addDocumentAsCategory(docFile);     //				dito
						objCatSerie.addDocumentAsCategory(docMovie); //				dito

						//Eintragen der Kategoriesierungswerte für File, Movie, Carrier in das Seriendokument
						objCatFile.addDocumentAsCategory(docSerie);       //					!!! Bei der Serie "Film" befürchte ich, dass die Kategorisierungsfelder zu groß werden können. Darum nix in ein SerienDokument reinschreiben, bzw. erst einmal beobachten.
						objCatMovie.addDocumentAsCategory(docSerie);  //					!!! Bei der Serie "Film" befürchte ich, dass die Kategorisierungsfelder zu groß werden können. Darum nix in ein SerienDokument reinschreiben, bzw. erst einmal beobachten.
						objCatCarrier.addDocumentAsCategory(docSerie); //					!!! Bei der Serie "Film" befürchte ich, dass die Kategorisierungsfelder zu groß werden können. Darum nix in ein SerienDokument reinschreiben, bzw. erst einmal beobachten.
					}
					
				
					
					//Speichern der Dokumente
					try{
						if(docCarrier != null){
							docCarrier.save();
							sLog = "Carrier document (" + docCarrier.getUniversalID() + ") saved in database: '" + docCarrier.getParentDatabase().getFilePath() + "' on server '" + docCarrier.getParentDatabase().getServer() + "'";
							//System.out.println(sLog);
							NotesReportLogZZZ.write(NotesReportLogZZZ.INFO, sLog);							
						}
					
						if(docFile != null){
							docFile.save();
							sLog = "File document (" + docFile.getUniversalID() + ") saved in database: '" + docFile.getParentDatabase().getFilePath() + "' on server '" + docFile.getParentDatabase().getServer() + "'";
							//System.out.println(sLog);
							NotesReportLogZZZ.write(NotesReportLogZZZ.INFO, sLog);							
						}
						
						if(docMovie != null){
							docMovie.save();
							sLog = "Movie document (" + docMovie.getUniversalID() + ") saved in database: '" + docMovie.getParentDatabase().getFilePath() + "' on server '" + docMovie.getParentDatabase().getServer() + "'";
							//System.out.println(sLog);
							NotesReportLogZZZ.write(NotesReportLogZZZ.INFO, sLog);													
						}
						
						if(docSerie != null){
							docSerie.save();
							sLog = "Serie document (" + docSerie.getUniversalID() + ") saved in database: '" + docSerie.getParentDatabase().getFilePath() + "' on server '" + docSerie.getParentDatabase().getServer() + "'";
							//System.out.println(sLog);
							NotesReportLogZZZ.write(NotesReportLogZZZ.INFO, sLog);
						}				
						
						
						//ALLE Antwortdokumentbeziehungen herstellen (das ist erst möglich, nachdem alle Dokumente einmal gespeichert worden sind.)
						//Merke: Es wird eine Antworthierarchie nur dann möglich, wenn das erste Dokument KEIN Antowritdokument ist (also kein $REF besitzt)
						if(!(docFile==null || docCarrier==null)){
							docFile.makeResponse(docCarrier);
							docFile.save();
						}
			
						
						/* Merke: Ein Dokument kann nicht mehrmals Antwortdokument sein
						if(!(docFile==null || docMovie==null)){
							docFile.makeResponse(docMovie);
							docFile.save();
						}
						if(!(docCarrier==null || docMovie == null)){
							docCarrier.makeResponse(docMovie);
							docCarrier.save();
						}
						*/
						
		
						if(!(docMovie==null || docSerie==null)){
							docMovie.makeResponse(docSerie);
							docMovie.save();
						}
						
					}catch(NotesException ne){
						ExceptionZZZ ez = new ExceptionZZZ(ne.text, iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
						throw ez;
					}
					
				
					//############ AUSGABE IM BROWSER bzw. im Swing-Textarea - Element
					
				//	/*  WICHTIG: NICHT LÖSCHEN. Zukünftiger AUSBAU !!!
					//FGL 20070414: Das funktioniert leider auf dem R6.5 Domino Server nicht. Eine Methode von org.w3c. ... wird nicht gefunden.
					//Die Methode, die nicht gefunden wird, wurde mit dem DOM-Level 2 eingeführt. 
					//Vielleicht funktioniert die Verwendung von Xerxes / JDOM /NekoHTML ja auf dem R7 Server.   ; FGL 20080222 Auf dem R8 Server funktioniert es.
					NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG,"KernelContextZZZ - Objekt erstellen für: '" + this.getClass().getName() + "' und '" + this.getClass().getName() +"'");
					KernelContextZZZ objKernelContext = new KernelContextZZZ(this.getClass().getName(), this.getClass().getName()); 
					NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG,"KernelContextZZZ - Objekt erfolgreich erstellt für: '" + this.getClass().getName() + "' und '" + this.getClass().getName() +"'");
					ResponseGeneratorVIA objGenerator = new ResponseGeneratorVIA(objKernelNotes, objKernelContext, null);					
					String sContent = objGenerator.generateContent4Success(docCarrier, docFile, docSerie, docMovie, this.getContentTypeUsed());
					NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, sContent);
					//out.println(sContent.toLowerCase());
					
//					Ausgabe im Browser. Hierbei kann der Client beim Aufruf des Servlets angeben, welchen ContentType er gerne hätte
					res.setContentType(this.getContentTypeUsed());
				
					//Merke: so geht es. Das Problem ist der Meta-Tag. Merke Dieser wird nicht richtig abgeschlossen.
					//sContent = "<HTML><HEAD><TITLE>Antwort des Servlets</TITLE></HEAD><BODY>Dies ist ein Test für die dt. Umlaute. Status:  CarrierID: </BODY></HTML>";
					out.println(sContent);
					out.flush();
					out.close();
				


					//   */
					
					/* Beispielsausgabe: !!! OHNE ZEILENUMBRÜCHE, AUCH NICHT AM ENDE
					
				<HTML><HEAD><META content="text/html; charset=ISO-8859-1" http-equiv="content-type" /><TITLE>Antwort des Servlets</TITLE></HEAD><BODY>Dies ist ein Test für die dt. Umlaute. <BR /><BR /><TABLE border="1" cellpadding="2" cellspacing="2" style="text-align: left; width: 496px; height: 144px;"><TBODY><TR><TD style="width: 106px;">Status:</TD><TD style="width: 861px;"><ZHTML>Datensätze erfolgreich verarbeitet</ZHTML></TD></TR><TR><TD style="width: 106px;">CarrierID:</TD><TD style="width: 861px;"><ZHTML>080106#1</ZHTML></TD></TR><TR><TD style="width: 106px;" /><TD style="width: 861px;" /></TR><TR><TD style="width: 106px;" /><TD style="width: 861px;" /></TR><TR><TD style="width: 106px;" /><TD style="width: 861px;" /></TR></TBODY></TABLE>
</BODY></HTML>					
					 */
					
		
					/*FGL 20080212 Versuche dies zu ersetzen 
					 * 
	
			//Erstellen der Ausgabe im Browser. Hier wird auch mal testweise ein zhtml-tag verwendet, das ja sonst als platzhalter für die "Variablen" ersetzung dient.
			//Merke: Das Taxtarea-Element in Swing unterdückt nicht ihm unbekannte tags. Wie z.B. in + "Success. CarrierID=<zhtml>" + docCarrier.getItemValueString("IDCarrier") + "</zhtml><br/>"
			//          Das ist ein Argument die Variablenersetztung mit einer rein textbasierten Klasse zu lösen. 
			String sContent =  "<html><head>\n"			
			+ "<title>Servlet Document Create - Response</title>"
			+ "</head><body>\n" 
			+ "Success. CarrierID=" + docCarrier.getItemValueString("IDCarrier")
			+ "<br></br>Carrier (Notes): " + docCarrier.getURL() //gibt die notes: URL
			+ "<br></br>Carrier (Http): " + docCarrier.getHttpURL();
			
			
			if(docFile!=null){
				sContent = sContent + "<br></br>File (Notes): " + docFile.getURL()
				+ "<br></br>File (Http): " + docFile.getHttpURL();
			}
			
			if(docMovie!=null){
				sContent = sContent + "<br></br>Movie (Notes): " + docMovie.getURL()
				+ "<br></br>Movie (Http): " + docMovie.getHttpURL();
			}
			
			if(docSerie!=null){
				sContent = sContent + "<br></br>Serie (Notes): " + docSerie.getURL()
				+ "<br></br>Serie (Http): " + docCarrier.getHttpURL();
			}
			
			sContent = sContent + "<br></br>More in log database:<br></br> "+dbLog.getURL()
			+ "<br></br>" + dbLog.getHttpURL();
			
			sContent = sContent + "<br></br>Application database:<br></br>"+docCarrier.getParentDatabase().getURL()
			+ "<br></br>" + docCarrier.getParentDatabase().getHttpURL();
			
			
			Database dbConfig = objKernelNotes.getDBConfigurationCurrent();
			sContent = sContent + "<br></br>Configuration database:<br></br>"+dbConfig.getURL()
			+ "<br></br>" + dbConfig.getHttpURL();
			
			
			sContent = sContent + "</body></html>";
			
//			Ausgabe im Browser
			res.setContentType("text/html");
			out.println(sContent);

			
			/*für debug - zwecke
		"Current Date: " + objDate.toString() + "<br/>" +
		"Request Method: " + req.getMethod() + "<br/>"  +
		"Parameter CarrierID: " + req.getParameter("carrierid") + "<br/>" +		
		"Parameter aus Kernel-Config File:" + this.objKernel.getParameter("KernelConfigVersion") + "<br/>" +
		"Name des NotesSession-Users: '" + this.objKernelNotes.getSession().getUserName() + "'" + "<br/>" +
		"Name des HttpServletRequest-RemoteUsers: '" + req.getRemoteUser() + "'" + "<br/>" +/*


			/* eine andere art eine Seite zu erstellen: Mit ECS
			ContentKernelInformationPageZZZ objContent = this.getContentPageCurrent();
			if(objContent!=null){
				objContent.compute();
				
				WriterHTMLZZZ objWriter = this.getHTMLWriter();
				objWriter.addContent(objContent);
				
				//TODO: Der Writer muss nun den Content an das  HttpServletRespnse Objekt übergeben.
			}*/
					
				
			}catch(NotesException ne){
				if (objLog != null) objLog.WriteLineDate(ReflectCodeZZZ.getMethodCurrentName() + "#" + ne.text);
				ExceptionZZZ ez = new ExceptionZZZ(ne.text, iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}finally{		 	
					//+++ Das letze Protokoll wieder überall schreiben. Merke: Ins finally, damit auch im Fehlerfall das log4j-Protokoll geschrieben wird.
					sLog = "ServletDocumentCreateVIA: End";
					if (objLog != null) objLog.WriteLineDate(sLog);
					if (objLogNotes!=null) objLogNotes.writeLog(sLog);
					NotesReportLogZZZ.write(NotesReportLogZZZ.INFO, sLog);
					NotesReportLogZZZ.endit(true);
					
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
			
			System.out.println("An error happend. Message from exception: " + t.getMessage());	
			t.printStackTrace();							
		}finally{
			
		}
	
		
	
		/*//DEBUG
		out.println("<html><head>\n");		
		out.println("<title> Domino Servlet KernelZZZ</title>");
		out.println("</head><body>");
		out.println("TEST, somit funktioniert lediglich die initierung des Servlets");
		out.println("</body></html>");
		*/		
		
	}//END doPost
	public ContentDocumentCreatedPageZZZ getContentPageCurrent() {
		return objInfo;
	}
	public void setContentPageCurrent(ContentDocumentCreatedPageZZZ objInfo) {
		this.objInfo = objInfo;
	}
	public KernelZZZ getKernelObject() {
		return objKernel;
	}
	public void setKernelObject(KernelZZZ objKernel) {
		this.objKernel = objKernel;
	}
	public KernelNotesZZZ getKernelNotesObject(){
		return this.objKernelNotes;
	}
	public void setKernelNotesObject(KernelNotesZZZ objKernelNotes){
		this.objKernelNotes = objKernelNotes;
	}
	
	public KernelWriterHtmlByEcsZZZ getHTMLWriter() throws ExceptionZZZ {
		if(this.objWriterHTML==null){
			org.apache.ecs.Document objDoc = new org.apache.ecs.Document();
			this.objWriterHTML = new KernelWriterHtmlByEcsZZZ(objKernel, objDoc, (String[]) null);
		}
		return this.objWriterHTML;
	}
	
	public void setHTMLWriter(KernelWriterHtmlByEcsZZZ objWriterHTML) {
		this.objWriterHTML = objWriterHTML;
	}
	
	public void setContentTypeUsed(String sContentType) throws ExceptionZZZ{
		String sLog = null;
		if(StringZZZ.isEmpty(sContentType)){
			sContentType = "text/html";
		}else if(! sContentType.equals("text/html") & ! sContentType.equals("text/xml")){
			sLog = "ContentType='" + sContentType + ", but expected 'text/html' or 'text/xml'";
			System.out.println(sLog);
			ExceptionZZZ ez = new ExceptionZZZ(sLog, iERROR_PARAMETER_VALUE, this, ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}
		sLog = "Verwende ContentType " + sContentType;
		System.out.println(sLog);
		this.sContentTypeUsed = sContentType;
		
		HttpServletResponse res = this.getResponseObject();
		if(res==null){
			System.out.println("ABER: ... Kein HttpServletResponseObject.");
		}else{
			System.out.println("HttpServletResponseObject vorhanden, setze ContentType.");
			res.setContentType(this.getContentTypeUsed());
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
		return "Wird von einer Swing basierten Applikation aus aufgerufen.";
	}
}
