package com.upco.androidesportes.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.upco.androidesportes.ui.news.NewsActivity

/**
 * Exibe a tela de splash da aplicação e então inicializa a [NewsActivity].
 */
class SplashActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa a NewsActivity e então finaliza essa activity
        val intent = Intent(this, NewsActivity::class.java)
        startActivity(intent)
        finish()
    }
}