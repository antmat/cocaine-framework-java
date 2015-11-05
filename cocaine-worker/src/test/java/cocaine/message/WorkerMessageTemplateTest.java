package cocaine.message;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.msgpack.MessagePack;

/**
 * @author Anton Bobukh <abobukh@yandex-team.ru>
 */
public class WorkerMessageTemplateTest {

    private MessagePack pack;

    @Before
    public void setUp() {
        this.pack = new MessagePack();
        this.pack.register(UUID.class, UUIDTemplate.getInstance());
    }

    @Test
    public void writeHandshakeMessage() throws IOException {
        UUID uuid = UUID.randomUUID();
        WorkerMessage msg = Messages.handshake(uuid);
        byte[] bytes = pack.write(Arrays.asList(1, 0, Arrays.asList(uuid)));

        byte[] result = pack.write(msg, WorkerMessageTemplate.getInstance());

        Assert.assertArrayEquals(bytes, result);
    }

    @Test
    public void writeHeartbeatMessage() throws IOException {
        WorkerMessage msg = Messages.heartbeat();
        byte[] bytes = pack.write(Arrays.asList(1, 0, Arrays.asList()));

        byte[] result = pack.write(msg, WorkerMessageTemplate.getInstance());

        Assert.assertArrayEquals(bytes, result);
    }

    @Test
    public void writeTerminateMessage() throws IOException {
        String message = "PANIC!";
        WorkerMessage msg = Messages.terminate(TerminateMessage.Reason.NORMAL, message);
        byte[] bytes = pack.write(Arrays.asList(1, 1, Arrays.asList(1, message)));

        byte[] result = pack.write(msg, WorkerMessageTemplate.getInstance());

        Assert.assertArrayEquals(bytes, result);
    }

    @Test
    public void writeInvokeMessage() throws IOException {
        long session = 1L;
        String method = "invoke";
        WorkerMessage msg = Messages.invoke(session, method);
        byte[] bytes = pack.write(Arrays.asList(session, 0, Arrays.asList(method)));

        byte[] result = pack.write(msg, WorkerMessageTemplate.getInstance());

        Assert.assertArrayEquals(bytes, result);
    }

    @Test
    public void writeWriteMessage() throws IOException {
        long session = 1L;
        byte[] data = new byte[] { 1, 2, 3, 4, 5 };
        WorkerMessage msg = Messages.write(session, data);
        byte[] bytes = pack.write(Arrays.asList(session, 0, Collections.singletonList(data)));

        byte[] result = pack.write(msg, WorkerMessageTemplate.getInstance());

        Assert.assertArrayEquals(bytes, result);
    }

    @Test
    public void writeErrorMessage() throws IOException {
        long session = 1L;
        int code = -200;
        String message = "Failed!";
        WorkerMessage msg = Messages.error(session, code, message);
        byte[] bytes = pack.write(Arrays.asList(session, 1, Arrays.asList(code, message)));

        byte[] result = pack.write(msg, WorkerMessageTemplate.getInstance());

        Assert.assertArrayEquals(bytes, result);
    }

    @Test
    public void writeCloseMessage() throws IOException {
        long session = 1L;
        WorkerMessage msg = Messages.close(session);
        byte[] bytes = pack.write(Arrays.asList(session, 2, Arrays.asList()));

        byte[] result = pack.write(msg, WorkerMessageTemplate.getInstance());

        Assert.assertArrayEquals(bytes, result);
    }

}