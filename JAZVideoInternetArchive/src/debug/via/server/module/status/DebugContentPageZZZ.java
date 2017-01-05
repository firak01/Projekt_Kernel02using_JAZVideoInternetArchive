package debug.via.server.module.status;

import org.apache.ecs.Document;

import lotus.domino.Database;
import lotus.domino.NotesException;
import use.via.server.module.status.ContentDocumentCreatedPageZZZ;
import basic.zBasic.ExceptionZZZ;
import basic.zKernel.html.writer.KernelWriterHtmlByEcsZZZ;
import basic.zNotes.kernel.NotesContextProviderZZZ;
import basic.zKernel.KernelZZZ;
import custom.zNotes.kernel.KernelNotesZZZ;

public class DebugContentPageZZZ { 

	/** TODO What the method does.
	 * @return void
	 * @param args 
	 * 
	 * lindhaueradmin; 11.10.2006 08:07:19
	 */
	public static void main(String[] args) {
		
		try {
			//Kernel Objekte 
			KernelZZZ objKernel = new KernelZZZ("VideoArchive", "01", "", "ZKernelConfigVideoArchiveServlet.ini",(String)null);
			
			//Der Context-Provider. Wir "faken" hier einen Agentennamen (das gilt für Servlets immer, aber auch für das Debuggen von JavaNotesAgenten)
			NotesContextProviderZZZ objContext = new NotesContextProviderZZZ(objKernel, "KernelNotes", "DocumentCreateServlet");
			
			//Der in der Konfigurationsdatenbank angegebenen ApplikationKey ist JAV. 
			//Merke: In der kernelzzz-.ini Datei wird der Pfad zur "Video Archiv - Application" Datenbank als "Kernel calling" datenbank definiert.
			KernelNotesZZZ objKernelNotes = new KernelNotesZZZ(objContext, "VIA", "01", null);
			
			
			
			//Das Content objekt, das später an den Writer übergeben wird
			ContentDocumentCreatedPageZZZ objContent = new ContentDocumentCreatedPageZZZ(objKernel, objKernelNotes);
		
			//Diverse Variablen, die beim "compute" an die entsprechende Stelle gesetzt werden 
			Database dbLog = objKernelNotes.getDBLogCurrent();
			if(dbLog != null){
				String stemp = dbLog.getTitle();
				objContent.setVar("DBLogTitle", stemp);
			}
		
			objContent.compute();
		
			
			//Der Writer
			Document objDoc = new Document();  //Das html-document, nicht zu verwechseln mit dem Notes-document
			KernelWriterHtmlByEcsZZZ objWriter = new KernelWriterHtmlByEcsZZZ(objKernel, objDoc, (String[]) null);		
			objWriter.addContent(objContent);
			objWriter.toFile("c:\\temp\\test.html");
	
		}catch(NotesException ne){
			System.out.println(ne.text);
			ne.printStackTrace();
		} catch (ExceptionZZZ ez) {
			System.out.println(ez.getDetailAllLast());
			ez.printStackTrace();
		}
	}//end main()

	}//end class

