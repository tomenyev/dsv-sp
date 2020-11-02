package cz.cvut.dsv.tomenyev.message;

import cz.cvut.dsv.tomenyev.network.Address;
import cz.cvut.dsv.tomenyev.network.Network;
import cz.cvut.dsv.tomenyev.network.Node;
import lombok.Getter;

import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

@Getter
public class Quit extends AbstractMessage {

    private final Address next;

    public Quit(Address origin, Address destination, Address next) {
        super(origin, destination);
        this.next = next;
    }

    @Override
    public void handleMessage(Node node) throws RemoteException, NotBoundException, UnknownHostException {
        if(node.getNext().equals(getOrigin())) {
            if(!node.getAddress().equals(getNext()))
                node.setNext(getNext());
           return;
        }
        Network.getInstance().send(node.getNext(), this);
    }
}
