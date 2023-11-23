package com.pluu.suspendglidesample.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.view.View
import android.view.ViewGroup
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

///////////////////////////////////////////////////////////////////////////
// - [Suspending over Views](https://chrisbanes.me/posts/suspending-views/)
// - [Suspending over Views Example](https://chrisbanes.me/posts/suspending-views-example/)
///////////////////////////////////////////////////////////////////////////

suspend fun Animator.awaitEnd() = suspendCancellableCoroutine { cont ->
    // Add an invokeOnCancellation listener. If the coroutine is
    // cancelled, cancel the animation too that will notify
    // listener's onAnimationCancel() function
    cont.invokeOnCancellation { cancel() }

    addListener(object : AnimatorListenerAdapter() {
        private var endedSuccessfully = true

        override fun onAnimationCancel(animation: Animator) {
            // Animator has been cancelled, so flip the success flag
            endedSuccessfully = false
        }

        override fun onAnimationEnd(animation: Animator) {
            // Make sure we remove the listener so we don't keep
            // leak the coroutine continuation
            animation.removeListener(this)

            if (cont.isActive) {
                // If the coroutine is still active...
                if (endedSuccessfully) {
                    // ...and the Animator ended successfully, resume the coroutine
                    cont.resume(Unit)
                } else {
                    // ...and the Animator was cancelled, cancel the coroutine too
                    cont.cancel()
                }
            }
        }
    })
}

fun View.generateWidthAnimator(newWidth: Int): Animator {
    val anim = ValueAnimator.ofInt(measuredWidth, newWidth)
    anim.addUpdateListener { valueAnimator ->
        val v = valueAnimator.animatedValue as Int
        val layoutParams: ViewGroup.LayoutParams = layoutParams
        layoutParams.width = v + paddingStart + paddingEnd
        this.layoutParams = layoutParams
    }
    return anim
}