package com.qbase.sophixdemo;

import android.content.Context;
import android.support.annotation.Keep;
import android.util.Log;

import com.taobao.sophix.PatchStatus;
import com.taobao.sophix.SophixApplication;
import com.taobao.sophix.SophixEntry;
import com.taobao.sophix.SophixManager;
import com.taobao.sophix.listener.PatchLoadStatusListener;

/**
 * Sophix入口类，专门用于初始化Sophix，不应包含任何业务逻辑。
 * 此类必须继承自SophixApplication，onCreate方法不需要实现。
 * 此类不应与项目中的其他类有任何互相调用的逻辑，必须完全做到隔离。
 * AndroidManifest中设置application为此类，而SophixEntry中设为原先Application类。
 * 注意原先Application里不需要再重复初始化Sophix，并且需要避免混淆原先Application类。
 */

public class SophixStubApp extends SophixApplication {


    // 此处SophixEntry应指定真正的Application，并且保证RealApplicationStub类名不被混淆。
    @Keep
    @SophixEntry(MyApp.class)
    static class RealApplicationStub {}


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//         如果需要使用MultiDex，需要在此处调用。
//         MultiDex.install(this);
        initSophix();
    }


    /**
     * initialize(): <必选>
     *      该方法主要做些必要的初始化工作以及如果本地有补丁的话会加载补丁, 但不会自动请求补丁。因此需要自行调用queryAndLoadNewPatch方法拉取补丁。
     *      这个方法调用需要尽可能的早, 必须在Application的attachBaseContext方法的最前面调用（在super.attachBaseContext之后，
     *      如果有Multidex，也需要在Multidex.install之后）, initialize()方法调用之前你需要先调用如下几个方法进行一些必要的参数设置, 方法调用说明如下:
     * setContext(application): <必选> 传入入口Application即可
     * setAppVersion(appVersion): <必选> 应用的版本号
     * setSecretMetaData(idSecret, appSecret, rsaSecret): <可选，推荐使用> 三个Secret分别对应AndroidManifest里面的三个，
     *      可以不在AndroidManifest设置而是用此函数来设置Secret。放到代码里面进行设置可以自定义混淆代码，更加安全，
     *      此函数的设置会覆盖AndroidManifest里面的设置，如果对应的值设为null，默认会在使用AndroidManifest里面的。
     * setEnableDebug(isEnabled): <可选> isEnabled默认为false, 是否调试模式, 调试模式下会输出日志以及不进行补丁签名校验.
     *      线下调试此参数可以设置为true, 查看日志过滤TAG:Sophix, 同时强制不对补丁进行签名校验, 所有就算补丁未签名或者签名失败也发现可以加载成功.
     *      但是正式发布该参数必须为false, false会对补丁做签名校验, 否则就可能存在安全漏洞风险
     * setAesKey(aesKey): <可选> 用户自定义aes秘钥, 会对补丁包采用对称加密。这个参数值必须是16位数字或字母的组合，
     *      是和补丁工具设置里面AES Key保持完全一致, 补丁才能正确被解密进而加载。此时平台无感知这个秘钥,
     *      所以不用担心阿里云移动平台会利用你们的补丁做一些非法的事情。
     * setPatchLoadStatusStub(new PatchLoadStatusListener()): <可选> 设置patch加载状态监听器, 该方法参数需要实现PatchLoadStatusListener接口,
     *      接口说明见1.3.2.说明
     * setUnsupportedModel(modelName, sdkVersionInt):<可选> 把不支持的设备加入黑名单，加入后不会进行热修复。modelName为该机型上Build.MODEL的值，
     *      这个值也可以通过adb shell getprop | grep ro.product.model取得。sdkVersionInt就是该机型的Android版本，也就是Build.VERSION.SDK_INT，
     *      若设为0，则对应该机型所有安卓版本。目前控制台也可以直接设置机型黑名单，更加灵活。
     * */
    private void initSophix() {
        String appVersion = "1.0";
        try {
            appVersion = this.getPackageManager()
                    .getPackageInfo(this.getPackageName(), 0)
                    .versionName;
        } catch (Exception e) {
        }
        final SophixManager instance = SophixManager.getInstance();
        instance.setContext(this)
                .setAppVersion(appVersion)
                .setSecretMetaData(null, null, null)
                .setEnableDebug(true)
                .setEnableFullLog()
                .setPatchLoadStatusStub(new PatchLoadStatusListener() {
                    @Override
                    public void onLoad(final int mode, final int code, final String info, final int handlePatchVersion) {
                        if (code == PatchStatus.CODE_LOAD_SUCCESS) {
                        } else if (code == PatchStatus.CODE_LOAD_RELAUNCH) {
                            // 如果需要在后台重启，建议此处用SharePreference保存状态。
                        }
                        String msg = new StringBuilder("").append("Mode:").append(mode)
                                .append(" Code:").append(code)
                                .append(" Info:").append(info)
                                .append(" HandlePatchVersion:").append(handlePatchVersion).toString();
                        Log.e("----",msg.toString());
                    }
                }).initialize();
    }
}
