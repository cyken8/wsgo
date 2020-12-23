package com.gnepux.wsgo.dispatch.disruptor.consumer;

import com.gnepux.wsgo.dispatch.disruptor.data.PCData;
import com.gnepux.wsgo.dispatch.message.Message;
import com.gnepux.wsgo.dispatch.resolver.Resolver;
import com.lmax.disruptor.WorkHandler;

/**
 * @ClassName: DisruptorConsumer 
 * @Description: Disruptor框架消费者
 * @author Chen Yongken
 * @date 2020-12-23 05:21:27 
 * @param <E> 
 */
public class DisruptorConsumer<E extends Message>  implements WorkHandler<PCData<E>> {

    private Resolver<E> resolver;

    public DisruptorConsumer(Resolver<E> resolver) {
        this.resolver = resolver;
    }

	@Override
	public void onEvent(PCData<E> pcData) throws Exception {
		E e = pcData.getData();
		handleMessage(e);
	}

	/**
	 * @Description: 消息处理
	 * @param e
	 * @date 2020-12-23 05:10:31
	 */
    public void handleMessage(E e) {
        resolver.resolve(e);
    }


}
