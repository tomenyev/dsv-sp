package cz.cvut.dsv.tomenyev.message;

import cz.cvut.dsv.tomenyev.network.Address;
import cz.cvut.dsv.tomenyev.network.Node;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

@RequiredArgsConstructor
@Getter
public abstract class AbstractMessage implements Serializable {

    private final Address origin;

    private final Address destination;

    private Integer i;

    public abstract void handleMessage(Node node) throws RemoteException, NotBoundException, UnknownHostException;

    public AbstractMessage setI(Integer i) {
        this.i = i;
        return this;
    }
}
