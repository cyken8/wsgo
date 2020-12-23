package com.gnepux.wsgo.dispatch.disruptor;
import com.gnepux.wsgo.dispatch.disruptor.data.PCData;
import com.gnepux.wsgo.dispatch.message.Message;
import com.lmax.disruptor.EventFactory;

/**
 * @Author: feiweiwei
 * @Description: 待处理类工厂
 * @Created Date: 18:55 17/9/10.
 * @Modify by:
 */
public class PCDataFactory<E extends Message> implements EventFactory<PCData<E>> {

	@Override
	public PCData<E> newInstance() {
		return new PCData<E>();
	}

	
 
}