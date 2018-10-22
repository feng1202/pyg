import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class MyRunnable2 implements Runnable {


    Jedis jedis = new Jedis("127.0.0.1", 6379);

    public MyRunnable2() {
    }

    @Override
    public void run() {
        try {
            String goodsNum = jedis.rpop("goodsNum");
            Thread.sleep(1000);
            if(goodsNum != null) {
                System.out.println(System.currentTimeMillis() + "==="  + "抢到了" + goodsNum );
            }else {
                System.out.println(System.currentTimeMillis() + "==="  + "没抢到");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }

    }

}