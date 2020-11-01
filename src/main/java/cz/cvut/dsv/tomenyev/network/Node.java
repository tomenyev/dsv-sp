package cz.cvut.dsv.tomenyev.network;

import cz.cvut.dsv.tomenyev.message.AbstractMessage;
import cz.cvut.dsv.tomenyev.message.Join;
import cz.cvut.dsv.tomenyev.message.MessageHandler;
import cz.cvut.dsv.tomenyev.utils.Log;
import lombok.*;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.Queue;

@Getter
@Setter
@ToString(of = {"address", "next", "leader", "ok", "fixing", "income", "outcome"})

public class Node extends UnicastRemoteObject implements AbstractNode {

    private final Address address;

    private final Log logger;

    private Address next;

    private Address leader;

    private boolean ok = false;

    private boolean fixing = false;

    private final Network network = Network.getInstance();

    private Queue<Join> income = new LinkedList<>();

    private Queue<Join> outcome = new LinkedList<>();

    public Node(Address address, Log logger) throws RemoteException {
        this.address = address;
        this.logger = logger;
    }

    public Node initNetwork() {
        this.setOk(network.init(this));
        return this;
    }

    public Node joinNetwork(Address remote) throws RemoteException, NotBoundException {
        this.setOk(network.join(this, remote));
        return this;
    }

    public Node initElection() throws RemoteException, NotBoundException {
        this.setOk(network.election(this));
        return this;
    }

    public Node sendMessage(String message) {
        System.out.println(message);
        this.setOk(network.send(this, message));
        return this;
    }

    public void quitNetwork() {
        this.setOk(network.quit(this));
    }

    public void forceQuitNetwork() {
        this.setOk(false);
    }

    public void printLog() {
        //TODO
    }


    @Override
    public void handleMessage(AbstractMessage message) throws RemoteException {
        MessageHandler runnable = new MessageHandler(message, this, null);
        Thread thread = new Thread(runnable);
        thread.start();
    }

}
