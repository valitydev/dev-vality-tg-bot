package dev.vality.tg.bot.bot

import dev.vality.tg.bot.constants.ActionsMenuItem.START_MENU
import dev.vality.tg.bot.service.AuthUserService
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
    private val authUserService: AuthUserService,
) : TelegramLongPollingBot() {

    @Value("\${bot.username}")
    lateinit var botUser: String

    @Value("\${bot.token}")
    lateinit var token: String

    @Value("\${chats.vality.chat.id}")
    lateinit var valityChatId: String

    override fun onUpdateReceived(update: Update) {
        try {
            val userId = UserUtils.getUserId(update)
            val chatMember: ChatMember = execute(GetChatMember(valityChatId, userId))
            if (!authUserService.isUserPermission(chatMember)) {
                val message = authUserService.createUserNotFoundMessage(update)
                execute(message)
            }

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
        return botUser
    }

    @Deprecated("Deprecated in Java")
    override fun getBotToken(): String {
        return token
    }
}
