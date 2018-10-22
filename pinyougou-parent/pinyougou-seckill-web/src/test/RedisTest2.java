import redis.clients.jedis.Jedis;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: duanzhu
 * @Date: 2018/10/21 17:46
 * @Description:
 */
public class RedisTest2 {
    public static void main(String[] args) {
      /*  String str = "5A";
        Integer in = Integer.valueOf(str,16);
        System.out.println(in);
        */

        final Jedis jedis = new Jedis("127.0.0.1", 6379);
        jedis.lpush("goodsNum","aaaaaaaaa");
        jedis.lpush("goodsNum","bbbbbbbbb");

        jedis.lpush("goodsNum","ccccccccc");
        jedis.lpush("goodsNum","ddddddddd");
        jedis.lpush("goodsNum","eeeeeeeee");
        jedis.lpush("goodsNum","fffffffff");
        jedis.close();

        ExecutorService executor = Executors.newFixedThreadPool(20);
        for (int i = 0; i < 100; i++) {// 测试一万人同时访问
            executor.execute(new MyRunnable2());
        }
        executor.shutdown();

    }
}
