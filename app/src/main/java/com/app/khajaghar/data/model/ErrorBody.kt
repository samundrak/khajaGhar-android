import android.widget.Toast
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.lang.Exception


class APIError(
        @SerializedName("error")
        val error: String? = null,
        @SerializedName("message")
        val message: String? = null
)

object ErrorUtils {
    fun parseError(exception: HttpException): String {
        val gson = GsonBuilder().create()
        var mError = APIError()
        try {
            mError = gson.fromJson(exception.response()!!.errorBody()!!.string(), APIError::class.java)
        } catch (e: IOException) {
            // handle failure to read error
        }
        return mError.message!!
    }
}