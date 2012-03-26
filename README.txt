===============================================================================
ROBOTIUM Remote
===============================================================================
This is the remote solution for Robotium. 

This solution has a standalone server apk and takes use of the client side and message mechanism of solution from activars (https://github.com/activars/remote-robotium)

* SETUP

To setup development enviroment in Eclipse, you need to install M2E: http://download.eclipse.org/technology/m2e/releases, and import the pom file in the project root.

After importing, there will be 4 projects:

  robotium-remote: the root project.

  robotium-common: the common library (message handling classes)

  robotium-agent:  the server side code which need to be compiled as android project, and installed on the phone. Use instrumentaion command to start the server.

  robotium-client: client side code through which user can control the phone with the agent started.

Run Maven Install on the root project (Ignore the error on robotium-agent).

* SERVER INSTALLATION

Run project robotium-agent as Android project. After it is installed on the emnulator or phone, run this command on shell:

"adb shell am instrument -w com.aps.arobot.agent/.RIS"

* CLIENT INSTALLATION

