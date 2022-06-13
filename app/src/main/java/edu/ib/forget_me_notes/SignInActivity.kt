package edu.ib.forget_me_notes

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        signup_btn_link.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        login_btn.setOnClickListener {
            loginUser()
        }
    }

    private fun loginUser() {

        val email = email_login.text.toString()
        val password = password_login.text.toString()

        when {
            TextUtils.isEmpty(email) -> Toast.makeText(this, "email is required", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(password) -> Toast.makeText(this, "password is required", Toast.LENGTH_LONG).show()

            else -> {
                val progressDialog = ProgressDialog(this@SignInActivity)
                progressDialog.setTitle("SignIn")
                progressDialog.setMessage("Pleas wait, this may take a while...")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val mAuth = FirebaseAuth.getInstance()
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        progressDialog.dismiss()

                        val intent = Intent(this@SignInActivity, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    } else {
                        val message = task.exception!!.toString()
                        Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()

                        FirebaseAuth.getInstance().signOut()
                        progressDialog.dismiss()
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        if (FirebaseAuth.getInstance().currentUser != null) {
            val intent = Intent(this@SignInActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}