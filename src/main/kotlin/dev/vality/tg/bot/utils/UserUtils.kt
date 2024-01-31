package dev.vality.tg.bot.utils

import org.telegram.telegrambots.meta.api.objects.Update

class UserUtils {
    companion object {
        fun getUserId(update: Update): Long {
            return if (update.hasMessage()) {
                update.message.from.id
            } else if (update.hasCallbackQuery()) {
                update.callbackQuery.from.id
            } else if (update.hasInlineQuery()) {
                update.inlineQuery.from.id
            } else {
                update.myChatMember.from.id
            }
        }

        fun getUserName(update: Update): String {
            return if (update.hasMessage()) {
                update.message.from.userName
            } else if (update.hasCallbackQuery()) {
                update.callbackQuery.message.from.userName
            } else if (update.hasInlineQuery()) {
                update.inlineQuery.from.userName
            } else {
                update.myChatMember.from.userName
            }
        }

        fun isUserInBot(update: Update): Boolean {
            return update.message.chatId == update.message.from.id
        }
    }
}
