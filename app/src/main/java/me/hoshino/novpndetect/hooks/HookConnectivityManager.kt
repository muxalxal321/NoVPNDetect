package me.hoshino.novpndetect.hooks

import android.net.ConnectivityManager
import android.util.Log
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import me.hoshino.novpndetect.XHook

class HookConnectivityManager : XHook {

    override val targetKlass: String
        get() = "android.net.ConnectivityManager"

    override fun injectHook() {
        hookNetworkInfo()
        // TODO: will apps detect VPN from isVpnLockdownEnabled?
    }

    private fun hookNetworkInfo() {
        XposedHelpers.findAndHookMethod(ConnectivityManager::class.java, "getNetworkInfo", Int::class.java, object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                Log.i("NoVPNDetect", "ConnectivityManager.getNetworkInfo (${param.args[0]})")
                if (param.args[0] == ConnectivityManager.TYPE_VPN) {
                    param.result = null
                }
            }
        })
    }
}