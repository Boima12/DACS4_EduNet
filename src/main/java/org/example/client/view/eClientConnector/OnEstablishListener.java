package org.example.client.view.eClientConnector;

import java.io.IOException;

@FunctionalInterface
public interface OnEstablishListener {
	void onEstablish(String serverIP, String port, String maLienKet) throws IOException;
}
