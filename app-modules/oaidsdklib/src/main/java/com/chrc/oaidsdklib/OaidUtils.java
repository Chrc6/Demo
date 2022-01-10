package com.chrc.oaidsdklib;

import android.content.Context;
import androidx.annotation.NonNull;
import android.util.Log;

/**
 * author : chrc
 * date   : 2019/10/16  11:38 AM
 * desc   :
 */
public class OaidUtils {

    public static void init(Context context) {
        JLibrary.InitEntry(context);
    }

    public static void getOaid(Context context) {
        MiitHelper miitHelper = new MiitHelper(appIdsUpdater);
        miitHelper.getDeviceIds(context);
    }

    private static MiitHelper.AppIdsUpdater appIdsUpdater = new MiitHelper.AppIdsUpdater() {
        @Override
        public void OnIdsAvalid(@NonNull String ids) {
            Log.e("mdidsdk", ids);
//            oaid = ids;
            
        }
    };
}
