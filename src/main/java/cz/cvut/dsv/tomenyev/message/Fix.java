package cz.cvut.dsv.tomenyev.message;

import cz.cvut.dsv.tomenyev.network.Address;
import cz.cvut.dsv.tomenyev.network.Network;
import cz.cvut.dsv.tomenyev.network.Node;
import lombok.Getter;
import lombok.Setter;

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

    private boolean done = false;

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
            if(Objects.isNull(node.getNext()) && Objects.isNull(node.getPrev())) {
                if(isDirection()) {
                    node.setPrev(getSendBy());
                    setDirection(false);
                    setPrev(node.getAddress());
                } else {
                    node.setNext(getSendBy());
                    setDirection(true);
                    setNext(node.getAddress());
                }
            } else {
                if(isDirection()) {
                    setDirection(false);
                    setPrev(node.getAddress());
                } else {
                    setDirection(true);
                    setNext(node.getAddress());
                }
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
            if(node.getAddress().equals(getDestination()) && Objects.nonNull(node.getNext()) && Objects.nonNull(node.getPrev())) {
                node.setLeader(getCandidate());
                setFixed(true);
            }
        }
        else {
            if(isDone() && node.getAddress().equals(getOrigin())) {
                node.setNext(!isDirection() ? getSendBy() : node.getNext());
                node.setPrev(isDirection() ? getSendBy() : node.getPrev());
                setDone(false);
                setFixed(true);
            } else if(!node.getAddress().equals(getOrigin()) && Objects.isNull(node.getNext())) {
                node.setNext(getOrigin());
                setDirection(true);
                setDone(true);
            } else if(!node.getAddress().equals(getOrigin()) && Objects.isNull(node.getPrev())) {
                node.setPrev(getOrigin());
                setDirection(false);
                setDone(true);
            } else if(getQuit().equals(node.getNext())) {
                node.setNext(null);
                setDirection(false);
            } else if(getQuit().equals(node.getPrev())) {
                node.setPrev(null);
                setDirection(true);
            } else if(node.getAddress().equals(getOrigin())) {
                setFixed(true);
            }
            if(!node.getAddress().equals(getOrigin()) && node.isFixing() || isDirection() && Objects.isNull(node.getNext()) || !isDirection() && Objects.isNull(node.getPrev())) {
                setDirection(!isDirection());
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

        setSendBy(node.getAddress());
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
