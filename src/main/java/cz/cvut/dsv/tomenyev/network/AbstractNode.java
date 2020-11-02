package cz.cvut.dsv.tomenyev.network;

import cz.cvut.dsv.tomenyev.message.AbstractMessage;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AbstractNode extends Remote {

    void handleMessage(AbstractMessage message) throws RemoteException;

}
