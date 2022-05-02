package com.example.tingting.utils


import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.view.animation.AccelerateInterpolator
import androidx.core.view.NestedScrollingParent
import androidx.core.view.ViewCompat
import androidx.customview.widget.ViewDragHelper
import com.example.tingting.R
import java.lang.ref.WeakReference
import java.util.*


class DASwipeLayout : ViewGroup {

    private var dragHelper: ViewDragHelper? = null
    private var leftView: View? = null
    private var rightView: View? = null
    private var centerView: View? = null
    private var velocityThreshold: Float = 0.toFloat()
    private var touchSlop: Float = 0.toFloat()
    private var swipeListener: OnSwipeListener? = null
    private var weakAnimator: WeakReference<ObjectAnimator>? = null
    private val hackedParents = WeakHashMap<View, Boolean>()
    /**
     * Enable or disable swipe gesture from left side
     */
    var isLeftSwipeEnabled = true
    /**
     * Enable or disable swipe gesture from right side
     */

    var isRightSwipeEnabled = true

    private var touchState = TOUCH_STATE_WAIT
    private var touchX: Float = 0.toFloat()
    private var touchY: Float = 0.toFloat()

    /**
     * get horizontal offset from initial position
     */
    /**
     * set horizontal offset from initial position
     */
    var offset: Int
        get() = if (centerView == null) 0 else centerView!!.left
        set(offset) {
            if (centerView != null) {
                offsetChildren(null, offset - centerView!!.left)
            }
        }

    /**
     * enable or disable swipe gesture handling
     */
    var isSwipeEnabled: Boolean
        get() = isLeftSwipeEnabled || isRightSwipeEnabled
        set(enabled) {
            this.isLeftSwipeEnabled = enabled
            this.isRightSwipeEnabled = enabled
        }

