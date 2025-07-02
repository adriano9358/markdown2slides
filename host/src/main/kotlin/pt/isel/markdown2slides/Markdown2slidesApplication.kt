package pt.isel.markdown2slides

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
class Markdown2slidesApplication{
	@Bean
	fun jdbi() =
		Jdbi
			.create(
				PGSimpleDataSource().apply {
					setURL(Environment.getDbUrl())
				},
			).configureWithAppRequirements()

	@Bean
	@Profile("jdbi")
	@Primary
	fun trxManagerJdbi(jdbi: Jdbi): TransactionManagerJdbi = TransactionManagerJdbi(jdbi)

}

fun main(args: Array<String>) {
	runApplication<Markdown2slidesApplication>(*args)
}
