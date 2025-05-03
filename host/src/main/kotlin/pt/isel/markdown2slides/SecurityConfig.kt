package pt.isel.markdown2slides

import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

const val MAX_SESSIONS_PER_USER = 3
const val MAX_SESSIONS_PREVENTS_LOGIN = false // false -> invalidate oldest


@Configuration
class SecurityConfig(private val customOAuth2UserService: CustomOAuth2UserService){
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors(withDefaults())
            .sessionManagement { session ->
                session
                    .maximumSessions(MAX_SESSIONS_PER_USER)
                    .maxSessionsPreventsLogin(MAX_SESSIONS_PREVENTS_LOGIN)
            }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/login", "/user", "/public/**").permitAll()
                    .anyRequest().authenticated()
                    //.anyRequest().permitAll()
            }
            .oauth2Login { login ->
                login.userInfoEndpoint{
                    it.oidcUserService(customOAuth2UserService)
                }
                login.defaultSuccessUrl("http://localhost:8000/", true)
            }
            .logout { logout ->
                logout.logoutSuccessHandler { request, response, authentication ->
                    request.session.invalidate()

                    /* SHOULD I DO THIS
                    val cookie = Cookie("JSESSIONID", "")
                    cookie.path = "/"
                    cookie.maxAge = 0
                    cookie.isHttpOnly = true
                    cookie.secure = false
                    response.addCookie(cookie)
                    */

                    response.status = HttpServletResponse.SC_OK
                }
            }
            .csrf { it.disable() }

        return http.build()
    }
}


@Configuration
class CorsConfig {
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val config = CorsConfiguration()
        config.allowedOrigins = listOf("http://localhost:8000")
        config.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
        config.allowedHeaders = listOf("*")
        config.allowCredentials = true

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", config)
        return source
    }
}



