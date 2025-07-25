package pt.isel.markdown2slides


import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.stereotype.Component
import pt.isel.markdown2slides.data.TransactionManager
import java.util.*



private val logger: Logger = LoggerFactory.getLogger(CustomOAuth2UserService::class.java)


@Component
class CustomOAuth2UserService(
    private val trxManager: TransactionManager,
) :OAuth2UserService<OidcUserRequest, OidcUser> {

    init {
        logger.info("CustomOAuth2UserService initialized")
    }

    override fun loadUser(userRequest: OidcUserRequest): OidcUser {
        val delegate = OidcUserService()
        val oidcUser = delegate.loadUser(userRequest)

        val email = oidcUser.email
            ?: throw IllegalArgumentException("Email not found in ID token or user info")
        val name = oidcUser.fullName ?: oidcUser.givenName ?: "Unknown"

        return trxManager.run {
            val user = repoUser.findByEmail(email)
            val mappedUser = if (user != null) {
                // logger.info("User found in repository: ${user.id}")
                user
            } else {
                // logger.info("User not found in repository, creating new user")
                val newUser = User(
                    id = UUID.randomUUID(),
                    name = name,
                    email = email
                )
                repoUser.save(newUser)
                newUser
            }

            val authorities = setOf(SimpleGrantedAuthority("ROLE_USER"))

            val customAttributes = mutableMapOf<String, Any>(
                "userId" to mappedUser.id.toString(),
                "email" to mappedUser.email,
                "name" to mappedUser.name
            )

            return@run DefaultOidcUser(
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
}