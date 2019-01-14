package use.via.client.module.export;

import java.io.File;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JPanel;

import basic.zKernel.IKernelZZZ;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.log.ReportLogZZZ;
import basic.zKernelUI.component.KernelJLabelListening4ComponentSelectionResetZZZ;
import basic.zKernelUI.component.KernelJListListening4ComponentSelectionResetZZZ;
import basic.zKernelUI.component.KernelJPanelCascadedZZZ;
import basic.zKernelUI.component.model.EventComponentSelectionResetZZZ;

/**Erweitert das "listening auf eine Änderung" in den Komponenten, an die Objekte dieser Klasse angemeldet wurden, dahingehend, das die doReset(event) MEthode konkretisiert wird.
 * 
 * In der doReset(EventComponentSelectionResetZZZ) Methode wird nun ein SwingWorker-Thread gestartet, der die ListBox f�llt (und zwar mit den Dateinamen)
 * 
 * 
 * ZUDEM:
 * Diese Klasse implementiert die Schnittstelle, die notwendig ist, eigene "Events" zu verschicken.
 * Diese Events werden in dem ListenerListFileSelectionVIA verschickt, nachdem ein Eintrag der Liste ausgew�hlt wurde.
 * Hintergrund: In dem neuen verschickten Event steht der komplette Pfad der Datei drin
 *  
 * @author lindhaueradmin
 *
 */
public class JListFileListening4ComponentResetVIA extends KernelJListListening4ComponentSelectionResetZZZ {
    private ListenerCombo4TreeRefreshVIA listenerComboBox;
    private KernelJPanelCascadedZZZ panelParent;
    
    private String sSelectedPrevious = null;   //mit dem File.Seperator am Ende, wie es in der Dialogbox erwartet wird.
	
	
    //Damit diese Komponente selbst auch events erzeugen kann, muss sie dies einbinden
    ArrayList listener = new ArrayList();  //hierin werden die "registrierten Komponenten" verwaltet.
    
    
	public JListFileListening4ComponentResetVIA(IKernelZZZ objKernel, DefaultListModel listModel, ListenerCombo4TreeRefreshVIA listenerComboBox, KernelJPanelCascadedZZZ panelParent) {
		super(objKernel, listModel);
		this.listenerComboBox = listenerComboBox;
		this.panelParent = panelParent;
	}

	public void doResetCustom(EventComponentSelectionResetZZZ event) {
		main:{
		System.out.println(ReflectCodeZZZ.getMethodCurrentName() +"#EVENTEVENT CATCHEN !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		
			String sPathSelected =event.getComponentText(); 
			ReportLogZZZ.write(ReportLogZZZ.DEBUG, "JList has received EventComponentSelectionResetZZZ. Mit der Auswahl: " + sPathSelected);
			
			//FGL 20080314 Der Worker Thread darf nur gestartet werden, wenn es die Datei auch gibt. 
			//                     Bei nicht eingelegter CD wird daraufhin nur einmal die Aufforderung angezeigt "Legen Sie einen Datentr�ger ein."
			File objFileSelected = new File(sPathSelected);
			if(objFileSelected.exists()){
				//if(sPathSelected.equals(this.sSelectedPrevious)) break main;
				//this.sSelectedPrevious = sPathSelected;
				
				//Versuch die Performance zu verbessern, indem man einen externen Thread startet
				try{
					String[] saFlag = null;
					SwingWorker4ProgramListContentVIA workerList = this.listenerComboBox.getWorkerThreadList();
					if(workerList != null){
						ReportLogZZZ.write(ReportLogZZZ.DEBUG, "BESTEHENDEN WorkerThread f�r den ListContent gefunden. Versuche diesen zu interrupten.(1)");
						System.out.println(ReflectCodeZZZ.getMethodCurrentName() +"#BESTEHENDEN WorkerThread f�r den ListContent gefunden. Versuche diesen zu interrupten.(2)");
						this.listenerComboBox.getWorkerThreadList().setFlag("Terminate", true);											
					}
					workerList = new SwingWorker4ProgramListContentVIA(this.getKernelObject(), (KernelJPanelCascadedZZZ)this.getPanelParent(), sPathSelected, saFlag);
					this.listenerComboBox.setWorkerThreadList(workerList); //Damit immer nur ein worker thread PRO ALIAS arbeitet. Theoretisch kann man ja nach dem Starten des Frames die Combo - Box �ndern. Hier wird dann ein bereits laufender Thread abgebrochen.
					ReportLogZZZ.write(ReportLogZZZ.DEBUG, "NEUER WorkerThread für den ListContent erzeugt. Versuche diesen zu starten.(1)");
					System.out.println(ReflectCodeZZZ.getMethodCurrentName() +"#NEUER WorkerThread f�r den ListContent erzeugt. Versuche diesen zu starten.(2)");
													
					this.listenerComboBox.getWorkerThreadList().start();
				}catch(ExceptionZZZ ez){
					ReportLogZZZ.write(ReportLogZZZ.ERROR, ez.getDetailAllLast());			
				}catch(Exception e){
					ReportLogZZZ.write(ReportLogZZZ.ERROR, e.getMessage());
				}
			}else{
				ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Verzeichnis existiert nicht. Hinweis in die Dateiliste setzen.(1)");
				System.out.println(ReflectCodeZZZ.getMethodCurrentName() +"#Verzeichnis existiert nicht. Hinweis in die Dateiliste setzen.(2)");
				
				SwingWorker4ProgramListContentVIA workerList = this.listenerComboBox.getWorkerThreadList();
				if(workerList != null){
					ReportLogZZZ.write(ReportLogZZZ.DEBUG, "BESTEHENDEN WorkerThread für den ListContent gefunden. Versuche diesen zu interrupten.(1)");
					System.out.println(ReflectCodeZZZ.getMethodCurrentName() +"#BESTEHENDEN WorkerThread f�r den ListContent gefunden. Versuche diesen zu interrupten.(2)");
					this.listenerComboBox.getWorkerThreadList().setFlag("Terminate", true);											
				}
				
				//Hinweis auf leeres Verzeichnis setzen
				DefaultListModel modelList = (DefaultListModel)  this.getModel();  //Das muss der JList im Konstruktor mitgegeben worden sein.
				modelList.clear();	
				modelList.addElement("No file (" + sPathSelected + ")");   //Das tritt z.B. bei leeren CDRom-Laufwerk ein
				this.setModel(modelList);
			    this.repaint();	
			}//end if file.exists();
			
		}//end main:
	}

	public JPanel getPanelParent(){
		return this.panelParent;
	}
	
	public void addFileSelectedListener(IListenerFileSelectedVIA l){
		listener.add(l);
	}
	public void removeFileSelectedListener(IListenerFileSelectedVIA l){
		listener.remove(l);
	}

	
	
	/*
	public void fireEvent(EventListFileSelectedVIA eventFileSelected){
		for(int i=0;i<listener.size();i++){
			IListenerFileSelectedVIA l = (IListenerFileSelectedVIA) listener.get(i);
			l.fileChanged(eventFileSelected);
		}
	}
	*/
	

}
