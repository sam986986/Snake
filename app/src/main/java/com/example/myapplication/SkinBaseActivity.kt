package com.example.myapplication

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.forEach
import androidx.lifecycle.ViewModel
import com.google.android.material.button.MaterialButton

/**
 * 切換主題顏色試做，仍有地方需要修改
 */
abstract class SkinBaseActivity : AppCompatActivity() {

    abstract fun viewModel(): ViewModel
    abstract fun initLayout(): View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = viewModel()
        if (viewModel is SkinBaseViewModel) {
            viewModel.theme.observe(this) {
                setTheme(initLayout())
            }
        }
    }

    private fun setTheme(view: View) {
        val typedValue = TypedValue()
        when (view) {
            //建議自定義View才切換顏色
            is ConstraintLayout -> {
                theme.resolveAttribute(R.attr.mainColor, typedValue, true)
                view.setBackgroundResource(typedValue.resourceId)

                view.forEach { setTheme(it) } // ViewGroup的要把裡面的View也都換色
            }

            is SwitchCompat -> {
                theme.resolveAttribute(R.attr.buttonColor, typedValue, true)
                view.thumbTintList = getColorStateList(typedValue.resourceId)
            }

            is MaterialButton -> {
                theme.resolveAttribute(R.attr.buttonColor, typedValue, true)
                view.backgroundTintList = getColorStateList(typedValue.resourceId)
            }

            is Toolbar -> {
                theme.resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValue, true)
                view.setBackgroundResource(typedValue.resourceId)
            }
        }
    }
}