TO DO: Remove JSON files from repository and add instructions to build them by running VillagersScenarioCreatorInstitutions.java.

#Requirements#
*  Download and run the Repast installer (https://repast.github.io/download.html)
*  Install Adopt OpenJDK 8 (LTS) with JVM OpenJ9 (https://adoptopenjdk.net/).
*  Install SWI Prolog 7.6.4 stable release (http://www.swi-prolog.org/download/stable). Note: on Windows, to call SWI Prolog from a Java application using the JPL library, you need to
   a) add a new environment variable SWI_HOME_DIR, set to the path where you installed SWI-Prolog; and b) Add the paths
   %SWI_HOME_DIR%\bin and %SWI_HOME_DIR%\lib\jpl.jar to your PATH environment variable. This is not necessary when running this Repast
   project as long as you follow step 7 below (to define an Eclipse path variable SWI_PROLOG_LOC) and run the simulation using the CaspFishing Model launcher.

#Installing the CASP Fishing Simulation#
1.  Clone the repository as a folder named CaspFishing within the Eclipse workspace you plan to use with Repast Simphony. The folder name is important as the Eclipse build scripts assume this is the project name.
2.  Copy Repast.settings.template to create Repast.settings ( Note: on Windows, go to the cloned repository through command prompt and use this command to copy)
3.  Copy .project.template to .project ( Note: on Windows, use command prompt )
4.  Open Repast Simphony (by running eclipse.exe in the eclipse folder in the Repast installation).
5.  File > Import... > General > Existing Projects into Workspace
6.  Select the cloned repository's folder, click the check box beside the folder name, then click on Finish
7.  In either the Eclipse Package Explorer or Navigator pane, right click on the top level CaspFishing node and select Properties (at the bottom of the menu). Open the Resource node and select Linked Resources. In the Path Variables tab, click on SWI_PROLOG_LOC than click on Edit. Enter the location where SWI Prolog is installed, e.g. C:\Program Files\swipl.
8.  Select File > Import ... then open the Team node and select Team Project Set. Select the file projectSet.psf within the top level of your CaspFishing folder, then click Finish.
9.  From the Project menu, deselect Build Automatically if it is checked.
10.  Compile caspFishing.GenerateVillagersJava.java using Project > Build Working Set > Code generation. Ignore errors relating to other files. The working set only contains GenerateVillagersJava.java, so it's not clear why others get compiled.
11.  Run GenerateVillagersJava via Run > Run Configurations > GenerateVillagersJava (or find that build configuration using the little downarrow button beside the Run icon).
12.  Right click on the project node in the Package Explorer and select Refresh
13.  Use Project > Build Project to compile all the other Java files.
14.  Run src\caspFishing\GenerateVillagersJava.java by right clicking on it and selecting Run As then Java Application
15.  Step 5 may have failed due to an unsatisfied link error for jpl.jar. However, an Eclipse launcher will have been generated, which can now be edited. In the Eclipse package explorer, right click on the project name then properties and select Run/Debug Settings. Select GenerateVillagersJava then Edit. In the Environment tab, add two new variables: PATH containing the paths to yourjpl.jar file and the SWI Prolog bin folder (e.g. C:\Program Files\swipl\lib\jpl.jar;C:\Program Files\swipl\bin), and SWI_HOME_DIR set to the path to your SWI Prolog installation folder (e.g. C:\Program Files\swipl).  
16.  If step 5 failed, run it again using the run configuration GenerateVillagersJava. Select configuration "Build installer for CaspFishing model" when prompted.
17.  Create folders /CaspFishing.rs/casp-config/villagers/scenarios and /CaspFishing.rs/casp-config/villagers/scenarios/institutions
(Note: this step could be avoided if the folder existed in the Git repository with, e.g., a README file in it)
18.  Run  VillagersScenarioCreatorInstitutions.java

You should now be ready to run the application. First make sure that the Eclipse console is showing (click on an icon on the right of Eclise's window showing a computer monitor). Then use the Run icon (a right-pointing triangle in a green circle) and selecting Casp Fishing model. After a while, another window will open. Click on the blue button showing a circle with a line at the top, wait for the console to show the words "Casp Fishing demo is Ready", then click on the play button (a triangle in a blue circle). When you get bored with watching console output, click on the stop button and close the window.

To re-run the application after making changes:

 If any Prolog code is changed:
 
 *  Run GenerateVillagersJava.java as Java application (to create Java code in src/generated)
 
 If Java changes:
 
 *  Run VillagersScenarioCreatorInstitutions.java (to create JSON files that scenario is run from).
