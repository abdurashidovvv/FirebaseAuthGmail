package com.abdurashidov.firebaseauthgmail

import android.content.Intent
import android.os.Bundle
import android.util.JsonToken
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.abdurashidov.firebaseauthgmail.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var googleSignInClient:GoogleSignInClient
    private  val TAG = "MainActivity"
    var RC_SIGN_IN=1
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val gso=GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient=GoogleSignIn.getClient(this, gso)
        auth= FirebaseAuth.getInstance()

        binding.signInBtn.setOnClickListener {
            signIn()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==RC_SIGN_IN){
            val task=GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account=task.getResult(ApiException::class.java)
                Log.d(TAG, "firebaseAuthWithGoogle: ${account.id}")
                firebaseAuthWithGoogle(account.idToken!!)
            }catch (e:Exception){
                Log.d(TAG, "onActivityResult: ${e.message}")
            }
        }
    }


    private fun firebaseAuthWithGoogle(idToken: String){
        val credential=GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this){ task->
                if (task.isSuccessful){
                    Log.d(TAG, "firebaseAuthWithGoogle: success")
                    val user=auth.currentUser
//                    updateUI(user)
                    Toast.makeText(this, "Yeah, you sign in my first App", Toast.LENGTH_SHORT).show()
                }else{
                    Log.d(TAG, "firebaseAuthWithGoogle: failure")
//                    updateUI(null)
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun signIn(){
        val signInIntent=googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
}