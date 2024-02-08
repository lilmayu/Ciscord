package dev.mayuna.ciscord.android.ui.chat

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import dev.mayuna.ciscord.android.databinding.FragmentChatBinding
import java.util.Date

class ChatFragment : Fragment() {

    // todo: sorted list by time
    val chats = mutableListOf<UIChat>()

    private var _binding: FragmentChatBinding? = null
    private var _context: Context? = null;
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)

        if (container != null) {
            _context = container.context
        }
        /*
        val textView: TextView = binding.textHome
        chatViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }*/

        prepare();

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun prepare() {
        binding.recyclerviewChat.layoutManager = LinearLayoutManager(_context);
        binding.recyclerviewChat.adapter = UIChatAdapter(chats)

        binding.btnSend.setOnClickListener {
            val message = binding.edittxtChatInput.text.toString()
            binding.edittxtChatInput.text.clear()

            // TODO: Better adding of chats
            chats.add(UIChat("mayuna", Date(), message))
            binding.recyclerviewChat.adapter?.notifyDataSetChanged()
            binding.recyclerviewChat.scrollToPosition(chats.size - 1);
        }
    }
}