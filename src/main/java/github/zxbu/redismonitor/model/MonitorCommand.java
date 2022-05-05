package github.zxbu.redismonitor.model;


import java.util.Date;

public class MonitorCommand {

    private String id;

    private Long microsecond;

    private Date time;

    private Integer database;

    private String server;

    private String client;

    private String action;

    private String key;

    private String prefix;

    private String args;

    private Long argsLength;

    private String command;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getServer() {
        return server;
    }

    public Long getArgsLength() {
        return argsLength;
    }

    public void setArgsLength(Long argsLength) {
        this.argsLength = argsLength;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public Long getMicrosecond() {
        return microsecond;
    }

    public void setMicrosecond(Long microsecond) {
        this.microsecond = microsecond;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public Integer getDatabase() {
        return database;
    }

    public void setDatabase(Integer database) {
        this.database = database;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getArgs() {
        return args;
    }

    public void setArgs(String args) {
        this.args = args;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
