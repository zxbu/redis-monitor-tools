package github.zxbu.redismonitor.service;

import github.zxbu.redismonitor.config.RedisConfigProperties;
import github.zxbu.redismonitor.dao.MonitorCommandRepository;
import github.zxbu.redismonitor.model.MonitorCommand;
import github.zxbu.redismonitor.util.PercentUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class QueryService {
    @Autowired
    private MonitorCommandRepository monitorCommandRepository;
    @Autowired
    private RedisConfigProperties redisConfigProperties;

    public void query(Writer writer) {
        Iterable<MonitorCommand> monitorCommands = monitorCommandRepository.findAll();
        List<MonitorCommand> monitorCommandList = StreamSupport.stream(monitorCommands.spliterator(), false).collect(Collectors.toList());

        total(writer, monitorCommandList);
        keys(writer, monitorCommandList);
        actions(writer, monitorCommandList);
        prefixes(writer, monitorCommandList);
        try {
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void keys(Writer writer, List<MonitorCommand> monitorCommandList) {
        write(writer, "----- Top keys -----");
        group(writer, monitorCommandList, MonitorCommand::getKey);
    }

    private void actions(Writer writer, List<MonitorCommand> monitorCommandList) {
        write(writer, "----- Top actions -----");
        group(writer, monitorCommandList, MonitorCommand::getAction);
    }

    private void prefixes(Writer writer, List<MonitorCommand> monitorCommandList) {
        write(writer, "----- Top prefixes -----");
        group(writer, monitorCommandList, MonitorCommand::getPrefix);
    }

    private void group(Writer writer, List<MonitorCommand> monitorCommandList, Function<MonitorCommand, String> classifier) {
        List<Map.Entry<String, Long>> entryList = monitorCommandList.stream()
                .filter(o -> classifier.apply(o) != null)
                .collect(Collectors.groupingBy(classifier, Collectors.counting()))
                .entrySet().stream().sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue())).limit(redisConfigProperties.getTop()).collect(Collectors.toList());

        for (Map.Entry<String, Long> entry : entryList) {
            write(writer, "'" + entry.getKey() + "' : " + entry.getValue() + " ( " + PercentUtil.format(entry.getValue() * 1.0, monitorCommandList.size()) + " )") ;
        }
        write(writer, "\n");
    }

    private void total(Writer writer, List<MonitorCommand> monitorCommandList) {
        write(writer, "----- Count -----");
        write(writer, "total : " + monitorCommandList.size());
        group(writer, monitorCommandList, MonitorCommand::getServer);
    }

    private void write(Writer writer, String content)  {
        try {
            writer.write(content + "\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
