package com.asp.android.rdp.server;

import java.util.ArrayList;

import android.content.Context;
import android.os.Binder;

import com.asp.android.rdp.interfaces.IRDPConnectionListener;
import com.asp.android.rdp.interfaces.IRDPConnectionManager;
import com.asp.android.rdp.localised.model.RDPConnection;

public class RDPConnectionManager extends Binder implements
		IRDPConnectionManager {

	ArrayList<RDPConnection> rdpConnections;

	private static final String BINDER_DESCRIPTOR = "IRDPConnectionManager";

	private Context context;

	public RDPConnectionManager(Context context) {
		this.context = context;
	}

	public void connect(final String host, final String domain,
			final String directory, final int flags,final int performanceFalgs, final String userName,
			final String password, final String screenResolution,
			final int colorDepth,
			final IRDPConnectionListener connectionListener) {
		final RDPConnection rdpConnection = new RDPConnection(context, this);
		Thread thread = new Thread(new Runnable() {

			public void run() {
				boolean status = rdpConnection.connect(host, domain, directory,
						flags,performanceFalgs, userName, password, screenResolution,
						colorDepth, connectionListener);
				if (!status) {
					return;
				}
				if (rdpConnections == null) {
					rdpConnections = new ArrayList<RDPConnection>();
				}
				rdpConnections.add(rdpConnection);
				connectionListener.connectionSucceeded(rdpConnections
						.indexOf(rdpConnection));
			}
		});
		thread.start();
	}

	public RDPConnection getRDPConnection(int id) {
		if (rdpConnections != null && rdpConnections.size() > id) {
			return rdpConnections.get(id);
		}
		return null;
	}

	public void endRDPSession(int id) {
		RDPConnection connection=getRDPConnection(id);
		if(connection!=null){
			rdpConnections.remove(connection);
		}
		System.gc();
	}

}
