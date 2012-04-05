package com.aps.arobot.agent;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import com.jayway.android.robotium.common.message.MessageFactory;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/*
 *  Service Class, start netty server on tcp port 8085 (or requested port)
 *  	receiving messages from client and parse to Solo server class RIS.java
 *
 *	@author Darren Zhao, cyberelfd@gmail.com
 */
public class ARSService extends Service {

	private static final String TAG = "ARSService";
		
	// Default socket, use 8080
	public static final int DEFAULT_PORT = 8085;
	public String SERVERIP;
		
	//In buffer from web server
	private CBuffer cmdBuffer = new CBuffer();
	//Out buffer to the client
	private CBuffer opBuffer = new CBuffer();
	
	private ChannelFactory channelfactory;
	private ChannelGroup channelGroup;
	
	private final IsService.Stub mBinder = new IsService.Stub() {
		public String getMessage() {
			return ARSService.this.getMessage();
		}
		public void output(String op) {
			ARSService.this.addOutput(op);
		}
	};
	
	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate");
		//Start server on creation
		startServer();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "onBind");
		return mBinder;
	}
	
	@Override
	public void onStart(Intent intent, int startid) {
		Log.d(TAG, "onStart");
	}
	
	
	@Override
	public void onDestroy() {
		ChannelGroupFuture future = channelGroup.close();
		future.awaitUninterruptibly();
		channelfactory.releaseExternalResources();
    }
	
	//
	public String getMessage() {
		try {
			return cmdBuffer.get();
		} catch (InterruptedException e) {
			Log.d(TAG, "Getting message Error: " + e.getMessage());
			return null;
		}
	}
	
	public void addOutput(String s) {
		opBuffer.add(s);
	}
	
	/**
     * Start the http server
     * 
     */
	private void startServer() {
		// The port number
		int p = DEFAULT_PORT;
		Log.d(TAG, "Start listening to port:" + p);
		// Create bootstrap
		channelfactory = new NioServerSocketChannelFactory(
				Executors.newCachedThreadPool(), Executors
				.newCachedThreadPool());
		ServerBootstrap bootstrap = new ServerBootstrap(channelfactory);
		bootstrap.setOption("keepAlive", true);
		
		Log.d(TAG, "Server bootstrap initialized");
		
		channelGroup = new DefaultChannelGroup();
		
		// Create Pipeline
		com.aps.arobot.sserver.ServerPipelineFactory pipelineFactory = new com.aps.arobot.sserver.ServerPipelineFactory();
		pipelineFactory.setHandler(new SimpleChannelUpstreamHandler() {
			private static final String TAG = "ServerHandler";
			
			@Override
			public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e)
					throws Exception {
				if (e instanceof ChannelStateEvent) {
					Log.d(TAG,(e.toString()));
				}
				super.handleUpstream(ctx, e);
			}
			@Override
			public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
					throws Exception {
				// first connection
				e.getChannel().write(MessageFactory.createTargetActivityRequestMessage().toString() + "\r\n");
			}
			@Override
			public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
				// We do not need to write a ChannelBuffer here.
				// We know the encoder inserted at ServerPipelineFactory will do the
				// conversion.
				ChannelFuture future = null;
				// never close for now
				boolean close = false;
				
				//Handle some special messages and drop the rest to the queue
				String messageString = (String) e.getMessage();
				if (messageString.equals("exit") || messageString.equals("disconnect")) {
					future = e.getChannel().write("Test End.\r\n");
					close = true;
				} else {
					Log.d(TAG, "Message got from client: " + messageString);
					cmdBuffer.add(messageString);
					try {
						e.getChannel().write(opBuffer.get() + "\r\n");
					} catch (InterruptedException ex) {
						Log.d(TAG,(e.toString()));
					}
				}
				if (close) {
					future.addListener(ChannelFutureListener.CLOSE);
				}
			}
			
		});
		bootstrap.setPipelineFactory(pipelineFactory);
		Log.d(TAG, "Configured server pipline factory");
		
		// NOTE: This is a work around to prevent the bad address error.
		// This was a bug exposed on Android 2.2
		// http://code.google.com/p/android/issues/detail?id=9431
		System.setProperty("java.net.preferIPv6Addresses", "false");
		
		// Bind and start to accept incoming connections.
		Channel channel = bootstrap.bind(new InetSocketAddress(p));
		channelGroup.add(channel);
		Log.d(TAG, "Server is now running");
	}
	
	
	
	/**
     * Command Buffer
     * 
     */
    public class CBuffer {
        private List<String> list =
            new ArrayList<String>();

        public synchronized void add(String cmd) {
            list.add(cmd);
            notifyAll();
        }

        public synchronized String get()
            throws InterruptedException
        {
            while (list.size() == 0)
                wait();
            return list.remove(0);
        }
    }
    
}
