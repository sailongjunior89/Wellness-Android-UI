package nus.iss.wellnessapp.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import nus.iss.wellnessapp.R
import nus.iss.wellnessapp.adapter.ChatMessageAdapter
import nus.iss.wellnessapp.adapter.SessionAdapter
import nus.iss.wellnessapp.api.RetrofitClient
import nus.iss.wellnessapp.storage.TokenManager
import nus.iss.wellnessapp.databinding.ActivityChatBinding
import nus.iss.wellnessapp.model.ChatRequest
import nus.iss.wellnessapp.model.UiChatMessage
import retrofit2.HttpException
// Author : Htet Nandar
class ChatActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_SESSION_ID    = "extra_session_id"
        const val EXTRA_SESSION_TITLE = "extra_session_title"
    }

    private lateinit var binding: ActivityChatBinding
    private lateinit var chatAdapter: ChatMessageAdapter
    private lateinit var sessionAdapter: SessionAdapter
    private lateinit var drawerToggle: ActionBarDrawerToggle

    private val userId: Long get() = TokenManager.getUserId()
    private var sessionId: Long = -1L
    private var isFirstMessage: Boolean = true  // tracks whether session exists yet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupDrawer()
        setupBottomNav()
        setupChatRecyclerView()
        setupInput()

        val existingSessionId = intent.getLongExtra(EXTRA_SESSION_ID, -1L)
        if (existingSessionId != -1L) {
            // Opening an existing session from drawer
            sessionId = existingSessionId
            isFirstMessage = false
            supportActionBar?.title = intent.getStringExtra(EXTRA_SESSION_TITLE) ?: "Wellness Chat"
            setInputEnabled(false)
            loadExistingMessages()
        } else {
            // New chat — show greeting locally, no API call yet
            isFirstMessage = true
            supportActionBar?.title = "New Chat"
            chatAdapter.addMessage(
                UiChatMessage(
                    content = "Hi! I'm your wellness assistant. Ask me anything about nutrition, sleep, exercise, or mental health.",
                    isUser  = false
                )
            )
            setInputEnabled(true)
        }
    }

    // ── Toolbar + hamburger ────────────────────────────────────────────────

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun setupDrawer() {
        drawerToggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.drawer_open,
            R.string.drawer_close
        )
        binding.drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        binding.toolbar.post {
            val navBtn = binding.toolbar.getChildAt(0)
            val dp16 = (16 * resources.displayMetrics.density).toInt()
            navBtn?.setPadding(dp16, dp16, dp16, dp16)
            navBtn?.minimumWidth  = (56 * resources.displayMetrics.density).toInt()
            navBtn?.minimumHeight = (56 * resources.displayMetrics.density).toInt()
        }

        sessionAdapter = SessionAdapter(
            onSessionClick = { session ->
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                if (session.id != sessionId) {
                    sessionId = session.id
                    isFirstMessage = false
                    supportActionBar?.title = session.title
                    chatAdapter.clearMessages()
                    setInputEnabled(false)
                    loadExistingMessages()
                }
            },
            onDeleteClick = { session ->
                AlertDialog.Builder(this)
                    .setTitle("Delete conversation")
                    .setMessage("Delete \"${session.title}\"? This cannot be undone.")
                    .setPositiveButton("Delete") { _, _ -> deleteSession(session.id) }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        )
        binding.recyclerSessions.layoutManager = LinearLayoutManager(this)
        binding.recyclerSessions.adapter = sessionAdapter

        binding.drawerLayout.addDrawerListener(object : androidx.drawerlayout.widget.DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerOpened(drawerView: View) {
                loadSessionsIntoDrawer()
            }
        })

        binding.btnNewChat.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            startNewChat()
        }
    }

    // ── Bottom Navigation ──────────────────────────────────────────────────

    private fun setupBottomNav() {
        binding.bottomNav.selectedItemId = R.id.nav_chat
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_chat      -> true
                R.id.nav_dashboard -> { finish(); false }
                else               -> false
            }
        }
    }

    // ── Chat RecyclerView ──────────────────────────────────────────────────

    private fun setupChatRecyclerView() {
        chatAdapter = ChatMessageAdapter()
        binding.recyclerChat.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        binding.recyclerChat.adapter = chatAdapter
    }

    // ── Input ──────────────────────────────────────────────────────────────

    private fun setupInput() {
        binding.btnSend.setOnClickListener { sendMessage() }
        binding.etMessage.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) { sendMessage(); true } else false
        }
    }

    // ── Session management ─────────────────────────────────────────────────
    private fun startNewChat() {
        chatAdapter.clearMessages()
        sessionId = -1L
        isFirstMessage = true
        supportActionBar?.title = "New Chat"
        chatAdapter.addMessage(
            UiChatMessage(
                content = "Hi! I'm your wellness assistant. Ask me anything about nutrition, sleep, exercise, or mental health.",
                isUser  = false
            )
        )
        setInputEnabled(true)
    }

    // ── API calls ──────────────────────────────────────────────────────────

    private fun loadExistingMessages() {
        lifecycleScope.launch {
            try {
                val messages = RetrofitClient.chatApi.getMessages(sessionId)
                messages.forEach { msg ->
                    chatAdapter.addMessage(UiChatMessage(
                        content = msg.content,
                        isUser  = msg.senderRole == "user"     // fix: role, not senderRole
                    ))
                }
                scrollToBottom()
                setInputEnabled(true)
            } catch (e: Exception) {
                Log.e("ChatActivity", "loadMessages: ${e.message}")
                Toast.makeText(this@ChatActivity, "Failed to load messages", Toast.LENGTH_SHORT).show()
                setInputEnabled(true)
            }
        }
    }

    private fun deleteSession(targetSessionId: Long) {
        lifecycleScope.launch {
            try {
                RetrofitClient.chatApi.deleteSession(targetSessionId)
                sessionAdapter.removeSession(targetSessionId)
                // If the deleted session is currently open, start a new chat
                if (targetSessionId == sessionId) {
                    startNewChat()
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                }
            } catch (e: Exception) {
                Log.e("ChatActivity", "deleteSession: ${e.message}")
                Toast.makeText(this@ChatActivity, "Failed to delete conversation", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadSessionsIntoDrawer() {
        lifecycleScope.launch {
            try {
                val sessions = RetrofitClient.chatApi.getSessions()
                    .sortedByDescending { it.id }
                sessionAdapter.setSessions(sessions)
            } catch (e: Exception) {
                Log.e("ChatActivity", "loadSessions: ${e.message}")
            }
        }
    }

    private fun sendMessage() {
        val text = binding.etMessage.text.toString().trim()
        if (text.isEmpty()) return

        chatAdapter.addMessage(UiChatMessage(content = text, isUser = true))
        binding.etMessage.setText("")
        scrollToBottom()
        setInputEnabled(false)
        binding.txtTyping.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                // First message of a new chat → create session with the message text as title
                if (isFirstMessage) {
                    val title = text.take(50)   // cap at 50 chars
                    val session = RetrofitClient.chatApi.createChatSession(
                        title = title
                    )
                    sessionId = session.id
                    supportActionBar?.title = title
                    isFirstMessage = false
                }

                val response = RetrofitClient.chatApi.sendMessage(
                    sessionId = sessionId,
                    request   = ChatRequest(message = text)
                )
                chatAdapter.addMessage(UiChatMessage(content = response.reply, isUser = false))
                scrollToBottom()
            } catch (e: Exception) {
                val userMsg = friendlyError(e)
                chatAdapter.addMessage(UiChatMessage(content = userMsg, isUser = false))
                scrollToBottom()
                Log.e("ChatActivity", "sendMessage error: ${e.message}", e)
            } finally {
                setInputEnabled(true)
                binding.txtTyping.visibility = View.GONE
            }
        }
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    private fun scrollToBottom() {
        binding.recyclerChat.postDelayed({
            val last = chatAdapter.itemCount - 1
            if (last >= 0) binding.recyclerChat.smoothScrollToPosition(last)
        }, 100)
    }

    private fun setInputEnabled(enabled: Boolean) {
        binding.etMessage.isEnabled = enabled
        binding.btnSend.isEnabled   = enabled
    }

    /** Returns a friendly message instead of raw HTTP errors. */
    private fun friendlyError(e: Exception): String = when {
        e is HttpException && e.code() in 500..599 ->
            "⚠️ The wellness AI service is currently unavailable. Please try again later."
        e is HttpException && e.code() == 401 ->
            "Your session has expired. Please log in again."
        e is HttpException && e.code() == 403 ->
            "You don't have permission to perform this action."
        e is java.net.ConnectException || e is java.net.SocketTimeoutException ||
        e is java.net.UnknownHostException ->
            "⚠️ Cannot reach the server. Please check your network connection."
        else ->
            "Something went wrong. Please try again."
    }
}
