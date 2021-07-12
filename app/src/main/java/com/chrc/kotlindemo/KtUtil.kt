package com.chrc.kotlindemo

import android.graphics.Rect
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chrc.kotlindemo.modle.PreImageInfo


/**
 *    @author : chrc
 *    date   : 2021/1/21  2:46 PM
 *    desc   :
 */
class KtUtil {

    companion object {
        /**
         *
         */
        fun getPreImageRects(urls: List<String>, layoutManager: LinearLayoutManager, radius: Float, spanCount: Int, leftSpacing: Int = 0, topSpaceing: Int = 0): List<PreImageInfo> {
            var preImageInfos: MutableList<PreImageInfo> = ArrayList()

            var visibileCountIndex = layoutManager.childCount - 1
            Log.i("anifrag===", "KtUtil childcount=${layoutManager.childCount} itemcount=${layoutManager.itemCount}")
            var i1 = urls.size - 1
            for (i in 0..i1) {
                var extraLeft = 0
                var extraTop = 0
                var factPos = 0
                var oldLine = 0
                var newLine = 0
                if (i > visibileCountIndex) {
                    factPos = visibileCountIndex / spanCount * spanCount + i % spanCount
                    extraLeft = i % spanCount * leftSpacing
                    //eg: spanCont = 3, i = 9, 这item其实是在第四行的
                    oldLine = layoutManager.childCount / spanCount + if (layoutManager.childCount % spanCount > 0) 1 else 0
                    newLine = (i + 1) / spanCount + if ((i + 1) % spanCount > 0) 1 else 0
                } else {
                    factPos = i
                }

                var findViewByPosition = layoutManager.findViewByPosition(factPos)
                findViewByPosition?.apply {
                    var rect = Rect()
                    getGlobalVisibleRect(rect)

                    val localRect = Rect()
                    getLocalVisibleRect(localRect)

                    var width = getViewWidth(findViewByPosition, rect)
                    var height = getViewHeigh(findViewByPosition, rect)
                    extraTop = (newLine - oldLine) * (topSpaceing + height)

                    var pointX: Int = rect.left + extraLeft
                    var pointY: Int = rect.top + extraTop
                    if (localRect.top > 0 && localRect.bottom - localRect.top < height!!) {//上半部分划出屏幕
                        pointY = rect.bottom - height
                    }

                    var preImageInfo = PreImageInfo(urls[i], pointX, pointY, width, height, radius)
                    preImageInfos.add(preImageInfo)
                    Log.i("anifrag===", "KtUtil i=$i left=${rect.left} top=${rect.top} extraLeft=${extraLeft} extraTop=${extraTop} factPos=${factPos}")
                }
            }
            return preImageInfos
        }

        fun getViewWidth(view: View?, rect: Rect): Int{
            return if (view?.width != null && view.width != 0) view.width else rect.right - rect.left
        }

        fun getViewHeigh(view: View?, rect: Rect): Int{
            return if (view?.height != null && view.height != 0) view.height else rect.bottom - rect.top
        }
    }
}