package cz.cvut.dsv.tomenyev.message;

import cz.cvut.dsv.tomenyev.network.Address;
import cz.cvut.dsv.tomenyev.network.Network;
import cz.cvut.dsv.tomenyev.network.Node;
import cz.cvut.dsv.tomenyev.utils.Constant;
import lombok.Getter;

import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

@Getter
public class Message extends AbstractMessage {

    private String message;

    public Message(Address origin, Address destination, String message) {
        super(origin, destination);
        this.message = message;
    }

    @Override
    public void handleMessage(Node node) throws RemoteException, NotBoundException, UnknownHostException {

        if(!node.getAddress().equals(getOrigin()))
            System.out.println(getOrigin() + " > " + getMessage());

        if(node.getNext().equals(getDestination()))
            return;

        Network.getInstance().send(node.getNext(), this);
    }
}