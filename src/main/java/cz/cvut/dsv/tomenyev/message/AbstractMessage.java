package cz.cvut.dsv.tomenyev.message;

import cz.cvut.dsv.tomenyev.network.Address;
import cz.cvut.dsv.tomenyev.network.Node;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
/**
 * Base message used for transfer information between network nodes.
 */
public abstract class AbstractMessage implements Serializable {

    /**
     * author address
     */
    private final Address origin;

    /**
     * first destination address
     */
    private final Address destination;

    /**
     * Base method, used for handle income messages.
     * @param node node that received the message.
     * @throws Exception
     */
    public abstract void handleMessage(Node node) throws Exception;

}
