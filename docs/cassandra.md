# AdVibe Cassandra Configuration Guide

In designing AdVibe’s data infrastructure, we aim to support a high-availability, write-intensive workload for a platform that demands both scalability and resilience. Our choice of Cassandra as the distributed database system reflects our need for a fault-tolerant, horizontally scalable setup. This document breaks down each configuration decision, along with the technical and operational justifications, to guide both the initial deployment and future adjustments.

---

## Keyspace Configuration

### Replication Strategy: `SimpleStrategy` with `replication_factor = 3`

#### Decision

We selected `SimpleStrategy` with a replication factor of 3 for our single data center.

#### Rationale

1. **High Availability**: By replicating data across three nodes, we ensure resilience in case of node failures, enabling read and write availability despite potential disruptions.
2. **Simplified Setup**: `SimpleStrategy` is optimal for single data center configurations, keeping configuration manageable for development and initial production phases.

#### Future Considerations

- **Multi-Data Center Expansion**: If we expand to multiple data centers, we’ll switch to `NetworkTopologyStrategy`, allowing more control over replication across regions.
- **Consistency Levels**: With three replicas, we can leverage `LOCAL_QUORUM` for a balance between consistency and availability, accommodating eventual consistency needs while keeping read latencies low.

---

## Table Schema Design

1. **Primary Key Choice**: Using `requestid` as the primary key ensures that each record remains unique and enables efficient data access patterns, such as querying for a specific event.
2. **Data Distribution**: The use of `requestid` as a partition key distributes records evenly, avoiding hotspots in the cluster and enabling consistent read/write throughput.

#### Considerations

- **Query Patterns**: If future queries demand filtering by `adid` or `appid`, clustering columns or secondary indexes may be required.
- **Time-Based Queries**: For queries based on time (e.g., finding impressions or clicks within a range), a change in schema—such as introducing clustering on time—could be beneficial.

---

## Bloom Filter Configuration

### Setting: `bloom_filter_fp_chance = 0.001`

#### Decision

We set the Bloom filter false positive rate to 0.1% (0.001) to minimize unnecessary disk reads and optimize memory usage.

#### Rationale

1. **Reducing Disk I/O**: A low false positive rate ensures efficient reads by reducing the likelihood of unnecessary disk access, thus boosting performance.
2. **Memory Considerations**: Although this configuration increases memory usage, the trade-off is beneficial for our read-heavy scenarios.

#### Example Calculations

To estimate Bloom filter memory usage, let’s consider a scenario where **1 million unique `requestid`s** are generated per second and SSTables flush every **10 minutes**.

- **Total Keys per SSTable**: $1,000,000 \times 600 = 600,000,000$ keys
- **Bloom Filter Size**:
  $$
  m = -\frac{n \times \ln(\text{FPR})}{(\ln 2)^2} \approx 8,657,399,000 \text{ bits} \approx 1,082 \text{ MB}
  $$
- **Number of Hash Functions**:
  $$
  k = \frac{m}{n} \ln 2 \approx 10 \text{ hash functions}
  $$

This configuration results in roughly 1 GB per SSTable, fitting well within our memory allocation limits.

#### Considerations

Monitoring memory usage is critical; if we approach our memory limit, adjusting `bloom_filter_fp_chance` to a slightly higher value can help balance memory use and performance.

---

## Read Repair Configuration

### Setting: `read_repair_chance = 0.05`

#### Decision

We configured a read repair chance of 5% to balance consistency with read latency.

#### Rationale

1. **Consistency Assurance**: Read repair corrects inconsistencies during reads, which can be critical in a distributed environment where transient failures are possible.
2. **Minimal Latency Impact**: Setting it at 5% keeps the impact on read performance minimal, avoiding excessive load on nodes.

#### Considerations

- **Write-Heavy Optimization**: Since AdVibe is write-intensive, we avoid high read repair rates to ensure read latency remains low.
- **Consistency Control**: By relying on `LOCAL_QUORUM` for writes, we achieve a higher baseline of consistency, reducing dependency on read repairs.

---

## Compaction Strategy

### Setting: `TimeWindowCompactionStrategy` with a 1-hour window

#### Decision

