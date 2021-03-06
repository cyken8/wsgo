package com.gnepux.wsgo.dispatch.resolver;

import com.gnepux.wsgo.EventListener;
import com.gnepux.wsgo.dispatch.dispatcher.Dispatcher;
import com.gnepux.wsgo.dispatch.message.command.Command;
import com.gnepux.wsgo.dispatch.message.command.ReconnectCmd;
import com.gnepux.wsgo.dispatch.message.event.Event;
import com.gnepux.wsgo.dispatch.message.event.OnCloseEvent;
import com.gnepux.wsgo.dispatch.message.event.OnDisConnectEvent;
import com.gnepux.wsgo.dispatch.message.event.OnMessageEvent;
import com.gnepux.wsgo.dispatch.message.event.OnRetryEvent;
import com.gnepux.wsgo.dispatch.message.event.OnSendEvent;

/**
 * Resolver for event type message.
 *
 * @author gnepux
 */
public class EventResolver implements Resolver<Event> {

    private EventListener listener;

    private Dispatcher<Command> commandDispatcher;

    public EventResolver(EventListener listener, Dispatcher<Command> commandDispatcher) {
        this.listener = listener;
        this.commandDispatcher = commandDispatcher;
    }

    @Override
    public void resolve(Event event) {
        switch (event.getEvent()) {
            case Event.ON_CONNECT:
                listener.onConnect();
                break;
            case Event.ON_DISCONNECT:
                listener.onDisConnect(((OnDisConnectEvent) event).getThrowable());
                break;
            case Event.ON_CLOSE:
                OnCloseEvent onCloseEvent = (OnCloseEvent) event;
                listener.onClose(onCloseEvent.getCode(), onCloseEvent.getReason());
                break;
            case Event.ON_MESSAGE:
                listener.onMessage(((OnMessageEvent) event).getText());
                break;
            case Event.ON_RETRY:
                OnRetryEvent retryEvent = (OnRetryEvent) event;
                listener.onReconnect(retryEvent.getRetryCount(), retryEvent.getDelayMillSec());
                commandDispatcher.sendMessageDelay(new ReconnectCmd(retryEvent.getRetryCount()), retryEvent.getDelayMillSec());
                break;
            case Event.ON_SEND:
                OnSendEvent sendEvent = (OnSendEvent) event;
                listener.onSend(sendEvent.getText(), sendEvent.isSuccess());
                break;
            default:
                break;
        }
    }
}
