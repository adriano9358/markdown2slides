package pt.isel.markdown2slides



import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User

@RestController
class UserController {

    @GetMapping("/api/user")
    fun user(@AuthenticationPrincipal principal: OAuth2User?): ResponseEntity<Any> {
        return if (principal == null) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        } else {
            return ResponseEntity.ok(
                mapOf(
                    "id" to (principal.attributes["userId"] ?: ""),
                    "name" to (principal.attributes["name"] ?: ""),
                    "email" to (principal.attributes["email"] ?: "")
                )
            )
        }
    }
}
