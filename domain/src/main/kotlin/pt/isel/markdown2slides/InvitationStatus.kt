package pt.isel.markdown2slides

enum class InvitationStatus {
    PENDING,
    ACCEPTED,
    DECLINED;

    companion object {
        fun fromString(status: String): InvitationStatus {
            return when (status.uppercase()) {
                "PENDING" -> PENDING
                "ACCEPTED" -> ACCEPTED
                "DECLINED" -> DECLINED
                else -> throw IllegalArgumentException("Unknown status: $status")
            }
        }
    }
}