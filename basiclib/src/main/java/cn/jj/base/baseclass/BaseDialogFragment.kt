package cn.jj.base.baseclass

import android.app.Dialog
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDialog
import androidx.fragment.app.DialogFragment
import cn.jj.basiclib.R

abstract class BaseDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = AppCompatDialog(context, R.style.DialogStyle_Alpha)
        dialog.setCancelable(true)
        dialog.window?.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        return dialog
    }

    override fun onStart() {
        dialog?.window?.addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        super.onStart()
        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
    }


}