package com.springmq.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.CountDownLatch;

@Service
public class InitService {
    private static  final Logger log = LoggerFactory.getLogger(InitService.class);
    public static final int ThreadNum = 10;
    private static int mobile = 0;
    @Autowired
    private ConcurrencyService concurrencyService;

    //@PostConstruct
    public void generateMultiThread(){
        log.info("开始初始化线程数--->");

        try{
            //计数器
            CountDownLatch countDownLatch = new CountDownLatch(1);
            for(int i =0; i<ThreadNum; i++){
                new Thread(new RunThread(countDownLatch)).start();
            }
            //TODO:启动多个线程
            countDownLatch.countDown();
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    private class RunThread implements Runnable{

        private final CountDownLatch startLatch;

        private RunThread(CountDownLatch startLatch) {
            this.startLatch = startLatch;
        }

        @Override
        public void run() {
            try{
                //TODO:线程等待
                startLatch.await();
                mobile +=1;
                concurrencyService.manageRobbing(String.valueOf(mobile));
                log.info("当前手机号:"+mobile);
            }catch (Exception e){

            }
        }
    }

}
