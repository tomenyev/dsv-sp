package cz.cvut.dsv.tomenyev.message;

import cz.cvut.dsv.tomenyev.network.Address;
import cz.cvut.dsv.tomenyev.network.Network;
import cz.cvut.dsv.tomenyev.network.Node;
import cz.cvut.dsv.tomenyev.utils.Constant;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;


@Getter
@Setter
/**
 * Message used for providing safety quit.
 */
public class Quit extends AbstractMessage {

    private Address next;

    private Address prev;

    private Address newDestination;

    public Quit(Address origin, Address destination, Address next, Address prev, Address newDestination) {
        super(origin, destination);
        this.next = next;
        this.prev = prev;
        this.newDestination = newDestination;
    }

    @Override
    public void handleMessage(Node node) throws Exception {
        if(node.getAddress().equals(getOrigin())) {
            node.clear();
            return;
        }

        if(node.getPrev().equals(getOrigin()))
            node.setPrev(getPrev().equals(node.getAddress()) ? null : getPrev());

        if(node.getNext().equals(getOrigin()))
            node.setNext(getNext().equals(node.getAddress()) ? null : getNext());

        if(Objects.isNull(node.getNext()) || node.getNext().equals(getNewDestination())) {
            if(Constant.AUTOPILOT)
                Network.getInstance().election(node);
            return;
        }

        Network.getInstance().send(node, node.getNext(), this);
    }

    @Override
    public String toString() {
        return "Quit{" +
                "origin=" + getOrigin() + " " +
                "destination=" + getDestination() + " " +
                "next=" + getNext() + " " +
                "prev=" + getPrev() +
                "}";
    }
}
