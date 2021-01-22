package cz.cvut.dsv.tomenyev.message;

import cz.cvut.dsv.tomenyev.network.Address;
import cz.cvut.dsv.tomenyev.network.Network;
import cz.cvut.dsv.tomenyev.network.Node;
import lombok.Getter;

@Getter
public class Elected extends AbstractMessage {

    private Address leader;

    public Elected(Address origin, Address destination, Address leader) {
        super(origin, destination);
        this.leader = leader;
    }

    @Override
    public void handleMessage(Node node) throws Exception {
        node.setLeader(getLeader());
        node.setVoted(false);
        if (!node.getNext().equals(getLeader())) {
            Elected elected = new Elected(node.getAddress(), node.getNext(), getLeader());
            Network.getInstance().send(node, node.getNext(), elected);
        }
    }

    @Override
    public String toString() {
        return "Elected{" +
                "origin=" + getOrigin() + " " +
                "destination=" + getDestination() + " " +
                "candidate=" + getLeader() +
                "}";
    }
}
