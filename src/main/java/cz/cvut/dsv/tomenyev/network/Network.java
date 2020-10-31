package cz.cvut.dsv.tomenyev.network;

import com.sun.tools.javac.util.Assert;
import cz.cvut.dsv.tomenyev.message.AbstractMessage;
import cz.cvut.dsv.tomenyev.message.Join;
import cz.cvut.dsv.tomenyev.utils.Constant;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Network {

    private static Network instance;

    public boolean init(Node node) {
        try {
            LocateRegistry
                    .createRegistry(node.getAddress().getPort())
                    .rebind(Constant.NAME, node);
        } catch (RemoteException ex) {
            //TODO
            return false;
        }
        return true;
    }

    public boolean election(Node node) {
        if (node.getNext() == null) {
            //TODO
            node.setLeader(node.getAddress());
        } else {
           //TODO
        }
        return true;
    }

    public boolean join(Node node, Address remote) throws RemoteException, NotBoundException {
            Join join = new Join(node.getAddress(), remote);
            node.setNext(remote);
            this.send(remote, join);
            return true;
    }
    public boolean quit(Node node) {
        return false;
    }

    public boolean send(Address remote, AbstractMessage message) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(remote.getIp(), remote.getPort());
        AbstractNode node = (AbstractNode) registry.lookup(Constant.NAME);
        node.handleMessage(message);
        return true;
    }

    public boolean send(Node origin, String message) {
        //TODO
        return true;
    }

    public static Network getInstance() {
        if (instance == null)
            instance = new Network();
        return instance;
    }
}
