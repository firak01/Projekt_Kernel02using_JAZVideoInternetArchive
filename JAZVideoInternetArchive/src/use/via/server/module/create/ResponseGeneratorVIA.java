package use.via.server.module.create;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import lotus.domino.Document;
import lotus.domino.DxlExporter;
import lotus.domino.NotesException;
import lotus.domino.Session;

import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zKernel.KernelContextZZZ;
import basic.zKernel.KernelZZZ;
import basic.zKernel.html.writer.KernelWriterHtmlByFileZZZ;
import basic.zKernel.html.writer.KernelWriterXmlByContentZZZ;
import basic.zKernel.markup.content.ContentFileZZZ;
import basic.zKernel.markup.content.ContentXmlZZZ;
import basic.zNotes.kernel.KernelNotesUseObjectZZZ;
import basic.zNotes.use.log4j.NotesReportLogZZZ;
import custom.zNotes.kernel.KernelNotesZZZ;

public class ResponseGeneratorVIA extends KernelNotesUseObjectZZZ{	
   public ResponseGeneratorVIA(KernelNotesZZZ objKernelNotes, KernelContextZZZ objKernelSection, String[] saFlag) throws ExceptionZZZ{
	   super(objKernelNotes, saFlag);
	   ServletRespondGeneratorNew_(objKernelSection, saFlag);
   }
   
   private void ServletRespondGeneratorNew_(KernelContextZZZ objKernelSection, String[] saFlagControl) throws ExceptionZZZ{
	   main:{
		   if(saFlagControl != null){
				String stemp; boolean btemp;
				for(int iCount = 0;iCount<=saFlagControl.length-1;iCount++){
					stemp = saFlagControl[iCount];
					btemp = setFlag(stemp, true);
					if(btemp==false){ 								   
						   ExceptionZZZ ez = new ExceptionZZZ(stemp, iERROR_FLAG_UNAVAILABLE, this, ReflectCodeZZZ.getMethodCurrentName()); 						 
						   throw ez;		 
					}
				}
				if(this.getFlag("init")==true) break main;
			}
		    this.setContextUsed(objKernelSection);
		   
	   }//End main:	 
   }
   
   /** Erzeuge eine Xml-Fehlermeldung, bei der die Fehlermeldung der Exception in das Statusmessage-Tag kommt.
    * Dabei wird auf den Einsatz weiterer Klassen verzichtet, um die Wahrscheinlichkeit zu minimieren, dass in dieser Fehlerbehandlungsroutine selbst ein Fehler auftritt.
    * Es wird sogar auf weitere Parameter im Methodenaufruf verzichtet, damit ein fehlender Parameter keinen Fehlerfall erzeugen kann.
    * Der Verzicht auf den Einsatz weiterer Klassen bewirkt auch, dass diese Methode static sein muss.
* @param ez
* @return
* @throws ExceptionZZZ
* 
* lindhaueradmin; 19.03.2008 07:04:21
 */
public static String generateContent4ErrorXml(ExceptionZZZ ez) throws ExceptionZZZ{
	   String sReturn = null;
	   main:{
		   String sTrace = ExceptionZZZ.computeStringFromStackTrace(ez);
		  
		   sReturn = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n";
		   sReturn += "<ZMessage><statusmessage>";
		   sReturn += "An error happend. Message from exceptionzzz: "+ ez.getDetailAllLast() + "\n";
		   sReturn += sTrace; 
		   sReturn += "</statusmessage><dummy>Muss gesetzt werden. Damit statusmessage als element unter dem rootelement erkannt wird. jdom - problem !</dummy></ZMessage>";		   		
	   }//end main:
	   return sReturn;
   }

/** Erzeuge eine Html-Fehlermeldung, bei der die Fehlermeldung der Exception in das Statusmessage-Tag kommt.
 * Dabei wird auf den Einsatz weiterer Klassen verzichtet, um die Wahrscheinlichkeit zu minimieren, dass in dieser Fehlerbehandlungsroutine selbst ein Fehler auftritt.
 * Es wird sogar auf weitere Parameter im Methodenaufruf verzichtet, damit ein fehlender Parameter keinen Fehlerfall erzeugen kann.
 * Der Verzicht auf den Einsatz weiterer Klassen bewirkt auch, dass diese Methode static sein muss.
* @param ez
* @return
* @throws ExceptionZZZ
* 
* lindhaueradmin; 19.03.2008 07:04:21
*/
public static String generateContent4ErrorHtml(ExceptionZZZ ez) throws ExceptionZZZ{
	   String sReturn = null;
	   main:{
		   	String sTrace = ExceptionZZZ.computeHtmlFromStackTrace(ez);
		   		   
			sReturn = "<html><head>\n";	
			sReturn += "<meta http-equiv=\"content-type\" content=\"text/html; charset=ISO-8859-1\">";
			sReturn += "<title> Domino Servlet KernelZZZ</title>";
			sReturn +="</head><body>";
			sReturn += "An error happend. Message from exceptionzzz: "+ ez.getDetailAllLast() + "<br>";
			sReturn += sTrace;
			sReturn += "</body></html>";
	   }//end main:
	   return sReturn;
}


   
   
