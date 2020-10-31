package cz.cvut.dsv.tomenyev.message;


import cz.cvut.dsv.tomenyev.network.Node;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MessageHandler implements Runnable {

    private final AbstractMessage message;

    private final Node node;

    private final Runnable onFinished;

    @Override
    public void run() {
        message.handleMessage(node);
        if(onFinished != null)
            onFinished.run();
    }
}
