package pt.isel.markdown2slides

import org.junit.jupiter.api.Test
import java.io.File

class RepositoryProjectContentTests {

    @Test
    fun test1(){
        val a = RepositoryProjectContent()
        a.saveMarkdown("123", "Hello world!")
        assert(File("$MARKDOWN_DIR/123.md").exists())
    }
}