package me.hoshino.novpndetect.hooks

import android.util.Log
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import me.hoshino.novpndetect.XHook
import java.net.NetworkInterface

class HookNetworkInterface : XHook {

    override val targetKlass: String
        get() = "android.net.NetworkInterface"

    override fun injectHook() {
        hookIsUp()
    }

    private fun hookIsUp() {
        XposedHelpers.findAndHookMethod(NetworkInterface::class.java, "isUp", object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                // make apps unable to detect vpn
                val name = (param.thisObject as NetworkInterface).name
                Log.i("NoVPNDetect", "NetworkInterface.isUp ($name)")
                if (name.startsWith("tun") || name.startsWith("ppp") || name.startsWith("pptp")) {
                    param.result = false
                }
            }
        })
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
}