   public String generateContent4Success(Document docCarrier, Document docFile, Document docSerie, Document docMovie, String sContentTypeIn) throws ExceptionZZZ{
	   String sReturn = null;
	   main:{
		   //0. Bestimmte Dokumente sind für den Erfolg unerläßlich
		   if(docCarrier==null){
				ExceptionZZZ ez  = new ExceptionZZZ("Carrier document", iERROR_PARAMETER_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;	
		   }
		   if(docFile==null){
				ExceptionZZZ ez  = new ExceptionZZZ("File document", iERROR_PARAMETER_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;	
		   }
		   if(docMovie==null){
			   ExceptionZZZ ez  = new ExceptionZZZ("Movie document", iERROR_PARAMETER_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;	
		   }
		   String sContentType=new String();
		   if(StringZZZ.isEmpty(sContentTypeIn)){
			   sContentType = "text/html";
		   }else{
			   if(!sContentTypeIn.toLowerCase().equals("text/html") && !sContentTypeIn.toLowerCase().equals("text/xml")){
				   ExceptionZZZ ez  = new ExceptionZZZ("ContentType - Flag='"+ sContentTypeIn +"', but expected 'text/html' or 'text/xml'", iERROR_PARAMETER_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
			   }else{
				   sContentType = sContentTypeIn.toLowerCase();
			   }
		   }
		   NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "Erzeuge Erfolgsmeldung mit dem ContentType: '" + sContentType + "'");
		   
		   //###################################################################################
		   
		   
			//1. Erstellen das Z-Kernel Objekt 
			KernelZZZ objKernel = this.getKernelObject();
			String sModule = this.getModuleUsed();
			String sProgram = this.getProgramUsed();
			
			ContentXmlZZZ objContentXmlStore = null;
			ContentFileZZZ objContentStore = null;
			
			//############
			//2. Objekt, das dann später an einen ContentWriter übergeben werden kann
			if(sContentType.equals("text/xml")){
				//+++++++++ZIEL: Ausgabe als XML		
				objContentXmlStore = new ContentXmlZZZ(objKernel, "ZMessage");
			}else{
				//+++++++++ ZIEL: Ausgabe als Html UND dafür Holen der Muster-Datei
				File filePattern =objKernel.getParameterFileByProgramAlias(sModule, sProgram, "FilePageSuccessPatternPath");
				if(filePattern.exists()==false){
					ExceptionZZZ ez  = new ExceptionZZZ("Pattern-Datei NICHT gefunden: '" + filePattern.getPath() + "'", iERROR_CONFIGURATION_VALUE, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;	
				}else{
					//System.out.println("Pattern-Datei gefunden.");
				}
				objContentStore = new ContentFileZZZ(objKernel, filePattern);
			}
				
			
			
			//############
			//3. Nun Elemente auf oberster Ebene füllen	(egal welcher Weg, dies ist eigenlich immer gleich, nur der ContntStore ist unterschiedlich)	
			//Besser ist es, das CarrierDokument würde am Ziel (dem JavaClient)  wieder in eine Art dummyNotesDocumentXml basiert entpackt und man 
			//könnte dort sagen: docNotesXml.GetItemValueString("IDCarrier") , dadurch wären auch beliebige andere Items in dem Java-Client greifbar.
				
				if(sContentType.equals("text/xml")){
	//					+++++++++ZIEL: Ausgabe als XML und die Tags der obersten Ebene bereitstellen
					objContentXmlStore.setVar("statusmessage", "Datensätze erfolgreich verarbeitet");
					try {
						String stemp = docCarrier.getItemValueString("IDCarrier");
						objContentXmlStore.setVar("idcarrier", stemp);
					} catch (NotesException e) {
						objContentXmlStore.setVar("idcarrier", e.getMessage());
					}
					try{
						String stemp = StringZZZ.right(docCarrier.getItemValueString("IDCarrier"), "#");
						objContentXmlStore.setVar("sequenzenrcarrier",stemp);
					} catch (NotesException e) {
						objContentXmlStore.setVar("sequenzenrcarrier", e.getMessage());
					}
					NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "String Variablen an den Store ContentXmlZZZ übergeben.");
				}else{
//					+++++++++ ZIEL: Ausgabe als Html und die Platzhalter für die Muster-Datei füllen.					
					objContentStore.setVar("statusmessage", "Datensätze erfolgreich verarbeitet");
					try {
						String stemp = docCarrier.getItemValueString("IDCarrier");
						objContentStore.setVar("idcarrier", stemp);
					} catch (NotesException e) {
						objContentStore.setVar("idcarrier", e.getMessage());
					}
					try{
						String stemp = StringZZZ.right(docCarrier.getItemValueString("IDCarrier"), "#");
						objContentStore.setVar("sequenzenrcarrier",stemp);
					} catch (NotesException e) {
						objContentStore.setVar("sequenzenrcarrier", e.getMessage());
					}
				}
				
				
				//############
				//4. Rückgabestring ausrechnen
				if(sContentType.equals("text/xml")){
//					+++++++++ZIEL: Ausgabe als XML
					sReturn = generateContent4SuccessXml(docCarrier, docFile, docSerie, docMovie, sReturn, objKernel, objContentXmlStore);
				
			}else if(sContentType.equals("text/html")){
				//#########################################################################################
				//+++++++++ ZIEL: Ausgabe als HTML
				sReturn = generateContent4SuccessHtml(objKernel, objContentStore);
			}
	   }
	   return sReturn;
   }

private String generateContent4SuccessHtml(KernelZZZ objKernel, ContentFileZZZ objContentStore) throws ExceptionZZZ {
	String sReturn;
	//TODO Das Compute muss eigentlich erst dann durchgeführt werden, wenn eine andere Klasse den Content Store nutzt !!!
	objContentStore.setFlag("RemoveZHTML", true); //FGL 20080212 Ziel ist es das ZHTML-Tag nicht merh im Ergebnis zu haben
	objContentStore.compute();
			
	/*++++ nur für eine Zwischenausgabe. NICHT LÖSCHEN
	KernelReaderHtmlZZZ objReader = objContentStore.getReaderCurrent();			
	org.jdom.Document doc = objReader.getDocument();
	KernelReaderHtmlZZZ.listChildrenValue(doc.getRootElement(), 0);  //Zu debugzwecken die Werte VOR Anwendung des XMLOutpuuters ausgeben.
	
	XMLOutputter xmlout = new XMLOutputter();
	Format format = xmlout.getFormat();
	System.out.println("Verwendetes Encoding Format: " + format.getEncoding());
	format.setEncoding("ISO-8859-1");
	System.out.println("Verwendetes Encoding Format: " + format.getEncoding());
	xmlout.setFormat(format);  //Das muss man wieder zurückgeben, sonst funktioniert es nicht
	try {
		xmlout.output(doc, System.out);
	} catch (IOException e) {
		e.printStackTrace();
	}
	//+++++++++++++++++++++++++++++++*/
	
	//4. Content Writer
	KernelWriterHtmlByFileZZZ objWriterHTML = new KernelWriterHtmlByFileZZZ(objKernel, (String[]) null);
	objWriterHTML.addContent(objContentStore);
	NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "Objekt KernelWriterHtmlByFileZZZ erfolgreich erstellt und mit dem ContentStore-Objekt gefüllt.");
	
	//5. als String zurückgeben
	//objWriterHTML.toFile("c:\\fglkernel\\kerneltest\\test.html");  //Create File (NUR FÜR TESTZEWECKE)
	sReturn = objWriterHTML.toStringContent();  //Merke: .toString() ist für gaaaanz andere Dinge reserviert !!!
	return sReturn;
}

private String generateContent4SuccessXml(Document docCarrier, Document docFile, Document docSerie, Document docMovie, String sReturn, KernelZZZ objKernel, ContentXmlZZZ objContentXmlStore) throws ExceptionZZZ {
	
					//Notesdocumente in XML - überführen und danach nach jdom überführen
					//Merke: Mit PipeWriter/Reader funktioniert es nicht als Servlet, obwohl als einfache Java-Applikation es funktioniert !!!
					//s.o. PipedWriter pipeOut = null;
	//				s.o. PipedReader pipeIn =null;
					//xxx  FileWriter fileOut = null;
					StringReader objReaderStringStream =null;
					SAXBuilder saxbuilder = null;
					//BufferedWriter bOut= null;
					org.jdom.Document docjdom = null;
					try {
						
					
					/*  Merke: Die Methode notesdocument.generateXML() bringt im Servlet immer den Server zum Absturz
						//TODO: Dafür eine eigene Klasse in einem eigenen Projekt entwickeln, das Notes und JDOM .jar Files beinhaltet. Und auf das JDOM-Dokument wie auf Notesdocumente zugreift.
						//a) CarrierDokument					
	//					s.o. pipeOut = new PipedWriter();
	//					s.o. bOut = new BufferedWriter(pipeOut);
						
						//s.o. pipeIn = new PipedReader();									
	//					s.o. pipeIn.connect(pipeOut);
						//Fehler: "Allready connected"    pipeOut.connect(pipeIn);	
						
						
						//fileOut = new FileWriter(objFile);
						//final BufferedWriter bOut = new BufferedWriter(fileOut);
						NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "FileWriter erzeugt. erzeuge nun XML (Carrier-Dokument) für den PipeWriter-Stream.");
						final Document docCarrier4Thread = docCarrier;
						Thread thread = new Thread(){
							public void run(){
								main:{
								try{
									System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "#Starte Verarbeitung im extenene Thread");
									//File objFile = new File("c:\\temp\\temp.xml");
									//FileEasyZZZ.removeFile(objFile.getAbsolutePath());
									//FileWriter fileOut = new FileWriter(objFile);	
									//BufferedWriter bOut= new BufferedWriter(fileOut);
									if(docCarrier4Thread == null){
										System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "#Externern Thread: Kein Handle mehr auf das Dokument.");
										break main;
									}else{
										System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "#Externern Thread: Handle  auf das Dokument vorhanden.");
									}
									System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "#doc.IDCarrier=" + docCarrier4Thread.getItemValueString("IDCarrier"));
									
									
									
									
									//!!! egal ob mit oder ohne WriterIO-Klasse. Diese Methode bringt den Domino-Server immer zum Absturz:   String stemp = docCarrier4Thread.generateXML();
									System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "#doc.generateXML()=" + stemp);
									//docCarrier4Thread.generateXML(bOut);
									//System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "#Externern Thread: Erfolgreich die Methode document.generateXML(bOut) aufgerufen.");
									//bOut.close();
									//fileOut.close();
		
								}catch(Throwable  t){
									t.printStackTrace();
								}
							}
							}//end main:
						};
						thread.start();
						try{
							thread.join();
						}catch(InterruptedException ie){
							System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "#" +ie.getStackTrace());
						}
						*/
					
					
						//Versuch dies über die DxlExporter - Klasse zu lösen
						Session session = this.getKernelNotesObject().getSession();					
						DxlExporter objExporter = session.createDxlExporter();
						if(objExporter==null){
							ExceptionZZZ ez  = new ExceptionZZZ("Kein DxlExporter aus der NotesSession erstellt.", iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
							throw ez;	
						}
						String stemp = objExporter.exportDxl(docCarrier);
						NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "XML (Carrier-Dokument) erfolgreich erzeugt.\n" + stemp );			
						
						objReaderStringStream = new StringReader(stemp);
						
						saxbuilder = new SAXBuilder();
						docjdom = saxbuilder.build(objReaderStringStream);					
						objContentXmlStore.setVar("documentcarrier", docjdom);
						NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "JDom als Variable 'documentcarrier' an den Store ContentXmlZZZ übergeben.");
						
					
			
						//b) MovieDokument
						objExporter = session.createDxlExporter();
						if(objExporter==null){
							ExceptionZZZ ez  = new ExceptionZZZ("Kein DxlExporter aus der NotesSession erstellt.", iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
							throw ez;	
						}
						stemp = objExporter.exportDxl(docMovie);
						NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "XML (Movie-Dokument) erfolgreich erzeugt.\n" + stemp );		
						
						objReaderStringStream = new StringReader(stemp);
						
						saxbuilder = new SAXBuilder();
						docjdom = saxbuilder.build(objReaderStringStream);
						objContentXmlStore.setVar("documentmovie", docjdom);
						NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "JDom als Variable 'documentmovie' an den Store ContentXmlZZZ übergeben.");
						
						
						//c) FileDokument
						objExporter = session.createDxlExporter();
						if(objExporter==null){
							ExceptionZZZ ez  = new ExceptionZZZ("Kein DxlExporter aus der NotesSession erstellt.", iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
							throw ez;	
						}
						stemp = objExporter.exportDxl(docFile);
						NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "XML (File-Dokument) erfolgreich erzeugt.\n" + stemp );	
						
						objReaderStringStream = new StringReader(stemp);
						
						saxbuilder = new SAXBuilder();
						docjdom = saxbuilder.build(objReaderStringStream);
						objContentXmlStore.setVar("documentfile", docjdom);
						NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "JDom als Variable 'documentfile' an den Store ContentXmlZZZ übergeben.");
							
						
						//d) SerieDokument
						if(docSerie!=null){
							objExporter = session.createDxlExporter();
							if(objExporter==null){
								ExceptionZZZ ez  = new ExceptionZZZ("Kein DxlExporter aus der NotesSession erstellt.", iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
								throw ez;	
							}
							stemp = objExporter.exportDxl(docSerie);
							NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "XML (Serie-Dokument) erfolgreich erzeugt.\n" + stemp );	
							
							objReaderStringStream = new StringReader(stemp);
							
							saxbuilder = new SAXBuilder();
							docjdom = saxbuilder.build(objReaderStringStream);
							objContentXmlStore.setVar("documentserie", docjdom);
							NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "JDom als Variable 'documenserie' an den Store ContentXmlZZZ übergeben.");
						}
					
						
					
