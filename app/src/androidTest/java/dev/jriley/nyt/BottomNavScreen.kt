package dev.jriley.nyt

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf

object BottomNavScreen {
    fun assertShowing() {
        onView(withId(R.id.navigation)).check(matches(isCompletelyDisplayed()))
    }

    fun selectHome() {
        onView(withId(R.id.navigation_home)).perform(click())
    }

    fun selectDashboard() {
        onView(withId(R.id.navigation_dashboard)).perform(click())
    }

    fun selectNotifications() {
        onView(withId(R.id.navigation_notifications)).perform(click())
    }

    fun assertHomeSelected() {
        assertNavIsSelected(R.id.navigation_home)
        onView(withId(R.id.top_title)).check(matches(allOf(isCompletelyDisplayed(), withText(R.string.top))))
    }

    fun assertDashboardSelected() {
        assertNavIsSelected(R.id.navigation_dashboard)
        onView(withId(R.id.best_title)).check(matches(allOf(isCompletelyDisplayed(), withText(R.string.best))))
    }

    fun assertNotificationsSelected() {
        assertNavIsSelected(R.id.navigation_notifications)
        onView(withId(R.id.new_title)).check(matches(allOf(isCompletelyDisplayed(), withText(R.string.new_title))))
    }

    private fun assertNavIsSelected(position: Int) {
        onView(withId(R.id.navigation)).check(matches(navItemIsSelected(position)))
    }

}

fun navItemIsSelected(navId: Int): Matcher<View> {
    return object : BoundedMatcher<View, BottomNavigationView>(BottomNavigationView::class.java) {
        override fun describeTo(description: Description?) {
            description?.appendText(" BottomNavigationView should be selected at position $navId")
        }

        override fun matchesSafely(item: BottomNavigationView?): Boolean =
            item?.menu?.findItem(navId)?.isChecked ?: false
    }
}