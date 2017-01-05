package use.via.client.module.export;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IConstantZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.file.DriveEasyZZZ;
import basic.zBasic.util.file.FileEasyZZZ;
import basic.zBasic.util.file.txt.TxtReaderZZZ;
import basic.zBasic.util.log.ReportLogZZZ;
import basic.zKernelUI.component.KernelJPanelCascadedZZZ;
import basic.zKernelUI.component.KernelJTextFieldListening4ComponentSelectionResetZZZ;

public class CommonUtilVIA  implements IConstantZZZ {
	private CommonUtilVIA() {
		//private construktor zum verbergen.
	}
	
	
	public static String computeDateLastModifiedByFile(File file){
		String sReturn="";
		main:{
			if(file==null) break main;			
			//Das hier nicht abfragen. Eine Abfrage auf ein CD-Rom Laufwerk erzeugt die Aufforderung eine CD-einzulegen. Das Problem ist, das diese Abfrage so h�ufig kommt, wie diese Methode aufgerufen wird. .... if(file.exists()==false)	break main;
			
			ReportLogZZZ.write(ReportLogZZZ.DEBUG, "DETAILS �ber das Erstelldatum des Laufwerks/der CD/DVD: " + file.lastModified());
			Date objDate = new Date(file.lastModified());
	
			Calendar cal = Calendar.getInstance();
			cal.setTime(objDate);
			int iYear = cal.get(Calendar.YEAR);
			if(iYear == 1900){ 
				//!!! aus irgendeinem Grund muss wohl ein Leerwert als 1.1.1900 zur�ckgegeben werden. Darauf pr�fen
				sReturn = "";
			}else{
				//Merke: Das Servlet setzt das �bergebene "Brenndatum/Erstelldatum" in das format yymmdd um.    String sFormat = "yyMMdd"; //das ist das normale Datumsformat "dd.MM.yyyy";
				String sFormat = "dd.MM.yyyy"; 
				SimpleDateFormat objFormat = new SimpleDateFormat(sFormat);
				sReturn = objFormat.format(objDate);
			}							
			ReportLogZZZ.write(ReportLogZZZ.DEBUG, "DETAILS �ber das Erstelldatum des Laufwerks/der CD/DVD: " + sReturn);
		}//end main:
		return sReturn;
	}
		
