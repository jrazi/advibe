CREATE KEYSPACE IF NOT EXISTS advibe_keyspace
WITH REPLICATION = { 'class': 'SimpleStrategy', 'replication_factor': 1 };
