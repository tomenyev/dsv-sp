package cz.cvut.dsv.tomenyev.network;

import cz.cvut.dsv.tomenyev.message.*;
import cz.cvut.dsv.tomenyev.utils.Counter;
import cz.cvut.dsv.tomenyev.utils.Log;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

@SuppressWarnings("UnusedReturnValue")
@Getter
@Setter
@ToString(of = {"address", "next", "prev", "leader", "ok", "fixing", "messages", "inbox", "drafts"})
public class Node extends UnicastRemoteObject implements AbstractNode {

    private final Address address;

    private Address next;

    private Address prev;

    private Address leader;

    private boolean ok = false;

    private boolean fixing = false;

    private List<String> messages = new ArrayList<>();

    private Queue<AbstractMessage> inbox = new LinkedList<>();

    private Queue<AbstractMessage> drafts = new LinkedList<>();

    public Node(Address address) throws RemoteException {
        this.address = address;
    }

    public Node initNetwork() {
        Log.getInstance().print(Log.To.BOTH, "Initializing new network("+getAddress()+")");
        try {
            Network.getInstance().init(this);
        } catch (Exception e) {
           Log.getInstance().print(Log.To.BOTH, "Failed to initialize new network("+getAddress()+")");
           this.setOk(false);
//           e.printStackTrace();
        }
        return this;
    }

    public Node joinNetwork(Address remote) {
        Log.getInstance().print(Log.To.BOTH, "Trying to join existing network("+remote+")");
        try {
            Network.getInstance().join(this, remote);
        } catch (Exception e) {
            Log.getInstance().print(Log.To.BOTH, "Failed to join existing network("+remote+")");
//            e.printStackTrace();
        }
        return this;
    }

    public Node initElection() {
        Log.getInstance().print(Log.To.BOTH, "Initializing leader election("+getAddress()+").");
        try {
            Network.getInstance().election(this);
        } catch (Exception e) {
            Log.getInstance().print(Log.To.BOTH, "Failed to initialize leader election("+getAddress()+").");
//            e.printStackTrace();
        }
        return this;
    }

    public Node sendMessage(String message) {
        Log.getInstance().print(Log.To.BOTH, "Node "+getAddress()+" is trying to send message: "+message);
        try {
            Network.getInstance().send(this, message);
        } catch (Exception e) {
            Log.getInstance().print(Log.To.BOTH, "Node "+getAddress()+" has failed to send message: "+message);
//            e.printStackTrace();
        }
        return this;
    }

    public void quitNetwork() {
        Log.getInstance().print(Log.To.BOTH, "Node " + getAddress() + " is trying to safety quit network");
        try {
            Network.getInstance().quit(this);
        } catch (Exception e) {
            Log.getInstance().print(Log.To.BOTH, "Node " + getAddress() + " has failed to quit network");
//            e.printStackTrace();
        }
    }

    public void fixNetwork(Address quit) {
        Log.getInstance().print(Log.To.BOTH, "Node "+quit+" has left network.\nNode " + getAddress() + " is trying fix network");
        try {
            Network.getInstance().fix(this, quit);
        } catch (Exception e) {
            Log.getInstance().print(Log.To.BOTH, "Node " + getAddress() + " has failed to fix network("+quit+")");
//            e.printStackTrace();
        }
    }

    public void forceQuitNetwork() {
        Log.getInstance().print(Log.To.BOTH, "Node " + getAddress() + " has forcibly quit network");
        this.setOk(false);
    }

    @Override
    public void handleMessage(AbstractMessage message) throws RemoteException {
        Counter.setInstance(message.getI());
        Log.getInstance().print(Log.To.BOTH, Counter.getInstance().inc(), getAddress() + " has RECEIVED message: \n\t "+message);

        MessageHandler runnable;

        if(this.isFixing()) {
            if(message instanceof Fix) {
                runnable = new MessageHandler(message, this, () -> Network.getInstance().handleFails(this));
            } else {
                this.addInbox(message);
                Log.getInstance().print(Log.To.BOTH, "Node " + getAddress() + " is fixing and can't handle message: "+message);
                return;
            }
        } else {
            runnable = new MessageHandler(message, this, null);
        }
        Thread thread = new Thread(runnable);
        thread.start();
    }

    public void addDraft(AbstractMessage message) {
        if(Objects.nonNull(this.getNext()) && Objects.nonNull(this.getPrev()) && !this.getNext().equals(getPrev())) {
            this.drafts.add(message);
            Log.getInstance().print(Log.To.BOTH, "Node " + getAddress() + " has saved message to the drafts("+message+")");
        }
    }

    public void addInbox(AbstractMessage message) {
        Log.getInstance().print(Log.To.BOTH, "Node " + getAddress() + " has saved message to the inbox("+message+")");
        this.inbox.add(message);
    }

    public void addMessage(String str) {
        Log.getInstance().print(Log.To.FILE, "Node " + getAddress() + " has received new regular message("+str+")");
        this.messages.add(str);
    }

    public Node clear() {
        setNext(null);
        setPrev(null);
        setLeader(null);
        return this;
    }
}
