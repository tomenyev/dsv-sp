package cz.cvut.dsv.tomenyev;

import cz.cvut.dsv.tomenyev.network.Address;
import cz.cvut.dsv.tomenyev.network.Node;
import cz.cvut.dsv.tomenyev.utils.Log;

public class Main {
    public static void main(String[] args) throws Exception {
        Log logger;
        Address address;
        Address remote;
        Node node;

        if(args.length < 2 || args.length > 3) {
            //TODO
            return;
        }

        address = new Address(args[0]);

        if(args.length != 2) {
            remote = new Address(args[1]);
        }

        try {
            logger = new Log(args[args.length == 2 ? 1 : 2]);

            System.setProperty("java.rmi.server.hostname",address.getIp());

            node = new Node(address, logger);

        } catch (Exception ignored) {
            //TODO
        }

    }
}
