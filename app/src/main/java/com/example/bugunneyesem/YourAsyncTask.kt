
import android.content.Context
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class FetchYemeklerTask(private val context: Context, private val textView: TextView) {

    fun execute(url: String, malzemeler: List<String>) {
        // Volley kuyruğunu başlat
        val requestQueue = Volley.newRequestQueue(context)

        // Gönderilecek JSON verisini oluştur
        val jsonObject = JSONObject()
        jsonObject.put("malzemeler", malzemeler)

        // JSON nesnesini POST isteği olarak gönder
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, jsonObject,
            Response.Listener { response ->
                // Yanıtı işle ve TextView'e yaz
                try {
                    val yemeklerArray = response.getJSONArray("onerilen_yemekler")
                    val yemeklerList = mutableListOf<String>()
                    for (i in 0 until yemeklerArray.length()) {
                        yemeklerList.add(yemeklerArray.getString(i))
                    }
                    textView.text = yemeklerList.joinToString("\n")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                // Hata durumunda ekrana bir mesaj yazdır
                error.printStackTrace()
                textView.text = "Bir hata oluştu: ${error.message}"
            })

        // Request'i kuyruğa ekle
        requestQueue.add(jsonObjectRequest)
    }
}