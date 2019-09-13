package ru.skillbranch.devintensive.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import ru.skillbranch.devintensive.extensions.mutableLiveData
import ru.skillbranch.devintensive.models.data.Chat
import ru.skillbranch.devintensive.models.data.ChatItem
import ru.skillbranch.devintensive.repositories.ChatRepository

class MainViewModel : ViewModel() {

    private val query = mutableLiveData("")


    private var chatRepository = ChatRepository


    private val chats = Transformations.map(chatRepository.loadChats()) { chats ->
        val archivedChats = chats.filter { it.isArchived }
        val notArchivedChats = chats.filter { !it.isArchived }

        val notArchivedChatItems = notArchivedChats
                .map { it.toChatItem() }
                .sortedBy { it.id.toInt() }

        val result = mutableListOf<ChatItem>()

        if (archivedChats.isNotEmpty()) {
            val archiveChatItem = Chat.createArchiveChatItem(archivedChats)
            result.add(archiveChatItem)
        }
        result.addAll(notArchivedChatItems)

        return@map result
    }

    fun getChatData() : LiveData<List<ChatItem>> {
        val result = MediatorLiveData<List<ChatItem>>()

        val filterF = {
            val queryStr = query.value!!
            val chatItems = chats.value!!

            result.value = if (queryStr.isEmpty()) chatItems
            else chatItems.filter { it.title.contains(queryStr, true) }
        }

        result.addSource(chats) { filterF.invoke() }
        result.addSource(query) { filterF.invoke() }

        return result

    }

    fun addToArchive(chatId: String) {
        val chat = chatRepository.find(chatId)
        chat ?: return
        chatRepository.update(chat.copy(isArchived = true))
    }

    fun restoreFromArchive(chatId: String) {
        val chat = chatRepository.find(chatId)
        chat ?: return
        chatRepository.update(chat.copy(isArchived = false))
    }

    fun handleSearchQuery(text: String) {
        query.value = text
    }
}