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
import com.noureddine.kotlin2.Interface.FragmentNavigationListener
import com.noureddine.kotlin2.R
import com.noureddine.kotlin2.activity.MainActivity

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class SigninFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var inputEmail: TextInputEditText
    lateinit var inputPassword: TextInputEditText
    lateinit var layoutEmail: TextInputLayout
    lateinit var layoutPassword: TextInputLayout
    lateinit var register: TextView
    lateinit var forgetPassword: TextView
    lateinit var singin: MaterialButton

    private var navigationListener: FragmentNavigationListener? = null
    private var auth = Firebase.auth


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
        val v = inflater.inflate(R.layout.fragment_signin, container, false)

        inputEmail = v.findViewById(R.id.emailEditText)
        inputPassword = v.findViewById(R.id.passwordEditText)
        layoutEmail = v.findViewById(R.id.emailInputLayout)
        layoutPassword = v.findViewById(R.id.passwordInputLayout)
        register = v.findViewById(R.id.regesterPromptTextView)
        forgetPassword = v.findViewById(R.id.forgotPasswordTextView)
        singin = v.findViewById(R.id.registerButton)

        register.setOnClickListener {
            navigationListener?.toFragment(RegisterFragment())
        }

        singin.setOnClickListener {

            if (inputEmail.text?.isEmpty() != true && inputPassword.text?.isEmpty() != true){
                val email = inputEmail.text.toString()
                val password = inputPassword.text.toString()

                auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        Log.d("Auth", "User logged in: ${it.user?.email}")
                        Toast.makeText(context,"Login sucess", Toast.LENGTH_SHORT).show()

                        var  i = Intent(activity, MainActivity::class.java)
                        startActivity(i)
                        requireActivity().finish()

                    }
                    .addOnFailureListener { e ->
                        Log.e("Auth", "Login failed", e)
                        Toast.makeText(context,"Login failed "+e.message, Toast.LENGTH_SHORT).show()
                    }
            }else{
                layoutEmail.error = "Please enter a valid email"
                layoutPassword.error = "Please enter a valid password"
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
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SigninFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SigninFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}