# Cassandra Configuration Report for AdVibe - Real-Time Impression & Click Processing

## Overview
This report dives into the optimal configuration of Apache Cassandra to handle the performance needs of the AdVibe application. Our aim is to efficiently manage the processing of a million ad impression events per second, along with a smaller subset of ad click events.

## Requirements
1. **High Throughput**: Support for 1 million requests per second.
2. **Low Latency**: Ensure that reads, especially for updates, occur within 0-60 seconds of the event.
3. **Scalability**: Manage increasing data volumes without performance degradation.
4. **Fault Tolerance**: Maintain data integrity and availability.

## Compaction Strategy
Choosing the right compaction strategy is crucial for balancing read and write performance.

- **Leveled Compaction Strategy (LCS)**
  - **Pros**: Reduces read amplification, keeps the number of SSTables per read operation low.
  - **Cons**: Higher write amplification compared to other strategies.
  - **Configuration**:
    ```yaml
    compaction_strategy: LeveledCompactionStrategy
    ```
  
- **Size-Tiered Compaction Strategy (STCS)**
  - **Pros**: Simpler and good for write-heavy workloads.
  - **Cons**: Can cause high read amplification.
  
**Decision**: We chose **LCS** due to our need for fast read performance, which aligns with our requirement for updating events quickly.

## Bloom Filters
Bloom filters help quickly determine if a partition key exists in an SSTable, minimizing disk I/O.

- **Row-Level Bloom Filters**
  - **False Positive Rate (FPR)**: Aim for an FPR of 0.01 (1%) to balance memory usage and read efficiency.
  - **Memory Allocation**: Sufficient off-heap memory is critical.

  - **Formula**: The memory required for a bloom filter is:
    $$
    M = -\frac{N \cdot \ln(FPR)}{(\ln(2))^2}
    $$
    Where:
    - $N$ is the number of items (events)
    - $FPR$ is the false positive rate

  Let's assume $N = 1,000,000$ and $FPR = 0.01$:
    $$
    M = -\frac{1,000,000 \cdot \ln(0.01)}{(\ln(2))^2} \approx 9,585,058 \text{ bits} \approx 1.14 \text{ MB}
    ```

## Read/Write Path Optimization
Optimizing the read and write paths ensures that Cassandra can handle our volume and speed requirements.

- **Read Repair Chance**: Set to 0 to reduce overhead.
  - **Configuration**:
    ```yaml
    read_repair_chance: 0.0
    ```

- **Hinted Hand-off**: Enable with a short duration to maintain availability without overloading the system.
  - **Configuration**:
    ```yaml
    hinted_handoff_enabled: true
    ```

## Caching
Effective use of caches can significantly improve read performance.

- **Key Cache**: Increase the size for faster primary key lookups.
  - **Configuration**:
    ```yaml
    key_cache_size_in_mb: <5% of heap size>
    ```

- **Row Cache**: Typically disabled unless specific workloads benefit significantly.
  - **Configuration**:
    ```yaml
    row_cache_size_in_mb: 0
    ```

## Resource Management
Proper resource allocation is key to optimal performance.

- **Heap Size**: Tune JVM heap size between 4-8 GB based on node hardware.
- **Off-Heap Memory**: Allocate memory for bloom filters and other caches.
- **Thread Pools**: Adjust based on CPU capabilities.
  - **Configuration**:
    ```yaml
    memtable_flush_writers: 8
    concurrent_reads: 32
    concurrent_writes: 32
    concurrent_compactors: 2
    ```

## Partitioning Strategy
Ensure an even distribution of data to prevent hotspots.

- **Composite Partition Keys**: Use if necessary to distribute data evenly across nodes.

## Consistency Levels
Balancing consistency and performance is crucial for high-throughput applications.

- **Write Consistency**: Use `QUORUM` to ensure durability and availability.
- **Read Consistency**: Use `LOCAL_QUORUM` to improve read performance.
  - **Configuration**:
    ```yaml
    write_consistency: QUORUM
    read_consistency: LOCAL_QUORUM
    ```

We’ve weighed the pros and cons of each configuration option, ensuring our choices align with AdVibe’s performance and reliability needs. Monitoring and adjusting these settings based on real-world performance will be essential to maintain optimal efficiency.
