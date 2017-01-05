package use.via.client;

import java.awt.BorderLayout;


import basic.zBasic.ExceptionZZZ;
import basic.zKernelUI.component.KernelJFrameCascadedZZZ;
import basic.zKernelUI.component.KernelJPanelCascadedZZZ;
import basic.zKernel.KernelZZZ;

public class PanelMainVIA  extends KernelJPanelCascadedZZZ{

	public PanelMainVIA(KernelZZZ objKernel, KernelJFrameCascadedZZZ frameParent) throws ExceptionZZZ{
		super(objKernel, frameParent);
		//try {
		
			//### Layout Manager
			this.setLayout(new BorderLayout());
				
			//### PANEL EAST
				PanelMain_EASTVIA objPanelEast = new PanelMain_EASTVIA(objKernel, this);				
				this.setPanelSub("EAST", objPanelEast);       //Backend Hashtable hinzufügen
				this.add(objPanelEast, BorderLayout.EAST); //Frontend hinzufügen
				
			//### PANEL CENTER
				PanelMain_CENTERVIA objPanelCenter = new PanelMain_CENTERVIA(objKernel, this);
				this.setPanelSub("CENTER", objPanelCenter);
				this.add(objPanelCenter, BorderLayout.CENTER); //Frontend hinzufügen
				
				
				/*
				//### PANEL SOUTH
				PanelConfig_SOUTHZZZ objPanelSouth = new PanelConfig_SOUTHZZZ(objKernel, objKernelChoosen, this);
				this.add(objPanelSouth, BorderLayout.SOUTH); //Frontend hinzufügen
				this.setPanelSub("SOUTH", objPanelSouth);    //Backend Hashtable hinzufügen
				
				
			}//end if(bConnected == false)
			*/
			
			/*
			} catch (ExceptionZZZ ez) {
				
				this.getLogObject().WriteLineDate(ez.getDetailAllLast());
				
				try{
					//Ineinander verschachtelte Exceptions !!!
				JLabel labelError = new JLabel("An error happend. For more details look at: " + this.getLogObject().getFilenameExpanded());
				this.add(labelError);
				}catch(ExceptionZZZ ez2){
					System.out.println(ez2.getDetailAllLast());
				}
			}
			*/	
	}
}
