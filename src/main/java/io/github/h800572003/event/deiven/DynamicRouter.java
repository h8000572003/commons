package io.github.h800572003.event.deiven;

import com.google.common.collect.Maps;
import io.github.h800572003.exception.ApBusinessException;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class DynamicRouter<T extends IMessage> implements IDynamicRouter<IMessage> {

    @SuppressWarnings("rawtypes")
    private final Map<String, IChannel> map = Maps.newConcurrentMap();

    private  IChannel defaultChannel = new UnregisterChannel();


    public void setDefaultChannel(IChannel defaultChannel) {
        this.defaultChannel = defaultChannel;
    }

    @Override
    public void registerChannelByName(String message, IChannel<IMessage> channel) {
        if (StringUtils.isBlank(message)) {
            throw new ApBusinessException("message is null");
        }
        map.put(message, channel);
    }

    static class UnregisterChannel implements IChannel {

        @Override
        public void dispatch(IMessage message) {
            throw new ApBusinessException("Unregister Channel channel:{0}", message.getTypeKey());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IMessage> void dispatch(T message) {
        String typeKey = message.getTypeKey();
        IChannel orDefault = this.map.getOrDefault(typeKey, defaultChannel);
        orDefault.dispatch(message);


    }

}