	/** Falls im Dateinamen Unterstriche vorhanden sind, werden diese ersetzt durch Leerzeichen oder Kommata.
	 *   Zudem wird ein ggf. vorhandener Serienname gesucht. Dieser sollte dann vor dem ersten Bindestrich stehen.
	 *   Der dort gefundene Wert wird aber auch noch mit dem "Katalog der Serien" gepr�ft. Ist er dort vorhanden, 
	 *   so wird er als Serienname akzeptiert.
	 *   
	 *   Merke: an Position 0 des Vektors steht der Serienname
	 *   an Position 1 des Vektors steht der ggf. �briggebliebenen Movietitle
	 *   
	 *    Diese Methode wird z.B. in den ListenerComponenten "Serie Combo Box" und "JTextField MovieTitle" eingesetzt.
	* @param file
	* @return
	* 
	* lindhaueradmin; 21.03.2007 14:48:30
	 * @throws ExceptionZZZ 
	 */
	public static Vector computeMovieDetailByFile(File fileToCheck, File fileCatalogSerie) throws ExceptionZZZ{
		Vector objReturn = new Vector();
		main:{
			if(fileToCheck==null) break main; 	//Existenzpr�fung der zu checkenden Datei nicht erforderlich
			
//			Der Default-Titel ist der Dateiname OHNE Endung. Er soll aber noch hinsichtlich der Unterstriche verarbeitet werden.
			String sAllRaw = FileEasyZZZ.getNameOnly(fileToCheck.getName());
			if(StringZZZ.isEmpty(sAllRaw)) break main;
			
			//###########################################
			//0. Vorbereitung : Es kann in seltenen Fällen vorkommen, dass kein Bindestrich - sondern - verwendet worden ist (ANSI Problem).
			sAllRaw = StringZZZ.replace(sAllRaw, "�", "-");
			
			//20170105: Fehler: Vor der "Bereichsaufteilung" erst noch den Bemerkungsteil von den anderen Teilen abtrennen.
			//TODO GOON
			
			
			//1. Ersetzen von Unterstrichen. Wichtig: Das muss für jden Bereich separat geschehen, z.B. weil an den Origin�ren Dateinamen Kommentare angeh�ngt worden sind
			StringTokenizer tokenizer = new StringTokenizer(sAllRaw, "-", false);
			String sAll = "";
			while(tokenizer.hasMoreTokens()){
				String stemp = (String) tokenizer.nextToken();
				stemp = stemp.trim();
				
				//1. Schritt Ersetzen von Unterstrichen durch ", "
				String stemp4proof = "";
				if(stemp.length()>=2){
					stemp4proof = stemp.substring(2); //Erst nach dem 2. Zeichen anfangen hinsichtlich der f�r ", " verwendeten Pr�fung
				}else{
					stemp4proof = stemp;
				}
				if(StringZZZ.contains(stemp4proof, "_")){					
					if(StringZZZ.contains(stemp, " ")){  //Merke: Ein Komma im Dateinamen ist nicht m�glich. Darum wurde dies ggf. durch Unterstrich ersetzt.
						stemp = StringZZZ.replace(stemp, "_", ", ");
					}					
				}	
				
//				2. Schritt nun noch vorhandene Unterstriche durch Leerzeichen ersetzen
				stemp = StringZZZ.replace(stemp, "_", " "); //Merke: Das "Aufnahmetool" setzt(e) ggf. f�r Leerzeichen automatisch den Unterstrich. Dies soll hier wieder korregiert werden.
				
				if(sAll.equals("")){
					sAll = stemp;
				}else{
					sAll = sAll + "-" + stemp;
				}
			}//End while
			

			
			//####### Herausziehen der Details
			//NEU 2008-08-16
			//deutsche Umlaute sollen ersetzt werden, aber nur, wenn es noch nicht einen Umlaut im Filenamen gibt, Groß- und Kleinbuchstaben der Umlaute (sofern Großbuchstebe vorhanden).
			org.apache.regexp.RE objReUmlaut = new org.apache.regexp.RE("[öÖüÜäÄß]");
			boolean bHasUmlaut = objReUmlaut.match(sAllRaw);
			
			
			//2. Herauziehen einen möglichen Seriennamens (und vergleichen mit einem ggf. vorhandenen Katalogeintrag)			
			String sSerie="";
			if(fileCatalogSerie!=null){
				//Falls der Katalog übergeben wurde wird geprüft auf Seriennamen			
				int iPos = sAll.indexOf("-");
							
				if(iPos >= 1){ //Position 0 w�rde bedeuten da ist kein Serienname voranstehend
//					2b. Prüfen auf Existenz in der Katalogdatei
					if(fileCatalogSerie.exists()==false){
						ReportLogZZZ.warn("provided catalog file '" + fileCatalogSerie.getPath() + "' for CatalogSerie, does not exist. No 'serie check' possible.");
					}else{
						String stemp = sAll.substring(0,iPos); //Also nur den Teil vor dem ersten Bindestrich auf "Serie" untersuchen.
						sSerie = stemp.trim();
						
						//+++ JETZT muss schon die Ersetzung der dt. Umlaute erfolgen
						if(bHasUmlaut==false){  //Also nur ersetzen, wenn es nicht schon einen Umlaut gibt.
							StringTokenizer tokenSerie = new StringTokenizer(sSerie, " ", false);
							sSerie = "";
							while(tokenSerie.hasMoreTokens()){
								//++++ Serie in Einzelworte zerlegen...
								String sReplaceOrig = (String) tokenSerie.nextToken();
								stemp = StringZZZ.replaceCharacterGerman(sReplaceOrig);
								
								//++++ ... und wieder zusammenbauen
								if(sSerie.equals("")){
									sSerie = stemp;
								}else{
									sSerie = sSerie + " " + stemp;
								}
							}//End while
						}//end bHasUmlaut
						
						//Nun die Katalogdatei auf Vorhandensein der Zeile pr�fen. Dabei wird NICHT AUF CASESENSITIVIT�T WERT GELEGT
						//FileTextParserZZZ parser = new FileTextParserZZZ(fileCatalogSerie);
						TxtReaderZZZ parser = new TxtReaderZZZ(fileCatalogSerie, "IgnoreCase");
						long lByte = parser.readPositionLineFirst(sSerie, 0);
						if(lByte>=0){
							//Katalogeintrag gefunden.
//							Ich gehe davon aus, dass die Katalogdatei besser gepflegt ist als die Daten aus dem zu verarbeitenden Dateinamen. Darum wird der Katalogeintrag verwendet.
							sSerie = parser.readLineByByte(lByte);
						}else{
							sSerie = "";
						}
					}
														
				}
			}
						
			//3. Herausziehen des möglichen Movietitels (incl. ggf. vorhandener Bemerkung)
			String sMovie="";
			if(!sSerie.equals("")){
				//Dann muss es einen Bindestrich gegeben haben.
				int itemp = sAll.indexOf("-");
				String stemp = sAll.substring(itemp + 1);
				sMovie = stemp.trim();
			}else{
				sMovie = sAll;
			}
			
			//4. Herausziehen einer mäglichen Bemerkung, ABER NUR, wenn es im MovieTitle noch 2 Bindestriche gibt
			//NEU 2007-06-14 
			//den letzten Bindestrich ermitteln, den Remark herausfinden und den Movietitle entsprechend anpassen.
			String sRemark = "";
			if(!sMovie.equals("")){
				if (StringZZZ.count(sMovie, "-")>=2){
					//int itemp = sMovie.lastIndexOf("-");
					int itemp = sMovie.indexOf("-");
					itemp = sMovie.indexOf("-", itemp+1);		
					if(itemp>=1){
						sRemark = StringZZZ.rightback(sMovie, itemp+1).trim();
						sMovie = sMovie.substring(0,itemp).trim();
					}		
				}
			}
						
			//### Ersetzung der dt. Umlaute im Movie
			if(bHasUmlaut==false){
				if(!StringZZZ.isEmpty(sMovie)){
					//1a. Movie in Einzelworte zerlegen
					StringTokenizer tokenMovie = new StringTokenizer(sMovie, " ", false);
					sMovie = "";
					while(tokenMovie.hasMoreTokens()){
						String sReplaceOrig = (String) tokenMovie.nextToken();
						String stemp = StringZZZ.replaceCharacterGerman(sReplaceOrig);
						
		//				1b) Wieder zusammenbauen
							if(sMovie.equals("")){
								sMovie = stemp;
							}else{
								sMovie = sMovie + " " + stemp;
							}
					}//End while
				}
			}//end bHasUmlaut
			

			//### Ersetzung der dt. Umlaute in den Bemerkungen
			if(bHasUmlaut==false){
				if(!StringZZZ.isEmpty(sRemark)){
					//3. Remark in Einzelworte zerlegen
					StringTokenizer tokenRemark = new StringTokenizer(sRemark, " ", false);
					sRemark = "";
					while(tokenRemark.hasMoreTokens()){
						String sReplaceOrig = (String) tokenRemark.nextToken();
						String stemp = StringZZZ.replaceCharacterGerman(sReplaceOrig);
											   
	//					3b) Wieder zusammenbauen
						if(sRemark.equals("")){
							sRemark = stemp;
						}else{
							sRemark = sRemark + " " + stemp;
						}
					}//End while
				}
			}//End bHasUmlaut
	
			//20090322 um die Bindestriche der Serie / des Filmtitels / in der Bemerkung soll jeweils ein Leerszeichen stehen.
			//!!! Vermeiden, dass auf diese Art doppelte Leerzeichen enstehen
			sMovie = StringZZZ.replaceFarFrom(sMovie, "-", " - ");
			sSerie = StringZZZ.replaceFarFrom(sSerie, "-", " - ");
			sRemark = StringZZZ.replaceFarFrom(sRemark, "-", " - ");
			
			//NEU 2007-06-13
			//Die Anf�nge der Werte sollen mit einem Gro�buchstaben belegt werden. Also: Ersten Buchstaben "Kapitalizen"
			sMovie = StringZZZ.capitalize(sMovie);
			sSerie = StringZZZ.capitalize(sSerie);
			sRemark = StringZZZ.capitalize(sRemark);

			
			//#################################################
			//4. Vektor füllen
			objReturn.add(0, sSerie);
			objReturn.add(1, sMovie);
			objReturn.add(2, sRemark);
		}
		return objReturn;
	}

	public static String computeDriveTitleByRootFile(File fileRoot) throws ExceptionZZZ{
		String sReturn = "";
		main:{		
				if(fileRoot==null){
					sReturn = JLabel_Listening_CarrierTitleVIA.sTEXT_INITIAL;
					break main;
				}
				
				//Keine Pr�fung auf file.exists .. bei cd-rom laufwerken kommt so eine Aufforderung die CD einzulegen. Das Problem ist, dass diese Aufforderung mehrmals kommt wenn hier die Pr�fung stattfindet....
				//if(fileRoot.exists()==false){
				//	sReturn = JLabel_Listening_CarrierTitleVIA.sTEXT_NO_DRIVE;
				//	break main;
				//}
				ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Hole den Titel von der Datei mit folgendem Pfad: FileRoot='" + fileRoot.getPath() + "'");
				sReturn = DriveEasyZZZ.getTitle(fileRoot);
				ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Gefundener Titel: sReturn ='" + sReturn + "'");
				if(StringZZZ.isEmpty(sReturn)) sReturn = JLabel_Listening_CarrierTitleVIA.sTEXT_NO_DRIVE; //besser als file.exists abzufragen !!!
		}//end main
		return sReturn;
	}
}
