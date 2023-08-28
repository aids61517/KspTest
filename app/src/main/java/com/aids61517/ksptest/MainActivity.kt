package com.aids61517.ksptest

import android.os.Bundle
import android.view.LayoutInflater
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import com.aids61517.ksptest.databinding.ActivityMainBinding
import com.aids61517.processor.annotation.TestClassAnnotation
import com.aids61517.processor.annotation.TestClassAnnotationWithArgument

@TestClassAnnotationWithArgument("myAmazing")
interface MyAmazingFunction {
    val arg1: String
    val arg2: Map<String, List<*>>
}

@TestClassAnnotationWithArgument("myAmazing2")
interface MyAmazingFunction2 {
    val arg1: String
    val arg2: Map<String, List<*>>
}

@TestClassAnnotationWithArgument("myAmazing3")
interface MyAmazingFunction3 {
    val arg1: String
    val arg2: Map<String, List<*>>
}

@TestClassAnnotation
class MainActivity : ViewBindingActivity<ActivityMainBinding>() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): ActivityMainBinding {
        return ActivityMainBinding.inflate(inflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}