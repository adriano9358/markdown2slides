package pt.isel.markdown2slides

import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

const val MAX_SESSIONS_PER_USER = 3
const val MAX_SESSIONS_PREVENTS_LOGIN = false // false -> invalidate oldest


@Configuration
class SecurityConfig(private val customOAuth2UserService: CustomOAuth2UserService){
    private val logger = org.slf4j.LoggerFactory.getLogger(SecurityConfig::class.java)
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            //.cors{ it.configurationSource(corsConfigurationSource()) }
            .sessionManagement { session ->
                session
                    .maximumSessions(MAX_SESSIONS_PER_USER)
                    .maxSessionsPreventsLogin(MAX_SESSIONS_PREVENTS_LOGIN)
            }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/login", "/api/user", "/public/**").permitAll()
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
                    response.status = HttpServletResponse.SC_OK
                }
            }

        return http.build()
    }


    @Bean
    fun corsConfigurationSource(): UrlBasedCorsConfigurationSource {
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



@Configuration
class CorsConfig : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins("http://localhost:8000")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
    }
}