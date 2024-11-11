package com.dicoding.asclepius.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.dicoding.asclepius.R
import com.dicoding.asclepius.database.History
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.repository.HistoryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private lateinit var historyRepository: HistoryRepository

    private var historyId: Int = 0
    private var imageUri: Uri = Uri.EMPTY
    private var analyzeLabel: String = ""
    private var analyzeScore: Float = 0F
    private var inferenceTime: Long = 0L
    private var date: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        historyRepository = HistoryRepository(application)

        historyId = intent.getIntExtra("HISTORY_ID", -1)

        imageUri = intent.getStringExtra("IMAGE_URI")?.toUri() ?: Uri.EMPTY
        analyzeLabel = intent.getStringExtra("ANALYZE_LABEL") ?: ""
        analyzeScore = intent.getFloatExtra("ANALYZE_SCORE", 0F)
        inferenceTime = intent.getLongExtra("ANALYZE_TIME", 0L)

        if (historyId == -1) {
            showData()
        } else {
            showFromDatabase(historyId)
        }
    }

    private fun showFromDatabase(historyId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val historyList = historyRepository.getHistory(historyId)

                imageUri = byteArrayToUri(baseContext, historyList.image, "TemporaryFile.jpg")
                analyzeLabel = historyList.label
                analyzeScore = historyList.score
                inferenceTime = historyList.inferenceTime
                date = historyList.date

                showData()
            } catch (e: Exception) {
                showToast("Gagal mengambil data")
            }
        }
    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun showData() {
        binding.resultImage.setImageURI(imageUri)

        var result = "Hasil Prediksi: ${analyzeLabel}\nSkor: ${
            String.format(
                "%.0f",
                analyzeScore * 100
            )
        }%\nDengan Waktu: ${inferenceTime}ms"
        if (date != null) {
            result += "\nTanggal Analisa : $date"
            binding.saveResultButton.visibility = View.GONE
        }

        binding.resultText.text = result

        if (analyzeLabel == "Cancer") {
            binding.resultCard.setCardBackgroundColor(ContextCompat.getColor(this, R.color.red))
        } else {
            binding.resultCard.setCardBackgroundColor(ContextCompat.getColor(this, R.color.green))
        }

        binding.saveResultButton.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                saveToDatabase(imageUri, analyzeLabel, analyzeScore, inferenceTime)
            }
        }
    }

    private fun byteArrayToUri(context: Context, byteArray: ByteArray, fileName: String): Uri {
        val file = File(context.cacheDir, fileName)

        try {
            val fos = FileOutputStream(file)
            fos.write(byteArray)
            fos.close()

            return Uri.fromFile(file)
        } catch (e: IOException) {
            showToast("Error menyimpan gambar sementara: ${e.message}")
            return Uri.EMPTY
        }
    }

    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    private suspend fun imageUriToByteArray(uri: Uri): ByteArray {
        return withContext(Dispatchers.IO) {
            val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri))
            bitmapToByteArray(bitmap)
        }
    }

    private suspend fun saveToDatabase(
        imageUri: Uri,
        label: String?,
        score: Float,
        inferenceTime: Long
    ) {
        if (label != null) {
            binding.progressIndicator.visibility = View.VISIBLE

            val dateFormat = SimpleDateFormat("EEEE, dd-MMMM-yyyy HH:mm:ss", Locale.getDefault())
            val date = dateFormat.format(Date())

            try {
                val imageByteArray = imageUriToByteArray(imageUri)

                val history = History(
                    label = label,
                    score = score,
                    inferenceTime = inferenceTime,
                    date = date,
                    image = imageByteArray
                )

                historyRepository.insert(history)

                binding.progressIndicator.visibility = View.GONE
                showToast("Hasil prediksi berhasil disimpan")
                binding.saveResultButton.visibility = View.GONE
            } catch (error: Exception) {
                binding.progressIndicator.visibility = View.GONE
                showToast("Gagal menyimpan hasil prediksi: ${error.message}")
            }
        } else {
            binding.progressIndicator.visibility = View.GONE
            showToast("Hasil prediksi tidak ada")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}