package com.jayway.android.robotium.remotesolo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class DeviceClientManager {

	private Class<?> targetClass;
	private String targetClassName;
	private static final Object lock = new Object();
	private static final int DEVICEPORT = 8085;

	DeviceClientManager() {
	}

	void setTargetClass(Class<?> targetClass) {
		this.targetClass = targetClass;
		this.targetClassName = targetClass.getName();
	}

	void setTargetClass(String targetClass) {
		this.targetClassName = targetClass;
	}

	/**
	 * Connects to a device that will be managed by the DeviceClientManager
	 * 
	 * @param deviceSerial
	 * @param pcPort
	 * @param devicePort
	 */
	void addDevice(String deviceSerial, int pcPort, int devicePort) {
		if (targetClass!=null) {
			DeviceClientImpl.newInstance(deviceSerial, pcPort, devicePort,
				targetClass);
		} else {
			DeviceClientImpl.newInstance(deviceSerial, pcPort, devicePort,
					targetClassName);
		}
	}

	void addDevice(String deviceSerial, int pcPort) {
		addDevice(deviceSerial, pcPort, DEVICEPORT);
	}

	void connectAll() {
		final Map<String, DeviceClient> devices = DeviceClientImpl
				.getCurrentDevices();
		Iterator<String> it = devices.keySet().iterator();
		ExecutorService pool = Executors.newFixedThreadPool(devices.size());
		final ArrayList<DeviceClient> failedToConnect = new ArrayList<DeviceClient>();
		while (it.hasNext()) {
			final DeviceClient dc = devices.get(it.next());
			pool.execute(new Runnable() {
				public void run() {
					// make device connection
					boolean connected = dc.connect();
					if (!connected) {
						synchronized (lock) {
							failedToConnect.add(dc);
							String errMsg = String
									.format("Device %s failed connecting to Robotium server.",
											dc.getDeviceSerial());
							System.err.println(errMsg);
						}
					}
				}
			});
		}
		pool.shutdown();
		try {
			pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	void disconnectAllDevices() throws RemoteException {
		synchronized (lock) {
			Map<String, DeviceClient> devices = DeviceClientImpl
					.getCurrentDevices();
			for (Object key : devices.keySet().toArray()) {
				devices.get(key).disconnect();
			}
		}
	}

}
