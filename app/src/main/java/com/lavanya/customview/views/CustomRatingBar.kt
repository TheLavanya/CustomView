package com.lavanya.customview.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.lavanya.customview.R

class CustomRatingBar : ViewGroup {

    val STAR_EMPTY: Int = R.drawable.star_empty
    val STAR_HALF: Int = R.drawable.star_half
    val STAR_FULL: Int = R.drawable.star_full
    private var mContext: Context? = null
    protected var mMaxStar = 0
    private var mPadding = 0
    protected var mStarWidth = 0
    protected var mStarHeight = 0
    protected var stars = 0f
    private var lastStars = 0f
    protected var mMinStar = 0f

    protected var mEmptyStar = 0
    protected var mFullStar = 0
    protected var mHalfStar = 0

    constructor(context: Context?) : this(context, null) {
    }

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0) {
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        mContext = context
        val a = mContext!!.obtainStyledAttributes(attrs, R.styleable.RB)
        initView(a)
        a.recycle()
    }

    private fun initView(a: TypedArray) {
        mMaxStar = a.getInteger(R.styleable.RB_maxStar, 5)
        mPadding = a.getDimension(R.styleable.RB_padding, 10f).toInt()
        mStarWidth = a.getDimension(R.styleable.RB_starWidth, 40f).toInt()
        mStarHeight = a.getDimension(R.styleable.RB_starHeight, 40f).toInt()
        mMinStar = a.getFloat(R.styleable.RB_minStar, 0f)
        stars = a.getFloat(R.styleable.RB_currentStar, 0f) * 2
        mEmptyStar = a.getResourceId(R.styleable.RB_emptyStar, STAR_EMPTY)
        mHalfStar = a.getResourceId(R.styleable.RB_halfStar, STAR_HALF)
        mFullStar = a.getResourceId(R.styleable.RB_fullStar, STAR_FULL)
        isCanChange = a.getBoolean(R.styleable.RB_canChange, true)
        for (i in 0 until mMaxStar) {
            val child = createChild(i)
            addView(child)
            list.add(child)
        }
    }

    var list: MutableList<LinearLayout> = ArrayList()

    @SuppressLint("SetTextI18n")
    private fun createChild(index: Int): LinearLayout {
        val linearLayout = LinearLayout(mContext)
        linearLayout.orientation = LinearLayout.HORIZONTAL
        linearLayout.background = ResourcesCompat.getDrawable(resources, R.drawable.empty_bg, null)
        val imageView = ImageView(mContext)
        imageView.setImageResource(R.drawable.star_empty)
        val childParams: LayoutParams = generateDefaultLayoutParams()
        childParams.width = mStarWidth
        childParams.height = mStarHeight
        imageView.layoutParams = childParams
        linearLayout.addView(imageView, 0)
        val textView = TextView(mContext)
        textView.text = (index + 1).toString()
        val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(5, 0, 0, 0)
        layoutParams.gravity = Gravity.CENTER
        textView.layoutParams = layoutParams
        textView.textSize = 15f
        textView.setTextColor(Color.parseColor("#FF000000"))
        linearLayout.addView(textView, 1)
        return linearLayout
    }

    private var isCanChange = false

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> return if (isCanChange) {
                val x = event.x
                val current = checkX(x)
                stars = fixStars(current)
                checkState()
                true
            } else {
                false
            }
        }
        return true
    }

    fun getOnStarChangeListener(): OnStarChangeListener? {
        return onStarChangeListener
    }

    fun setOnStarChangeListener(onStarChangeListener: OnStarChangeListener?) {
        this.onStarChangeListener = onStarChangeListener
    }

    fun removeOnStarChangeLisetener() {
        onStarChangeListener = null
    }

    interface OnStarChangeListener {
        fun onStarChange(ratingBar: CustomRatingBar?, star: Int)
    }

    private var onStarChangeListener: OnStarChangeListener? = null

    private fun checkState() {
        if (lastStars != stars) {
            lastStars = stars
            if (onStarChangeListener != null) {
                onStarChangeListener!!.onStarChange(this, Math.round(stars / 2))
            }
            setView()
        }
    }

    private fun setView() {
        if (stars < mMinStar * 2) {
            stars = mMinStar * 2
        }
        val stars = stars.toInt()
        if (stars % 2 == 0) {
            for (i in 0 until mMaxStar) {
                if (i < stars / 2) {
                    setFullView(list[i])
                } else {
                    setEmptyView(list[i])
                }
            }
        } else {
            for (i in 0 until mMaxStar) {
                if (i < stars / 2) {
                    setFullView(list[i])
                } else if (i == stars / 2) {
                    setFullView(list[i])
                } else {
                    setEmptyView(list[i])
                }
            }
        }
    }

    private fun setEmptyView(view: LinearLayout) {
        view.background = ResourcesCompat.getDrawable(resources, R.drawable.empty_bg, null)
        val imageView: ImageView = view.getChildAt(0) as ImageView
        imageView.setImageResource(mEmptyStar)
        val textView: TextView = view.getChildAt(1) as TextView
        textView.setTextColor(Color.parseColor("#FF000000"))
    }

    private fun setHalfView(view: LinearLayout) {
        val imageView: ImageView = view.getChildAt(0) as ImageView
        imageView.setImageResource(mHalfStar)
    }

    private fun setFullView(view: LinearLayout) {
        view.background = ResourcesCompat.getDrawable(resources, R.drawable.full_bg, null)
        val imageView: ImageView = view.getChildAt(0) as ImageView
        imageView.setImageResource(mFullStar)
        val textView: TextView = view.getChildAt(1) as TextView
        textView.setTextColor(Color.parseColor("#FFFFFFFF"))

    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        setView()
    }

    fun getMax(): Int {
        return mMaxStar
    }

    fun setMax(mCount: Int) {
        mMaxStar = mCount
    }

    fun getPadding(): Int {
        return mPadding
    }

    fun setPadding(mPadding: Int) {
        this.mPadding = mPadding
    }

    fun getStarWidth(): Int {
        return mStarWidth
    }

    fun setStarWidth(mStarWidth: Int) {
        this.mStarWidth = mStarWidth
    }

    fun getStarHeight(): Int {
        return mStarHeight
    }

    fun setStarHeight(mStarHeight: Int) {
        this.mStarHeight = mStarHeight
    }

    fun getMinStar(): Float {
        return mMinStar
    }

    fun setMinStar(mMinStar: Float) {
        this.mMinStar = mMinStar
    }

    fun setRatingStars(stars: Float) {
        this.stars = stars
    }

    fun getRatingStars(): Float {
        return stars
    }

    fun setCanChange(canChange: Boolean) {
        isCanChange = canChange
    }

    fun isCanChange(): Boolean {
        return isCanChange
    }

    private fun fixStars(current: Int): Float {
        if (current > mMaxStar * 2) {
            return (mMaxStar * 2).toFloat()
        } else if (current < mMinStar * 2) {
            return mMinStar * 2
        }
        return current.toFloat()
    }

    private fun checkX(x: Float): Int {
        val width = width
        val per = width / mMaxStar / 2
        return (x / per).toInt() + 1
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChildren(widthMeasureSpec, heightMeasureSpec)
        val childCount = childCount
        var width = 0
        var height = 0
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val measuredWidth = child.measuredWidth
            val measuredHeight = child.measuredHeight
            width += measuredWidth
            height = measuredHeight
            if (i != childCount - 1) {
                width += mPadding
            }
        }
        setMeasuredDimension(width, height)
    }

    override fun generateLayoutParams(attrs: AttributeSet?): MarginLayoutParams? {
        return MarginLayoutParams(context, attrs)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var l = l
        var t = t
        var r = r
        var b = b
        t = 0
        l = 0
        val childCount = childCount
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            r = l + child.measuredWidth
            b = t + child.measuredHeight
            child.layout(l, t, r, b)
            l = r + mPadding
        }
    }
}