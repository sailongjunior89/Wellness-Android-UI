package nus.iss.wellnessapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import nus.iss.wellnessapp.R
import nus.iss.wellnessapp.model.UiChatMessage

class ChatMessageAdapter(
    private val messages: MutableList<UiChatMessage> = mutableListOf()
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_USER      = 0
        private const val TYPE_ASSISTANT = 1
    }

    // ── ViewHolders ────────────────────────────────────────────────────────

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtMessage: TextView = view.findViewById(R.id.txtMessage)
    }

    class AssistantViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtMessage: TextView = view.findViewById(R.id.txtMessage)
    }

    // ── Adapter overrides ──────────────────────────────────────────────────

    override fun getItemViewType(position: Int) =
        if (messages[position].isUser) TYPE_USER else TYPE_ASSISTANT

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_USER) {
            UserViewHolder(inflater.inflate(R.layout.item_message_user, parent, false))
        } else {
            AssistantViewHolder(inflater.inflate(R.layout.item_message_assistant, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        when (holder) {
            is UserViewHolder      -> holder.txtMessage.text = message.content
            is AssistantViewHolder -> holder.txtMessage.text = message.content
        }
    }

    override fun getItemCount() = messages.size

    // ── Public helpers ─────────────────────────────────────────────────────

    fun addMessage(message: UiChatMessage) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    fun clearMessages() {
        messages.clear()
        notifyDataSetChanged()
    }
}
