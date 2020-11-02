package cz.cvut.dsv.tomenyev.message;

import cz.cvut.dsv.tomenyev.network.Node;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor
public class MessageHandler implements Runnable {

    private final AbstractMessage message;

    private final Node node;

    private final Runnable finished;

    @SneakyThrows
    @Override
    public void run() {
        message.handleMessage(node);
        if(finished != null)
            finished.run();
    }

}
