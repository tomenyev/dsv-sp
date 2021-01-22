package cz.cvut.dsv.tomenyev.message;

import cz.cvut.dsv.tomenyev.network.Address;
import cz.cvut.dsv.tomenyev.network.Network;
import cz.cvut.dsv.tomenyev.network.Node;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
/**
 * Message used for providing leader election in the network.
 */
public class Election extends AbstractMessage {

    private Address candidate;

    public Election(Address origin, Address destination, Address candidate) {
        super(origin, destination);
        this.candidate = candidate;
    }

    @Override
    public void handleMessage(Node node) throws Exception {
        if(getCandidate().isGreaterThan(node.getAddress())) {
            node.setVoted(true);
            Election election = new Election(node.getAddress(), node.getNext(), getCandidate());
            Network.getInstance().send(node, node.getNext(), election);
        } else if(node.getAddress().isGreaterThan(getCandidate()) && !node.isVoted()) {
            node.setVoted(true);
            Election election = new Election(node.getAddress(), node.getNext(), node.getAddress());
            Network.getInstance().send(node, node.getNext(), election);
        } else if(getCandidate().equals(node.getAddress())) {
            node.setLeader(getCandidate());
            node.setVoted(false);
            Elected elected = new Elected(node.getAddress(), node.getNext(), node.getLeader());
            Network.getInstance().send(node, node.getNext(), elected);
        }
    }

    @Override
    public String toString() {
        return "Election{" +
                "origin=" + getOrigin() + " " +
                "destination=" + getDestination() + " " +
                "candidate=" + getCandidate() +
                "}";
    }
}
