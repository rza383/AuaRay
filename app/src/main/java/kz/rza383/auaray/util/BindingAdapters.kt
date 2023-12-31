package kz.rza383.auaray.util

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.loadinganimation.LoadingAnimation
import kz.rza383.auaray.R
import kz.rza383.auaray.model.WeatherItem
import kz.rza383.auaray.ui.ForecastAdapter
import kz.rza383.auaray.ui.WeatherApiStatus

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView,
                     data: List<WeatherItem>?){
    val adapter = recyclerView.adapter as ForecastAdapter
    adapter.submitList(data)
}

@BindingAdapter("setDayTimeAnimation")
fun bindAnimation(view:LottieAnimationView,
                  isDay: Int){
    when(isDay){
        1 -> view.setAnimation(R.raw.day_animation)
        else -> view.setAnimation(R.raw.night_animation)
    }
}

@BindingAdapter("setTextFromViewModel")
fun bindText(view: TextView,
             @StringRes resId: Int?) {
    resId ?: return
    if (resId == ResourcesCompat.ID_NULL){
        view.text = ""
    } else
        view.setText(resId)
}

@BindingAdapter("constraintLayoutVisibilityCondition")
fun bindConstraintGroupVisibility(group: ConstraintLayout,
                                  permGranted: Boolean) {
    when(permGranted) {
        false -> group.visibility = ConstraintLayout.GONE
        true -> group.visibility = ConstraintLayout.VISIBLE
    }
}

@BindingAdapter("viewVisibilityCondition")
fun bindVisibilityCondition(view: View,
                            status: WeatherApiStatus?){
    when(status){
        WeatherApiStatus.DONE -> view.visibility = View.VISIBLE
        else -> view.visibility = View.GONE
    }

}

@BindingAdapter("weatherApiStatusLoading")
fun bindStatusLoading(spinner: LoadingAnimation,
                      status: WeatherApiStatus?) {
    when (status) {
        WeatherApiStatus.LOADING -> {
            spinner.visibility = View.VISIBLE
        }
        else -> spinner.visibility = View.GONE
    }
}


@BindingAdapter("weatherApiStatusError")
fun bindStatusError(statusImageView: ImageView,
               status: WeatherApiStatus?){
    when(status){
        WeatherApiStatus.ERROR -> {
            statusImageView.visibility = View.VISIBLE
        }
        else -> statusImageView.visibility = View.GONE
    }
}