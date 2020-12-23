package com.gnepux.wsgo.dispatch.disruptor.producer;

import com.gnepux.wsgo.dispatch.disruptor.data.PCData;
import com.gnepux.wsgo.dispatch.message.Message;
import com.gnepux.wsgo.dispatch.queue.MessageQueue;
import com.lmax.disruptor.RingBuffer;

/**
 * @ClassName: DisruptorProducer 
 * @Description: Disruptor框架生产者
 * @author Chen Yongken
 * @date 2020-12-23 05:22:07 
 * @param <E> 
 */
public class DisruptorProducer<E extends Message> extends Thread implements IProducer<E>  {
	
    private final RingBuffer<PCData<E>> dataRingBuffer;
    private volatile boolean isAlive;
    private MessageQueue<E> queue;
    
    public DisruptorProducer(String name, MessageQueue<E> queue,RingBuffer<PCData<E>> dataRingBuffer) {
        this.dataRingBuffer = dataRingBuffer;
        this.queue = queue;
        this.isAlive = true;
    }
    
    @Override
    public void run() {
        while (isAlive) {
        	E e = queue.poll();
        	product(e);
        }
    }
    
    /**
     * @Description: 关闭生产
     * @date 2020-12-23 03:48:26
     */
    public void shutdown() {
        isAlive = false;
        interrupt();
    }
    
    /**
     * @Description: 向RingBuffer中发布新数据
     * @param e	数据
     * @date 2020-12-23 03:46:21
     */
    public  void  product(E e) {
        //获取下一个位置
        long next = dataRingBuffer.next();
        try {
            //获取容器
            PCData<E> pcData = dataRingBuffer.get(next);
            //设置数据
            pcData.setData(e);
        } finally {
            //插入
            dataRingBuffer.publish(next);
        }
    }
    
    /**
     * @Description: 向消息队列中添加元素
     * @param e		数据
     * @date 2020-12-23 03:47:11
     */
    @Override
    public void sendMessage(E e) {
    	queue.offer(e);
    }    
    
    /**
     * @Description: 向消息队列中添加元素,延时时间到后可取
     * @param e		数据
     * @param delay	延时时间毫秒
     * @date 2020-12-23 03:47:28
     */
    @Override
    public void sendMessageDelay(E e, long delay) {
    	queue.offer(e, delay);
    }


}
