REM START OHNE ECLIPSE: java -jar c:\java\VIAClient\VIAClientServer_fat.jar -k VIA -s 02 -d c:\java\VIAClient -f ZKernelConfigVideoArchiveClientFGL02.ini
REM START DES CODES IM ECLIPSE WORKSPACE IST OHNE JAR ZU KOMPLIZIERT, WG. DER CLASSPATHS, die man alle manuell setzen müsste: 
REM java C:\1fgl\repo_Eclipse4_v02\Projekt_Kernel02using_JAZVideoInternetArchive\JAZVideoInternetArchive\src\use\via\ApplicationSingletonVIA -k VIA -s 02 -d c:\java\VIAClient -f ZKernelConfigVideoArchiveClientFGL02.ini

REM DARUM STARTE PER .jar 
REM 1. Wechsle in das Klassenverzeichnis
REM /d ist notwendig, um per Variable das Verzeichnis/Laufwerk zu wechseln. Beachte VErzeichnisphad in Hochkommata.
set myDirectory="C:\1fgl\client\VIAClient"
cd /d %myDirectory%
java -jar VIAClientServer20190204_fat.jar -k VIA -s 02 -d c:\java\VIAClient -f ZKernelConfigVideoArchiveClientFGL02.ini

pause
