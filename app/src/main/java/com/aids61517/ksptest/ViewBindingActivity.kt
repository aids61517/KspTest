package com.aids61517.ksptest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.aids61517.processor.annotation.TestClassAnnotation

@TestClassAnnotation
abstract class ViewBindingActivity<T : ViewBinding> : AppCompatActivity() {

    protected val binding: T
        get() = _binding ?: throw ViewBindingNotAvailableException(
            "View binding is only available after onCreate()."
        )
    private var _binding: T? = null

    abstract fun onCreateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onCreateViewBinding(
            layoutInflater,
            findViewById(android.R.id.content),
            savedInstanceState
        ).also {
            _binding = it
            setContentView(it.root)
        }
    }
}