We implemented `TimeWindowCompactionStrategy` (TWCS) with a one-hour window to optimize compaction for time-series data.

#### Rationale

1. **Efficiency with Time-Series Data**: TWCS is ideal for time-ordered, mostly immutable data, which matches our ad event logging pattern.
2. **Efficient Expiration Handling**: If we decide to use TTL, TWCS will handle expired data gracefully by dropping old SSTables instead of compacting them unnecessarily.

#### Considerations

- **Write Patterns**: Our primary workload involves inserting events, with occasional updates for clicks.
- **Data Retention**: Should we choose to retain data for a limited period, TWCS will optimize this by removing expired SSTables without the need for manual cleanup.

---

## Cluster Configuration

### Data Center: `datacenter1`

#### Decision

Named the data center as `datacenter1` to align with application configurations and simplify operations.

#### Rationale

- **Driver Optimization**: By matching the Cassandra data center name with the application’s local-datacenter configuration, we ensure that the driver optimally routes requests within the local data center, reducing cross-region latency.

---

### Authentication and Authorization

**Settings**:

- `CASSANDRA_AUTHENTICATOR=AllowAllAuthenticator`
- `CASSANDRA_AUTHORIZER=AllowAllAuthorizer`

#### Decision

Disabled authentication and authorization for the development environment to prioritize ease of access.

#### Rationale

1. **Development Focus**: Without the burden of credentials, development and testing can proceed unencumbered by security layers.
2. **Simplified Testing**: In development, faster access without managing permissions allows a focus on application logic.

#### Considerations

- **Security**: These settings are strictly for development. In production, we’ll enable secure authenticator and authorizer configurations.
- **Environment Segregation**: Ensuring that these settings apply only in non-production environments is critical to prevent security lapses.

---

### Remote Connection Enablement

**Setting**: `CASSANDRA_ENABLE_REMOTE_CONNECTIONS=true`

#### Decision

Enabled remote connections to allow flexibility in testing scenarios.

#### Rationale

- **Development Convenience**: This allows the application to run on the host while Cassandra is inside Docker, simplifying setup.
- **Enhanced Testing**: Permits integration testing, debugging, and remote access without reconfiguration.

#### Considerations

- **Network Security**: For production, remote connections should be restricted to trusted networks.
- **Firewall Rules**: Port `9042` must be accessible from the application host for remote connections to function.

---

## Application Configuration

### Contact Points and Data Center

#### Decision

Set `127.0.0.1` as the contact point and aligned the local data center with Cassandra’s `datacenter1` setting.

#### Rationale

1. **Local Development Setup**: Since the application and Cassandra are typically co-located on the same machine or Docker network, this configuration streamlines connectivity.
2. **Consistency Across Services**: Using the same data center name across application and database improves routing accuracy and reduces connection errors.

#### Considerations

- **Docker Networking**: If the application runs inside Docker, the contact points might require adjustment, for instance, using the `cassandra` service name.
- **Scaling Up**: In production, multiple contact points or a load balancer might be needed to handle distributed workloads.

---

## Kafka Configuration (Briefly)

While not a primary focus, we’ve configured Kafka for message handling:

- **Bootstrap Servers**: `127.0.0.1:9092`
- **Consumer Group ID**: `advibe-consumer-group`
- **JSON Serialization**: Configured Kafka to process JSON payloads seamlessly.

#### Considerations

- **Dependencies**: Kafka availability is vital since AdVibe relies on it for event-driven data flows.
- **Port Configuration**: Exposing Kafka’s ports facilitates testing across environments.

---

## Docker Compose Setup

### Cassandra Service

Configured with Bitnami’s Cassandra image to simplify deployment:


#### Highlights

- **Volume Mounts**: Persisted data and init scripts streamline data storage and setup.
- **Environment Variables**: Easily adaptable to different configurations (e.g., dev vs. prod).

#### Rationale

1. **Ease of Setup**: Bitnami’s image provides a reliable, well-maintained base.
2. **Consistency**: The Docker setup ensures that development closely mirrors production configurations.

#### Considerations

- **Health Checks**: Monitoring Cassandra readiness before application initialization is crucial for a smooth startup.
- **Maintenance**: Automating schema and keyspace creation through mounted `init-scripts` simplifies deployment but may require adjustments for