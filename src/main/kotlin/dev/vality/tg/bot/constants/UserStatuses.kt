package dev.vality.tg.bot.constants

enum class UserStatuses(
    private val value: String? = null,
) {

    CREATOR("creator"), ADMINISTRATOR("administrator"), MEMBER("member");

    companion object {
        val ALLOWED_USER_STATUSES: Set<String?> = setOf(
            CREATOR.value,
            ADMINISTRATOR.value,
            MEMBER.value,
        )
    }
}
