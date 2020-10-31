package cz.cvut.dsv.tomenyev.message;

import cz.cvut.dsv.tomenyev.network.Address;
import cz.cvut.dsv.tomenyev.network.Node;

public class AcceptJoin extends AbstractMessage {

    public AcceptJoin(Address origin, Address destination, Address next) {
        super(origin, destination);
    }

    @Override
    public void handleMessage(Node node) {

    }
}
