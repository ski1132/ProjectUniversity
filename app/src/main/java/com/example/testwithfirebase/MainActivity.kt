package com.example.testwithfirebase

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    var onPermissionListener: OnPermissionListener? =null
    var REQUEST_CODE_ASK_PERMISSIONS = 123 // not fix number

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction().add(R.id.contentMainFrag,MainFragment()).commit()
    }
    fun checkPermission(permission: String, onPermissionListener: OnPermissionListener): AppCompatActivity {
        this.onPermissionListener = onPermissionListener // take stucture edited in MainFragment to this
        check(permission)
        return this
    }

    @SuppressLint("WrongConstant")
    fun check(permission: String) {
        val hasPermission: Int
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            hasPermission = checkSelfPermission(permission)
            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(permission), REQUEST_CODE_ASK_PERMISSIONS)
            } else {
                if (onPermissionListener != null) {
                    onPermissionListener?.onGrantedPermission()
                }
            }
        } else {
            if (onPermissionListener != null) {
                onPermissionListener?.onGrantedPermission()
            }
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_ASK_PERMISSIONS -> if (grantResults[0] === PackageManager.PERMISSION_GRANTED) {
                if (onPermissionListener != null) {
                    onPermissionListener?.onGrantedPermission()
                }
            } else {
                if (onPermissionListener != null) {
                    onPermissionListener?.onDeniedPermission()
                }
            }
        }
    }
    interface OnPermissionListener{
        fun onGrantedPermission()
        fun onDeniedPermission()
    }

}
