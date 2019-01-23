package basic.zBasic;

import basic.zBasic.util.ConfigVIA;
import basic.zKernel.IKernelConfigZZZ;
import basic.zKernel.KernelKernelZZZ;

public class KernelSingletonVIA extends KernelKernelZZZ{
	private static KernelSingletonVIA objKernelSingleton; //muss als Singleton static sein	
	public static KernelSingletonVIA getInstance() throws ExceptionZZZ{
		if(objKernelSingleton==null){
			//Das hier nur zu initialisieren ist falsch. Schliesslich kennt man doch den Application-Key
			//String[] saFlagZ={"init"};
			//objKernelSingelton = new KernelSingletonZZZ(saFlagZ);	
			
			//Verwende hier Config-Objekt mit dem gleichen Suffix der Klasse, also THM
			IKernelConfigZZZ objConfig = new ConfigVIA();
			objKernelSingleton = new KernelSingletonVIA(objConfig, (String) null);			
		}
		return objKernelSingleton;	
	}
		
	public static  KernelSingletonVIA getInstance(IKernelConfigZZZ objConfig, String sFlagControl) throws ExceptionZZZ{
		if(objKernelSingleton==null){
			objKernelSingleton = new KernelSingletonVIA(objConfig, sFlagControl);
		}
		return objKernelSingleton;	
	}
	
	public static  KernelSingletonVIA getInstance(IKernelConfigZZZ objConfig, String[] saFlagControl) throws ExceptionZZZ{
		if(objKernelSingleton==null){
			objKernelSingleton = new KernelSingletonVIA(objConfig, saFlagControl);
		}
		return objKernelSingleton;	
	}
		
	public static KernelSingletonVIA getInstance(String sSystemNumber, String sFileConfigPath, String sFileConfigName, String[] saFlagControl ) throws ExceptionZZZ{			
		if(objKernelSingleton==null){
			//Verwende hier das Suffix der Klasse als Applicationkey, also VIA.
			objKernelSingleton = new KernelSingletonVIA("VIA", sSystemNumber, sFileConfigPath, sFileConfigName, saFlagControl);
		}
		return objKernelSingleton;	
	}
	
//	public static KernelSingletonTHM getInstance(String sApplicationKey, String sSystemNumber, String sFileConfigPath, String sFileConfigName, String[] saFlagControl ) throws ExceptionZZZ{
//	if(objKernelSingelton==null){
//		objKernelSingelton = new KernelSingletonTHM(sApplicationKey, sSystemNumber, sFileConfigPath, sFileConfigName, saFlagControl);
//	}
//	return objKernelSingelton;	
//}
	
	//Die Konstruktoren nun verbergen, wg. Singleton
		private KernelSingletonVIA() throws ExceptionZZZ{
			super();
		}
		
		private KernelSingletonVIA(String[] saFlagControl) throws ExceptionZZZ{
			super(saFlagControl);
		}
		
		//Die Konstruktoren nun verbergen, wg. Singleton
		private KernelSingletonVIA(IKernelConfigZZZ objConfig, String sFlagControl) throws ExceptionZZZ{
			super(objConfig, sFlagControl);
		}
		
		//Die Konstruktoren nun verbergen, wg. Singleton
		private KernelSingletonVIA(IKernelConfigZZZ objConfig, String[] saFlagControl) throws ExceptionZZZ{
			super(objConfig, saFlagControl);
		}
		
		private KernelSingletonVIA(String sApplicationKey, String sSystemNumber, String sFileConfigPath, String sFileConfigName, String[] saFlagControl ) throws ExceptionZZZ{
			super(sApplicationKey, sSystemNumber, sFileConfigPath, sFileConfigName, saFlagControl);
		}		
		
		public String getFileConfigKernelName() throws ExceptionZZZ{			
			return super.getFileConfigKernelName();
		}
		public String getApplicationKey() throws ExceptionZZZ{			
			return super.getApplicationKey();
		}
		
		
		//#### Interfaces
		public IKernelConfigZZZ getConfigObject() throws ExceptionZZZ{
			IKernelConfigZZZ objConfig = super.getConfigObject();
			if(objConfig==null){
				objConfig = new ConfigVIA();
				super.setConfigObject(objConfig);
			}
			return objConfig;
}
}
