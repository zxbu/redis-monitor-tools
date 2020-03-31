package github.zxbu.redismonitor.config;

import github.zxbu.redismonitor.service.MonitorCommandService;
import github.zxbu.redismonitor.service.QueryService;
import github.zxbu.redismonitor.util.PercentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisMonitor;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@EnableConfigurationProperties(RedisConfigProperties.class)
@Configuration
public class RedisMonitorAutoConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisMonitorAutoConfig.class);
    private static AtomicInteger counter = new AtomicInteger(0);

    @Autowired
    private RedisConfigProperties redisConfigProperties;

    @Autowired
    private MonitorCommandService monitorCommandService;

    @Autowired
    private QueryService queryService;

    @PostConstruct
    public void init() {
        List<String> servers = redisConfigProperties.getServers();
        servers.forEach(this::monitor);
        new Thread(this::input).start();
    }
    private void input() {
        String s;
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        try {
            s = br.readLine();
            while (true) {
                if (s.equals("monitor") || s.equals("m")) {
                    queryService.query(new BufferedWriter(new OutputStreamWriter(System.out)));
                }
                if (s.equals("exit")) {
                    queryService.query(new BufferedWriter(new OutputStreamWriter(System.out)));
                    System.exit(0);
                }
                s = br.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void monitor(String server) {
        String[] split = server.split(":");
        new Thread(() -> {
            Jedis jedis = new Jedis(split[0], Integer.parseInt(split[1]), 10000);
            LOGGER.info("{} start monitor...", server);
            jedis.monitor(new JedisMonitor() {
                @Override
                public void onCommand(String command) {
                    monitorCommandService.save(server, command);
                    counter.incrementAndGet();
                    if (redisConfigProperties.getNumbers() > 0 && counter.get() % (redisConfigProperties.getNumbers() / 10) == 0) {
                        LOGGER.info("now counter number is {} ({})", counter.get(), PercentUtil.format(counter.get(), redisConfigProperties.getNumbers()));
                        if ((counter.get() >= redisConfigProperties.getNumbers())) {
                            queryService.query(new BufferedWriter(new OutputStreamWriter(System.out)));
                            System.exit(0);
                        }
                    }
                }

            });
        }).start();
    }
}
