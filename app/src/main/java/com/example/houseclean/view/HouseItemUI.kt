package com.example.houseclean.view

/*import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.view.ViewGroup
import com.example.houseclean.R
import org.jetbrains.anko.*
import org.jetbrains.anko.cardview.v7.cardView

class HouseItemUI(): AnkoComponent<ViewGroup> {

    override fun createView(ui: AnkoContext<ViewGroup>) = with(ui) {
        cardView {
            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 32f
                setColor(Color.WHITE)
            }

            relativeLayout {
                lparams(matchParent, wrapContent){
                    horizontalPadding = dip(16)
                    verticalPadding = dip(8)
                }
                imageView {
                    id = R.id.houseLstImg
                }.lparams(dip(48),dip(48))
                textView {
                    id = R.id.houseLstTitle
                    textColor = Color.DKGRAY
                    typeface = Typeface.DEFAULT_BOLD
                    textSize = 18f
                }.lparams(wrapContent, wrapContent) {
                    rightOf(R.id.itemLstImg)
                    sameTop(R.id.itemLstImg)
                    marginStart = dip(16)
                }
                textView {
                    id = R.id.houseLstAddress
                    textSize = 16f
                    textColor = Color.DKGRAY
                    lines = 1
                    ellipsize = TextUtils.TruncateAt.END
                }.lparams {
                    below(R.id.houseLstTitle)
                    sameStart(R.id.houseLstTitle)
                    marginEnd = dip(24)
                }
                textView {
                    text = ""
                }.lparams {
                    alignParentTop()
                    alignParentEnd()
                }
            }
        }
    }
}*/