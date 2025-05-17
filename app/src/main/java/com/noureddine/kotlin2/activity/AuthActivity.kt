package com.noureddine.kotlin2.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.google.firebase.auth.FirebaseAuth
import com.noureddine.kotlin2.Interface.FragmentNavigationListener
import com.noureddine.kotlin2.R
import com.noureddine.kotlin2.fragment.SigninFragment
import com.noureddine.kotlin2.fragment.SplashSecreenFragment

class AuthActivity : AppCompatActivity() , FragmentNavigationListener{


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_auth)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainerView, SplashSecreenFragment())
                .commit()
        }

    }

    override fun toFragment(fragment: Fragment){
        //removeFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, fragment)
            //.addToBackStack(null)  // Add to back stack to allow back navigation
            .commit()
    }

    fun removeFragment(){
        val fragmentToRemove = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
        fragmentToRemove?.let {
            supportFragmentManager.beginTransaction()
                .remove(it)
                .commit()
        }

    }















}