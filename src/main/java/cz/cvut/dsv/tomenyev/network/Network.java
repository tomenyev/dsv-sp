package cz.cvut.dsv.tomenyev.network;

import cz.cvut.dsv.tomenyev.message.*;
import cz.cvut.dsv.tomenyev.utils.Constant;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Network {

    private static Network instance;

    public void init(Node node) {
        try {
            LocateRegistry
                    .createRegistry(node.getAddress().getPort())
                    .rebind(Constant.NAME, node);
        } catch (RemoteException ex) {
            //TODO
           node.setOk(false);
        }
        node.setOk(true);
    }

    public void election(Node node) throws RemoteException, NotBoundException {
        if (node.getNext() == null) {
            //TODO
            node.setLeader(node.getAddress());
        } else {
            Election election = new Election(node.getAddress(), node.getNext(), node.getAddress());
            this.send(node.getNext(), election);
        }
    }

    public void join(Node node, Address remote) throws RemoteException, NotBoundException {
        Join join = new Join(node.getAddress(), remote);
        this.send(remote, join);
    }

    public void quit(Node node) throws RemoteException, NotBoundException {
        if(Objects.isNull(node.getNext())) {
            //TODO
            return;
        }
        Quit quit = new Quit(
                node.getAddress(),
                node.getAddress().equals(node.getLeader()) ? node.getNext() : node.getLeader(),
                node.getNext()
        );
        this.send(node.getAddress().equals(node.getLeader()) ? node.getNext() : node.getLeader(), quit);
        node.setOk(false);
    }

    public void send(Address destination, AbstractMessage message) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(destination.getIp(), destination.getPort());
        AbstractNode node = (AbstractNode) registry.lookup(Constant.NAME);
        node.handleMessage(message);
    }

    public void send(Node node, String message) {
        if(!Objects.isNull(node.getNext())) {
            Message m = new Message(node.getAddress(), node.getLeader(), message);
            try {
                this.send(node.getLeader(), m);
            } catch (Exception e) {
                //TODO
            }
        } else {
            //TODO
        }
    }

    public static Network getInstance() {
        if (instance == null)
            instance = new Network();
        return instance;
    }
}
