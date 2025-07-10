rootProject.name = "markdown2slides"

plugins{
    kotlin("jvm") version "1.9.25" apply false
    kotlin("plugin.spring") version "1.9.25" apply false
    id("org.springframework.boot") version "3.4.3" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
}

include("host")
include("http-api")
include("domain")
include("services")
include("repository")
include("repository-jdbi")
include("repository-filesystem")
