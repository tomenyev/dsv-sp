package cz.cvut.dsv.tomenyev.message;

import cz.cvut.dsv.tomenyev.network.Address;
import cz.cvut.dsv.tomenyev.network.Network;
import cz.cvut.dsv.tomenyev.network.Node;
import cz.cvut.dsv.tomenyev.utils.Constant;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@Getter
public class Join extends AbstractMessage {

    private Address sendBy;

    public Join(Address origin, Address destination, Address sendBy) {
        super(origin, destination);
        this.sendBy = sendBy;
    }

    @Override
    public void handleMessage(Node node) throws Exception {
        if(node.getAddress().equals(getOrigin())) {
            node.setNext(getDestination());
            node.setPrev(getSendBy());
            if(Constant.AUTOPILOT)
                Network.getInstance().election(node);

        } else if(node.getAddress().equals(getDestination())) {
            Address sendTo = Objects.isNull(node.getPrev()) ? getSendBy() : node.getPrev();
            node.setNext(Objects.isNull(node.getNext()) ? getSendBy() : node.getNext());
            node.setPrev(getSendBy());
            Network.getInstance().send(node, sendTo, this.withSendBy(node.getAddress()));
        } else if(node.getNext().equals(getDestination())) {
            node.setNext(getOrigin());
            Network.getInstance().send(node, node.getNext(), this.withSendBy(node.getAddress()));
        } else {
            System.out.println("join exception has occurred.");
        }
    }

    private Join withSendBy(Address sendBy) {
        this.sendBy = sendBy;
        return this;
    }

    @Override
    public String toString() {
        return "Join{" +
                "origin=" + getOrigin() + " " +
                "destination=" + getDestination() + " " +
                "sendBy=" + getSendBy() +
                "}";
    }
}
