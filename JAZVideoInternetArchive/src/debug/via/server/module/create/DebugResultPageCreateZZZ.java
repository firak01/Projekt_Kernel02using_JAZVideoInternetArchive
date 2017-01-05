package debug.via.server.module.create;

import java.io.File;

import basic.zBasic.ExceptionZZZ;
import basic.zKernel.html.writer.KernelWriterHtmlByFileZZZ;
import basic.zKernel.markup.content.ContentFileZZZ;
import basic.zKernel.KernelZZZ;


public class DebugResultPageCreateZZZ {
	/**Zum Debuggen der Konfiguration und zur Testausgabe eines log4j-protokolls
	 * @param args
	 * 
	 * lindhaueradmin; 13.11.2006 07:55:06
	 */
	public static void main(String[] args) {
		
		System.out.println("Start");
		main:{
			try {
	
			//1. Erstellen das Z-Kernel Objekt
			KernelZZZ objKernel = new KernelZZZ("FGL", "01", "", "", (String)null);
			File objFilePattern = new File("pagServletDocumentCreateResponsePattern.html");
			if(objFilePattern.exists()==false){
				System.out.println("Pattern-Datei NICHT gefunden.");
				break main;
			}else{
				System.out.println("Pattern-Datei gefunden.");
			}
			
			//3. Objekt, das dann später an einen ContentWriter übergeben werden kann
			ContentFileZZZ objContentStore = new ContentFileZZZ(objKernel, objFilePattern);
			objContentStore.setVar("statusmessage", "Hä, dat würd ich so tun.");
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
			
			//5. Create File
			objWriterHTML.toFile("c:\\temp\\test.html");

			
			} catch (ExceptionZZZ ez) {
					System.out.println(ez.getDetailAllLast());
			}
		}//End main:
		System.out.println("Ende");
		
	}

}
