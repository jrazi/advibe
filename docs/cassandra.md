# AdVibe Cassandra Configuration Guide

This document outlines the **desired** configuration for Cassandra in the AdVibe environment. It's important to note that due to an issue with mounting the `cassandra.yaml` file, the configurations specified here are not currently active in the deployed Cassandra instance. This guide serves as a blueprint for achieving optimal performance, scalability, and reliability once the configuration issues are resolved.

In designing AdVibe’s data infrastructure, the focus is on supporting a high-availability, write-intensive workload essential for a platform that demands both scalability and resilience. Cassandra was chosen as the distributed database system to meet these needs, offering fault tolerance and horizontal scalability. Below, each configuration decision is explained to provide clarity on the reasoning behind the settings and their impact on the application.

---

## Keyspace Configuration

### Replication Strategy: `SimpleStrategy` with `replication_factor = 3`

**Why this configuration was chosen:**

Using `SimpleStrategy` with a replication factor of 3 ensures that each piece of data is duplicated across three different nodes within the same data center. This setup provides a solid foundation for data redundancy and availability, safeguarding against node failures without the complexity of multi-data center management.

**How it affects the application:**

A replication factor of 3 strikes a balance between data safety and resource usage. It guarantees that even if two nodes go down simultaneously, the data remains accessible from the third replica. This is crucial for AdVibe’s real-time processing, where uninterrupted access to ad impressions and clicks is necessary to maintain seamless user experiences and accurate analytics.

---

## Table Schema Design

### Primary Key and Partitioning

**Why this configuration was chosen:**

The primary key is designed with `requestid` as the partition key to ensure each ad event is uniquely identifiable and evenly distributed across the cluster. This design prevents data hotspots, enabling balanced load distribution and optimizing both read and write performance.

**How it affects the application:**

Using `requestid` as the partition key allows Cassandra to distribute data uniformly, which is essential for maintaining high write throughput and low latency. This setup supports AdVibe’s requirement for handling a vast number of concurrent ad impressions and clicks, ensuring that the system remains responsive and efficient under heavy load.

---

## Bloom Filter Configuration

### Bloom Filter False Positive Rate: `bloom_filter_fp_chance = 0.001`

**Why this configuration was chosen:**

A Bloom filter false positive rate of 0.1% was selected to minimize unnecessary disk I/O operations. This low false positive rate ensures that the Bloom filters effectively reduce the number of disk accesses for non-existent keys, enhancing read performance without consuming excessive memory.

**How it affects the application:**

Maintaining a low false positive rate means that fewer unnecessary disk reads occur, which optimizes read performance and reduces latency. For AdVibe’s real-time analytics, this configuration ensures that data retrieval is both swift and efficient, supporting the platform's high-performance requirements.

---

## Read Repair Configuration

### Read Repair Chance: `read_repair_chance = 0.05`

**Why this configuration was chosen:**

Setting a read repair chance of 5% allows Cassandra to periodically check and correct inconsistencies between replicas during read operations. This ensures data integrity without imposing significant overhead on the system.

**How it affects the application:**

A 5% read repair chance ensures that data remains consistent across replicas, which is vital for maintaining accurate real-time analytics. This configuration helps prevent stale or inconsistent data from being served to users, thereby enhancing the reliability of AdVibe’s reporting and analytics functionalities.

---

## Compaction Strategy

### Compaction Strategy: `TimeWindowCompactionStrategy` with 1-Hour Window

**Why this configuration was chosen:**

Implementing `TimeWindowCompactionStrategy` (TWCS) with a one-hour window optimizes the compaction process for time-series data, which aligns with AdVibe’s ad event logging pattern. TWCS groups SSTables into hourly buckets, making compaction more efficient and tailored to the temporal nature of the data.

**How it affects the application:**

This strategy enhances write performance by reducing write amplification and managing SSTable sizes effectively. It ensures that data remains organized chronologically, which is beneficial for time-based queries and analytics. For AdVibe, TWCS supports high write throughput and simplifies data retention policies by allowing easy expiration of outdated SSTables.

---

## Caching Configuration

### Key Cache Size: `key_cache_size = 100MiB`

**Why this configuration was chosen:**

Allocating 100MiB to the key cache improves read performance by storing frequently accessed keys in memory. This reduces the need for disk seeks, enabling faster data retrieval and enhancing the overall responsiveness of the system.

**How it affects the application:**

A well-sized key cache accelerates read operations by minimizing disk I/O, which is crucial for AdVibe’s real-time data access requirements. This configuration ensures that the most commonly accessed keys are readily available in memory, thereby improving the efficiency of real-time queries and reducing latency.

---

## Hinted Handoff Configuration

### Hinted Handoff Enabled: `hinted_handoff_enabled = true`

**Why this configuration was chosen:**

