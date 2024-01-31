package dev.vality.tg.bot.exception

class TgBotException(message: String, e: Exception?) : RuntimeException(message, e)
