package use.via.server;

/** Damit werden alle momentan m�glichen Aktionen beschreiben. S. ServletActionExecuteZZZ.....
 * @author lindhaueradmin
 *
 */
public interface IActionConstantZZZ {
	/**Action-name, wie er in einer URL auftauchen k�nnte
	 * 
	 */
	public static String sACTION_DELETE = "deleteDocument";
	public static String sACTION_UPDATE = "updateDocument";
	public static String sACTION_SEARCH_BY_CATEGORY = "searchForCategoryDocument";
	
	public static String sPARAMETER_DOCID="docid";
	public static String sPARAMETER_FIELDNAME = "field";  //z.B. f�r die update Action
	public static String sPARAMETER_FIELDVALUE = "value"; //Mehrfachwerte werden mit diesem Paremtenr in der JASON Syntax �bergeben.
	public static String sPARAMETER_ALIAS = "alias";
	
	/**Action Ergebnis
	 * 
	 */
	public static int iSUCCESS_CASE = -1;
	public static int iFALSE_CASE = 0;
}
