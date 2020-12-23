package com.gnepux.wsgo.dispatch.disruptor.data;

import com.gnepux.wsgo.dispatch.message.Message;

/** 
 * @ClassName: PCData 
 * @Description: Disruptor框架生产者消费者模式要消费的数据
 * @author Chen Yongken
 * @date 2020-12-22 07:26:58  
 */
public class PCData<E extends Message> {
	
    private E data;

    public E getData() {
        return data;
    }

    public void setData(E data) {
        this.data = data;
    }
}
