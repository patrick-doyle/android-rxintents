package com.twistedequations.rxintent

import android.app.Activity
import android.content.Intent
import com.twistedequations.rxintent.internal.PreConditions
import org.jetbrains.spek.api.Spek
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import rx.observers.TestSubscriber

/**
 * BDD Testing with SPEK
 */

@RunWith(PowerMockRunner::class)
@PrepareForTest(PreConditions::class)
class RxIntentTest : Spek({

    var activity: Activity = Mockito.mock(Activity::class.java)

    beforeEach {
        activity = Mockito.mock(Activity::class.java)
        PowerMockito.mockStatic(PreConditions::class.java)
        PowerMockito.doNothing().`when`(PreConditions.throwIfNotOnMain())
    }

    on("On open document intent fired", {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        val testSubscriber = TestSubscriber<RxIntentResult>();
        RxIntent.forResult(activity, intent)
        .subscribe(testSubscriber)

        it("should call the activity to get the fragment manager", {
            Mockito.verify(activity).fragmentManager
        })
    })

})