package pt.isel.markdown2slides

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Markdown2slidesApplication

fun main(args: Array<String>) {
	runApplication<Markdown2slidesApplication>(*args)
}
