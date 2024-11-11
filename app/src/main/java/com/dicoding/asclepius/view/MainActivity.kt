package com.dicoding.asclepius.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.yalantis.ucrop.UCrop
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(currentImageUri == null) {
            binding.analyzeButton.visibility = View.GONE
        }

        binding.galleryButton.setOnClickListener {
            startGallery()
        }

        binding.analyzeButton.setOnClickListener {
            analyzeImage()
        }

        binding.historyButton.setOnClickListener {
            moveToHistory()
        }

        binding.newsButton.setOnClickListener {
            moveToNews()
        }
    }

    private val uCropContract = object : ActivityResultContract<List<Uri>, Uri>() {
        override fun createIntent(context: Context, input: List<Uri>): Intent {
            val inputUri = input[0]
            val outputUri = input[1]

            val uCrop = UCrop.of(inputUri, outputUri)

            return uCrop.getIntent(context)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri {
            return if (resultCode == RESULT_OK && intent != null) {
                UCrop.getOutput(intent)!!
            } else {
                Uri.EMPTY
            }
        }
    }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let {
                val outputUri = File(filesDir, "croppedImage.jpg").toUri()

                val listUri = listOf<Uri>(uri, outputUri)
                cropImage.launch(listUri)
            } ?: showToast("Tidak ada gambar dipilih")
        }

    private val cropImage = registerForActivityResult(uCropContract) { uri ->
        if (uri != Uri.EMPTY) {
            currentImageUri = uri
            showImage()
        } else {
            showToast("Proses cropping dibatalkan")
        }
    }

    private fun startGallery() {
        galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun showImage() {
        currentImageUri?.let {
            binding.previewImageView.setImageURI(null)
            binding.previewImageView.setImageDrawable(null)
            binding.previewImageView.setImageURI(currentImageUri)
            binding.analyzeButton.visibility = View.VISIBLE
        } ?: showToast("Gambar tidak ditemukan")
    }

    private fun analyzeImage() {
        try {
            currentImageUri?.let { uri ->
                binding.progressIndicator.visibility = View.VISIBLE
                binding.progressIndicator.isIndeterminate = true

                val classifierHelper = ImageClassifierHelper(
                    context = this,
                    classifierListener = object : ImageClassifierHelper.ClassifierListener {
                        override fun onError(error: String) {
                            binding.progressIndicator.visibility = View.GONE
                            showToast("Error: $error")
                        }

                        override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                            binding.progressIndicator.visibility = View.GONE
                            results?.firstOrNull()?.categories?.firstOrNull()?.let { category ->
                                moveToResult(uri, category.label, category.score, inferenceTime)
                            } ?: showToast("Gagal melakukan klasifikasi")
                        }
                    }
                )
                classifierHelper.classifyStaticImage(uri)
            } ?: showToast("Pilih gambar")
        } catch (e: UnsatisfiedLinkError) {
            binding.progressIndicator.visibility = View.GONE
            showToast("Perangkat tidak mendukung TensorFlow Lite")
        } catch (e: Exception) {
            binding.progressIndicator.visibility = View.GONE
            showToast("Terjadi kesalahan saat load model")
        }
    }

    private fun moveToResult(imageUri: Uri, label: String, score: Float, inferenceTime: Number) {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra("IMAGE_URI", imageUri.toString())
        intent.putExtra("ANALYZE_LABEL", label)
        intent.putExtra("ANALYZE_SCORE", score)
        intent.putExtra("ANALYZE_TIME", inferenceTime)
        startActivity(intent)
    }

    private fun moveToHistory() {
        val intent = Intent(this, HistoryActivity::class.java)
        startActivity(intent)
    }

    private fun moveToNews() {
        val intent = Intent(this, NewsActivity::class.java)
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}