package github.zxbu.redismonitor.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "redis.command")
public class RedisConfigProperties {
    private List<String> servers;

    private Integer numbers;

    private Integer top;

    public List<String> getServers() {
        return servers;
    }

    public void setServers(List<String> servers) {
        this.servers = servers;
    }

    public Integer getNumbers() {
        return numbers;
    }

    public Integer getTop() {
        return top;
    }

    public void setTop(Integer top) {
        this.top = top;
    }

    public void setNumbers(Integer numbers) {
        this.numbers = numbers;
    }
}
