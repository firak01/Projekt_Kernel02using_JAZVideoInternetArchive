package use.via.client.module.export;

import java.io.File;

import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.log.ReportLogZZZ;
import basic.zKernel.IKernelZZZ;

import basic.zKernelUI.component.IPanelCascadedUserZZZ;
import basic.zKernelUI.component.IPanelCascadedZZZ;
import basic.zKernelUI.component.KernelJPanelCascadedZZZ;
import basic.zKernelUI.component.model.EventComponentSelectionResetZZZ;
import basic.zKernelUI.component.model.KernelSenderComponentSelectionResetZZZ;
import basic.zKernelUI.component.model.JTree.ModelJTreeNodeDirectoyZZZ;

public class ListenerTreeDirectorySelectionVIA extends KernelSenderComponentSelectionResetZZZ implements TreeSelectionListener, IPanelCascadedUserZZZ{
	/** Dieser Listener wird am JTree registriert und die Methode valueChanged(...) wird bei Auswahl eines anderen Verzeichnisses gestartet.
	 *   Gleichzeitig k�nnen sich andere Komponenten an diesen Listener registrieren, da er KernelServerComponentSelectionResetZZZ erweitert.
	 *   			Die sich an dem Listener registrierten Komponenten werden mit .fireEvent(event) �ber den EventComponetSelectionResetZZZ informiert. 
	 */
	IPanelCascadedZZZ panelParent=null;
	
	public ListenerTreeDirectorySelectionVIA(IKernelZZZ objKernel, KernelJPanelCascadedZZZ panelParent){
		super(objKernel);
		this.panelParent = panelParent;
	}
	
		/* (non-Javadoc)
		 * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
		 * 
		 * 
		 * Dieser Listener ist gleichzeitig auch die Quelle f�r das Abfeuern des "SelectionReset" Events.
		 */		
		public void valueChanged(TreeSelectionEvent evt){
		//	try{
			
					JTree treeDirectory = (JTree) evt.getSource();
//						Weil ich neben dem ModelJTreeNodeDirectory ggf. auch andere Modelle verwenden will. 
						//Z.B. ein dummy Modell, das beim Laden des Baums vor�bergehend angezeigt wird, der aber keinen Event erzeugen soll.						
					TreeModel model = treeDirectory.getModel();
					Object objRoot = (Object) model.getRoot();
					if(objRoot instanceof ModelJTreeNodeDirectoyZZZ){
						TreePath path = treeDirectory.getSelectionPath();
						if(path==null) return;
						//System.out.println(path.toString());		
						
						ModelJTreeNodeDirectoyZZZ node = (ModelJTreeNodeDirectoyZZZ) path.getLastPathComponent();
						if(node==null) return;
					
						File file = node.getDirectoryCurrent();
						if(file==null) return;
						ReportLogZZZ.write (ReportLogZZZ.DEBUG, ReflectCodeZZZ.getMethodCurrentName() + "# Selected entry: "+ file.getPath());
						if(file.exists()){  //Besser hier abfragen, als in den Komponenten, die an dem Event registriert sind
							//Nun einen neuen Event erzeugen, der andere Komponenten �ndern soll und diesen abfeuern
							String sPath = file.getAbsolutePath();
							EventComponentSelectionResetZZZ eventReset = new EventComponentSelectionResetZZZ(this, 10001,sPath);
							this.fireEvent(eventReset);
						}
					}else{
						return;
					}
					/*
			}catch(Exception e){
				throw new RuntimeException();
			}*/
		}

		public IPanelCascadedZZZ getPanelParent() {			
			return this.panelParent;
		}

		public void setPanelParent(IPanelCascadedZZZ objPanel) {
			this.panelParent = objPanel;
		}
}
