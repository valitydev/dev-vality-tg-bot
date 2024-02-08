package dev.vality.tg.bot.service

import dev.vality.tg.bot.constants.ActionsMenuItem
import dev.vality.tg.bot.constants.ActionsMenuReactionItem.CTO_QUESTION
import dev.vality.tg.bot.constants.ActionsMenuReactionItem.TECH_VALITY_QUESTION
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard

@Component
class MainMenuService {

    fun isMenuCommand(update: Update) = update.message.text.matches(Regex("^/.*"))

    fun initMenu(): SetMyCommands {
        val askCto = BotCommand(ActionsMenuItem.ASK_QUESTION_CTO, "Задать вопрос CTO")
        val ascTechInfo = BotCommand(ActionsMenuItem.ASK_TECH_INFO, "Запросить техническое описание работы процессинга")
        return SetMyCommands().apply {
            commands = arrayListOf(askCto, ascTechInfo)
        }
    }

    fun handleMenu(update: Update): SendMessage? {
        when (update.message.text) {
            ActionsMenuItem.ASK_QUESTION_CTO_MENU -> return SendMessage().apply {
                setChatId(update.message.chatId)
                text = CTO_QUESTION
                replyMarkup = ForceReplyKeyboard()
            }

            ActionsMenuItem.ASK_TECH_INFO_MENU -> {
                return SendMessage().apply {
                    setChatId(update.message.chatId)
                    text = TECH_VALITY_QUESTION
                    replyMarkup = ForceReplyKeyboard()
                }
            }
        }
        throw RuntimeException("Unknown command!")
    }
}
