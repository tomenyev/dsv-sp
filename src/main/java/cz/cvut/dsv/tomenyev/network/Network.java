package cz.cvut.dsv.tomenyev.network;

import cz.cvut.dsv.tomenyev.message.*;
import cz.cvut.dsv.tomenyev.utils.Constant;
import cz.cvut.dsv.tomenyev.utils.Log;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Network {

    /**
     * SINGLETON
     */
    private static Network instance;

    /**
     * Initializing the new network
     * @param node node that is initialized the network
     * @throws Exception
     */
    public void init(Node node) throws Exception {
        try {
            LocateRegistry
                    .createRegistry(node.getAddress().getPort())
                    .rebind(Constant.NAME, node);
            node.setOk(true);
        } catch (Exception e) {
            node.setOk(false);
            throw new Exception();
        }
    }

    /**
     * Providing leader election in the network
     * @param node node that initialized the election
     * @return itself
     * @throws Exception
     */
    public Network election(Node node) throws Exception {
        if (node.getNext() == null) {
            Log.getInstance().print(Log.To.BOTH, "Node "+node.getAddress()+" is single");
            node.setLeader(node.getAddress());
        } else {
            Election election = new Election(node.getAddress(), node.getNext(), node.getAddress());
            this.send(node, node.getNext(), election);
        }
        return this;
    }

    /**
     * Append new node to the existing network
     * @param node new node
     * @param remote existing network
     * @throws Exception
     */
    public void join(Node node, Address remote) throws Exception {
        Join join = new Join(node.getAddress(), remote, node.getAddress());
        this.send(node, remote, join);
    }

    /**
     * Providing safety quit
     * @param node node that quit the network
     * @return itself
     * @throws Exception
     */
    public Network quit(Node node) throws Exception {
        if(Objects.isNull(node.getNext())) {
            Log.getInstance().print(Log.To.BOTH, "Node "+node.getAddress()+" is single");
            node.clear();
            return this;
        }
        Quit quit = new Quit(
                node.getAddress(),
                node.getAddress().equals(node.getLeader()) ? node.getNext() : node.getLeader(),
                node.getNext(),
                node.getPrev(),
                node.getAddress().equals(node.getLeader()) ? node.getNext() : node.getLeader()
        );
        this.send(node, node.getAddress().equals(node.getLeader()) ? node.getNext() : node.getLeader(), quit);
        node.clear();
        return this;
    }

    /**
     * Send message from origin to the destination
     * @param origin
     * @param destination
     * @param message
     * @throws Exception
     */
    public void send(Node origin, Address destination, AbstractMessage message) throws Exception {
        Log.getInstance().print(Log.To.BOTH, origin.getAddress() + " has SENT the message: \n\t "+message);
        try {
            Registry registry = LocateRegistry.getRegistry(destination.getIp(), destination.getPort());
            AbstractNode node = (AbstractNode) registry.lookup(Constant.NAME);
            node.handleMessage(message);
        } catch (Exception e) {
            Log.getInstance().print(Log.To.BOTH, origin.getAddress() + " has FAILED to SEND the message: \n\t "+message);
            origin.addDraft(message);
            if(Constant.AUTOPILOT)
                origin.fixNetwork(destination);
            throw new Exception();
        }
    }

    /**
     * Broadcast text message to the network
     * @param node
     * @param message
     * @throws Exception
     */
    public void send(Node node, String message) throws Exception {
        if(Objects.nonNull(node.getNext())) {
            Message m = new Message(node.getAddress(), node.getLeader(), message);
            this.send(node, node.getLeader(), m);
        } else {
            Log.getInstance().print(Log.To.BOTH, "Node " + node.getAddress() + " is single");
        }
    }

    /**
     * fix broken network
     * @param node node that init fixing
     * @param quit node that forcibly left the network
     * @throws Exception
     */
    public void fix(Node node, Address quit) throws Exception {
        if(quit.equals(node.getPrev()) && quit.equals(node.getNext()) || Objects.isNull(node.getNext()) || Objects.isNull(node.getPrev())) {
            Log.getInstance().print(Log.To.BOTH, "Node " + node.getAddress() + " is single");
            node.clear();
            return;
        }
        node.setFixing(true);
        try {
            Address sendTo = node.getPrev().equals(quit) ? node.getNext() : node.getPrev();
            Fix fix = new Fix(node.getAddress(), sendTo, quit, node.getAddress(), node.getPrev().equals(quit));
            node.setLeader(node.getAddress());
            this.send(node, sendTo, fix);
        } catch (Exception e) {
            node.clear().setFixing(false);
            throw new Exception();
        }
    }

    /**
     * Handle income/outcome messages that were accumulating while fixing.
     * @param node
     */
    public void handleFails(Node node) {
        boolean quit = false;
        Log.getInstance().print(Log.To.BOTH, "Node " + node.getAddress() + " is TRYING to HANDLE DRAFTS and INBOX");
        node.setFixing(false);
        if(Objects.nonNull(node.getNext())) {
            try {
                while (!node.getDrafts().isEmpty()) {
                    AbstractMessage message = node.getDrafts().remove();
                    if (message instanceof Message) {
                        Message m = (Message) message;
                        m.setNewDestination(node.getLeader());
                        this.send(node, node.getLeader(), m);
                    } else if (message instanceof Quit) {
                        quit = true;
                        Quit q = (Quit) message;
                        if (!(q.getDestination().equals(node.getLeader()) || q.getDestination().equals(node.getNext()))) {
                            q.setNewDestination(node.getAddress().equals(node.getLeader()) ? node.getNext() : node.getLeader());
                            q.setNext(node.getNext());
                            q.setPrev(node.getPrev());
                        }
                        this.send(node, q.getNewDestination(), q);
                    } else if (message instanceof Join) {
                        this.send(node, node.getAddress(), message);
                    } else {
                        this.send(node, node.getNext(), message);
                    }
                }
                while (!node.getDrafts().isEmpty()) {
                    AbstractMessage message = node.getInbox().remove();
                    node.handleMessage(message);
                }
            } catch (Exception e) {
//                e.printStackTrace();
                  Log.getInstance().print(Log.To.BOTH, "Node " + node.getAddress() + " has FAILED to HANDLE DRAFTS and INBOX");
            }
        }
        if(quit) {
            node.clear();
            Log.getInstance().print(Log.To.CONSOLE, Constant.QUIT_MESSAGE);
        }
    }

    public static Network getInstance() {
        if (instance == null)
            instance = new Network();
        return instance;
    }

}
