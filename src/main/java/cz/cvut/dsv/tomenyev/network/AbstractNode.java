package cz.cvut.dsv.tomenyev.network;

import cz.cvut.dsv.tomenyev.message.AbstractMessage;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Base remote node representation in the network.
 */
public interface AbstractNode extends Remote {
    /**
     * Handle income messages.
     * @param message income message
     * @throws RemoteException
     */
    void handleMessage(AbstractMessage message) throws RemoteException;

}
