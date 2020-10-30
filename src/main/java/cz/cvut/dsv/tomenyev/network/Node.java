package cz.cvut.dsv.tomenyev.network;

import cz.cvut.dsv.tomenyev.utils.Constants;
import cz.cvut.dsv.tomenyev.utils.Log;
import lombok.*;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteObject;
import java.rmi.server.UnicastRemoteObject;

@Getter
@Setter
public class Node extends UnicastRemoteObject {

    private final Address address;

    private final Log logger;

    private Address next;

    private Address prev;

    private Address leader;

    private boolean running = false;

    private final NetworkManager network = new NetworkManager();

    public Node(Address address, Log logger) throws RemoteException {
        this.address = address;
        this.logger = logger;
    }

    public Node createNetwork() {
        running = network.create(this);
        return this;
    }

}
