package dev.vality.tg.bot.service

import dev.vality.tg.bot.constants.UserStatuses
import dev.vality.tg.bot.utils.UserUtils
import mu.KotlinLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember

private val log = KotlinLogging.logger {}

@Component
class AuthUserService {
    fun isUserPermission(chatMember: ChatMember): Boolean {
        return UserStatuses.ALLOWED_USER_STATUSES.contains(chatMember.status)
    }

    fun createUserNotFoundMessage(update: Update): SendMessage {
        log.info { "User ${UserUtils.getUserName(update)} not found in chat" }
        val message = SendMessage()
        message.setChatId(UserUtils.getUserId(update))
        message.text = "У вас нет прав на взаимодействие с данным ботом"
        return message
    }
}
