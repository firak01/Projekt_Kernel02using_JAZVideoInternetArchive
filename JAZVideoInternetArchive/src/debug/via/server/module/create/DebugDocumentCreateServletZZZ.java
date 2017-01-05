package debug.via.server.module.create;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.util.log.ReportLogCommonZZZ;
import basic.zNotes.kernel.NotesContextProviderZZZ;
import basic.zNotes.use.log4j.NotesReportContextProviderZZZ;
import basic.zNotes.use.log4j.NotesReportLogZZZ;
import basic.zKernel.KernelZZZ;
import custom.zNotes.kernel.KernelNotesLogZZZ;
import custom.zNotes.kernel.KernelNotesZZZ;

public class DebugDocumentCreateServletZZZ {

	/**Zum Debuggen der Konfiguration und zur Testausgabe eines log4j-protokolls
	 * @param args
	 * 
	 * lindhaueradmin; 13.11.2006 07:55:06
	 */
	public static void main(String[] args) {
		
		try { 
			//Kernel Objekt
			KernelZZZ objKernel =  new KernelZZZ("use.via.server.module.create.VIADocumentCreateServletZZZ", "01", "", "ZKernelConfigVideoArchiveServlet.ini",(String)null);
			
	
			//NotesContext, die Info befindet sich in der Datei des KernelObjekts - und dem dort defineirten Modul  - unter dem Agentennamen 
			NotesContextProviderZZZ objContextNotes = new NotesContextProviderZZZ(objKernel,"use.via.server.VIADocumentCreateServletZZZ", "use.via.server.VIADocumentCreateServletZZZ");
			
			//NotesKernelObjekt, der Notes-Profildokument-Schlüssel ist VIA
		    KernelNotesZZZ objKernelNotes = new KernelNotesZZZ(objContextNotes, "VIA", "01", null);
			KernelNotesLogZZZ objLog = objKernelNotes.getKernelNotesLogObject();
			objLog.writeLog("Start debug der Dokumenterstellung");
			
		    
		    //### NotesReportLogZZZ-Object
			NotesReportContextProviderZZZ objContextReport = new NotesReportContextProviderZZZ(objContextNotes, objKernelNotes);
			NotesReportLogZZZ.loadKernelContext(objContextReport, true);   //Diese Klasse ist static. Hierdurch wird auch das Singleton Objekt initialisiert.
			NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "Start debug der Dokumenterstellung");		
			
			
			//### Nun die Datenstruktur aufbauen, die durch das Internet übertragen werden soll
			//a) Struktur
			NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "DocumentCreate: Aufbau der Feldstruktur, Start");		
			
			NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "DocumentCreate: Aufbau der Feldstruktur, Ende");		
			
			
			//b) Werte
			NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "DocumentCreate: Füllen der Werte in die Feldstruktur, Start");		
			
			NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "DocumentCreate: Füllen der Werte in die Feldstruktur, Ende");		
			
			
			//### Basierend auf der Datenstruktur und den Werten in der als Applikation konfigurierten Datenbank Dokumente erstellen
			NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "DocumentCreate: Erstellen des Dokuments, basierend auf den Werten in der Feldstruktur, Start");		
			
			NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "DocumentCreate: Erstellen des Dokuments, basierend auf den Werten in der Feldstruktur, Ende");
			
			
			//### Abschliessende Protokollierung
			NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "Ende debug der Dokumenterstellung");
			NotesReportLogZZZ.endit(true);
			objLog.writeLog("Ende debug der Dokumenterstellung");
			
			
		} catch (ExceptionZZZ e) {
			System.out.print(e.getDetailAllLast());
			e.printStackTrace();
		}
		
	}

}
