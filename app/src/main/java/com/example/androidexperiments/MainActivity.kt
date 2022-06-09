package com.example.androidexperiments

import android.graphics.*
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso


class MainActivity : AppCompatActivity() {

    private companion object{
        const val URL =
            "https://zavistnik.com/wp-content/uploads/2020/03/Android-knigi-zastavka.jpg"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val agreementTextView = findViewById<TextView>(R.id.agreementTextView)
        val fullText = getString(R.string.agreement_full_text)
        val confidential = getString(R.string.confidential_info)
        val policy = getString(R.string.privacy_policy)
        val spannableString = SpannableString(fullText)
        val imageView = findViewById<ImageView>(R.id.image_view)
        val downloadedImage = findViewById<ImageView>(R.id.image_view_for_download)
        val image = findViewById<ImageView>(R.id.glide_image_view)
        downloadedImage.loadGlide(URL)
        image.loadPicasso(URL)

//        val netImage = NetImage(URL,object : ImageCallback {
//            override fun failed() {
//                Snackbar.make(imageView, "failed", Snackbar.LENGTH_LONG).show()
//            }
//
//            override fun success(bitmap: Bitmap) {
//                downloadedImage.setImageBitmap(bitmap)
//            }
//        })
//        netImage.start()

        val confidentialClickable = MyClickableSpan {
            Snackbar.make(it,"Go to link1", Snackbar.LENGTH_LONG).show()
        }

        val policyClickable = MyClickableSpan {
            Snackbar.make(it,"Go to link2", Snackbar.LENGTH_LONG).show()
        }

        spannableString.setSpan(
            confidentialClickable,
            fullText.indexOf(confidential),
            fullText.indexOf(confidential)+confidential.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannableString.setSpan(
            policyClickable,
            fullText.indexOf(policy),
            fullText.indexOf(policy)+policy.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        agreementTextView.run {
            text = spannableString
            movementMethod = LinkMovementMethod.getInstance()
            highlightColor = Color.TRANSPARENT
        }

    }

    private fun ImageView.loadGlide(url:String){
        Glide.with(this).load(url).circleCrop().into(this)
    }

    private fun ImageView.loadPicasso(url:String){
        Picasso.get().load(url).transform(CircleTransform()).into(this)
    }
}