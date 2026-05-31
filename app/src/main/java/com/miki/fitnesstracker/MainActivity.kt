package com.miki.fitnesstracker

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.work.WorkManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.miki.fitnesstracker.room.FitnessViewModel
import com.miki.fitnesstracker.service.StepCounterService
import com.miki.fitnesstracker.worker.ResetDailyDataWorker
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private val viewModel: FitnessViewModel by viewModels()
    private lateinit var navController: NavController
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (checkSelfPermission(Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.ACTIVITY_RECOGNITION), 101)
            } else {
                startStepService()
            }
        } else {
            startStepService()
        }

        setupNavigation()
        scheduleDailyReset()
    }

    private fun startStepService() {
        val serviceIntent = Intent(this, StepCounterService::class.java)
        startService(serviceIntent)
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        bottomNav = findViewById(R.id.bottom_navigation)
        bottomNav.setupWithNavController(navController)

        val topLevelDestinations = setOf(
            R.id.homeFragment,
            R.id.profileFragment
        )
        val appBarConfiguration = AppBarConfiguration(topLevelDestinations)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.signInFragment, R.id.signUpFragment, R.id.splashFragment -> bottomNav.visibility = View.GONE
                else -> bottomNav.visibility = View.VISIBLE
            }
        }
    }

    private fun scheduleDailyReset() {
        val now = Calendar.getInstance()
        val nextMidnight = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            add(Calendar.DAY_OF_YEAR, 1)
        }

        val delay = nextMidnight.timeInMillis - now.timeInMillis

        val request = androidx.work.PeriodicWorkRequestBuilder<ResetDailyDataWorker>(
            1, java.util.concurrent.TimeUnit.DAYS
        ).build()

        WorkManager.getInstance(this).enqueue(request)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startStepService()
            } else {
                Toast.makeText(this, "Permission denied for step counter", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
