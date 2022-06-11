package com.example.androidexperiments

import android.app.Dialog
import android.graphics.*
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.util.Log
import android.util.Patterns.EMAIL_ADDRESS
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.squareup.picasso.Picasso

private const val TAG = "TextWatcherTag"

class MainActivity : AppCompatActivity() {

    private companion object{
        const val URL =
            "https://zavistnik.com/wp-content/uploads/2020/03/Android-knigi-zastavka.jpg"
    }

    private lateinit var textInputEditText: TextInputEditText

    private val textWatcher: TextWatcher = object : SimpleTextWatcher() {
        override fun afterTextChanged(p0: Editable?) {
            Log.d(TAG,"afterTextChanged $p0")
            val input = p0.toString()
            if(input.endsWith("@g")){
                Log.d(TAG,"programmatically set text")
                setText("${input}mail.com")
            }
        }
    }

    private fun setText(text: String){
        textInputEditText.removeTextChangedListener(textWatcher)
        textInputEditText.setTextCorrectly(text)
        textInputEditText.addTextChangedListener(textWatcher)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val agreementTextView = findViewById<TextView>(R.id.agreementTextView)
        val fullText = getString(R.string.agreement_full_text)
        val confidential = getString(R.string.confidential_info)
        val policy = getString(R.string.privacy_policy)
        val spannableString = SpannableString(fullText)
        val downloadedImage = findViewById<ImageView>(R.id.image_view_for_download)
        val image = findViewById<ImageView>(R.id.glide_image_view)
        val textInputLayout = findViewById<TextInputLayout>(R.id.textInputLayout)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val checkBox = findViewById<CheckBox>(R.id.checkBox)
        val processBar = findViewById<ProgressBar>(R.id.progressBar)
        val contentLayout = findViewById<ConstraintLayout>(R.id.constraintLayout)
        textInputEditText = textInputLayout.editText as TextInputEditText
        textInputEditText.addTextChangedListener(textWatcher)

        downloadedImage.loadGlide(URL)
        image.loadPicasso(URL)

        loginButton.isEnabled = false
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            loginButton.isEnabled = true
        }

        loginButton.setOnClickListener{
            if(EMAIL_ADDRESS.matcher(textInputEditText.text.toString()).matches()){
                hideKeyboard(textInputEditText)
                loginButton.isEnabled = false
                contentLayout.visibility = View.GONE
                processBar.visibility = View.VISIBLE
                Snackbar.make(loginButton,"Go ti postLogin", Snackbar.LENGTH_LONG).show()
                Handler(Looper.myLooper()!!).postDelayed({
                    contentLayout.visibility = View.VISIBLE
                    processBar.visibility = View.GONE
                    val dialog = Dialog(this)
                    val view = LayoutInflater.from(this).inflate(R.layout.dialog, contentLayout,false)
                    dialog.setCancelable(false)
                    view.findViewById<View>(R.id.closeButton).setOnClickListener{
                        dialog.dismiss()
                    }
                    dialog.setContentView(view)
                    dialog.show()
                },3000)
                } else {
                    textInputLayout.isErrorEnabled = true
                    textInputLayout.error = getString(R.string.invalid_email_message)
            }
        }

        textInputEditText.listenChanges { textInputLayout.isErrorEnabled = false }

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

    private fun TextInputEditText.setTextCorrectly(text:CharSequence){
        setText(text)
        setSelection(text.length)
    }

    fun TextInputEditText.listenChanges(block:(text:String) -> Unit) {
        addTextChangedListener(object : SimpleTextWatcher(){
            override fun afterTextChanged(p0: Editable?) {
                block.invoke(p0.toString())
            }
        })
    }

    fun AppCompatActivity.hideKeyboard(view: View){
        val imm = this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(textInputEditText.windowToken, 0)
    }

}



