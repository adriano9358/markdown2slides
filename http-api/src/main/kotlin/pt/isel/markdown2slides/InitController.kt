package pt.isel.markdown2slides

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import pt.isel.markdown2slides.model.UserInput

@RestController
class InitController {

    @PostMapping("/route")
    fun createUser(
        @RequestBody userInput: UserInput,
    ): ResponseEntity<*> {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .build<Unit>()



    }

}