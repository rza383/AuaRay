package kz.rza383.auaray.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kz.rza383.auaray.model.WeatherItem
import kz.rza383.auaray.databinding.WeatherItemBinding

class ForecastAdapter: ListAdapter<WeatherItem,
        ForecastAdapter.ForecastViewHolder>(DiffCallback) {
    class ForecastViewHolder(private var binding:
                             WeatherItemBinding):
        RecyclerView.ViewHolder(binding.root){
            fun bind(forecast: WeatherItem){
                binding.apply {
                    date = forecast.date
                    rainChance = forecast.chanceOfRain
                    tMax = forecast.tempMax.toString()
                    tMin = forecast.tempMin.toString()
                    sunset = forecast.sunsetTime
                    sunrise = forecast.sunriseTime
                }
                binding.executePendingBindings()
            }
        }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ForecastViewHolder {
        return ForecastViewHolder(WeatherItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false))
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        val weatherForecast = getItem(position)
        holder.bind(weatherForecast)
    }

    companion object DiffCallback: DiffUtil.ItemCallback<WeatherItem>() {
        override fun areItemsTheSame(oldItem: WeatherItem, newItem: WeatherItem): Boolean {
            return oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: WeatherItem, newItem: WeatherItem): Boolean {
            return oldItem == newItem
        }

    }
}