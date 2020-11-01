package cz.cvut.dsv.tomenyev.message;

import cz.cvut.dsv.tomenyev.network.Address;
import cz.cvut.dsv.tomenyev.network.Network;
import cz.cvut.dsv.tomenyev.network.Node;
import lombok.Getter;
import lombok.Setter;

import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

@Getter
@Setter
public class Election extends AbstractMessage {

    private Address candidate;

    public Election(Address origin, Address destination, Address candidate) {
        super(origin, destination);
        this.candidate = candidate;
    }

    @Override
    public void handleMessage(Node node) throws RemoteException, NotBoundException, UnknownHostException {
        if(node.getAddress().equals(getCandidate())) {
            node.setLeader(node.getAddress());
            return;
        }
        if(node.getAddress().isGreaterThan(getCandidate())) {
            node.setLeader(node.getAddress());
            Election election = new Election(node.getAddress(), node.getNext(), node.getAddress());
            Network.getInstance().send(node.getNext(), election);
        } else {
            node.setLeader(getCandidate());
            Network.getInstance().send(node.getNext(), this);
        }
    }
}
