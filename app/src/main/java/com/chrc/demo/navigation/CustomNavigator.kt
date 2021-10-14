package com.chrc.demo.navigation

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigator

/**
 *    @author : chrc
 *    date   : 9/18/21  9:58 AM
 *    desc   :
 */
class CustomNavigator(var context: Context, var manager: FragmentManager, var containerId: Int): FragmentNavigator(context, manager, containerId) {

//    override fun navigate(destination: Destination, args: Bundle?, navOptions: NavOptions?, navigatorExtras: Navigator.Extras?): NavDestination? {
//        var className = destination.className
//        if (className[0] == '.') {
//            className = context.packageName + className
//        }
//        var fragment = instantiateFragment(context, manager, className, args)
//        return
//    }
}