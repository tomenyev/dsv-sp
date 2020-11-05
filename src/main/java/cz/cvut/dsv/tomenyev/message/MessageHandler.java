package cz.cvut.dsv.tomenyev.message;

import cz.cvut.dsv.tomenyev.network.Node;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MessageHandler implements Runnable {

    private final AbstractMessage message;

    private final Node node;

    private final Runnable finished;

    @Override
    public void run() {
        try {
            message.handleMessage(node);
        } catch (Exception ignored) {}
        if(finished != null)
            finished.run();
    }

}
