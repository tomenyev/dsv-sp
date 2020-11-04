package cz.cvut.dsv.tomenyev.message;

import cz.cvut.dsv.tomenyev.network.Address;
import cz.cvut.dsv.tomenyev.network.Network;
import cz.cvut.dsv.tomenyev.network.Node;
import cz.cvut.dsv.tomenyev.utils.Constant;
import javafx.util.Pair;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Objects;

@Getter
@ToString
public class Message extends AbstractMessage {

    private final String message;

    @Setter
    private Address newDestination;

    public Message(Address origin, Address destination, String message) {
        super(origin, destination);
        this.message = message;
    }

    @Override
    public void handleMessage(Node node) throws RemoteException, NotBoundException, UnknownHostException {

        if(!node.getAddress().equals(getOrigin())) {
            System.out.println(getOrigin() + " > " + getMessage());
            node.addMessage(getOrigin() + " > " + getMessage());
        }

        if(node.getNext().equals(Objects.isNull(getNewDestination()) ? getDestination() : getNewDestination()))
            return;

        Network.getInstance().send(node, node.getNext(), this);
    }

    @Override
    public String toString() {
        return "Message{" +
                "origin=" + getOrigin() + " " +
                "destination=" + getDestination() + " " +
                "newDestination=" + getNewDestination() + " " +
                "message" + getMessage() +
                "}";
    }
}