    private val dragCallback = object : ViewDragHelper.Callback() {

        private var initLeft: Int = 0

        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            initLeft = child.left
            return true
        }

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            return if (dx > 0) {
                clampMoveRight(child, left)
            } else {
                clampMoveLeft(child, left)
            }
        }

        override fun getViewHorizontalDragRange(child: View): Int {
            return width
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            Log.d(TAG, "VELOCITY $xvel; THRESHOLD $velocityThreshold")

            val dx = releasedChild.left - initLeft
            if (dx == 0) return


            var handled = false
            if (dx > 0) {

                handled = if (xvel >= 0) onMoveRightReleased(
                    releasedChild,
                    dx,
                    xvel
                ) else onMoveLeftReleased(releasedChild, dx, xvel)

            } else {

                handled = if (xvel <= 0) onMoveLeftReleased(
                    releasedChild,
                    dx,
                    xvel
                ) else onMoveRightReleased(releasedChild, dx, xvel)
            }

            if (!handled) {
                startScrollAnimation(
                    releasedChild,
                    releasedChild.left - centerView!!.left,
                    false,
                    dx > 0
                )
            }
        }

        private fun leftViewClampReached(leftViewLP: LayoutParams): Boolean {
            if (leftView == null) return false

            when (leftViewLP.clamp) {
                LayoutParams.CLAMP_PARENT -> return leftView!!.right >= width

                LayoutParams.CLAMP_SELF -> return leftView!!.right >= leftView!!.width

                else -> return leftView!!.right >= leftViewLP.clamp
            }
        }

        private fun rightViewClampReached(lp: LayoutParams): Boolean {
            if (rightView == null) return false

            when (lp.clamp) {
                LayoutParams.CLAMP_PARENT -> return rightView!!.right <= width

                LayoutParams.CLAMP_SELF -> return rightView!!.right <= width

                else -> return rightView!!.left + lp.clamp <= width
            }
        }

        override fun onViewPositionChanged(
            changedView: View,
            left: Int,
            top: Int,
            dx: Int,
            dy: Int
        ) {
            offsetChildren(changedView, dx)

            if (swipeListener == null) return

            var stickyBound: Int
            if (dx > 0) {
                //move to right

                if (leftView != null) {
                    stickyBound = getStickyBound(leftView)
                    if (stickyBound != LayoutParams.STICKY_NONE) {
                        if (leftView!!.right - stickyBound > 0 && leftView!!.right - stickyBound - dx <= 0)
                            swipeListener!!.onLeftStickyEdge(this@DASwipeLayout, true)
                    }
                }

                if (rightView != null) {
                    stickyBound = getStickyBound(rightView)
                    if (stickyBound != LayoutParams.STICKY_NONE) {
                        if (rightView!!.left + stickyBound > width && rightView!!.left + stickyBound - dx <= width)
                            swipeListener!!.onRightStickyEdge(this@DASwipeLayout, true)
                    }
                }
            } else if (dx < 0) {
                //move to left

                if (leftView != null) {
                    stickyBound = getStickyBound(leftView)
                    if (stickyBound != LayoutParams.STICKY_NONE) {
                        if (leftView!!.right - stickyBound <= 0 && leftView!!.right - stickyBound - dx > 0)
                            swipeListener!!.onLeftStickyEdge(this@DASwipeLayout, false)
                    }
                }

                if (rightView != null) {
                    stickyBound = getStickyBound(rightView)
                    if (stickyBound != LayoutParams.STICKY_NONE) {
                        if (rightView!!.left + stickyBound <= width && rightView!!.left + stickyBound - dx > width)
                            swipeListener!!.onRightStickyEdge(this@DASwipeLayout, false)
                    }
                }
            }
        }

        private fun getStickyBound(view: View?): Int {
            val lp = getLayoutParams(view!!)
            if (lp.sticky == LayoutParams.STICKY_NONE) return LayoutParams.STICKY_NONE

            return if (lp.sticky == LayoutParams.STICKY_SELF) view.width else lp.sticky
        }

        private fun clampMoveRight(child: View, left: Int): Int {
            if (leftView == null) {
                return if (child === centerView) Math.min(left, 0) else Math.min(left, width)
            }

            val lp = getLayoutParams(leftView!!)
            when (lp.clamp) {
                LayoutParams.CLAMP_PARENT -> return Math.min(
                    left,
                    width + child.left - leftView!!.right
                )

                LayoutParams.CLAMP_SELF -> return Math.min(left, child.left - leftView!!.left)

                else -> return Math.min(left, child.left - leftView!!.right + lp.clamp)
            }
        }

        private fun clampMoveLeft(child: View, left: Int): Int {
            if (rightView == null) {
                return if (child === centerView) Math.max(left, 0) else Math.max(left, -child.width)
            }

            val lp = getLayoutParams(rightView!!)
            when (lp.clamp) {
                LayoutParams.CLAMP_PARENT -> return Math.max(child.left - rightView!!.left, left)

                LayoutParams.CLAMP_SELF -> return Math.max(
                    left,
                    width - rightView!!.left + child.left - rightView!!.width
                )

                else -> return Math.max(left, width - rightView!!.left + child.left - lp.clamp)
            }
        }

        private fun onMoveRightReleased(child: View, dx: Int, xvel: Float): Boolean {

            if (xvel > velocityThreshold) {
                val left = if (centerView!!.left < 0) child.left - centerView!!.left else width
                val moveToOriginal = centerView!!.left < 0
                startScrollAnimation(child, clampMoveRight(child, left), !moveToOriginal, true)
                return true
            }

            if (leftView == null) {
                startScrollAnimation(child, child.left - centerView!!.left, false, true)
                return true
            }

            val lp = getLayoutParams(leftView!!)

            if (dx > 0 && xvel >= 0 && leftViewClampReached(lp)) {
                if (swipeListener != null) {
                    swipeListener!!.onSwipeClampReached(this@DASwipeLayout, true)
                }
                return true
            }

            if (dx > 0 && xvel >= 0 && lp.bringToClamp != LayoutParams.BRING_TO_CLAMP_NO && leftView!!.right > lp.bringToClamp) {
                val left = if (centerView!!.left < 0) child.left - centerView!!.left else width
                startScrollAnimation(child, clampMoveRight(child, left), true, true)
                return true
            }

            if (lp.sticky != LayoutParams.STICKY_NONE) {
                val stickyBound =
                    if (lp.sticky == LayoutParams.STICKY_SELF) leftView!!.width else lp.sticky
                val amplitude = stickyBound * lp.stickySensitivity

                if (isBetween(-amplitude, amplitude, (centerView!!.left - stickyBound).toFloat())) {
                    val toClamp =
                        lp.clamp == LayoutParams.CLAMP_SELF && stickyBound == leftView!!.width ||
                                lp.clamp == stickyBound ||
                                lp.clamp == LayoutParams.CLAMP_PARENT && stickyBound == width
                    startScrollAnimation(
                        child,
                        child.left - centerView!!.left + stickyBound,
                        toClamp,
                        true
                    )
                    return true
                }
            }
            return false
        }

        private fun onMoveLeftReleased(child: View, dx: Int, xvel: Float): Boolean {
            if (-xvel > velocityThreshold) {
                val left = if (centerView!!.left > 0) child.left - centerView!!.left else -width
                val moveToOriginal = centerView!!.left > 0
                startScrollAnimation(child, clampMoveLeft(child, left), !moveToOriginal, false)
                return true
            }

            if (rightView == null) {
                startScrollAnimation(child, child.left - centerView!!.left, false, false)
                return true
            }


            val lp = getLayoutParams(rightView!!)

            if (dx < 0 && xvel <= 0 && rightViewClampReached(lp)) {
                if (swipeListener != null) {
                    swipeListener!!.onSwipeClampReached(this@DASwipeLayout, false)
                }
                return true
            }

            if (dx < 0 && xvel <= 0 && lp.bringToClamp != LayoutParams.BRING_TO_CLAMP_NO && rightView!!.left + lp.bringToClamp < width) {
                val left = if (centerView!!.left > 0) child.left - centerView!!.left else -width
                startScrollAnimation(child, clampMoveLeft(child, left), true, false)
                return true
            }

            if (lp.sticky != LayoutParams.STICKY_NONE) {
                val stickyBound =
                    if (lp.sticky == LayoutParams.STICKY_SELF) rightView!!.width else lp.sticky
                val amplitude = stickyBound * lp.stickySensitivity

                if (isBetween(
                        -amplitude,
                        amplitude,
                        (centerView!!.right + stickyBound - width).toFloat()
                    )
                ) {
                    val toClamp =
                        lp.clamp == LayoutParams.CLAMP_SELF && stickyBound == rightView!!.width ||
                                lp.clamp == stickyBound ||
                                lp.clamp == LayoutParams.CLAMP_PARENT && stickyBound == width
                    startScrollAnimation(
                        child,
                        child.left - rightView!!.left + width - stickyBound,
                        toClamp,
                        false
                    )
                    return true
                }
            }

            return false
        }

        private fun isBetween(left: Float, right: Float, check: Float): Boolean {
            return check >= left && check <= right
        }
    }

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        dragHelper = ViewDragHelper.create(this, 1f, dragCallback)
        velocityThreshold = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            VELOCITY_THRESHOLD,
            resources.displayMetrics
        )
        touchSlop = ViewConfiguration.get(getContext()).scaledTouchSlop.toFloat()

        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.DASwipe_Layout)
            if (a.hasValue(R.styleable.DASwipe_Layout_da_swipe_enabled)) {
                isLeftSwipeEnabled = a.getBoolean(R.styleable.DASwipe_Layout_da_swipe_enabled, true)
                isRightSwipeEnabled = a.getBoolean(R.styleable.DASwipe_Layout_da_swipe_enabled, true)
            }
            if (a.hasValue(R.styleable.DASwipe_Layout_da_left_swipe_enabled)) {
                isLeftSwipeEnabled = a.getBoolean(R.styleable.DASwipe_Layout_da_left_swipe_enabled, true)
            }
            if (a.hasValue(R.styleable.DASwipe_Layout_da_right_swipe_enabled)) {
                isRightSwipeEnabled =
                    a.getBoolean(R.styleable.DASwipe_Layout_da_right_swipe_enabled, true)
            }

            a.recycle()
        }
    }

    fun setOnSwipeListener(swipeListener: OnSwipeListener) {
        this.swipeListener = swipeListener
    }

    /**
     * reset swipe-layout state to initial position
     */
    fun reset() {
        if (centerView == null) return

        finishAnimator()
        dragHelper!!.abort()

        offsetChildren(null, -centerView!!.left)
    }

    /**
     * reset swipe-layout state to initial position with animation (200ms)
     */
    fun animateReset() {
        if (centerView != null) {
            runAnimation(centerView!!.left, 0)
        }
    }

    /**
     * Swipe with animation to left by right view's width
     *
     *
     * Ignores [DASwipeLayout.isSwipeEnabled] and [DASwipeLayout.isLeftSwipeEnabled]
     */
    fun animateSwipeLeft() {
        if (centerView != null && rightView != null) {
            val target = -rightView!!.width
            runAnimation(offset, target)
        }
    }

    /**
     * Swipe with animation to right by left view's width
     *
     *
     * Ignores [DASwipeLayout.isSwipeEnabled] and [DASwipeLayout.isRightSwipeEnabled]
     */
    fun animateSwipeRight() {
        if (centerView != null && leftView != null) {
            val target = leftView!!.width
            runAnimation(offset, target)
        }
    }

    private fun runAnimation(initialX: Int, targetX: Int) {
        finishAnimator()
        dragHelper!!.abort()

        val animator = ObjectAnimator()
        animator.target = this
        animator.setPropertyName("offset")
        animator.interpolator = AccelerateInterpolator()
        animator.setIntValues(initialX, targetX)
        animator.duration = 200
        animator.start()
        this.weakAnimator = WeakReference(animator)
    }

    private fun finishAnimator() {
        if (weakAnimator != null) {

            val animator = this.weakAnimator!!.get()
            if (animator != null) {
                this.weakAnimator!!.clear()
                if (animator.isRunning) {
                    animator.end()
                }
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var heightMeasureSpec = heightMeasureSpec
        val count = childCount

        var maxHeight = 0

        // Find out how big everyone wants to be
        if (View.MeasureSpec.getMode(heightMeasureSpec) == View.MeasureSpec.EXACTLY) {
            measureChildren(widthMeasureSpec, heightMeasureSpec)
        } else {
            //find a child with biggest height
            for (i in 0 until count) {
                val child = getChildAt(i)
                measureChild(child, widthMeasureSpec, heightMeasureSpec)
                maxHeight = Math.max(maxHeight, child.measuredHeight)
            }

            if (maxHeight > 0) {
                heightMeasureSpec =
                    View.MeasureSpec.makeMeasureSpec(maxHeight, View.MeasureSpec.EXACTLY)
                measureChildren(widthMeasureSpec, heightMeasureSpec)
            }
        }

        // Find rightmost and bottom-most child
        for (i in 0 until count) {
            val child = getChildAt(i)
            if (child.visibility != View.GONE) {
                val childBottom: Int

                childBottom = child.measuredHeight
                maxHeight = Math.max(maxHeight, childBottom)
            }
        }

        maxHeight += paddingTop + paddingBottom
        maxHeight = Math.max(maxHeight, suggestedMinimumHeight)

        setMeasuredDimension(
            View.resolveSize(suggestedMinimumWidth, widthMeasureSpec),
            View.resolveSize(maxHeight, heightMeasureSpec)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        layoutChildren(left, top, right, bottom)
    }

    private fun layoutChildren(left: Int, top: Int, right: Int, bottom: Int) {
        val count = childCount

        val parentTop = paddingTop

        centerView = null
        leftView = null
        rightView = null
        for (i in 0 until count) {
            val child = getChildAt(i)
            if (child.visibility == View.GONE) continue

            val lp = child.layoutParams as LayoutParams
            when (lp.gravity) {
                LayoutParams.CENTER -> centerView = child

                LayoutParams.LEFT -> leftView = child

                LayoutParams.RIGHT -> rightView = child
            }
        }

        if (centerView == null) throw RuntimeException("Center view must be added")

        for (i in 0 until count) {
            val child = getChildAt(i)
            if (child.visibility != View.GONE) {
                val lp = child.layoutParams as LayoutParams

                val width = child.measuredWidth
                val height = child.measuredHeight

                val childLeft: Int
                val childTop: Int

                val orientation = lp.gravity

                when (orientation) {
                    LayoutParams.LEFT -> childLeft = centerView!!.left - width

                    LayoutParams.RIGHT -> childLeft = centerView!!.right

                    LayoutParams.CENTER -> childLeft = child.left
                    else -> childLeft = child.left
                }
                childTop = parentTop

                child.layout(childLeft, childTop, childLeft + width, childTop + height)
            }
        }
    }

    private fun startScrollAnimation(
        view: View,
        targetX: Int,
        moveToClamp: Boolean,
        toRight: Boolean
    ) {
        if (dragHelper!!.settleCapturedViewAt(targetX, view.top)) {
            ViewCompat.postOnAnimation(view, SettleRunnable(view, moveToClamp, toRight))
        } else {
            if (moveToClamp && swipeListener != null) {
                swipeListener!!.onSwipeClampReached(this@DASwipeLayout, toRight)
            }
        }
    }

    private fun getLayoutParams(view: View): LayoutParams {
        return view.layoutParams as LayoutParams
    }

    private fun offsetChildren(skip: View?, dx: Int) {
        if (dx == 0) return

        val count = childCount
        for (i in 0 until count) {
            val child = getChildAt(i)
            if (child === skip) continue

            child.offsetLeftAndRight(dx)
            invalidate(child.left, child.top, child.right, child.bottom)
        }
    }

    private fun hackParents() {
        var parent: ViewParent? = parent
        while (parent != null) {
            if (parent is NestedScrollingParent) {
                val view = parent as View?
                hackedParents[view] = view!!.isEnabled
            }
            parent = parent.parent
        }
    }

    private fun unHackParents() {
        for ((view, value) in hackedParents) {
            if (view != null) {
                view.isEnabled = value
            }
        }
        hackedParents.clear()
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return if (isSwipeEnabled)
            internalOnInterceptTouchEvent(event)
        else
            super.onInterceptTouchEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val defaultResult = super.onTouchEvent(event)
        if (!isSwipeEnabled) {
            return defaultResult
        }

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> onTouchBegin(event)

            MotionEvent.ACTION_MOVE -> if (touchState == TOUCH_STATE_WAIT) {
                val dx = Math.abs(event.x - touchX)
                val dy = Math.abs(event.y - touchY)

                val isLeftToRight = event.x - touchX > 0

                if ((isLeftToRight && !isLeftSwipeEnabled || !isLeftToRight && !isRightSwipeEnabled) && offset == 0) {

                    return defaultResult
                }

                if (dx >= touchSlop || dy >= touchSlop) {
                    touchState =
                        if (dy == 0f || dx / dy > 1f) TOUCH_STATE_SWIPE else TOUCH_STATE_SKIP
                    if (touchState == TOUCH_STATE_SWIPE) {
                        requestDisallowInterceptTouchEvent(true)

                        hackParents()

                        if (swipeListener != null)
                            swipeListener!!.onBeginSwipe(this, event.x > touchX)
                    }
                }
            }

            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                if (touchState == TOUCH_STATE_SWIPE) {
                    unHackParents()
                    requestDisallowInterceptTouchEvent(false)
                }
                touchState = TOUCH_STATE_WAIT
            }
        }

        if (event.actionMasked != MotionEvent.ACTION_MOVE || touchState == TOUCH_STATE_SWIPE) {
            dragHelper!!.processTouchEvent(event)
        }

        return true
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun generateLayoutParams(attrs: AttributeSet): LayoutParams {
        return LayoutParams(context, attrs)
    }

    override fun generateLayoutParams(p: ViewGroup.LayoutParams): LayoutParams {
        return LayoutParams(p)
    }

    override fun checkLayoutParams(p: ViewGroup.LayoutParams): Boolean {
        return p is LayoutParams
    }

    private fun internalOnInterceptTouchEvent(event: MotionEvent): Boolean {
        if (event.actionMasked == MotionEvent.ACTION_DOWN) {
            onTouchBegin(event)
        }
        return dragHelper!!.shouldInterceptTouchEvent(event)
    }

    private fun onTouchBegin(event: MotionEvent) {
        touchState = TOUCH_STATE_WAIT
        touchX = event.x
        touchY = event.y
    }

    private inner class SettleRunnable internal constructor(
        private val view: View,
        private val moveToClamp: Boolean,
        private val moveToRight: Boolean
    ) : Runnable {

        override fun run() {
            if (dragHelper != null && dragHelper!!.continueSettling(true)) {
                ViewCompat.postOnAnimation(this.view, this)
            } else {
                Log.d(TAG, "ONSWIPE clamp: $moveToClamp ; moveToRight: $moveToRight")
                if (moveToClamp && swipeListener != null) {
                    swipeListener!!.onSwipeClampReached(this@DASwipeLayout, moveToRight)
                }
            }
        }
    }

    class LayoutParams : ViewGroup.LayoutParams {

        public var gravity = CENTER
        public var sticky: Int = 0
        public var stickySensitivity = DEFAULT_STICKY_SENSITIVITY
        public var clamp = CLAMP_SELF
        public var bringToClamp = BRING_TO_CLAMP_NO

        constructor(c: Context, attrs: AttributeSet) : super(c, attrs) {

            val a = c.obtainStyledAttributes(attrs, R.styleable.DASwipe_Layout)

            val N = a.indexCount
            for (i in 0 until N) {
                val attr = a.getIndex(i)
                if (attr == R.styleable.DASwipe_Layout_da_swipe_gravity) {
                    gravity = a.getInt(attr, CENTER)
                } else if (attr == R.styleable.DASwipe_Layout_da_sticky) {
                    sticky = a.getLayoutDimension(attr, STICKY_SELF)
                } else if (attr == R.styleable.DASwipe_Layout_da_clamp) {
                    clamp = a.getLayoutDimension(attr, CLAMP_SELF)
                } else if (attr == R.styleable.DASwipe_Layout_da_bring_to_clamp) {
                    bringToClamp = a.getLayoutDimension(attr, BRING_TO_CLAMP_NO)
                } else if (attr == R.styleable.DASwipe_Layout_da_sticky_sensitivity) {
                    stickySensitivity = a.getFloat(attr, DEFAULT_STICKY_SENSITIVITY)
                }
            }
            a.recycle()
        }

        constructor(source: ViewGroup.LayoutParams) : super(source) {}

        constructor(width: Int, height: Int) : super(width, height) {}

        companion object {

            val LEFT = -1
            val RIGHT = 1
            val CENTER = 0

            val CLAMP_PARENT = -1
            val CLAMP_SELF = -2
            val BRING_TO_CLAMP_NO = -1

            val STICKY_SELF = -1
            val STICKY_NONE = -2
            private val DEFAULT_STICKY_SENSITIVITY = 0.9f
        }
    }

    interface OnSwipeListener {
        fun onBeginSwipe(swipeLayout: DASwipeLayout, moveToRight: Boolean)

        fun onSwipeClampReached(swipeLayout: DASwipeLayout, moveToRight: Boolean)

        fun onLeftStickyEdge(swipeLayout: DASwipeLayout, moveToRight: Boolean)

        fun onRightStickyEdge(swipeLayout: DASwipeLayout, moveToRight: Boolean)
    }

    companion object {

        private val TAG = DASwipeLayout::class.java.simpleName
        private val VELOCITY_THRESHOLD = 1500f
        private val TOUCH_STATE_WAIT = 0
        private val TOUCH_STATE_SWIPE = 1
        private val TOUCH_STATE_SKIP = 2
    }
}

