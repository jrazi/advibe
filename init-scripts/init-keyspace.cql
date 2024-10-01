CREATE KEYSPACE IF NOT EXISTS advibe_keyspace
            WITH REPLICATION = { 'class': 'SimpleStrategy', 'replication_factor': 1 };

USE advibe_keyspace;

CREATE TABLE IF NOT EXISTS ad_event
(
    requestid      text PRIMARY KEY,
    adid           text,
    adtitle        text,
    advertisercost double,
    appid          text,
    apptitle       text,
    impressiontime bigint,
    clicktime      bigint
) WITH bloom_filter_fp_chance = 0.001

