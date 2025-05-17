package com.noureddine.kotlin2.Interface

import android.app.Activity
import androidx.fragment.app.Fragment

interface FragmentNavigationListener {
    fun toFragment(fragment: Fragment)
}