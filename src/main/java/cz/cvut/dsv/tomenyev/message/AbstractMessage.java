package cz.cvut.dsv.tomenyev.message;

import cz.cvut.dsv.tomenyev.network.Address;
import cz.cvut.dsv.tomenyev.network.Node;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.sql.Time;

@RequiredArgsConstructor
@Getter
public abstract class AbstractMessage implements Serializable {

    private final Address origin;

    private final Address destination;

    public abstract void handleMessage(Node node);

}
