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
import java.util.Objects;

@Getter
@Setter
public class Fix extends AbstractMessage{

    private final Address quit;

    private Address next;

    private Address prev;

    private Address sendBy;

    private boolean joinFailed = false;

    private boolean direction;

    private boolean fixed = false;

    private Address candidate;

    public Fix(Address origin, Address destination, Address quit, Address candidate, boolean direction) {
        super(origin, destination);
        this.quit = quit;
        this.candidate = candidate;
        this.direction = direction;
        this.sendBy = origin;
    }

    @Override
    public void handleMessage(Node node) throws Exception {
        if((Objects.isNull(node.getNext()) || Objects.isNull(node.getPrev())) && Objects.isNull(node.getLeader()) && !isJoinFailed()) {
            setJoinFailed(true);
            if(isDirection()) {
                node.setPrev(getSendBy());
                setDirection(false);
                setPrev(node.getAddress());
            } else {
                node.setNext(getSendBy());
                setDirection(true);
                setNext(node.getAddress());
            }
        } else if(isJoinFailed()) {
            if(Objects.isNull(node.getNext())) {
                node.setNext(getNext());
                node.setLeader(getCandidate());
                setFixed(true);
            }
            if(Objects.isNull(node.getPrev())) {
                node.setPrev(getPrev());
                node.setLeader(getCandidate());
                setFixed(true);
            }
            if(getQuit().equals(node.getPrev())) {
                node.setPrev(getPrev());
                setNext(node.getAddress());
            }
            if(getQuit().equals(node.getNext())) {
                node.setNext(getNext());
                setPrev(node.getAddress());
            }
            if(node.getAddress().equals(getDestination()) && Objects.nonNull(node.getNext())
            && Objects.nonNull(node.getPrev())) {
                node.setLeader(getCandidate());
                setFixed(true);
            }
        } else {
            if(getQuit().equals(node.getNext()) && Objects.isNull(getNext()) && Objects.isNull(getPrev())) {
                node.setNext(null);
                setPrev(node.getAddress());
                setDirection(false);
            } else if (getQuit().equals(node.getPrev()) && Objects.nonNull(getPrev()) && Objects.isNull(getNext())) {
                node.setPrev(getPrev());
                setNext(node.getAddress());
            } else if(Objects.isNull(node.getNext()) && Objects.nonNull(getNext()) && Objects.nonNull(getPrev())) {
                node.setNext(getNext());
                setFixed(true);
            }

            if(getQuit().equals(node.getPrev()) && Objects.isNull(getNext()) && Objects.isNull(getPrev())) {
                node.setPrev(null);
                setNext(node.getAddress());
                setDirection(true);
            } else if (getQuit().equals(node.getNext()) && Objects.nonNull(getNext()) && Objects.isNull(getPrev())) {
                node.setNext(getNext());
                setPrev(node.getAddress());
            } else if (Objects.isNull(node.getPrev()) && Objects.nonNull(getPrev()) && Objects.nonNull(getNext())) {
                node.setPrev(getPrev());
                setFixed(true);
            }
        }

        if(node.getAddress().equals(getCandidate()) && isFixed()) {
            node.setLeader(getCandidate());
            if(!getQuit().equals(node.getNext()) && !getQuit().equals(node.getPrev()))
                return;
        } else if(node.getAddress().isGreaterThan(getCandidate())) {
            setCandidate(node.getAddress());
        } else {
            node.setLeader(getCandidate());
        }

        Network.getInstance().send(node, isDirection() ? node.getNext() : node.getPrev(), this);

    }


    @Override
    public String toString() {
        return "Fix{" +
                "origin=" + getOrigin() + " " +
                "destination=" + getDestination() + " " +
                "quit=" + getQuit() +
                "}";
    }
}
