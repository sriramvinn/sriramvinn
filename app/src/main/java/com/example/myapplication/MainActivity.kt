package com.example.myapplication

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.ml.Efnet4
import org.tensorflow.lite.support.image.TensorImage


private const val MAX_RESULT_DISPLAY = 1


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    lateinit var bitmap: Bitmap
    lateinit var imgView: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imgView = findViewById(R.id.imageView)

        val tv:TextView = findViewById(R.id.textView)

        val select: Button = findViewById(R.id.button5)
        select.setOnClickListener(View.OnClickListener {
            val intent: Intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, 100)
        })

        val predict: Button = findViewById(R.id.button6)
        predict.setOnClickListener(View.OnClickListener {

            val resize: Bitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true)

            val model = Efnet4.newInstance(this)

// Creates inputs for reference.
            val tfImage = TensorImage.fromBitmap(bitmap)


// Runs model inference and gets result.
            val outputs = model.process(tfImage)
                .probabilityAsCategoryList.apply {
                    sortByDescending { it.score } // Sort with highest confidence first
                }.take(MAX_RESULT_DISPLAY) // take the top results


            tv.setText(outputs.toString())




// Releases model resources if no longer used.
            model.close()
        })
    }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)

            imgView.setImageURI(data?.data)

            var uri: Uri? = data?.data

            bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
        }
    }
