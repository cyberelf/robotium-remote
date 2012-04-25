# This script shows how to use jython client to control the device
#	Step1: Build assembly 
#			mvn clean package assembly:single
#	Step2: Run jython with assembly jar files
#			jython -Dpython.path=$ANDROID_HOME/platforms/android-[version]/android.jar:robotium/robotium-common/target/robotium-common-1.0.0-SNAPSHOT-jar-with-dependencies.jar:robotium/robotium-common/target/robotium-client-1.0.0-SNAPSHOT-jar-with-dependencies.jar
#	
from com.jayway.android.robotium.remotesolo import RemoteSolo

target = "com.example.android.notepad.NotesList"
solo = RemoteSolo(target)

solo.addDevice("3432B7F9D37C00EC", 8085)
solo.connect()

solo.clickOnMenuItem("Add note")
solo.assertCurrentActivity("Expected NoteEditor activity", "NoteEditor")
solo.enterText(0, "Note 1")
solo.goBack()
