package cz.cvut.dsv.tomenyev.network;

import cz.cvut.dsv.tomenyev.message.AbstractMessage;
import cz.cvut.dsv.tomenyev.message.Join;
import cz.cvut.dsv.tomenyev.message.MessageHandler;
import cz.cvut.dsv.tomenyev.utils.Log;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@SuppressWarnings("UnusedReturnValue")
@Getter
@Setter
@ToString(of = {"address", "next", "leader", "ok", "fixing", "income", "outcome"})
public class Node extends UnicastRemoteObject implements AbstractNode {

    private final Address address;

    private Address next;

    private Address leader;

    private boolean ok = false;

    private boolean fixing = false;

    private List<String> messages = new ArrayList<>();

    private Queue<Join> income = new LinkedList<>();

    private Queue<Join> outcome = new LinkedList<>();

    public Node(Address address) throws RemoteException {
        this.address = address;
    }

    public Node initNetwork() throws RemoteException, NotBoundException {
        Network.getInstance().init(this);
        return this;
    }

    public Node joinNetwork(Address remote) throws RemoteException, NotBoundException {
        Network.getInstance().join(this, remote);
        return this;
    }

    public Node initElection() throws RemoteException, NotBoundException {
        Network.getInstance().election(this);
        return this;
    }

    public Node sendMessage(String message) {
        Network.getInstance().send(this, message);
        return this;
    }

    public void quitNetwork() throws RemoteException, NotBoundException {
        Network.getInstance().quit(this);
    }

    public void forceQuitNetwork() {
        this.setOk(false);
    }

    @Override
    public void handleMessage(AbstractMessage message) throws RemoteException {
        MessageHandler runnable = new MessageHandler(message, this, null);
        Thread thread = new Thread(runnable);
        thread.start();
    }

}
