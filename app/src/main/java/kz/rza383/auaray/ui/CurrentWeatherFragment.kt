package kz.rza383.auaray.ui

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.location.CurrentLocationRequest
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kz.rza383.auaray.R
import kz.rza383.auaray.databinding.FragmentCurrentWeatherBinding
@AndroidEntryPoint

class CurrentWeatherFragment : Fragment() {

    private var _binding: FragmentCurrentWeatherBinding? = null
    private val binding get() = _binding!!
    private var currentTemperature: TextView? = null
    private var currentElevation: TextView? = null
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val permReqLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all {
                it.value
            }
            if (granted){
                sharedViewModel.getLocation()
            }
        }
    private val sharedViewModel: CurrentWeatherViewModel by activityViewModels()
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCurrentWeatherBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = sharedViewModel
        getLocation()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            currentTemperature = tvCurrentTemperature
            currentElevation = tvCurrentElevation
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            sharedViewModel.getLocation()
        }
        else requestPermissionToGetLocation()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestPermissionToGetLocation() {
       if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
           && shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.permissionTitle))
                    .setMessage(getString(R.string.permissionText))
                    .setPositiveButton(getString(R.string.permissionPositive)) { _, _ ->
                        permReqLauncher.launch(permissions)
                    }
                    .setNegativeButton(getString(R.string.persmissionNegative), null)
                    .show()
            }
            else
                permReqLauncher.launch(permissions)
    }

    companion object {
        private val permissions = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION)
    }
}