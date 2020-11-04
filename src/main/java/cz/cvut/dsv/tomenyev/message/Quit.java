package cz.cvut.dsv.tomenyev.message;

import cz.cvut.dsv.tomenyev.network.Address;
import cz.cvut.dsv.tomenyev.network.Network;
import cz.cvut.dsv.tomenyev.network.Node;
import cz.cvut.dsv.tomenyev.utils.Constant;
import javafx.util.Pair;
import lombok.Getter;
import lombok.ToString;
import sun.nio.ch.Net;

import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Objects;

@ToString
@Getter
public class Quit extends AbstractMessage {

    private final Address next;

    private final Address prev;

    public Quit(Address origin, Address destination, Address next, Address prev) {
        super(origin, destination);
        this.next = next;
        this.prev = prev;
    }

    @Override
    public void handleMessage(Node node) throws RemoteException, NotBoundException, UnknownHostException {

        if(node.getPrev().equals(getOrigin()))
            node.setPrev(getPrev().equals(node.getAddress()) ? null : getPrev());

        if(node.getNext().equals(getOrigin()))
            node.setNext(getNext().equals(node.getAddress()) ? null : getNext());

        if(Objects.isNull(node.getNext()) || node.getNext().equals(getDestination())) {
            if(Constant.AUTOPILOT)
                Network.getInstance().election(node);
            return;
        }

        Network.getInstance().send(node, node.getNext(), this);

    }

    @Override
    public String toString() {
        return "Message{" +
                "origin=" + getOrigin() + " " +
                "destination=" + getDestination() + " " +
                "next=" + getNext() + " " +
                "prev=" + getPrev() +
                "}";
    }
}
