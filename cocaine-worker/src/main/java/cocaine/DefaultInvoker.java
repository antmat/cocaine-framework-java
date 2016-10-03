package cocaine;

import cocaine.request.RequestIdStack;
import rx.Observable;
import rx.Observer;

import java.util.List;
import java.util.Map;

/**
 * @author akirakozov
 */
public class DefaultInvoker implements Invoker {
    private final EventHandlersProvider provider;

    public DefaultInvoker(EventHandlersProvider provider) {
        this.provider = provider;
    }

    @Override
    public void invoke(String event, List<List<Object>> headers, Observable<byte[]> request, Observer<byte[]> response) throws Exception {
        EventHandler handler = provider.getHandler(event);
        if (handler != null) {
            try {
                headers.forEach(RequestIdStack::pushReplaceId);
                handler.handle(request, response);
            } finally {
                RequestIdStack.pop();
            }
        } else {
            throw new UnknownClientMethodException(event);
        }
    }

    public static DefaultInvoker createFromHandlers(Map<String, EventHandler> handlers) {
        return new DefaultInvoker(new DefaultEventHandlersProvider(handlers));
    }
}
