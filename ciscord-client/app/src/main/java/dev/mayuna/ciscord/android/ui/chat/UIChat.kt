package dev.mayuna.ciscord.android.ui.chat

import dev.mayuna.ciscord.android.backend.Ciscord
import dev.mayuna.ciscord.commons.objects.CiscordChatMessage
import java.util.Date

data class UIChat(val username: String, var date: Date, var message: String, val id: Long) {

    constructor(ciscordChatMessage: CiscordChatMessage) : this(Ciscord.user.username, Date(ciscordChatMessage.timestamp), ciscordChatMessage.content, ciscordChatMessage.id)
}