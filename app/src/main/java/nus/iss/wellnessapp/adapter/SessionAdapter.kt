package nus.iss.wellnessapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import nus.iss.wellnessapp.databinding.ItemSessionBinding
import nus.iss.wellnessapp.model.ChatSessionResponse
// Author : Htet Nandar

class SessionAdapter(
    private val onSessionClick: (ChatSessionResponse) -> Unit,
    private val onDeleteClick: (ChatSessionResponse) -> Unit
) : RecyclerView.Adapter<SessionAdapter.SessionViewHolder>() {

    private val sessions = mutableListOf<ChatSessionResponse>()

    fun setSessions(list: List<ChatSessionResponse>) {
        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize() = sessions.size
            override fun getNewListSize() = list.size
            override fun areItemsTheSame(oldPos: Int, newPos: Int) =
                sessions[oldPos].id == list[newPos].id
            override fun areContentsTheSame(oldPos: Int, newPos: Int) =
                sessions[oldPos] == list[newPos]
        })
        sessions.clear()
        sessions.addAll(list)
        diff.dispatchUpdatesTo(this)
    }

    fun removeSession(sessionId: Long) {
        val index = sessions.indexOfFirst { it.id == sessionId }
        if (index != -1) {
            sessions.removeAt(index)
            notifyItemRemoved(index)
        }
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

            binding.txtSessionDate.text = if (session.createdAt.size >= 3) {
                val year  = session.createdAt[0]
                val month = session.createdAt[1]
                val day   = session.createdAt[2]
                "%04d-%02d-%02d".format(year, month, day)
            } else {
                ""
            }

            binding.root.setOnClickListener { onSessionClick(session) }
            binding.btnDeleteSession.setOnClickListener { onDeleteClick(session) }
        }
    }
}
