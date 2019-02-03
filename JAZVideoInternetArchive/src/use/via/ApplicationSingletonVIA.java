package use.via;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.KernelSingletonVIA;
import basic.zBasic.util.ConfigVIA;
import basic.zBasic.util.log.ReportLogZZZ;
import basic.zKernel.IKernelConfigZZZ;
import basic.zKernel.IKernelZZZ;

public class ApplicationSingletonVIA extends ApplicationVIA{
	private static ApplicationSingletonVIA objApplicationSingleton = null; //muss static sein, wg. getInstance()!!!
	
	/**Konstruktor ist private, wg. Singleton
	 * @param objKernel
	 * @param objFrame
	 * @throws ExceptionZZZ
	 */
	private ApplicationSingletonVIA(IKernelZZZ objKernel) throws ExceptionZZZ{
		super(objKernel);
	}
	private ApplicationSingletonVIA(){
		super(); 
	}
	
	public static ApplicationSingletonVIA getInstance() throws ExceptionZZZ{
		if(objApplicationSingleton==null){
			objApplicationSingleton = new ApplicationSingletonVIA();
			
			//!!! Das Singleton Kernel Objekt holen und setzen !!!
			KernelSingletonVIA objKernel = KernelSingletonVIA.getInstance();
			objApplicationSingleton.setKernelObject(objKernel);			
		}
		return objApplicationSingleton;		
	}
	
	public static ApplicationSingletonVIA getInstance(IKernelZZZ objKernel) throws ExceptionZZZ{
		if(objApplicationSingleton==null){
			objApplicationSingleton = new ApplicationSingletonVIA(objKernel);
		}
		return objApplicationSingleton;		
	}

	
	
		
	//###################################################
	/** Start der Anwendung
	 * @param args
	 * 
	 * lindhaueradmin; 10.09.2008 14:31:05
	 */
	public static void main(String[] saArgs) {
		try{		
		//---- Nun das eigentliche KernelObjekt initiieren. Dabei können z.B. Debug-Einstellungen ausgwählt worden sein.
		//KernelZZZ objKernel = new KernelZZZ(sApplicationKey, sSystemNr, sDir, sFile,(String)null);
		//20170413 ERSETZE DIESE ZENTRALE STELLE DURCH EIN SINGELTON... KernelZZZ objKernel = new KernelZZZ("THM", "01", "", "ZKernelConfigTileHexMap02Client.ini", (String[]) null);
	    String[] saFlagZpassed={"USEFORMULA","USEFORMULA_MATH"};
	    
	    KernelSingletonVIA objKernel = null;
	    if(saArgs==null){
	    	//Starten mit defaultwerten
	    	objKernel = KernelSingletonVIA.getInstance( "01", "", "ZKernelConfigVideoArchiveClient.ini", saFlagZpassed);
	    }else{
	    	//Entgegennehmen von Argumenten aus der Kommandozeile
	    	IKernelConfigZZZ objConfig = new ConfigVIA(saArgs);
	    	objKernel = KernelSingletonVIA.getInstance( objConfig, saFlagZpassed);
	    }
		
		ApplicationSingletonVIA objApplication = ApplicationSingletonVIA.getInstance(objKernel);
		objApplication.launchIt();
		
		
		} catch (ExceptionZZZ ez) {				
			ReportLogZZZ.write(ReportLogZZZ.ERROR, ez.getDetailAllLast());
		}
}
	
}
