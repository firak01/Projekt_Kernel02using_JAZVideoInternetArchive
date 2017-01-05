package use.via.client.module.export;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.HashMap;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;



import basic.zKernel.KernelZZZ;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.log.ReportLogZZZ;
import basic.zKernelUI.component.KernelActionCascadedZZZ;
import basic.zKernelUI.component.KernelJPanelCascadedZZZ;
import basic.zKernelUI.component.model.JTree.ModelJTreeNodeDirectoyZZZ;

public class ListenerCombo4TreeRefreshVIA extends KernelActionCascadedZZZ{
	//Falls es diesen Thread schon gibt, dann muss er beendet werden. Grund: sonst läuft er weiter und überschreibt ggf. das Ergebnis eines neuen Threads,
	//wenn der neue Thread eher fertig ist als der alte.
		
	private SwingWorker4ProgramTreeContentVIA workerTree =null;
	private SwingWorker4ProgramListContentVIA workerList = null;
	
	public ListenerCombo4TreeRefreshVIA(KernelZZZ objKernel, KernelJPanelCascadedZZZ panelParent) {
		super(objKernel, panelParent);		
	}

	public boolean actionPerformCustom(ActionEvent ae, boolean bQueryResult) throws ExceptionZZZ {
		//try {
			ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Performing action: 'ComboBox has changed'");
												
			//Hier eine Action der ComboBox ausführen
			//JComboBox combo = (JComboBox) ae.getSource();
			
//			NEIN, die Quelle kann nun auch ein Button sein (Refresh Button) Daher ist die Combobox über das panelParent und ihren Alias ermitteln:  JComboBox combo = (JComboBox) eventAction.getSource();
			JComboBox combo = (JComboBox) this.getPanelParent().getComponent("combo");
			String sNew = new String((String)combo.getSelectedItem());
						
			System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "# Dieser Listener ist auch für '" + ae.getSource().getClass().getName() + "'  registriert");
			System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "# Neu ausgewähltes Laufwerk: " + sNew);
			
			//DEN JTREE und die JLIST SICH AKTUALISIEREN LASSEN
			KernelJPanelCascadedZZZ panelCascaded = (KernelJPanelCascadedZZZ) this.getPanelParent();
			
			/*  Es wird ein Event in ListenerComboDriveSelectionResetzZZZ abgefeuert, der von der Listbox aufgefangen wird und dort den SwingWorkerThread startet.
			 * darum ist das an dieser Stelle falsch und gefährlich
			//### LIST
			//+++ Versuch die Performance zu verbessern, indem man einen externen Thread startet			
			//Falls es schon einen anderen worker gibt, der läuft, diesen beenden.
			if(this.workerList!=null){
				ReportLogZZZ.write(ReportLogZZZ.DEBUG , "Es gibt schon einen anderen WorkerThread für List.(1)");
				System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "#Es gibt schon einen anderen WorkerThread für List.(2)");
				this.workerList.interrupt();
				ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Versuche den alten WorkerThread für List zu interrupten.(1)");
				System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "#Versuche den alten WorkerThread für List zu interrupten.(2)");
			}
			
			//Neuer Worker
			String[] saFlagList = null;			
			this.workerList = new SwingWorker4ProgramListContentVIA(objKernel, panelCascaded, sNew, saFlagList);
			this.workerList .start();  //Merke: Das Setzen des Label Felds geschieht durch einen extra Thread, der mit SwingUtitlities.invokeLater(runnable) gestartet wird.
*/
			
			
			
			
			//### TREE
			//+++ Versuch die Performance zu verbessern, indem man einen externen Thread startet			
			//Falls es schon einen anderen worker gibt, der läuft, diesen beenden.
			if(this.workerTree!=null){
				ReportLogZZZ.write(ReportLogZZZ.DEBUG , "Es gibt schon einen anderen WorkerThread für Tree.(1)");
				System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "#Es gibt schon einen anderen WorkerThread für Tree.(2)");
				this.workerTree.interrupt();
				ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Versuche den alten WorkerThread für Tree zu interrupten.(1)");
				System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "#Versuche den alten WorkerThread für Tree zu interrupten.(2)");
			}
			
			//Neuer Worker
			String[] saFlagTree = null;			
			this.workerTree = new SwingWorker4ProgramTreeContentVIA(objKernel, panelCascaded, sNew, saFlagTree);
			this.workerTree .start();  //Merke: Das Setzen des Label Felds geschieht durch einen extra Thread, der mit SwingUtitlities.invokeLater(runnable) gestartet wird.

	
			
		/*} catch (ExceptionZZZ ez) {				
			this.getLogObject().WriteLineDate(ez.getDetailAllLast());
			ReportLogZZZ.write(ReportLogZZZ.ERROR, ez.getDetailAllLast());
		}	*/
			
		return true;
	}

	public boolean actionPerformQueryCustom(ActionEvent ae) throws ExceptionZZZ {
		return true;
	}

	public void actionPerformPostCustom(ActionEvent ae, boolean bQueryResult) throws ExceptionZZZ {
		// TODO Auto-generated method stub
		
	}
	
	/**Durch diese Getter//Setter Methodne wird der Thread von aussen erreichbar, bzw. er wird ja schon bei Starten des Panels erzeugt
	und hierher übergeben. Ziel: wenn der Worker Thread beim Starten des Panels noch läuft, ein anderes Laufwerk ausgewählt wird, 
	dann muss der zuvor laufende Thread beendet werden.
	* @return worker, ist SwingWorker4ProgramTreeContentVIA
	* 
	* lindhaueradmin; 28.01.2007 11:27:36
	 */
	public SwingWorker4ProgramTreeContentVIA getWorkerThreadTree(){
		return this.workerTree;
	}
	
	/**Durch diese Getter//Setter Methodne wird der Thread von aussen erreichbar, bzw. er wird ja schon bei Starten des Panels erzeugt
	und hierher übergeben. Ziel: wenn der Worker Thread beim Starten des Panels noch läuft, ein anderes Laufwerk ausgewählt wird, 
	dann muss der zuvor laufende Thread beendet werden.
	
	!!! Darum gibt es hier auch nur ein worker Objekt. Ein bestehender Thread wird mit interrupt() abgebrochen.
	
	
	* @param worker, ist SwingWorker4ProgramTreeContentVIA
	* 
	* lindhaueradmin; 28.01.2007 11:27:36
	 */
	public void setWorkerThreadTree(SwingWorker4ProgramTreeContentVIA workerTree){		
		if(this.workerTree!=null){
			ReportLogZZZ.write(ReportLogZZZ.DEBUG , "Es gibt schon einen anderen WorkerThread für Tree. Versuche nun den alten WorkerThread zu interrupten");
			this.workerTree.interrupt();
			ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Der alte WorkerThread für Tree wurde ggf. interrupted.");
		}
		this.workerTree = workerTree;
	}


	/**Durch diese Getter//Setter Methodne wird der Thread von aussen erreichbar, bzw. er wird ja schon bei Starten des Panels erzeugt
	und hierher übergeben. Ziel: wenn der Worker Thread beim Starten des Panels noch läuft, ein anderes Laufwerk ausgewählt wird, 
	dann muss der zuvor laufende Thread beendet werden.

	* @return worker, ist SwingWorker4ProgramTreeContentVIA
	* 
	* lindhaueradmin; 28.01.2007 11:27:36
	 */
	public SwingWorker4ProgramListContentVIA getWorkerThreadList(){
		return this.workerList;
	}
	
	/**Durch diese Getter//Setter Methodne wird der Thread von aussen erreichbar, bzw. er wird ja schon bei Starten des Panels erzeugt
	und hierher übergeben. Ziel: wenn der Worker Thread beim Starten des Panels noch läuft, ein anderes Laufwerk ausgewählt wird, 
	dann muss der zuvor laufende Thread beendet werden.
	
			!!! Darum gibt es hier auch nur ein worker Objekt. Ein bestehender Thread wird mit interrupt() abgebrochen.
		
	
	* @param worker, ist SwingWorker4ProgramTreeContentVIA
	* 
	* lindhaueradmin; 28.01.2007 11:27:36
	 */
	public void setWorkerThreadList(SwingWorker4ProgramListContentVIA workerList){
		if(this.workerList!=null){
			ReportLogZZZ.write(ReportLogZZZ.DEBUG , "Es gibt schon einen anderen WorkerThread für List. Versuche nun den alten WorkerThread zu interrupten");
			this.workerList.interrupt();
			ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Der alte WorkerThread für List wurde ggf. interrupted.");
		}
		this.workerList = workerList;
	}

	public void actionPerformCustomOnError(ActionEvent ae, ExceptionZZZ ez) {
		// TODO Auto-generated method stub
		
	}
}
