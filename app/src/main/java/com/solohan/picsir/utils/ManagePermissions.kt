package com.solohan.picsir.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.widget.Toast

class ManagePermissions(val activity: Activity, val list: List<String>, val code: Int){
    fun checkPermissions(){
        if(isPermissionGranted() != PackageManager.PERMISSION_GRANTED){
            showAlert()
        }else{
            activity.toast("권한이 이미 수락되었습니다.")
        }
    }

    fun lastCheckPermissions():Boolean{
        return isPermissionGranted() == PackageManager.PERMISSION_GRANTED
    }

    private fun isPermissionGranted():Int{
        // PERMISSION_GRANTED: Constant Value: 0
        // PERMISSION_DENIED: Constant Value: -1
        var counter = 0
        for (permission in list){
            counter += ContextCompat.checkSelfPermission(activity, permission)
        }
        return counter
    }

    private fun deniedPermission(): String{
        for(permission in list){
            if(ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_DENIED) return permission
        }
        return ""
    }

    private fun showAlert(){
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("권한이 필요합니다.")
        builder.setMessage("이 작업을 수행하기 위해 권한이 필요합니다.")
        builder.setPositiveButton("수락") { dialog, which -> requestPermissions() }
        builder.setNeutralButton("취소", null)
        val dialog = builder.create()
        dialog.show()
    }

    private fun requestPermissions(){
        val permission = deniedPermission()
        if(ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)){
            activity.toast("설명 필요")
        }else{
            ActivityCompat.requestPermissions(activity, list.toTypedArray(), code)
        }
    }

    fun processPermissionResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray):Boolean{
        var result = 0
        if(grantResults.isNotEmpty()){
            for(item in grantResults){
                result+=item
            }
        }
        if(result == PackageManager.PERMISSION_GRANTED) return true
        return false
    }
}

fun Context.toast(message: String){
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}