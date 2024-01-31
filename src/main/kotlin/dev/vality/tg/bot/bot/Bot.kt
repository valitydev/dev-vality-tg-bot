package dev.vality.tg.bot.bot

import dev.vality.tg.bot.constants.ActionsMenuItem.START_MENU
import dev.vality.tg.bot.constants.UserStatuses.Companion.ALLOWED_USER_STATUSES
import dev.vality.tg.bot.exception.TgBotException
import dev.vality.tg.bot.service.CallbackCommandService
import dev.vality.tg.bot.service.MainMenuService
import dev.vality.tg.bot.utils.UserUtils
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember
import org.telegram.telegrambots.meta.api.methods.menubutton.SetChatMenuButton
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember
import org.telegram.telegrambots.meta.api.objects.menubutton.MenuButtonCommands
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

private val log = KotlinLogging.logger {}

@Component
class Bot(
    private val mainMenuService: MainMenuService,
    private val callbackCommandService: CallbackCommandService,
) : TelegramLongPollingBot() {

    @Value("\${bot.username}")
    private val botUsername: String? = null

    @Value("\${bot.token}")
    private val botToken: String? = null

    @Value("\${chats.vality.chat.id}")
    private val valityChatId: String? = null

    override fun onUpdateReceived(update: Update) {
        try {
            if (update.hasMessage()) {
                if (update.message.text.equals(START_MENU)) {
                    handleStartMessage(update)
                } else if (mainMenuService.isMenuCommand(update)) {
                    execute(mainMenuService.handleMenu(update))
                } else {
                    handleCallback(update)
                }
            }
        } catch (e: TelegramApiException) {
            log.error { e }
        }
    }

    private fun handleStartMessage(update: Update) {
        try {
            val createMenuCommands = mainMenuService.initMenu()
            val createChatMenuButton = SetChatMenuButton().apply {
                setChatId(update.message.chatId)
                menuButton = MenuButtonCommands.builder()
                    .build()
            }
            execute(createMenuCommands)
            execute(createChatMenuButton)
        } catch (e: TelegramApiException) {
            log.error { e }
        }
    }

    private fun handleCallback(update: Update) {
        try {
            val callbackMessages: List<SendMessage> = callbackCommandService.handleCallbackMessage(update)
            callbackMessages.forEach { item -> execute(item) }
        } catch (e: TelegramApiException) {
            log.error { e }
        }
    }

    override fun getBotUsername(): String {
        return botUsername!!
    }

    @Deprecated("Deprecated in Java")
    override fun getBotToken(): String {
        return botToken!!
    }

    fun isUserPermission(update: Update?): Boolean {
        val userId: Long = UserUtils.getUserId(update!!)
        val userName: String = UserUtils.getUserName(update)
        try {
            return isChatMemberPermission(userId)
        } catch (e: TelegramApiException) {
            checkExceptionIsUserInChat(e, userName, userId)
            throw TgBotException(String.format("Error while checking user %s permissions", userName), e)
        }
    }

    @Throws(TelegramApiException::class)
    fun isChatMemberPermission(userId: Long): Boolean {
        val chatMember: ChatMember =
            execute(GetChatMember(valityChatId!!, userId))
        if (ALLOWED_USER_STATUSES.contains(chatMember.status)) {
            return true
        } else {
            val message = SendMessage()
            message.setChatId(userId)
            message.text = "У вас нет прав на vality"
            execute(message)
            return false
        }
    }

    private fun checkExceptionIsUserInChat(e: TelegramApiException, userName: String, userId: Long) {
        if (e.message!!.contains("[400] Bad Request: user not found")) {
            log.info("User {} not found in chat", userName)
            val message = SendMessage()
            message.setChatId(userId)
            message.text = "У вас нет прав на взаимодействие с данным ботом"
            try {
                execute(message)
                throw TgBotException(String.format("User %s don't have permissions", userName), e)
            } catch (ex: TelegramApiException) {
                throw TgBotException(String.format("Error while checking user %s permissions", userName), e)
            }
        }
    }
}
