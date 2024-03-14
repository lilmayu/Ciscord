package dev.mayuna.ciscord.android.ui.chat

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.mayuna.ciscord.android.backend.Ciscord
import dev.mayuna.ciscord.android.backend.networking.tcp.tasks.ChannelTasks
import dev.mayuna.ciscord.android.backend.networking.tcp.tasks.SendMessageTask
import dev.mayuna.ciscord.android.databinding.FragmentChatBinding
import dev.mayuna.ciscord.commons.objects.CiscordChatMessage
import java.util.Date
import java.util.Timer
import java.util.TimerTask

class ChatFragment : Fragment() {

    // todo: sorted list by time
    val chats = mutableListOf<UIChat>()

    private var _binding: FragmentChatBinding? = null
    private var _context: Context? = null;

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val timer = Timer()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)

        if (container != null) {
            _context = container.context
        }

        prepare();

        return binding.root
    }

    override fun onDestroyView() {
        timer.cancel()
        super.onDestroyView()
        _binding = null
    }

    fun prepare() {
        binding.recyclerviewChat.layoutManager = LinearLayoutManager(_context);
        binding.recyclerviewChat.adapter = UIChatAdapter(chats)

        binding.btnSend.setOnClickListener { onSendPress() }

        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                if (Ciscord.currentChannel == null) {
                    clearChats();
                    return;
                }

                if (Ciscord.resetMessages) {
                    Ciscord.resetMessages = false;
                    clearChats();

                    ChannelTasks.fetchMessages(Ciscord.currentChannel!!.id, 0)
                        .whenComplete { result, throwable ->
                            if (throwable != null) {
                                return@whenComplete;
                            }

                            result.result?.let {
                                for (message in it) {
                                    addChat(message)
                                }
                            }
                        }
                }
            }
        }, 0, 100)

        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                if (Ciscord.currentChannel == null) {
                    return;
                }

                val lastMessage = chats.lastOrNull();
                val lastId = lastMessage?.id ?: 0;

                ChannelTasks.fetchMessages(Ciscord.currentChannel!!.id, lastId)
                    .whenComplete { result, throwable ->
                        if (throwable != null) {
                            return@whenComplete;
                        }

                        result.result?.let {
                            for (message in it) {
                                addChat(message)
                            }
                        }
                    }
            }
        }, 0, 1000)
    }

    private fun clearChats() {
        activity?.runOnUiThread {
            chats.clear()
            binding.recyclerviewChat.adapter?.notifyDataSetChanged()
        }
    }

    fun onSendPress() {
        if (Ciscord.currentChannel == null) {
            MaterialAlertDialogBuilder(_context!!)
                .setTitle("Error")
                .setMessage("No channel selected")
                .setPositiveButton("OK", null)
                .show()

            return;
        }

        val message = binding.edittxtChatInput.text.toString()
        binding.edittxtChatInput.text.clear()

        Ciscord.currentChannel?.let {
            SendMessageTask.send(it.id, message).whenCompleteAsync { result, throwable ->
                if (!result.isSuccess) {
                    activity?.runOnUiThread {
                        MaterialAlertDialogBuilder(_context!!)
                            .setTitle("Error")
                            .setMessage("Failed to send message: " + result.errorMessage)
                            .setPositiveButton("OK", null)
                            .show()
                    }

                    return@whenCompleteAsync;
                }

                //addChat(result.result!!)
            };
        }
    }

    fun addChat(ciscordChatMessage: CiscordChatMessage) {
        chats.add(UIChat(ciscordChatMessage))
        binding.recyclerviewChat.adapter?.notifyItemInserted(chats.size - 1)
        binding.recyclerviewChat.scrollToPosition(chats.size - 1);
    }
}