package cz.cvut.dsv.tomenyev.network;

import cz.cvut.dsv.tomenyev.utils.Constants;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class NetworkManager {

    public boolean create(Node node) {
        try {
            LocateRegistry
                    .createRegistry(node.getAddress().getPort())
                    .rebind(Constants.NAME, node);
        } catch (RemoteException ex) {
            //TODO
            return false;
        }
        return true;
    }

    public boolean join(Node node, Address address) {
            //TODO
            return true;
    }
    public boolean leave(Node node) {
        return true;
    }

    public void send() {
        //TODO
    }
}
