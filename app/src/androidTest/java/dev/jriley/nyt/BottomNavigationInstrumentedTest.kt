package dev.jriley.nyt

import androidx.test.rule.ActivityTestRule
import org.junit.Rule
import org.junit.Test

class BottomNavigationInstrumentedTest {
    @get:Rule
    val activityRule = ActivityTestRule(BottomNavActivity::class.java, false, false)

    @Test
    fun select_and_verify_navigation_items() {

        activityRule.launchActivity(null)

        BottomNavScreen.assertShowing()

        BottomNavScreen.assertHomeSelected()

        BottomNavScreen.selectDashboard()

        BottomNavScreen.assertDashboardSelected()

        BottomNavScreen.selectNotifications()

        BottomNavScreen.assertNotificationsSelected()

        BottomNavScreen.selectHome()

        BottomNavScreen.assertHomeSelected()

    }
}
