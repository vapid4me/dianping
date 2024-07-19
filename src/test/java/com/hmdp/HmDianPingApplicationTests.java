package com.hmdp;

import com.hmdp.service.impl.ShopServiceImpl;
import com.hmdp.utils.RedisIdWorker;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
class HmDianPingApplicationTests {
    @Resource
    private ShopServiceImpl shopService;

    @Resource
    private RedisIdWorker redisIdWorker;

    private ExecutorService executorService = Executors.newFixedThreadPool(500);

    /**
     * 测试 nextID 函数在高并发场景下的性能
     * @throws InterruptedException
     */
    @Test
    void testIdWorker() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(300);
        // 定义线程任务
        Runnable task = () -> {
            for (int i = 0; i < 100; i++) {
                long id = redisIdWorker.nextId("Order");
                System.out.println("id:" + id);
            }
            latch.countDown();
        };
        long begin = System.currentTimeMillis();
        // 将300个线程任务提交
        for (int i = 0 ; i < 300; i++) {
            executorService.submit(task);
        }
        latch.await();
        long end = System.currentTimeMillis();
        System.out.println("time = " + (end - begin));
    }


    @Test
    void testShopService() {
        try {
            shopService.saveShop2Redis(1L, 10L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 测试消息发布
     */
    @Test
    void testMQService() {
        // 队列名称
        String queueName = "simple.queue";
        // 消息
        String message = "hello, spring amqp!";
        // 发送消息
        rabbitTemplate.convertAndSend(queueName, message);
    }
}
