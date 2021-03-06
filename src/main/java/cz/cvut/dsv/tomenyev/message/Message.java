package cz.cvut.dsv.tomenyev.message;

import cz.cvut.dsv.tomenyev.network.Address;
import cz.cvut.dsv.tomenyev.network.Network;
import cz.cvut.dsv.tomenyev.network.Node;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
/**
 * Message used for broadcasting text content to the network.
 */
public class Message extends AbstractMessage {

    private final String message;

    private Address newDestination;

    public Message(Address origin, Address destination, String message) {
        super(origin, destination);
        this.message = message;
    }

    @Override
    public void handleMessage(Node node) throws Exception {
        if(!node.getAddress().equals(getOrigin()) && !getDestination().equals(getNewDestination()))
            node.addMessage(getMessage());

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
                "message=" + getMessage() +
                "}";
    }
}
