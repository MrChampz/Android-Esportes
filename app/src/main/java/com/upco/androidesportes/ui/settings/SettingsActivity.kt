package com.upco.androidesportes.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.upco.androidesportes.R
import kotlinx.android.synthetic.main.app_bar_news.*

/**
 * Possibilita modificar as configurações da aplicação.
 */
class SettingsActivity : AppCompatActivity() {

    /**
     * Método chamado quando a activity está iniciando,
     * é aqui que o código de inicialização deve ir.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* Define o layout dessa activity */
        setContentView(R.layout.activity_settings)

        /* Configura a Toolbar */
        setupToolbar()

        /* Carrega o SettingsFragment no lugar do FrameLayout definido no código estático */
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.fl_settings, SettingsFragment())
                .commit()
    }

    /**
     * Configura a Toolbar.
     */
    private fun setupToolbar() {
        /* Define o título da Toolbar */
        toolbar.title = getString(R.string.settings_activity_title)

        /* Define a Toolbar como supportActionBar dessa activity */
        setSupportActionBar(toolbar)

        /*
         * Define o que deve ser feito ao clicar no ícone de navegação.
         * Nesse caso, a activity será encerrada e a NewsActivity será exibida novamente.
         */
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    /**
     * Fragment que é carregado na activity e infla as views de acordo com as preferencias
     * definidas no código estático.
     */
    class SettingsFragment : PreferenceFragmentCompat() {

        /**
         * Chamado no [onCreate(Bundle)] para fornecer as preferencias ao fragment.
         * Deve chamar [setPreferenceScreen(PreferenceScreen)] diretamente ou através de
         * métodos auxiliares como [setPreferencesFromResource(Int, String)].
         */
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            /*
             * Infla as preferencias e substitui a hierarquia de preferencias atual
             * (se houver alguma).
             */
            setPreferencesFromResource(R.xml.preferences, rootKey)
        }
    }
}