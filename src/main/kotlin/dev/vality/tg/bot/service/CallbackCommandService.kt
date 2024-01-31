package dev.vality.tg.bot.service

import dev.vality.tg.bot.constants.ActionsMenuReactionItem.CTO_QUESTION
import dev.vality.tg.bot.constants.ActionsMenuReactionItem.TECH_VALITY_QUESTION
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

@Component
class CallbackCommandService {

    @Value("\${chats.all.hands.chat.id}")
    private val allHandsChatId: String? = null

    @Value("\${chats.tech.vality.chat.id}")
    private val techValityChatId: String? = null

    fun handleCallbackMessage(update: Update) =
        when (update.message.replyToMessage.text) {
            CTO_QUESTION -> initCtoCallbackActions(update)
            TECH_VALITY_QUESTION -> initTechValityCallbackActions(update)
            else -> initDefaultEctions(update)
        }

    private fun initDefaultEctions(update: Update) = listOf(
        SendMessage().apply {
            setChatId(update.message.chatId)
            text = "Неизвестная ошибка, попробуйте еще раз!"
        },
    )

    private fun initTechValityCallbackActions(update: Update) = listOf(
        SendMessage().apply {
            setChatId(update.message.chatId)
            text = "Создана задача на описание данной функции процессинга. Спасибо! \uD83D\uDE0A"
        },
        SendMessage().apply {
            chatId = techValityChatId!!
            text = update.message.text
        },
    )

    private fun initCtoCallbackActions(update: Update) = listOf(
        SendMessage().apply {
            setChatId(update.message.chatId)
            text = "Вопрос принят и будет рассмотрен на all hands. Спасибо! \uD83D\uDE0A"
        },
        SendMessage().apply {
            chatId = allHandsChatId!!
            text = update.message.text
        },
    )
}
