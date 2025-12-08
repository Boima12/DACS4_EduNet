package org.example.client.view.eClientConnector;

import java.io.IOException;

@FunctionalInterface
public interface OnEstablishListener {
	void onEstablish() throws IOException;
}
