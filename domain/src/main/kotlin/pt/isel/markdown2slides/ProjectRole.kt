package pt.isel.markdown2slides

enum class ProjectRole {
    EDITOR, VIEWER, ADMIN;
    companion object {
        fun fromString(role: String): ProjectRole {
            return when (role.uppercase()) {
                "EDITOR" -> EDITOR
                "VIEWER" -> VIEWER
                "ADMIN" -> ADMIN
                else -> throw IllegalArgumentException("Unknown role: $role")
            }
        }
    }
}