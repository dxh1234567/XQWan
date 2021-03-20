package cn.jj.base.baseclass

import android.app.Dialog
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDialog
import androidx.fragment.app.DialogFragment
import cn.jj.base.common.view.setNavigationBarTransparent
import cn.jj.base.common.view.setStatusBarFullTransparent
import cn.jj.basiclib.R

abstract class CenterDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = AppCompatDialog(context, R.style.CenterDialogStyle)
        dialog.setStatusBarFullTransparent()
        dialog.setNavigationBarTransparent()
        return dialog
    }

    override fun onStart() {
        dialog?.window?.addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        super.onStart()
        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
    }


}