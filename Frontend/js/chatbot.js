function toggleAIChat() {
    const panel = document.getElementById("aiChatPanel");
    panel.style.display = panel.style.display === "flex" ? "none" : "flex";
}

async function sendAIMessage() {
  const input = document.getElementById("aiInput");
  const messages = document.getElementById("aiMessages");

  const text = input.value.trim();
  if (!text) return;
  

  // user message
  const userMsg = document.createElement("div");
  userMsg.className = "ai-msg user";
  userMsg.innerText = text;
  messages.appendChild(userMsg);

  input.value = "";

  //loading
  const loadingMsg = document.createElement("div");
  loadingMsg.className = "ai-msg bot";
  loadingMsg.innerText = "🤖 Đang suy nghĩ... Bạn đợi mình một xíu nhé!";
  messages.appendChild(loadingMsg);
  messages.scrollTop = messages.scrollHeight;

  try {
    const res = await fetch("http://localhost:8081/beefchef/chat", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
        message: text
      })
    });

    const reply = await res.text(); 

    loadingMsg.remove();

    const botMsg = document.createElement("div");
    botMsg.className = "ai-msg bot";
    botMsg.innerHTML = formatMessage(reply);

    messages.appendChild(botMsg);
    messages.scrollTop = messages.scrollHeight;

  } catch (err) {
    loadingMsg.innerText = "❌ Lỗi kết nối server";
    console.error("Lỗi:", err);
  }
}

function formatMessage(text) {
  return text.replace(/\*\*(.*?)\*\*/g, "<b>$1</b>");
}