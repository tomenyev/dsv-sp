package cz.cvut.dsv.tomenyev.message;

import cz.cvut.dsv.tomenyev.network.Address;
import cz.cvut.dsv.tomenyev.network.Network;
import cz.cvut.dsv.tomenyev.network.Node;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Join extends AbstractMessage {


    public Join(Address origin, Address destination) {
        super(origin, destination);
    }

    @Override
    public void handleMessage(Node node) throws RemoteException, NotBoundException {
        if(getOrigin().equals(node.getAddress()) && node.getNext() == null) {
            node.setNext(getDestination());
            return;
        }
        if(node.getNext() == null) {
            node.setNext(getOrigin());
            Network.getInstance().send(getOrigin(), this);
            return;
        }
        if(node.getNext().equals(getDestination())) {
            node.setNext(getOrigin());
            Network.getInstance().send(getOrigin(), this);
        } else {
            Network.getInstance().send(node.getNext(), this);
        }
    }
}
