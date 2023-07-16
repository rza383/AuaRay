package kz.rza383.auaray.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import kz.rza383.auaray.R
import kz.rza383.auaray.databinding.FragmentSettingsBinding
import java.util.Locale

private const val TAG = "Settings"
@AndroidEntryPoint
class SettingsFragment : Fragment(), AdapterView.OnItemSelectedListener {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var themeSwitcher: SwitchCompat
    private lateinit var spinner: Spinner
    private val sharedViewModel: CurrentWeatherViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("NewApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        themeSwitcher = binding.themeSwitcher
        spinner = binding.languageSpinner
        val lang = requireContext()
            .resources
            .configuration.
            locales.
            get(0).
            language
        val pos = if(lang == "kk") 0 else 1
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.language_choices,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
            spinner.adapter = adapter
            spinner.setSelection(pos, false)
            spinner.onItemSelectedListener = this
        }
        updateSwitch()
        themeSwitcher.setOnCheckedChangeListener {_, isChecked ->
            sharedViewModel.savePreference(isChecked)
            updateUI()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    @SuppressLint("NewApi")
    private fun updateUI(){
        themeSwitcher.apply {
            if(isChecked)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    @SuppressLint("NewApi")
    private fun updateSwitch(){
        themeSwitcher.apply {
            isChecked = sharedViewModel.getPreference()
        }
    }

    fun setLocale(locale: String){
        val localeList = LocaleListCompat.forLanguageTags(locale)
        AppCompatDelegate.setApplicationLocales(localeList)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        Log.d(TAG, id.toString())
        if(position == 0)
            setLocale("kk")
        else setLocale("en")
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }



}