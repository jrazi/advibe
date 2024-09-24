import org.springframework.context.annotation.Configuration
import org.springframework.data.cassandra.repository.config.EnableReactiveCassandraRepositories

@Configuration
@EnableReactiveCassandraRepositories(basePackages = ["com.tapsell.platform.ad.adtrack.repository"])
open class CassandraConfig {

}
