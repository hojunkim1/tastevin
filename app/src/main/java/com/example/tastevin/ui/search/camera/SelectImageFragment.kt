package com.example.tastevin.ui.search.camera

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.tastevin.databinding.FragmentSelectImageBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.FileNotFoundException
import java.io.InputStream

class SelectImageFragment : Fragment() {

    private lateinit var binding: FragmentSelectImageBinding
    private var imagePath: Uri? = null

    private var uri: Uri? = null
    private var bitmap: Bitmap? = null
    private var image: InputImage? = null
    private lateinit var recognizer: TextRecognizer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args: SelectImageFragmentArgs by navArgs()
        imagePath = Uri.parse(args.imageUri)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSelectImageBinding.inflate(inflater)

        imagePath?.let {
            binding.selectImage.setImageURI(it)
            setImage(it)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        binding.selectOcrButton.setOnClickListener {
            textRecognition(recognizer)
        }

        binding.selectRetryButton.setOnClickListener {
            val action = SelectImageFragmentDirections.actionNavigationSelectImageToCameraFragment()
            findNavController().navigate(action)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            uri = data?.data
            uri?.let { setImage(it) }
        }
    }

    private fun setImage(uri: Uri) {
        try {
            val `in`: InputStream? = requireActivity().contentResolver.openInputStream(uri)
            bitmap = BitmapFactory.decodeStream(`in`)
            bitmap = rotateBitmapIfNeeded(bitmap!!, uri)
            binding.selectImage.setImageBitmap(bitmap)

            bitmap?.let {
                image = InputImage.fromBitmap(it, 0)
            }
            Log.e("setImage", "이미지 to 비트맵")
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun rotateBitmapIfNeeded(bitmap: Bitmap, uri: Uri): Bitmap {
        val exif = ExifInterface(requireActivity().contentResolver.openInputStream(uri)!!)
        val orientation =
            exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

        val matrix = Matrix()

        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
        }

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun textRecognition(recognizer: TextRecognizer) {
        image?.let {
            recognizer.process(it)
                .addOnSuccessListener { visionText ->
                    Log.e("텍스트 인식", "성공")
                    val resultText = visionText.text
                    val action =
                        SelectImageFragmentDirections.actionNavigationSelectImageToSearchListFragment(
                            resultText,
                            "1"
                        )
                    findNavController().navigate(action)
                }
                .addOnFailureListener { e ->
                    Log.e("텍스트 인식", "실패: ${e.message}")
                    val resultText = "텍스트 인식 실패"
                    val action =
                        SelectImageFragmentDirections.actionNavigationSelectImageToSearchListFragment(
                            resultText,
                            "1"
                        )
                    findNavController().navigate(action)
                }
        }
    }
}