package cn.jj.base.baseclass

import android.app.Dialog
import android.content.res.Configuration
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDialog
import androidx.fragment.app.DialogFragment
import cn.jj.base.common.view.setStatusBarFullTransparent
import cn.jj.basiclib.R

abstract class BottomDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val isLand = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        val theme =
            if (isLand) {
                R.style.RightDialogStyle
            } else {
                R.style.BottomDialogStyle
            }
        val dialog = AppCompatDialog(context, theme)
        dialog.setCancelable(true)
        dialog.setStatusBarFullTransparent()
        if (isLand) {
            dialog.window?.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        } else {
            dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        return dialog
    }

    override fun onStart() {
        dialog?.window?.addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        super.onStart()
        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
    }
}