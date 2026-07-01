package nus.iss.wellnessapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import nus.iss.wellnessapp.databinding.ItemSessionBinding
import nus.iss.wellnessapp.model.ChatSessionResponse

class SessionAdapter(
    private val onSessionClick: (ChatSessionResponse) -> Unit
) : RecyclerView.Adapter<SessionAdapter.SessionViewHolder>() {

    private val sessions = mutableListOf<ChatSessionResponse>()

    fun setSessions(list: List<ChatSessionResponse>) {
        sessions.clear()
        sessions.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        val binding = ItemSessionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SessionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        holder.bind(sessions[position])
    }

    override fun getItemCount() = sessions.size

    inner class SessionViewHolder(
        private val binding: ItemSessionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(session: ChatSessionResponse) {
            binding.txtSessionTitle.text = session.title

            // Format createdAt: [year, month, day, hour, minute, ...]
            binding.txtSessionDate.text = if (session.createdAt.size >= 3) {
                val year  = session.createdAt[0]
                val month = session.createdAt[1]
                val day   = session.createdAt[2]
                "%04d-%02d-%02d".format(year, month, day)
            } else {
                ""
            }

            binding.root.setOnClickListener { onSessionClick(session) }
        }
    }
}
