package use.via.client;

import basic.zBasic.ExceptionZZZ;
import basic.zKernel.KernelConfigZZZ;

public class ConfigVIA extends KernelConfigZZZ{
	public ConfigVIA() throws ExceptionZZZ{
		super();
	}
	public ConfigVIA(String[] saArg) throws ExceptionZZZ {
		super(saArg); 
	} 
	
	public String getApplicationKeyDefault() {
		return "VIA"; 
	}

	public String getConfigDirectoryNameDefault() {
		return ".";
	}

	public String getConfigFileNameDefault() {
		return "ZKernelConfigVideoArchiveClient.ini";
	}

	public String getPatternStringDefault() {
		return "k:s:d:f:";
	}

	public String getSystemNumberDefault() {
		return "01";
	}
}