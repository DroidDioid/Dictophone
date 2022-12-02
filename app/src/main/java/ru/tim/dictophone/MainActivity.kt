package ru.tim.dictophone

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

/** Главная активность. Отвечает за навигацию по фрагментам. */
class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottom_navigation)

        val navController = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_container)!!
            .findNavController()

        bottomNavigationView.setupWithNavController(navController)
    }
}