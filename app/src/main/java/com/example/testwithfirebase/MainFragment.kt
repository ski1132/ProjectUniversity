package com.example.testwithfirebase

import android.Manifest
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.messages.Message
import com.google.android.gms.nearby.messages.MessageListener
import com.kotlinpermissions.KotlinPermissions
import kotlinx.android.synthetic.main.fragment_main.*
import me.dm7.barcodescanner.zxing.ZXingScannerView

class MainFragment : Fragment() {
    var mMessageListener: MessageListener? = null
    var mMessage: Message? = null
    private var zXingScannerView: ZXingScannerView? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        btScan.setOnClickListener {
            activity?.let { fragActivity ->
                KotlinPermissions.with(fragActivity) // where this is an FragmentActivity instance
                    .permissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.BLUETOOTH
                    ).onAccepted {
                        Toast.makeText(
                            fragActivity, "Permission Access",
                            Toast.LENGTH_LONG
                        ).show()
                        scanQR()
                    }.onDenied {
                        Toast.makeText(
                            fragActivity, "Permission Denied",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    .onForeverDenied {
                        Toast.makeText(
                            fragActivity, " Forever Denied",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    .ask()
            }
        }
        mMessageListener = object : MessageListener() {
            override fun onFound(message: Message?) {
                val content = message?.content?.toString(
                    Charsets.UTF_8
                )
                Log.e("Found message == : ", content)
            }

            override fun onLost(message: Message?) {
                Log.e("Lost message == : ", message.toString())
            }
        }

        val message = "Hello World".toByteArray(
            Charsets.UTF_8
        )
        mMessage = object : Message(
            message
        ){}
    }

    override fun onStart() {
        super.onStart()
        mMessage?.let { mMessage ->
            activity?.let {
                Nearby.getMessagesClient(it).publish(mMessage)
                Log.e(" onStart mMessage ==", mMessage.content.toString())
            }
        }
        mMessageListener?.let { mMessageListener ->
            activity?.let {
                Nearby.getMessagesClient(it).subscribe(mMessageListener)
                Log.e("onStart MessageListener", mMessage?.content.toString())
            }
        }
    }

    override fun onStop() {
        mMessage?.let { mMessage ->
            activity?.let {
                Nearby.getMessagesClient(it).unpublish(mMessage)
                Log.e(" onStop mMessage ==", Nearby.getMessagesClient(it).unpublish(mMessage).toString())
            }
        }
        mMessageListener?.let { mMessageListener ->
            activity?.let {
                Nearby.getMessagesClient(it).unsubscribe(mMessageListener)
                Log.e("onStop mMessageListener", Nearby.getMessagesClient(it).unsubscribe(mMessageListener).toString())
            }
        }
        super.onStop()
    }
    fun scanQR() {
        zXingScannerView = ZXingScannerView(context)
        activity?.setContentView(zXingScannerView)
        zXingScannerView?.run {
            startCamera()
            Log.e("== on click ==", "eiei")
            setResultHandler {
                Log.e("=== in Result Handle ==", it.toString())
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