					//5. ggf. Berechnungen durchführen, nachdem nun alle Variablen gesetzt wurden.
					objContentXmlStore.compute(); //Merke: Momentan passiert damit nix
					NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "Methode ContentXmlZZZ.compute() erfolgreich durchgeführt.");
					
					
					//+++++++++++++++++++++++++++++
	//				6. Content Writer
					KernelWriterXmlByContentZZZ objWriterXml = new KernelWriterXmlByContentZZZ(objKernel);
					objWriterXml.addContent(objContentXmlStore);
					NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "ContentXmlZZZ an den KernelWriterXmlByContentZZZ übergeben.");
					
					
					//5. als String zurückgeben
					objWriterXml.setEncoding("ISO-8859-1");  //Das ist zwar schon default, aber sicher ist sicher
					//objWriterHTML.toFile("c:\\fglkernel\\kerneltest\\test.html");  //Create File (NUR FÜR TESTZEWECKE)
					sReturn = objWriterXml.toStringContent();  //Merke: .toString() ist für gaaaanz andere Dinge reserviert !!!
					
					} catch (IOException ioe) {
						ExceptionZZZ ez = new ExceptionZZZ("IOException: " + ioe.getMessage(), iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
						throw ez;					
					} catch(NotesException ne){
						ExceptionZZZ ez = new ExceptionZZZ("NotesException: " + ne.getMessage(), iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
						throw ez;
					} catch(JDOMException jdome){
						ExceptionZZZ ez = new ExceptionZZZ("NotesException: " + jdome.getMessage(), iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
						throw ez;
					}
	return sReturn;
}
   
}//End class
