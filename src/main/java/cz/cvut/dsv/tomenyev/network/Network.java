package cz.cvut.dsv.tomenyev.network;

import cz.cvut.dsv.tomenyev.message.*;
import cz.cvut.dsv.tomenyev.utils.Constant;
import cz.cvut.dsv.tomenyev.utils.Counter;
import cz.cvut.dsv.tomenyev.utils.Log;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Objects;

@SuppressWarnings("UnusedReturnValue")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Network {

    private static Network instance;

    public void init(Node node) throws RemoteException {
        LocateRegistry
                .createRegistry(node.getAddress().getPort())
                .rebind(Constant.NAME, node);
        node.setOk(true);
    }

    public Network election(Node node) throws RemoteException, NotBoundException {
        if (node.getNext() == null) {
            Log.getInstance().print(Log.To.BOTH, "Node "+node.getAddress()+" is single");
            node.setLeader(node.getAddress());
        } else {
            Election election = new Election(node.getAddress(), node.getNext(), node.getAddress());
            this.send(node, node.getNext(), election);
        }
        return this;
    }

    public Network join(Node node, Address remote) throws RemoteException, NotBoundException {
        Join join = new Join(node.getAddress(), remote, node.getAddress());
        this.send(node, remote, join);
        return this;
    }

    public Network quit(Node node) throws RemoteException, NotBoundException {
        if(Objects.isNull(node.getNext())) {
            Log.getInstance().print(Log.To.BOTH, "Node "+node.getAddress()+" is single");
            node.clear();
            return this;
        }
        Quit quit = new Quit(
                node.getAddress(),
                node.getAddress().equals(node.getLeader()) ? node.getNext() : node.getLeader(),
                node.getNext(),
                node.getPrev()
        );
        this.send(node, node.getAddress().equals(node.getLeader()) ? node.getNext() : node.getLeader(), quit);
        node.clear();
        return this;
    }

    public Network send(Node origin, Address destination, AbstractMessage message) throws RemoteException, NotBoundException {
        message.setI(Counter.getInstance().inc());
        Log.getInstance().print(Log.To.BOTH, Counter.getInstance().getI(), origin.getAddress() + " has SEND message: \n\t "+message);

        try {
            Registry registry = LocateRegistry.getRegistry(destination.getIp(), destination.getPort());
            AbstractNode node = (AbstractNode) registry.lookup(Constant.NAME);
            node.handleMessage(message);
        } catch (Exception e) {
            origin.addDraft(message);
            if(Constant.AUTOPILOT)
                origin.fixNetwork(destination);
        }
        return this;
    }

    public Network send(Node node, String message) throws RemoteException, NotBoundException {
        if(!Objects.isNull(node.getNext())) {
            Message m = new Message(node.getAddress(), node.getLeader(), message);
            this.send(node, node.getLeader(), m);
        } else {
            Log.getInstance().print(Log.To.BOTH, "Node " + node.getAddress() + " is single");
        }
        return this;
    }

    public Network fix(Node node, Address quit) throws RemoteException, NotBoundException {
        if(quit.equals(node.getPrev()) && quit.equals(node.getNext()) || Objects.isNull(node.getNext()) || Objects.isNull(node.getPrev())) {
            Log.getInstance().print(Log.To.BOTH, "Node " + node.getAddress() + " is single");
            node.clear().setFixing(false);
            return this;
        }

        node.setFixing(true);

        //TODO handle error
        try {
            Fix fix = new Fix(node.getAddress(), node.getPrev(), quit, node.getAddress());
            node.setLeader(node.getAddress());
            this.send(node, node.getPrev(), fix);
        } catch (Exception e) {
            node.clear().setFixing(false);
        }

        return this;
    }

    public Network handleFails(Node node) {
        node.setFixing(false);
        if(Objects.nonNull(node.getNext())) {
            try {
                while(!node.getDrafts().isEmpty()) {
                    AbstractMessage message = node.getDrafts().remove();
                    if(message instanceof Message) {
                        ((Message) message).setNewDestination(node.getLeader());
                        this.send(node, node.getLeader(), message);
                    } else {
                        this.send(node, node.getNext(), message);
                    }
                }
                while(!node.getInbox().isEmpty()) {
                    AbstractMessage message = node.getInbox().remove();
                    node.handleMessage(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    public static Network getInstance() {
        if (instance == null)
            instance = new Network();
        return instance;
    }

}
