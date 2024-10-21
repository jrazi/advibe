import com.datastax.oss.driver.api.core.CqlSession
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.data.cassandra.core.cql.session.init.KeyspacePopulator
import org.springframework.data.cassandra.core.cql.session.init.ResourceKeyspacePopulator
import org.springframework.data.cassandra.repository.config.EnableReactiveCassandraRepositories

@Configuration
@EnableReactiveCassandraRepositories(basePackages = ["com.tapsell.recruitment.advibe.repository"])
open class CassandraConfig {

//    @Bean
//    open fun sessionFactoryInitializer(session: CqlSession): KeyspacePopulator {
//        val populator = ResourceKeyspacePopulator()
//        populator.setScripts(ClassPathResource("cassandra_config.cql"))
//        return populator
//    }
}
