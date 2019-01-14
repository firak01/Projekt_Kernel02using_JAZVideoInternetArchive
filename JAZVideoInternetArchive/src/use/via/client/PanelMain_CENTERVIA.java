/**
 * 
 */
package use.via.client;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import use.via.client.PanelMain_EASTVIA.ActionDlgIPLaunchVIA;
import use.via.client.PanelMain_EASTVIA.ActionFormAboutLaunchVIA;
import use.via.client.PanelMain_EASTVIA.ActionFormExportDataLaunchVIA;
import basic.zKernel.IKernelZZZ;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.log.ReportLogZZZ;
import basic.zKernelUI.component.KernelJPanelCascadedZZZ;

/**
 * @author 0823
 *
 */
public class PanelMain_CENTERVIA extends KernelJPanelCascadedZZZ {
	private Image objImage;
	public PanelMain_CENTERVIA(IKernelZZZ objKernel, KernelJPanelCascadedZZZ panelParent) throws ExceptionZZZ{
		super(objKernel, panelParent);
		try{
			//BoxLayout objLayout = new BoxLayout((JPanel) this, BoxLayout.Y_AXIS);
			//this.setLayout(objLayout);  //!!! Nur den LayoutManager zu initialisieren reicht nicht. Auch wenn das Panel-Objekt mit �bergeben wird.
			
			//Merke: Der Parameter f�r den Logo-Dateinamen findet man aus Modulebene.
			KernelJPanelCascadedZZZ panelRoot = this.searchPanelRoot();
			JFrame frameParent = panelRoot.getFrameParent();
			String sModule = frameParent.getClass().getName();
			String sLogo = objKernel.getParameterByModuleAlias(sModule , "FileLogo");
			
			if(!StringZZZ.isEmpty(sLogo)){
				objKernel.getLogObject().WriteLineDate("Pfad f�r das Logo: " + sLogo);
				ImageIcon objImageIcon = new ImageIcon(sLogo);
				this.objImage = objImageIcon.getImage();	
				if(this.objImage.getWidth(null)<=0){
					objKernel.getLogObject().WriteLineDate("Logobreite ist kleiner 0 !!!");
				}else{
					objKernel.getLogObject().WriteLineDate("Logobreite ist " + this.objImage.getWidth(null) );
				}
				if(this.objImage.getHeight(null)<=0){
					objKernel.getLogObject().WriteLineDate("Logoh�he ist kleiner 0 !!!");
				}else{
					objKernel.getLogObject().WriteLineDate("Logoh�he ist " + this.objImage.getHeight(null));
				}
				
				Dimension dim = new Dimension(this.objImage.getWidth(null), this.objImage.getHeight(null));		
				
				this.setSize(dim);
				this.setPreferredSize(dim);
				this.setMinimumSize(dim);
				this.setMaximumSize(dim);
				this.setLayout(null);
				
				//!!! Das Hinzuf�gen des ImageIcons mu� durch �berscheiben von paintComponent passieren
				
			}else{
				objKernel.getLogObject().WriteLineDate("Pfad f�r das Logo ist leer");
			}
			
		} catch (ExceptionZZZ ez) {				
			this.getLogObject().WriteLineDate(ez.getDetailAllLast());
			ReportLogZZZ.write(ReportLogZZZ.ERROR, ez.getDetailAllLast());
		}
	}
	public void paintComponent(Graphics g){
			g.drawImage(this.objImage, 0, 0, null);
	}
}
