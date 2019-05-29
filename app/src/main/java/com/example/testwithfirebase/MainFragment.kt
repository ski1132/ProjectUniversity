package com.example.testwithfirebase


import android.Manifest
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.kotlinpermissions.KotlinPermissions
import kotlinx.android.synthetic.main.fragment_main.*
import me.dm7.barcodescanner.zxing.ZXingScannerView


class MainFragment : Fragment() {
    private var zXingScannerView: ZXingScannerView? = null
    private val PermissionsRequestCode = 123
    private lateinit var listPermission:List<String>
    private lateinit var managePermissions: ManagePermission
    private var mainActivity : MainActivity? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainActivity = activity as MainActivity

        listPermission = listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )


        btScan.setOnClickListener{
            activity?.let {
                KotlinPermissions.with(it) // where this is an FragmentActivity instance
                    .permissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA)
                    .onAccepted { permissions ->
                        scanQR()
                    }
                    .onDenied { permissions ->
                        Toast.makeText(
                            it, "Permission Denied",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    .onForeverDenied { permissions ->
                        Toast.makeText(
                            it, " Forever Denied",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    .ask()
            }

        }

    }
    fun scanQR(){
        zXingScannerView = ZXingScannerView(context)
        activity!!.setContentView(zXingScannerView)
        zXingScannerView!!.run {
            startCamera()
            Log.e("== on click ==","eiei")
            setResultHandler {
                Log.e("=== in Result Handle ==",it.toString())
                stopCamera()
                activity!!.setContentView(R.layout.activity_main)
                val resultString = it.text.toString()
                Toast.makeText(
                    activity, "QR code = $resultString",
                    Toast.LENGTH_LONG
                ).show()
                Log.d("12MarchV1", "QR code ==> $resultString")
                activity!!.supportFragmentManager.beginTransaction()
                    .replace(R.id.contentMainFrag, MainFragment()).commit()
            }
        }
    }

}