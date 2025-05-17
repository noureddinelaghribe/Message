package com.noureddine.kotlin2.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.values
import com.noureddine.kotlin2.Interface.FragmentNavigationListener
import com.noureddine.kotlin2.model.User
import com.noureddine.kotlin2.R
import com.noureddine.kotlin2.activity.MainActivity
import kotlin.jvm.java
import kotlin.random.Random

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class RegisterFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    lateinit var inputName: TextInputEditText
    lateinit var inputEmail: TextInputEditText
    lateinit var inputPassword: TextInputEditText
    lateinit var layoutName: TextInputLayout
    lateinit var layoutEmail: TextInputLayout
    lateinit var layoutPassword: TextInputLayout
    lateinit var singin: TextView
    lateinit var register: MaterialButton

    private var navigationListener: FragmentNavigationListener? = null
    private var auth = Firebase.auth
    private lateinit var database: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_register, container, false)

        inputName = v.findViewById(R.id.nameEditText)
        inputEmail = v.findViewById(R.id.emailEditText)
        inputPassword = v.findViewById(R.id.passwordEditText)
        layoutName = v.findViewById(R.id.nameInputLayout)
        layoutEmail = v.findViewById(R.id.emailInputLayout)
        layoutPassword = v.findViewById(R.id.passwordInputLayout)
        singin = v.findViewById(R.id.singinPromptTextView)
        register = v.findViewById(R.id.registerButton)

        database = FirebaseDatabase.getInstance().reference

        singin.setOnClickListener {
            navigationListener?.toFragment(SigninFragment())
        }

        register.setOnClickListener {
            val email = inputEmail.text.toString()
            val password = inputPassword.text.toString()
            val name = inputName.text.toString()

            if (name.isEmpty() != true && name.length>3){
                layoutName.error = null
                if (email.isEmpty() != true && password.isEmpty() != true){
                    layoutEmail.error = null
                    layoutPassword.error = null
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener {
                            Log.d("Auth", "User logged in: ${it.user?.email}")

                            val user = User(auth.uid.toString(),name,R.drawable.user1.toLong(),Random.nextInt(1000000),email,false)
                            database.child("users").child(auth.uid.toString()).setValue(user).addOnSuccessListener {

                                Toast.makeText(context,"Login sucess", Toast.LENGTH_SHORT).show()
                                navigationListener?.toFragment(PickImageFragment())

                            }.addOnFailureListener { e ->
                                Log.w("TAG", "Error writing data", e)
                                Toast.makeText(context,"Creat account failed", Toast.LENGTH_SHORT).show()
                            }

                        }
                        .addOnFailureListener { e ->
                            Log.e("Auth", "Login failed", e)
                            Toast.makeText(context,"Creat account failed "+e.message, Toast.LENGTH_SHORT).show()
                        }
                }else{
                    layoutEmail.error = "Please enter a valid email"
                    layoutPassword.error = "Please enter a valid password"
                }
            }else{
                layoutName.error = "Name cannot be empty"
            }

        }


        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Make sure the activity implements our interface
        if (context is FragmentNavigationListener) {
            navigationListener = context
        } else {
            throw RuntimeException("$context must implement FragmentNavigationListener")
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) = RegisterFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}