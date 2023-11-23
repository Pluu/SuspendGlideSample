package com.pluu.suspendglidesample

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.pluu.suspendglidesample.databinding.ActivityMainBinding
import com.pluu.suspendglidesample.utils.awaitEnd
import com.pluu.suspendglidesample.utils.awaitLoad
import com.pluu.suspendglidesample.utils.generateWidthAnimator
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Date
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val list = listOf(
        "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEgKCkf4f1m9xpvZdRDZgkbSiHdZMkuAsucfACxNcCnHdEO51NbuMn-QjjR_SaNX5CJb9uvAhfdiWxiG1TZk0fl4XiLw4Pj16t0FFae9ONjqdXeWMMEZq_56ggIn0pzEFJVgfgaWzwhOWWTgrPOND_zsGZ7xa5e13wKvgSUODhblKqVZfRb8gUgJaSlkmFE/s1600/androidsocial.png",
        "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEiG9IB2UtU3rRPR5rALE8VVZlzWRUQ4V3smaYuVjep1fxny-AbLn05l5YzmdJcZwNBl9FEYR6PgOVDYzoPqRphnBImJ_oEpvzI_xOivQ2yBTnmjo-mFsy9lgmz7Q4Ld1ESeTRFp0f8A7Sdqa4JBc3zENAW18L_-MQPGI7pkk6vr8j3T1SqWvrnjpWXFz_Q/s1600/Social-AndroidX-moving-to-minSdkVersion-19-.png",
        "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEhaeilClIEDghsWy9Ne5Pj0cLo86QDmez69WBQmS6raEI2D6FULgncKluBdDvVYq6kSMvuNNKSEDYhPULy1ejlaRvPf1Q-Qv_RYheXMh09Wirz7IF99v6HeXc8yBVj0eJMrMXOZNzLJ-uWRgaymmYp0yRWThTEJ0EWyxMCi08UhSqBVxI0LpVFhyZrO84c/s1600/wearOS_editorial-social.png",
    )

    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.playButton.setOnClickListener {
            playSample()
        }
    }

    private fun playSample() {
        var index = 0
        job?.cancel()
        job = binding.root.findViewTreeLifecycleOwner()?.lifecycleScope?.launch {
            while (isActive) {
                playStep(list[index++ % list.size])
                delay(500.milliseconds)
            }
        }
    }

    private suspend fun playStep(url: String) {
        binding.imageView.awaitLoad(url)
        binding.notiText.run {
            // Simple
            text = "Show ${Date()}"
            // ▼▼▼ Crazy ▼▼▼
//            updateLayoutParams {
//                width = ViewGroup.LayoutParams.WRAP_CONTENT
//            }
//            text = "GO"
            // ▲▲▲▲ Crazy ▲▲▲
            isVisible = true
            startAwaitEnd(generateFadeIn(this))
//            // ▼▼▼ Crazy ▼▼▼
//            delay(1.seconds)
//            updateText(StringGenerator.getRandomString((4..30).random()))
//            // ▲▲▲▲ Crazy ▲▲▲
            delay(2.seconds)
            startAwaitEnd(generateFadeOut(this))
            isVisible = false
        }
    }

    private fun generateFadeIn(target: View): Animator {
        return AnimatorSet().apply {
            interpolator = FastOutSlowInInterpolator()
            playTogether(
                ObjectAnimator.ofFloat(target, View.ALPHA, 0f, 1f),
                ObjectAnimator.ofFloat(target, View.TRANSLATION_Y, 50f, 0f)
            )
        }
    }

    private fun generateFadeOut(target: View): Animator {
        return AnimatorSet().apply {
            interpolator = FastOutSlowInInterpolator()
            playTogether(
                ObjectAnimator.ofFloat(target, View.ALPHA, 1f, 0f),
                ObjectAnimator.ofFloat(target, View.TRANSLATION_Y, 0f, 50f)
            )
        }
    }

    private suspend fun View.startAwaitEnd(animator: Animator) {
        animator.setTarget(this)
        animator.start()
        animator.awaitEnd()
    }

    private suspend fun TextView.updateText(updateText: String) {
        val newWidth = paint.measureText(updateText)
        val k = generateWidthAnimator(newWidth.toInt())
        k.duration = (200L..500L).random()
        k.start()
        text = updateText
        k.awaitEnd()
    }
}