Enabling hinted handoff ensures that write operations are not lost during transient node outages. When a node becomes temporarily unavailable, hints are stored and replayed once the node recovers, maintaining data consistency across the cluster.

**How it affects the application:**

This feature enhances fault tolerance by ensuring that write operations are eventually propagated to all replicas, even if some nodes are temporarily down. For AdVibe, this means that ad event data remains consistent and reliable, preventing data loss and ensuring that analytics remain accurate and comprehensive.

### Maximum Hint Window: `max_hint_window = 3h`

**Why this configuration was chosen:**

Setting the maximum hint window to 3 hours provides a sufficient timeframe for temporarily unavailable nodes to recover and receive their hints. This window accommodates typical transient outages without overloading the system with stale hints.

**How it affects the application:**

A 3-hour window ensures that hints are delivered promptly once nodes are back online, maintaining data consistency without overwhelming the system. For AdVibe, this means that the system can recover quickly from temporary disruptions, ensuring continuous and reliable data processing.

### Maximum Hints Delivery Threads: `max_hints_delivery_threads = 4`

**Why this configuration was chosen:**

Allocating 4 threads for hints delivery accelerates the process of replaying stored hints once nodes recover. This ensures that data consistency is restored swiftly, minimizing the window during which data may be missing or outdated.

**How it affects the application:**

By increasing the number of delivery threads, AdVibe can handle the replay of hints more efficiently, reducing the time required to synchronize data across the cluster after a node recovery. This enhances the overall responsiveness and reliability of the system, ensuring that real-time data processing remains uninterrupted.

---

## Security Configuration

### Authenticator: `PasswordAuthenticator`

**Why this configuration was chosen:**

Implementing `PasswordAuthenticator` enforces user authentication through username and password pairs, enhancing the security of AdVibe’s Cassandra cluster by ensuring that only authorized users can access and manipulate data.

**How it affects the application:**

This setting restricts access to the database, preventing unauthorized interactions and safeguarding sensitive ad event data. For AdVibe, which handles potentially sensitive user interactions, maintaining strict access controls is essential for data privacy and compliance with security standards.

### Authorizer: `CassandraAuthorizer`

**Why this configuration was chosen:**

Selecting `CassandraAuthorizer` enables detailed permission management, allowing administrators to define precise access controls for different users and roles within the Cassandra cluster.

**How it affects the application:**

This configuration ensures that users have appropriate permissions for their roles, preventing unauthorized data access and modifications. For AdVibe, this means that only designated personnel can perform critical operations, maintaining the integrity and security of the data processing workflows.

---

## Networking Configuration

### Snitch: `GossipingPropertyFileSnitch`

**Why this configuration was chosen:**

Using `GossipingPropertyFileSnitch` allows Cassandra to dynamically adapt to network topology changes, ensuring efficient data routing and replication strategies tailored to AdVibe's distributed setup.

**How it affects the application:**

This snitch provides Cassandra with up-to-date information about the network topology, which optimizes replica placement and data locality. For AdVibe, this means reduced latency and improved performance, as data is stored and accessed in the most efficient manner possible within the cluster.

### Listen Address: `<node_ip_address>`

**Why this configuration was chosen:**

Binding Cassandra to the specific node IP address ensures clear and secure communication within the AdVibe cluster, preventing unintended network interactions and ensuring that all nodes communicate correctly.

**How it affects the application:**

Setting the `listen_address` to the node's IP guarantees that Cassandra nodes can discover and communicate with each other reliably. This is crucial for maintaining cluster health and ensuring that data replication and consistency mechanisms function as intended, supporting AdVibe’s real-time processing requirements.

### RPC Address: `<node_ip_address>`

**Why this configuration was chosen:**

Configuring the RPC address to the node's IP allows AdVibe’s application to connect directly and securely to the Cassandra instance, facilitating efficient client-server communication.

**How it affects the application:**

By setting the `rpc_address` to the correct IP, clients can establish stable and secure connections to the Cassandra cluster. This ensures that AdVibe’s application can reliably perform read and write operations, maintaining the system’s responsiveness and data integrity.

### Native Transport Port: `9042`

**Why this configuration was chosen:**

Maintaining the default native transport port ensures compatibility with standard CQL clients used by AdVibe, streamlining client integration and connectivity.

**How it affects the application:**

Using the default port simplifies the configuration process and avoids potential conflicts with other services. It ensures that AdVibe’s application can seamlessly communicate with Cassandra, leveraging existing tools and libraries without additional configuration overhead.

---

## Conclusion

Once the issue with mounting the `cassandra.yaml` file is resolved, applying these configurations will establish a robust and efficient Cassandra cluster that supports AdVibe’s operational needs. Ensuring that these settings are correctly implemented will provide a solid foundation for maintaining data integrity, optimizing performance, and scaling the system as AdVibe continues to grow.

