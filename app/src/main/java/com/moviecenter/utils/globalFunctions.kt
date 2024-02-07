package com.app.moviecenter.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

fun manageViews(vararg views: View, mode: String) {
    for (view in views) {
        if (mode == "GONE") {
            view.visibility = View.GONE
        } else {
            view.visibility = View.VISIBLE
        }
    }
}

fun closeKeyboard(context: Context, view: View) {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}