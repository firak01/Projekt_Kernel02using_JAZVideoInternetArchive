package use.via.client.module.export;

import java.io.File;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.TreePath;

import basic.zKernel.KernelZZZ;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.log.ReportLogZZZ;
import basic.zKernel.KernelUseObjectZZZ;
import basic.zKernelUI.component.IPanelCascadedUserZZZ;
import basic.zKernelUI.component.IPanelCascadedZZZ;
import basic.zKernelUI.component.KernelJPanelCascadedZZZ;
import basic.zKernelUI.component.model.JTree.ModelJTreeNodeDirectoyZZZ;

/** Diese Klasse ist als ListSelectionListener an der ListBox mit den Dateinamen angemeldet.
 *   Gleichzeitig dient sie auch als Sender für den Event, dass sich die ausgewählte Datei geändert hat.
 *   
 * @author lindhaueradmin
 *
 */
public class ListenerListFileSelectionVIA extends KernelUseObjectZZZ  implements ListSelectionListener , ISenderFileChangedVIA{
	private KernelJPanelCascadedZZZ panelParent = null;
	private ArrayList listaLISTENER_REGISTERED = new ArrayList();  //Das ist die Arrayliste, in welche  die registrierten Komponenten eingetragen werden 
	  //wichtig: Sie muss private sein und kann nicht im Interace global definiert werden, weil es sonst nicht möglich ist 
    //             mehrere Events, an verschiedenen Komponenten, unabhängig voneinander zu verwalten.
	
	
    public ListenerListFileSelectionVIA(KernelZZZ objKernel, KernelJPanelCascadedZZZ panelParent){
    	super(objKernel);
    	this.panelParent = panelParent;
    }
	/* (non-Javadoc)
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 * 
	 * Merke: Dieser Event wird auch gestartet, wenn das DefaultListModel leer gesetzt wird.
	 * 
	 * 
	 */
	public void valueChanged(ListSelectionEvent event) {		
		ReportLogZZZ.write(ReportLogZZZ.DEBUG, "List value selected: Event received.");
		main:{
		try{
			
			//Merke: Der ListSelectionEvent übergibt nur .getFirstIndex(), .getLastIndex(), ... . Ergo bekommt man so nicht den gewünschten Dateinamen
			JList list = (JList)event.getSource();
			String sValue = (String)list.getSelectedValue();
			ReportLogZZZ.write(ReportLogZZZ.DEBUG, "List value selected: '" + sValue + "'");
			if(sValue==null ) break main;  //Wenn der Event aufgrund des Leersetzens des Models gestartet wird, dann hat das alles keine echten sinn.
			if(sValue.startsWith("No file (")) break main; //damit dieser Ausdruck nicht in die Data-Felder übernommen wird. Hintergrund: Es wir nicht mehr auf die Existenz der Datei abgeprüft. (FGL 20080816, Dadurch werden Einträge auch Selektierbar wenn die CD schon entfernt worden ist.) 
			
			
			//Nun müsste ein eigener Event gestartet werden, der die entsprechenden angemeldeten Komponenten über die neue Auswahl informiert.
			//Damit nicht jede Komponente sich ggf. den Pfad holen muss, wird er hier dem Event mitgegeben.
			String sPathTotal;
			JTree tree = (JTree) this.getPanelParent().getComponent("tree");
			if(tree==null){
				ExceptionZZZ ez = new ExceptionZZZ("No component available with the alas 'tree'", iERROR_ZFRAME_DESIGN, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			TreePath treepath = tree.getSelectionPath();
			if(treepath!=null){ 
				//DefaultMutableTreeNode nodeSelected = (DefaultMutableTreeNode) treepath.getLastPathComponent();
				/*Type Cast Mismatch File filePath = (File) nodeSelected.getUserObject();			
				ReportLogZZZ.write(ReportLogZZZ.DEBUG, "List value selected, path from tree '" + filePath.getPath() + "'");
				sPathTotal = filePath.getPath() + File.pathSeparator + sValue;
				*/
				/*Type Cast Mismatch 	String sPath = (String) nodeSelected.getUserObject();
				ReportLogZZZ.write(ReportLogZZZ.DEBUG, "List value selected, path from tree '" + sPath + "'");
				sPathTotal = sPath + File.pathSeparator + sValue;
				*/
				
				ModelJTreeNodeDirectoyZZZ nodeSelected = (ModelJTreeNodeDirectoyZZZ) treepath.getLastPathComponent();
				File filePath = nodeSelected.getDirectoryCurrent();			
				ReportLogZZZ.write(ReportLogZZZ.DEBUG, "List value selected, path from tree: '" + filePath.getPath() + "'");
				if(filePath.getPath().endsWith(File.separator) | sValue.startsWith(File.separator)){
					sPathTotal = filePath.getPath() + sValue;
				}else{
					sPathTotal = filePath.getPath() + File.separator + sValue;	
				}
			}else{
				//Das ist dann der Fall, wenn das Auslesen der Verzeichnisse eines Laufwerks noch nicht beendet ist.
				JComboBox combo = (JComboBox)this.getPanelParent().getComponent("combo");
				if(combo==null){
					ExceptionZZZ ez = new ExceptionZZZ("No component available with the alas 'combo'", iERROR_ZFRAME_DESIGN, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				String sRoot = (String) combo.getSelectedItem();
				ReportLogZZZ.write(ReportLogZZZ.DEBUG, "List value selected, path from combo '" + sRoot + "'");
				if(sRoot.endsWith(File.separator) | sValue.startsWith(File.separator)){
					sPathTotal = sRoot + sValue;
				}else{
					sPathTotal = sRoot + File.separator + sValue;	
				}			
			}
			
			
			//Also den event erstellen.
			File fileSelected = new File(sPathTotal);
			///*FGL 20080818 Es soll auch möglich sein den Eintrag zu selektieren, wenn z.B. die CD schon entfernt/gewechselt wurde und noch nicht der RefreshButton gedrückt worden ist.
			//NEIN: Weil nach dem Entfernen der CD keine Dateils (z.B. Dateigröße) ausgelesen werden können darf das nicht passieren. Es muss ein Hinweis ausgegeben werden.
			boolean bRetry = false;
			do{
			if(fileSelected.isFile()==false){
				ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Auswahl ist keine Datei: " + sPathTotal);
				
				//Hinweismeldung: 
				int iResult = JOptionPane.showConfirmDialog(this.getPanelParent(), "File no longer found. Insert the previous data carrier.");			
			
				//Wenn auf "Ja" gedrückt wurde, dann neuer Versuch
				if(iResult != JOptionPane.YES_OPTION){				
					ExceptionZZZ ez = new ExceptionZZZ("Selected file does not exist: '"+sPathTotal+"'", iERROR_PARAMETER_VALUE, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}else{
					bRetry = true;
				}
			}else if(fileSelected.exists()==false){
				ReportLogZZZ.write(ReportLogZZZ.DEBUG, "File existiert nicht " + sPathTotal);
				
				//Hinweismeldung: 
				int iResult = JOptionPane.showConfirmDialog(this.getPanelParent(), "File no longer found. Insert the previous data carrier.");
				
				//Wenn auf "Ja" gedrückt wurde, dann neuer Versuch
				if(iResult != JOptionPane.YES_OPTION){				
					ExceptionZZZ ez = new ExceptionZZZ("Selected file does not exist: '"+sPathTotal+"'", iERROR_PARAMETER_VALUE, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}else{
					bRetry = true;
				}
			}else{
				bRetry = false;
				
				ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Erstelle EventListFileSelected basierend auf " + sPathTotal);
				EventListFileSelectedVIA eventMySelection = new EventListFileSelectedVIA(list, 10003, fileSelected);
				JListFileListening4ComponentResetVIA list2 = (JListFileListening4ComponentResetVIA) event.getSource();
			    this.fireEvent(eventMySelection);
			}
		}while(bRetry==true);
		}catch(ExceptionZZZ ez){
			ReportLogZZZ.write(ReportLogZZZ.ERROR, ez.getDetailAllLast());			
		}catch(Exception e){
			ReportLogZZZ.write(ReportLogZZZ.ERROR, e.getMessage());
			e.printStackTrace();
		}
		}//End main
	}

	// Aus den Kernel Interfaced
	public KernelJPanelCascadedZZZ getPanelParent() {		
		return this.panelParent;
	}
	public void setPanelParent(KernelJPanelCascadedZZZ objPanel) {
		this.panelParent = objPanel;
	}


	public void fireEvent(EventListFileSelectedVIA event) {		
			for(int i = 0 ; i < this.getListenerRegisteredAll().size(); i++){
				IListenerFileSelectedVIA l = (IListenerFileSelectedVIA) this.getListenerRegisteredAll().get(i);				
				System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "# EventListFileSelectedVIA by 'this' - object fired: " + i);
				l.fileChanged(event);
			}
	}
	public void removeListenerFileSelection(IListenerFileSelectedVIA l) {
		this.getListenerRegisteredAll().remove(l);
	}
	public void addListenerFileSelection(IListenerFileSelectedVIA l) {
		this.getListenerRegisteredAll().add(l);
	}
	public final ArrayList getListenerRegisteredAll(){
		return listaLISTENER_REGISTERED;
	}

}
