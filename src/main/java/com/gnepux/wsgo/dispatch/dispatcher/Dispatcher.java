package com.gnepux.wsgo.dispatch.dispatcher;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import com.gnepux.wsgo.dispatch.disruptor.PCDataFactory;
import com.gnepux.wsgo.dispatch.disruptor.consumer.DisruptorConsumer;
import com.gnepux.wsgo.dispatch.disruptor.data.PCData;
import com.gnepux.wsgo.dispatch.disruptor.producer.DisruptorProducer;
import com.gnepux.wsgo.dispatch.disruptor.producer.IProducer;
import com.gnepux.wsgo.dispatch.message.Message;
import com.gnepux.wsgo.dispatch.queue.MessageQueue;
import com.gnepux.wsgo.dispatch.resolver.Resolver;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;


/**
 * @ClassName: Dispatcher 
 * @Description: 消息处理分配
 * @author Chen Yongken
 * @date 2020-12-23 05:27:06 
 * @param <E> 
 */
public class Dispatcher<E extends Message> implements IProducer<E> {
    /**
     * Disruptor核心
     */
    private RingBuffer<PCData<E>> ringBuffer;
    /**
     * 生产者
     */
    private DisruptorProducer<E> producerProxy;
    /**
     * Disruptor
     */
    private Disruptor<PCData<E>> dataDisruptor;

    /**
     * @Description: Start producer and consumer
     * @param type
     * @param resolver
     * @date 2020-12-23 05:15:48
     */
    public void loop(String type, Resolver<E> resolver) {
        //创建线程池
        //ExecutorService service = Executors.newCachedThreadPool();
        //创建数据工厂
        PCDataFactory<E> pcDataFactory = new PCDataFactory<E>();
        //设置缓冲区大小，必须为2的指数，否则会有异常
        int buffersize = 1024*1024;
        BasicThreadFactory build = new BasicThreadFactory.Builder().namingPattern("disruptor-"+type+"-tfactory-%d").build();
        //Disruptor<PCData<E>> dataDisruptor = new Disruptor<PCData<E>>(pcDataFactory, buffersize,build);
        dataDisruptor = new Disruptor<PCData<E>>(pcDataFactory, buffersize, build);
        //创建消费者线程
        dataDisruptor.handleEventsWithWorkerPool(new DisruptorConsumer<E>(resolver));
        //启动
        dataDisruptor.start();
        //获取其队列
        ringBuffer = dataDisruptor.getRingBuffer();
        MessageQueue<E> queue = new MessageQueue<>();
        producerProxy = new DisruptorProducer<>("wsgo-" + type + "-producer", queue,ringBuffer);
        producerProxy.start();
        setProxy(producerProxy);
    }
    
    private void setProxy(DisruptorProducer<E> producer) {
        this.producerProxy = producer;
    }
 
    /**
     * Stop the dispatcher
     */
    public void stop() {
    	producerProxy.shutdown();
    	dataDisruptor.shutdown();
    }

    
    @Override
    public void sendMessage(E e) {
    	producerProxy.sendMessage(e);
    }

    @Override
    public void sendMessageDelay(E e, long delay) {
    	producerProxy.sendMessageDelay(e,delay);
    }
}
