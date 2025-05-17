package com.noureddine.kotlin2.fragment

import android.annotation.SuppressLint
import android.content.Context
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.postDelayed
import com.noureddine.kotlin2.R
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.noureddine.kotlin2.Interface.FragmentNavigationListener
import com.noureddine.kotlin2.activity.MainActivity
import kotlin.jvm.java

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class SplashSecreenFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private var navigationListener: FragmentNavigationListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_splash_secreen, container, false)

        val auth = Firebase.auth

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({

            if (auth.currentUser != null){
                var  i = Intent(activity, MainActivity::class.java)
                startActivity(i)
                requireActivity().finish()

            }else{
                navigationListener?.toFragment(RegisterFragment())
            }
        }, 3000)

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
        fun newInstance(param1: String, param2: String) = SplashSecreenFragment().apply {
            arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}