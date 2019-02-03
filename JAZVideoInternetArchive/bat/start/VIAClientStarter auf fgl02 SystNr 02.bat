REM START OHNE ECLIPSE: java -jar c:\java\VIAClient\VIAClientServer_fat.jar -k VIA -s 02 -d c:\java\VIAClient -f ZKernelConfigVideoArchiveClientFGL02.ini
REM START UNTER ECLIPSE: 
REM java C:\1fgl\repo_Eclipse4_v02\Projekt_Kernel02using_JAZVideoInternetArchive\JAZVideoInternetArchive\src\use\via\ApplicationSingletonVIA -k VIA -s 02 -d c:\java\VIAClient -f ZKernelConfigVideoArchiveClientFGL02.ini
cd\
cd C:\1fgl\repo_Eclipse4_v02\Projekt_Kernel02using_JAZVideoInternetArchive\JAZVideoInternetArchive
REM java use.via.ApplicationSingletonVIA -k VIA -s 02 -d c:\java\VIAClient -f ZKernelConfigVideoArchiveClientFGL02.ini

REM Das startet zwar die Java-Application, findet aber keine anderen eingebundenen Projekte
REM cd bin
REM java use.via.ApplicationSingletonVIA -k VIA -s 02 -d c:\java\VIAClient -f ZKernelConfigVideoArchiveClientFGL02.ini

set Projekt_DUMMY=C:\1fgl\repo_Eclipse4_v02\Projekt_Kernel02_JAZKernel\JAZDummy;
set Projekt_Kernel=C:\1fgl\repo_Eclipse4_v02\Projekt_Kernel02_JAZKernel\JAZKernel;

set classpath = %CLASSPATH%;JAVA_HOME\lib;%Projekt_DUMMY%\bin;%Projekt_Kernel%\bin;
cd bin
java use.via.ApplicationSingletonVIA -k VIA -s 02 -d c:\java\VIAClient -f ZKernelConfigVideoArchiveClientFGL02.ini
pause