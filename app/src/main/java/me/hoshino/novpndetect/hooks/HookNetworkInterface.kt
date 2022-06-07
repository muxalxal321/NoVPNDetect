package me.hoshino.novpndetect.hooks

import android.util.Log
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import me.hoshino.novpndetect.XHook
import me.hoshino.novpndetect.util.getRandomString
import java.net.NetworkInterface

class HookNetworkInterface : XHook {

    override val targetKlass: String
        get() = "android.net.NetworkInterface"

    override fun injectHook() {
        hookGetName()
        hookIsVirtual()
    }

    private fun hookIsVirtual() {
        XposedHelpers.findAndHookMethod(NetworkInterface::class.java, "isVirtual", object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                Log.i("NoVPNDetect", "NetworkInterface.isVirtual")
                // VPNs are always virtual
                param.result = false
            }
        })
    }

    private fun hookGetName() {
        XposedHelpers.findAndHookMethod(NetworkInterface::class.java, "getName", object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                Log.i("NoVPNDetect", "NetworkInterface.getName (${param.result})")
                // breaks VPN name detection
                if (param.result is String) {
                    val name = param.result as String
                    if (name.startsWith("tun") || name.startsWith("ppp") || name.startsWith("pptp")) {
                        param.result = getRandomString(name.length)
                    }
                } else {
                    Log.e("NoVPNDetect", "NetworkInterface.getName: result is not String")
                }
            }
        })
    }
}