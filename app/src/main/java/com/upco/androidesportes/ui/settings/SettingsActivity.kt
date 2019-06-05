package com.upco.androidesportes.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.upco.androidesportes.R
import com.upco.androidesportes.ui.common.BaseActivity
import kotlinx.android.synthetic.main.app_bar_news.*

/**
 * Possibilita modificar as configurações da aplicação.
 */
class SettingsActivity: BaseActivity() {

    /**
     * Método chamado quando a activity está iniciando,
     * é aqui que o código de inicialização deve ir.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* Define o layout dessa activity */
        setContentView(R.layout.activity_settings)

        /* Configura a Toolbar */
        setupToolbar(getString(R.string.settings_activity_title), true)

        /* Carrega o SettingsFragment no lugar do FrameLayout definido no código estático */
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.fl_settings, SettingsFragment())
                .commit()
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