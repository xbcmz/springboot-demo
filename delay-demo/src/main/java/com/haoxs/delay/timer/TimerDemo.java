package com.haoxs.delay.timer;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @Description Timer
 * @Date 2021/9/18 11:29
 * @Created by Mr.Hao
 */
@Slf4j
public class TimerDemo {

    public static void main(String[] args) {
        Timer timer = new Timer();
        /**
         * scheduleAtFixedRate和schedule的区别：
         * scheduleAtFixedRate会尽量减少漏掉调度的情况，如果前一次执行时间过长，导致一个或几个任务漏掉了，
         * 那么会补回来，而schedule过去的不会补，直接加上间隔时间执行下一次任务。
         *
         * 同一个Timer下添加多个TimerTask，如果其中一个没有捕获抛出的异常，则全部任务都会终止运行。但是多个Timer是互不影响
         */
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                log.info("TimerTask run:{}", LocalDateTime.now());
            }
        }, 1000, 5000);
    }

}
