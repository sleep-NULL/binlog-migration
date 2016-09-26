# binlog-migration
Mysql binlog migration tool

# Reference resources
- **[python-mysql-replication](https://github.com/noplay/python-mysql-replication)**
- **[mysql-binlog-connector-java](https://github.com/shyiko/mysql-binlog-connector-java)**
- **[open-replicator](https://github.com/whitesock/open-replicator)**

# Mysql Server config
```
server_id=1
log_bin=mysql-bin
binlog-format=ROW
character_set_server=utf8
```

# Example
```java
BinlogClient binlogClient = new BinlogClient("localhost", 3306, "canal", "canal", 2, "mysql-bin.000001", 4);
binlogClient.setListener(new EventListener() {
	@Override
	public void onEentry(Entry entry) {
		System.out.println(entry);
	}
});
binlogClient.start();
```