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
import java.util.LongSummaryStatistics;
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
        bigKeys(writer, monitorCommandList);
        try {
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void keys(Writer writer, List<MonitorCommand> monitorCommandList) {
        write(writer, "----- Top keys -----");
        group(writer, monitorCommandList, new Function<MonitorCommand, String>() {
            @Override
            public String apply(MonitorCommand monitorCommand) {
                return monitorCommand.getAction() + " - " + monitorCommand.getKey();
            }
        });
    }

    private void actions(Writer writer, List<MonitorCommand> monitorCommandList) {
        write(writer, "----- Top actions -----");
        group(writer, monitorCommandList, MonitorCommand::getAction);
    }

    private void prefixes(Writer writer, List<MonitorCommand> monitorCommandList) {
        write(writer, "----- Top prefixes -----");
        group(writer, monitorCommandList, new Function<MonitorCommand, String>() {
            @Override
            public String apply(MonitorCommand monitorCommand) {
                return monitorCommand.getAction() + " - " + monitorCommand.getPrefix();
            }
        });
    }

    private void bigKeys(Writer writer, List<MonitorCommand> monitorCommandList) {
        write(writer, "----- Set bigKeys -----");

        monitorCommandList = monitorCommandList.stream()
            .sorted((o1, o2) -> o2.getArgsLength().compareTo(o1.getArgsLength()))
            .limit(redisConfigProperties.getTop())
            .collect(Collectors.toList());
        for (MonitorCommand monitorCommand : monitorCommandList) {
            write(writer, "'" + monitorCommand.getAction() + " - " + monitorCommand.getKey() + "' : " + monitorCommand.getArgsLength()) ;
        }

        write(writer, "\n");
    }

    private void group(Writer writer, List<MonitorCommand> monitorCommandList, Function<MonitorCommand, String> classifier) {
        Map<String, List<MonitorCommand>> monitorCommandMap = monitorCommandList.stream()
            .filter(o -> classifier.apply(o) != null)
            .collect(Collectors.groupingBy(classifier));
        List<Map.Entry<String, Long>> entryList = monitorCommandList.stream()
                .filter(o -> classifier.apply(o) != null)
                .collect(Collectors.groupingBy(classifier, Collectors.counting()))
                .entrySet().stream().sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue()))
            .limit(redisConfigProperties.getTop())
            .collect(Collectors.toList());

        for (Map.Entry<String, Long> entry : entryList) {
            String format = String.format("'%s' : %s (%s)", entry.getKey(), entry.getValue(), PercentUtil.format(entry.getValue() * 1.0, monitorCommandList.size()));
            List<MonitorCommand> monitorCommands = monitorCommandMap.get(entry.getKey());
            LongSummaryStatistics longSummaryStatistics = monitorCommands.stream().mapToLong(MonitorCommand::getArgsLength).summaryStatistics();
            if (longSummaryStatistics.getMax() > 0) {
                double average = longSummaryStatistics.getAverage();
                format = format + " " + average;
            }

            write(writer, format) ;
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
