package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.google.android.material.button.MaterialButton

/**
 * 切換主題顏色試做，仍有地方需要修改
 */

abstract class SkinBaseFragment : Fragment() {

    abstract fun viewModel(): ViewModel
    abstract fun initLayout(): View
    abstract fun context(): Context

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel = viewModel()
        if (viewModel is SkinBaseViewModel) {
            viewModel.theme.observe(viewLifecycleOwner) {
                setTheme(initLayout())
            }
        }
    }

    private fun setTheme(view: View) {
        val typedValue = TypedValue()
        when (view) {
            //建議自定義View才切換顏色
            is ConstraintLayout -> {
                context().theme.resolveAttribute(R.attr.mainColor, typedValue, true)
                view.setBackgroundResource(typedValue.resourceId)

                view.forEach { setTheme(it) } // ViewGroup的要把裡面的View也都換色
            }

            is SwitchCompat -> {
                context().theme.resolveAttribute(R.attr.buttonColor, typedValue, true)
                view.thumbTintList = context().getColorStateList(typedValue.resourceId)
            }

            is MaterialButton -> {
                context().theme.resolveAttribute(R.attr.buttonColor, typedValue, true)
                view.backgroundTintList = context().getColorStateList(typedValue.resourceId)
            }
        }
    }
}