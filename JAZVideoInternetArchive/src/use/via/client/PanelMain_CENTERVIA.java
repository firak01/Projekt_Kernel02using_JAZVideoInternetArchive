/**
 * 
 */
package use.via.client;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;

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
import basic.zBasic.util.file.FileEasyZZZ;
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
			ImageIcon objImageIcon = objKernel.getParameterImageIconByModuleAlias(sModule, "FileLogo");
			this.objImage = objImageIcon.getImage();	
			if(this.objImage.getWidth(null)<=0){
				objKernel.getLogObject().WriteLineDate("Logobreite ist kleiner 0 !!!");
			}else{
				objKernel.getLogObject().WriteLineDate("Logobreite ist " + this.objImage.getWidth(null) );
			}
			if(this.objImage.getHeight(null)<=0){
				objKernel.getLogObject().WriteLineDate("Logohöhe ist kleiner 0 !!!");
			}else{
				objKernel.getLogObject().WriteLineDate("Logohöhe ist " + this.objImage.getHeight(null));
			}
				
			Dimension dim = new Dimension(this.objImage.getWidth(null), this.objImage.getHeight(null));		
			
			this.setSize(dim);
			this.setPreferredSize(dim);
			this.setMinimumSize(dim);
			this.setMaximumSize(dim);
			this.setLayout(null);
			
			//!!! Das Hinzufügen des ImageIcons muß durch überscheiben von paintComponent passieren
			
		} catch (ExceptionZZZ ez) {				
			this.getLogObject().WriteLineDate(ez.getDetailAllLast());
			ReportLogZZZ.write(ReportLogZZZ.ERROR, ez.getDetailAllLast());
		}
	}
	public void paintComponent(Graphics g){
			g.drawImage(this.objImage, 0, 0, null);
			
			//20190220: Jetzt ist die Idee in das Logo wichtige Informationen einzufügen:
			//https://stackoverflow.com/questions/10929524/how-to-add-text-to-an-image-in-java
			//z.B. die Systemnummber
//          Zum Beschreiben des Bildes ausserhalb einer paintComponent() - Methode:
//			final BufferedImage image = ImageIO.read(new URL("http://upload.wikimedia.org/wikipedia/en/2/24/Lenna.png"));
//			Graphics g = image.getGraphics();
//		    g.setFont(g.getFont().deriveFont(30f));
//		    g.drawString("Hello World!", 100, 100);
//		    g.dispose();
//			ImageIO.write(image, "png", new File("test.png"));
			try {
				String s = "Systemkey: " + this.getKernelObject().getSystemKey();
			
			    g.setFont(g.getFont().deriveFont(30f));
			    g.drawString(s, 0, 30); //0,0 ist links oben in der Ecke, aber wohl ausserhalb des Panels,weil "nach oben geschrieben wird"...
			    g.dispose();
				    
			} catch (ExceptionZZZ e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
}
