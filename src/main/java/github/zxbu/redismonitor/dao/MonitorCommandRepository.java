package github.zxbu.redismonitor.dao;


import github.zxbu.redismonitor.model.MonitorCommand;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class MonitorCommandRepository {
    private AtomicInteger idCounter = new AtomicInteger(1);
    private Map<String, MonitorCommand> repo = new ConcurrentHashMap<>();
    public void save(MonitorCommand monitorCommand) {
        monitorCommand.setId(String.valueOf(idCounter.getAndIncrement()));
        repo.put(monitorCommand.getId(), monitorCommand);
    }

    public Iterable<MonitorCommand> findAll() {
        return new ArrayList<>(repo.values());
    }
}
