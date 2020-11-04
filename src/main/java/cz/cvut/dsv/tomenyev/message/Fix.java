package cz.cvut.dsv.tomenyev.message;

import cz.cvut.dsv.tomenyev.network.Address;
import cz.cvut.dsv.tomenyev.network.Network;
import cz.cvut.dsv.tomenyev.network.Node;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

@Getter
@ToString
public class Fix extends AbstractMessage{

    private final Address quit;

    private Address next;

    @Setter
    @Getter
    private Address candidate;

    public Fix(Address origin, Address destination, Address quit, Address candidate) {
        super(origin, destination);
        this.quit = quit;
        this.candidate = candidate;
    }

    @Override
    public void handleMessage(Node node) throws RemoteException, NotBoundException, UnknownHostException {
        if(node.getAddress().equals(getCandidate())) {
            node.setLeader(getCandidate());
            if(!quit.equals(node.getNext()) && !quit.equals(node.getPrev()))
                return;
        } else if(node.getAddress().isGreaterThan(getCandidate())) {
            setCandidate(node.getAddress());
        } else {
            node.setLeader(getCandidate());
        }

        if(node.getPrev().equals(getQuit())) {
            node.setPrev(getOrigin());
        }

        if(node.getAddress().equals(getOrigin())) {
            node.setNext(getNext());
            if(node.getAddress().equals(node.getNext()) || node.getAddress().equals(node.getPrev()) || node.getNext().equals(quit) || node.getPrev().equals(quit)) {
                node.setNext(null);
                node.setPrev(null);
                node.setLeader(null);
            }
        }
        Network.getInstance().send(node, node.getPrev(), this.withNext(node.getAddress()));
    }

    private AbstractMessage withNext(Address next) {
        this.next = next;
        return this;
    }

    @Override
    public String toString() {
        return "Fix{" +
                "origin=" + getOrigin() + " " +
                "destination=" + getDestination() + " " +
                "quit=" + getQuit() + " " +
                "next=" + getNext() + " " +
                "candidate=" + getCandidate() +
                "}";
    }
}
