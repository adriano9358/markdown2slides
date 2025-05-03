package pt.isel.markdown2slides


import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import pt.isel.markdown2slides.mem.RepositoryUserInMem
import java.util.*



private val logger: Logger = LoggerFactory.getLogger(CustomOAuth2UserService::class.java)


@Component
class CustomOAuth2UserService(
    private val userRepository: RepositoryUserInMem
) :OAuth2UserService<OidcUserRequest, OidcUser> {

    init {
        logger.info("CustomOAuth2UserService initialized")
    }


    /*override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val delegate = DefaultOAuth2UserService()
        val oAuth2User = delegate.loadUser(userRequest)

        val email = oAuth2User.getAttribute<String>("email")
            ?: throw IllegalArgumentException("Email not found in attributes")
        val name = oAuth2User.getAttribute<String>("name")
            ?: throw IllegalArgumentException("Name not found in attributes")

        logger.info("User email: $email")
        logger.info("User name: $name")

        val user = userRepository.findByEmail(email)
            if(user != null) {
                logger.info("User found in repository: ${user.id}")
                return DefaultOAuth2User(
                    setOf(SimpleGrantedAuthority("ROLE_USER")),
                    mapOf("userId" to user.id.toString(), "email" to user.email, "name" to user.name),
                    "userId"
                )
            } else {
                logger.info("User not found in repository, creating new user")
                val newUser = User(
                    id = UUID.randomUUID(),
                    name = name,
                    email = email
                )
                userRepository.save(newUser)
                return DefaultOAuth2User(
                    setOf(SimpleGrantedAuthority("ROLE_USER")),
                    mapOf("userId" to newUser.id.toString(), "email" to newUser.email, "name" to newUser.name),
                    "userId"
                )
            }
    }*/

    override fun loadUser(userRequest: OidcUserRequest): OidcUser {
        val delegate = OidcUserService()
        val oidcUser = delegate.loadUser(userRequest)

        val email = oidcUser.email
            ?: throw IllegalArgumentException("Email not found in ID token or user info")
        val name = oidcUser.fullName ?: oidcUser.givenName ?: "Unknown"

        logger.info("OIDC User email: $email")
        logger.info("OIDC User name: $name")

        val user = userRepository.findByEmail(email)
        val mappedUser = if (user != null) {
            logger.info("User found in repository: ${user.id}")
            user
        } else {
            logger.info("User not found in repository, creating new user")
            val newUser = User(
                id = UUID.randomUUID(),
                name = name,
                email = email
            )
            userRepository.save(newUser)
            newUser
        }

        val authorities = setOf(SimpleGrantedAuthority("ROLE_USER"))

        val customAttributes = mutableMapOf<String, Any>(
            "userId" to mappedUser.id.toString(),
            "email" to mappedUser.email,
            "name" to mappedUser.name
        )

        return DefaultOidcUser(
            authorities,
            oidcUser.idToken,
            oidcUser.userInfo,
            "email"
        ).let {
            object : OidcUser by it {
                override fun getAttributes(): Map<String, Any> = customAttributes
            }
        }
    }
}