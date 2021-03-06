package cz.cvut.dsv.tomenyev.network;

import cz.cvut.dsv.tomenyev.message.AbstractMessage;
import cz.cvut.dsv.tomenyev.message.Fix;
import cz.cvut.dsv.tomenyev.message.Join;
import cz.cvut.dsv.tomenyev.message.MessageHandler;
import cz.cvut.dsv.tomenyev.utils.Constant;
import cz.cvut.dsv.tomenyev.utils.Log;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

@Getter
@Setter
@ToString(of = {"address", "next", "prev", "leader", "ok", "fixing", "messages", "inbox", "drafts"})
/**
 * Network node representation.
 */
public class Node extends UnicastRemoteObject implements AbstractNode {

    /**
     * node unique id.
     */
    private final Address address;

    /**
     * next node in the ring network
     */
    private Address next;

    /**
     * previous node in the ring network
     */
    private Address prev;

    /**
     * leader of the network
     */
    private Address leader;

    private boolean ok = false;

    private boolean fixing = false;

    private boolean voted = false;

    /**
     * text messages archive
     */
    private List<String> messages = new ArrayList<>();

    /**
     * inbox for saving income messages while fixing.
     */
    private Queue<AbstractMessage> inbox = new LinkedList<>();

    /**
     * drafts for saving outcome messages while fixing.
     */
    private Queue<AbstractMessage> drafts = new LinkedList<>();

    public Node(Address address) throws RemoteException {
        this.address = address;
    }

    /**
     * Initializing new network
     * @return current node instance
     */
    public Node initNetwork() {
        boolean ok;
        Log.getInstance().print(Log.To.BOTH, "Node " +getAddress() +" is TRYING to INITIALIZE the network");
        try {
            Network.getInstance().init(this);
            ok = true;
        } catch (Exception e) {
            ok = false;
            Log.getInstance().print(Log.To.BOTH, "Node " +getAddress() +" has FAILED to INITIALIZE the network");
            this.setOk(false);
//           e.printStackTrace();
        }
        if (ok)
            Log.getInstance().print(Log.To.BOTH, "Node " +getAddress() +" has INITIALIZED the network");
        return this;
    }

    /**
     * Joining existing network
     * @param remote address of existing node.
     */
    public void joinNetwork(Address remote) {
        boolean ok;
        Log.getInstance().print(Log.To.BOTH, "Node " + getAddress()+" is TRYING to JOIN the network("+remote+")");
        try {
            Network.getInstance().join(this, remote);
            ok = true;
        } catch (Exception e) {
            ok = false;
            Log.getInstance().print(Log.To.BOTH, "Node " + getAddress()+" has FAILED to JOIN the network("+remote+")");
//            e.printStackTrace();
        }
        if (ok)
            Log.getInstance().print(Log.To.BOTH, "Node " + getAddress()+" has JOINED the network("+remote+")");
    }

    /**
     * Initializing leader election in the network ring
     */
    public void initElection() {
        Log.getInstance().print(Log.To.BOTH, "Node " +getAddress() +" is TRYING to INITIALIZE LEADER ELECTION");
        try {
            Network.getInstance().election(this);
        } catch (Exception e) {
            Log.getInstance().print(Log.To.BOTH, "Node " + getAddress() + " has FAILED to INITIALIZE LEADER ELECTION");
//            e.printStackTrace();
        }
    }

    /**
     * Broadcasting text message to the network.
     * @param message text message
     */
    public void sendMessage(String message) {
        Log.getInstance().print(Log.To.BOTH, "Node "+getAddress()+" has SENT the TEXT message: " + message);
        try {
            Network.getInstance().send(this, message);
        } catch (Exception e) {
            Log.getInstance().print(Log.To.BOTH, "Node "+getAddress()+" has FAILED to SEND the TEXT message: " + message);
//            e.printStackTrace();
        }
    }


    /**
     * Fix the network
     * @param quit address of quit node
     */
    public void fixNetwork(Address quit) {
        Log.getInstance().print(Log.To.BOTH, "Node " + getAddress() + " is TRYING to FIX the network");
        try {
            Network.getInstance().fix(this, quit);
        } catch (Exception e) {
            Log.getInstance().print(Log.To.BOTH, "Node " + getAddress() + " has FAILED to FIX the network");
//            e.printStackTrace();
        }
    }

    /**
     * Safety quit the network
     */
    public void quitNetwork() {
        boolean ok;
        Log.getInstance().print(Log.To.BOTH, "Node " + getAddress() + " is TRYING to SAFETY QUIT the network");
        try {
            ok = true;
            Network.getInstance().quit(this);
        } catch (Exception e) {
            ok = false;
            Log.getInstance().print(Log.To.BOTH, "Node " + getAddress() + " has FAILED to SAFETY QUIT the network");
//            e.printStackTrace();
        }
        if(ok) {
            Log.getInstance().print(Log.To.CONSOLE, "Node " + getAddress() + " has SAFETY QUIT the network");
            Log.getInstance().print(Log.To.CONSOLE, Constant.QUIT_MESSAGE);
        }
    }

    /**
     * Forcibly, unsafely quit the network.
     */
    public void forceQuitNetwork() {
        Log.getInstance().print(Log.To.BOTH, "Node " + getAddress() + " has FORCIBLY QUIT the network");
        setOk(false);
    }

    /**
     * handle income messages
     * @param message income message
     * @throws RemoteException
     */
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
