ROBOTIUM Remote
===============

This is the remote solution for Robotium. 

This solution has a standalone server apk and takes use of the client side and message mechanism of solution from activars (https://github.com/activars/remote-robotium)

COMPILE FROM SOURCE
-----

### ENVIROMENT SETUP

To setup development enviroment in Eclipse, you need to install [M2E](http://download.eclipse.org/technology/m2e/releases), and import the pom file in the project root.

After importing, there will be 4 projects:
* robotium-remote: the root project.
* robotium-common: the common library (message handling classes)
* robotium-agent:  the server side code which need to be compiled as android project, and installed on the phone. Use instrumentaion command to start the server.
* robotium-client: client side code through which user can control the phone with the agent started.

### COMPILATION

Follow the steps to compile and run 

* Run `Maven Install` on project robotium-common and robotium-client.
* Right click on 'src' folder on project robotium-agent, and select `Build Path` then `Use as Source Folder`.
* Run project robotium-agent as android project.

RUN YOUR TEST
-------------

### Start the agent

* Start your emulator with the robotium-agent installed.
* Run this command on shell: `adb shell am instrument -w com.aps.arobot.agent/.RIS`

### Run the java client example

* Install android example project 'Notepad' on the emulator.
* Open project robotium-client and `Use as Source Folder` for folder 'src/main/test'
* Run 'example.android.notepad.test.' as JUnit Test.

### Run the python client example

* Install Jython.
* Run `mvn clean package assembly:single` on project robotium-common and robotium-client.
* run the jython command: 
  `jython -Dpython.path=android.jar:robotium-common-1.0.0-SNAPSHOT-jar-with-dependencies.jar:robotium-client-1.0.0-SNAPSHOT-jar-with-dependencies.jar`
* Copy the lines in file `examples/rclient.py` to the jython shell.

### Client of Other languages

To be added...