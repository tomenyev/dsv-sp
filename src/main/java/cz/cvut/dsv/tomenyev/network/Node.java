package cz.cvut.dsv.tomenyev.network;

import cz.cvut.dsv.tomenyev.message.*;
import cz.cvut.dsv.tomenyev.utils.Constant;
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
        Log.getInstance().print(Log.To.BOTH, "Node " +getAddress() +" is TRYING to INITIALIZE network");
        try {
            Network.getInstance().init(this);
        } catch (Exception e) {
           Log.getInstance().print(Log.To.BOTH, "Node " +getAddress() +" has FAILED to INITIALIZE network");
           this.setOk(false);
//           e.printStackTrace();
        }
        return this;
    }

    public Node joinNetwork(Address remote) {
        Log.getInstance().print(Log.To.BOTH, "Node " + getAddress()+" is TRYING to JOIN the network("+remote+")");
        try {
            Network.getInstance().join(this, remote);
        } catch (Exception e) {
            Log.getInstance().print(Log.To.BOTH, "Node " + getAddress()+" has FAILED to JOIN the network("+remote+")");
//            e.printStackTrace();
        }
        return this;
    }

    public Node initElection() {
        Log.getInstance().print(Log.To.BOTH, "Node " +getAddress() +" is TRYING to INITIALIZE LEADER ELECTION");
        try {
            Network.getInstance().election(this);
        } catch (Exception e) {
            Log.getInstance().print(Log.To.BOTH, "Node " + getAddress() + " has FAILED to INITIALIZE LEADER ELECTION");
//            e.printStackTrace();
        }
        return this;
    }

    public Node sendMessage(String message) {
        Log.getInstance().print(Log.To.BOTH, "Node "+getAddress()+" has SENT the TEXT message: " + message);

        try {
            Network.getInstance().send(this, message);
        } catch (Exception e) {
            Log.getInstance().print(Log.To.BOTH, "Node "+getAddress()+" has FAILED to SEND the TEXT message: " + message);
//            e.printStackTrace();
        }
        return this;
    }

    public void quitNetwork() {
        boolean success;
        Log.getInstance().print(Log.To.BOTH, "Node " + getAddress() + " is TRYING to SAFETY QUIT the network");
        try {
            success = true;
            Network.getInstance().quit(this);
        } catch (Exception e) {
            success = false;
            Log.getInstance().print(Log.To.BOTH, "Node " + getAddress() + " has FAILED to SAFETY QUIT the network");
//            e.printStackTrace();
        }
        if(success)
            Log.getInstance().print(Log.To.CONSOLE, Constant.QUIT_MESSAGE);
    }

    public void fixNetwork(Address quit) {
        Log.getInstance().print(Log.To.BOTH, "Node " + getAddress() + " is TRYING to FIX the network");
        try {
            Network.getInstance().fix(this, quit);
        } catch (Exception e) {
            Log.getInstance().print(Log.To.BOTH, "Node " + getAddress() + " has FAILED to FIX the network");
//            e.printStackTrace();
        }
    }

    public void forceQuitNetwork() {
        Log.getInstance().print(Log.To.BOTH, "Node " + getAddress() + " has FORCIBLY QUIT the network");
        setOk(false);
    }

    @Override
    public void handleMessage(AbstractMessage message) throws RemoteException {
        Log.getInstance().print(Log.To.BOTH, "Node "+getAddress() + " has RECEIVED the message: \n\t "+message);

        MessageHandler runnable;

        if(this.isFixing()) {
            if(message instanceof Fix) {
                runnable = new MessageHandler(message, this, () -> Network.getInstance().handleFails(this));
            } else {
                this.addInbox(message);
                Log.getInstance().print(Log.To.BOTH, "Node " + getAddress() + " is FIXING and can't handle the message:\n\t"+message);
                return;
            }
        } else {
            runnable = new MessageHandler(message, this, null);
        }
        Thread thread = new Thread(runnable);
        thread.start();
    }

    public void addDraft(AbstractMessage message) {
        if(message instanceof Join)
            return;
        if(Objects.nonNull(this.getNext()) && Objects.nonNull(this.getPrev()) && !this.getNext().equals(getPrev())) {
            this.drafts.add(message);
            Log.getInstance().print(Log.To.BOTH, "Node " + getAddress() + " has SAVED message to the DRAFTS"+"\n\t"+message);
        }
    }

    public void addInbox(AbstractMessage message) {
        Log.getInstance().print(Log.To.BOTH, "Node " + getAddress() + " has SAVED message to the INBOX"+"\n\t"+message);
        this.inbox.add(message);
    }

    public void addMessage(String str) {
        Log.getInstance().print(Log.To.BOTH, getAddress() + " > message > " + str);
        this.messages.add(str);
    }

    public Node clear() {
        setNext(null);
        setPrev(null);
        setLeader(null);
        return this;
    }
}
