package com.example.architecturesandbox.base

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

abstract class BaseActivity : AppCompatActivity() {

    override fun onResume() {
        super.onResume()
        println("BaseActivity")
        this.getToolbarInstance()?.let {
            it.title = ""
            this.initView(it)
        }
    }

    private fun initView(toolbar: Toolbar) = setSupportActionBar(toolbar)

    abstract fun getToolbarInstance(): Toolbar?

}