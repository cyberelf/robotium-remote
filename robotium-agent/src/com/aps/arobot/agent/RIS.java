//Command: adb shell am instrument -w com.aps.arobot.agent/.RIS
package com.aps.arobot.agent;

import java.lang.reflect.InvocationTargetException;
import com.aps.arobot.sserver.MessageWorker;
import com.jayway.android.robotium.common.message.EventInvokeMethodMessage;
import com.jayway.android.robotium.common.message.Message;
import com.jayway.android.robotium.common.message.MessageFactory;
import com.jayway.android.robotium.common.message.TargetActivityMessage;
import com.jayway.android.robotium.solo.Solo;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

public class RIS extends Instrumentation {
	
	private static final String TAG = "RIS";
	
	private Intent mTargetIntent;
	
	private final Bundle mResults = new Bundle();
	
	private AServiceConnection mConnection;
	
	private MessageWorker messageWorker;
	
	@Override
	public void onCreate(Bundle arguments) {
		Log.d(TAG, "onCreate");
		super.onCreate(arguments);
		messageWorker = new MessageWorker();
		//mIntent = getTargetContext().getPackageManager().getLaunchIntentForPackage(getTargetContext().getPackageName());
		//mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		start();
	}
	
	/**
     * Initialize the current thread as a looper.
     */
    void prepareLooper() {
        Looper.prepare();
    }
    
	@Override
	public void onStart() {
		prepareLooper();
		Log.d(TAG, "Starting Target...");
		//Start the target activity
		mResults.putString(Instrumentation.REPORT_KEY_IDENTIFIER, "RIS");
		//mActivity = startActivitySync(mIntent);
		//waitForIdleSync();
		Log.d(TAG, "Activity started...");

		//Bind server 		
		mConnection = new AServiceConnection();
		Intent sintent = new Intent("com.aps.arobot.agent.ARSService");
		try {
			//Stop the service and rebind
			getTargetContext().stopService(sintent);
			boolean r = getTargetContext().bindService(sintent, mConnection, Context.BIND_AUTO_CREATE);
			if (r) {
				Log.d(TAG, "Bound server");
				mResults.putString(Instrumentation.REPORT_KEY_STREAMRESULT,"SUCCESS");
			} else {
				Log.e(TAG, "Failed binding service");
				mResults.putString(Instrumentation.REPORT_KEY_STREAMRESULT,"FAILED");
			}
			
		} catch(Throwable t) {
			Log.e(TAG, "Failed binding service");
			mResults.putString(Instrumentation.REPORT_KEY_STREAMRESULT,"ERROR");
			t.printStackTrace();
		}
	}
	
	@Override
	public void onDestroy() {
		getTargetContext().unbindService(mConnection);
		//callActivityOnDestroy(mActivity);
    }
	
	/*
	 * Connector
	 */
	private class AServiceConnection implements ServiceConnection {
        Thread sthd;
        IsService sr;
		
        public void onServiceConnected(ComponentName name, IBinder service) {
        	Log.d(TAG, "onServiceConnected");
        	sr = IsService.Stub.asInterface(service);
        	//Start listening to the socket
			sthd = new Thread() {
				Solo mSolo;
				private Activity mActivity;
			    public void run() {
			        try {
			            while (true) {
			                // LISTEN FOR INCOMING messages
			            	Log.d(TAG, "Waiting for new messages...");
			                String m = sr.getMessage();
			                Log.d(TAG, "Message got from service: " + m);
			                Message mMessage = messageWorker.parseMessage(m);
			                
			                Message rslt = null;
			                
			                if (mMessage instanceof TargetActivityMessage) {
			    				Log.d(TAG, ((TargetActivityMessage) mMessage)
			    						.getMessageHeader());
			    				// the message contain Instrumentation information for the
			    				// Activity
			    				String activityClassName = ((TargetActivityMessage) mMessage)
			    						.getTargetClassName();

			    				Intent intent = new Intent(Intent.ACTION_MAIN);
			    				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			    				intent.setClassName(RIS.this.getTargetContext()
			    						.getPackageName(), activityClassName);
			    				mActivity = RIS.this.startActivitySync(intent);

			    				// initialize Solo 
			    				mSolo = new Solo(RIS.this, mActivity);
			    				// set configuration for message worker
			    				messageWorker.setConfiguration(mSolo, mActivity,
			    						RIS.this, mTargetIntent);

			    				// create response message and copy the old message UUID
			    				rslt = MessageFactory.createSuccessMessage();
			    			} else if (mMessage instanceof EventInvokeMethodMessage) {
			    				EventInvokeMethodMessage msg = (EventInvokeMethodMessage) mMessage;
			    				try {
			    					rslt = messageWorker.receivedEventInvokeMethodMessage(msg);
			    				} catch (IllegalArgumentException e1) {
			    					e1.printStackTrace();
			    					rslt = MessageFactory.createExceptionMessage(e1, "Error invoking method");
			    				} catch (IllegalAccessException e1) {
			    					e1.printStackTrace();
			    					rslt = MessageFactory.createExceptionMessage(e1, "Error invoking method");
			    				} catch (InvocationTargetException e1) {
			    					e1.printStackTrace();
			    					rslt = MessageFactory.createExceptionMessage(e1, "Error invoking method");
			    				}
			    			}
			                sr.output(rslt.toString());
			                Log.d(TAG, "Sent output to service..");
			            }
			        } catch (Exception e) {
			            e.printStackTrace();
			        }
			    }
			};
			sthd.start();
        	
        }

        public void onServiceDisconnected(ComponentName name) {
        	Log.d(TAG, "onServiceDisconnected");
        }
    }
}
