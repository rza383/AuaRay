package kz.rza383.auaray.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import kz.rza383.auaray.R
import kz.rza383.auaray.databinding.FragmentSettingsBinding

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var themeSwitcher: SwitchCompat
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
        updateSwitch()
        themeSwitcher.setOnCheckedChangeListener {_, isChecked ->
            sharedViewModel.savePreference(isChecked)
            updateUI()
        }
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


}