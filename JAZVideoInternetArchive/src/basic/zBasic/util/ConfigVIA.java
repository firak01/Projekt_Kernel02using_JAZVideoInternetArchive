package basic.zBasic.util;

import basic.zBasic.ExceptionZZZ;
import basic.zKernel.KernelConfigZZZ;

public class ConfigVIA extends KernelConfigZZZ{
	private static final long serialVersionUID = 1L;
	private static String sDIRECTORY_CONFIG_DEFAULT = ".";
	private static String sFILE_CONFIG_DEFAULT = "ZKernelConfigVideoArchiveClient.ini";
	private static String sKEY_APPLICATION_DEFAULT = "VIA";
	private static String sNUMBER_SYSTEM_DEFAULT= "01";

	public ConfigVIA() throws ExceptionZZZ{
		super();
	}
	public ConfigVIA(String[] saArg) throws ExceptionZZZ {
		super(saArg); 
	} 
	
	@Override
	public String getApplicationKeyDefault() {
		return ConfigVIA.sKEY_APPLICATION_DEFAULT;
	}
	@Override
	public String getConfigDirectoryNameDefault() {
		return ConfigVIA.sDIRECTORY_CONFIG_DEFAULT;
	}
	@Override
	public String getConfigFileNameDefault() {		
		return ConfigVIA.sFILE_CONFIG_DEFAULT;
	}
	@Override
	public String getPatternStringDefault() {
		return "k:s:f:d:";
	}
	@Override
	public String getSystemNumberDefault() {
		return ConfigVIA.sNUMBER_SYSTEM_DEFAULT;
}
}