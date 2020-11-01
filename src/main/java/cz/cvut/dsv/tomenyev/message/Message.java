package cz.cvut.dsv.tomenyev.message;

import cz.cvut.dsv.tomenyev.network.Address;
import cz.cvut.dsv.tomenyev.network.Node;

import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Message extends AbstractMessage {
    public Message(Address origin, Address destination, String message) {
        super(origin, destination);
    }

    @Override
    public void handleMessage(Node node) throws RemoteException, NotBoundException, UnknownHostException {

    }
}
