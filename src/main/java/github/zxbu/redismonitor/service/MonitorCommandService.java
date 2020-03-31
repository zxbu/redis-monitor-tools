package github.zxbu.redismonitor.service;

import github.zxbu.redismonitor.dao.MonitorCommandRepository;
import github.zxbu.redismonitor.model.MonitorCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MonitorCommandService {
    @Autowired
    private MonitorCommandRepository monitorCommandRepository;

    private static Pattern commandPattern = Pattern.compile("^(?<timestamp>[\\d+.]+)\\s\\[(?<db>\\d+)\\s(?<client>\\S+)\\]\\s\"(?<action>\\w+)\"\\s\"(?<key>\\S+)\"(?<args>[\\s\\S]*)$");


    public void save(String server, String command) {
        MonitorCommand monitorCommand = new MonitorCommand();
        monitorCommand.setCommand(command);
        monitorCommand.setServer(server);
        parse(command, monitorCommand);
        monitorCommandRepository.save(monitorCommand);
    }

    private void parse(String command, MonitorCommand monitorCommand) {
        Matcher matcher = commandPattern.matcher(command);
        if (!matcher.find()){
            return;
        }

        String timestamp = matcher.group("timestamp");
        String db = matcher.group("db");
        String client = matcher.group("client");
        String action = matcher.group("action");
        String key = matcher.group("key");
        String args = matcher.group("args").trim();

        String[] split = timestamp.split("\\.");
        long microsecond = Long.parseLong(split[0]) * 1000 * 1000 + Long.parseLong(split[1]);
        monitorCommand.setMicrosecond(microsecond);
        monitorCommand.setTime(new Date(microsecond / 1000));
        monitorCommand.setDatabase(Integer.parseInt(db));
        monitorCommand.setClient(client);
        monitorCommand.setAction(action);
        monitorCommand.setKey(key);
        if (!StringUtils.isEmpty(key) && key.contains(":")) {
            monitorCommand.setPrefix(key.substring(0, key.lastIndexOf(":")));
        }
        monitorCommand.setArgs(args);
    }


}
