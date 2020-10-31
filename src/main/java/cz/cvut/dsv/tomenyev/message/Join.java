package cz.cvut.dsv.tomenyev.message;

import cz.cvut.dsv.tomenyev.network.Address;
import cz.cvut.dsv.tomenyev.network.Network;
import cz.cvut.dsv.tomenyev.network.Node;

public class Join extends AbstractMessage {


    public Join(Address origin, Address destination) {
        super(origin, destination);
    }

    @Override
    public void handleMessage(Node node) {
        if(node.getNext() == null) {
            node.setNext(this.getOrigin());
            node.initElection();
        } else {

//            Network.getInstance().send(getOrigin(),)
        }
    }
}
