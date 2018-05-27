package grp2.fitness;

import android.view.MenuItem;

import org.junit.Test;
import org.mockito.internal.matchers.Any;

import grp2.fitness.fragments.HomeFragment;

import static junit.framework.Assert.assertFalse;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NavigationActivityTest {


    @Test
    public void testOnNavigationItemSelected_titleSet() {
        NavigationActivity activity = mock(NavigationActivity.class);
        int itemId = R.id.home;
        CharSequence itemName = "name";
        MenuItem menuItem = mock(MenuItem.class);
        when(activity.onNavigationItemSelected(menuItem)).thenCallRealMethod();
        when(menuItem.getItemId()).thenReturn(itemId);
        when(menuItem.getTitle()).thenReturn(itemName);
        doNothing().when(activity).updateView(Any.class);

        activity.onNavigationItemSelected(menuItem);

        verify(activity).setTitle(itemName);
    }

    @Test
    public void testOnNavigationItemSelected_itemChecked() {
        NavigationActivity activity = mock(NavigationActivity.class);
        int itemId = R.id.home;
        CharSequence itemName = "name";
        MenuItem menuItem = mock(MenuItem.class);
        when(activity.onNavigationItemSelected(menuItem)).thenCallRealMethod();
        when(menuItem.getItemId()).thenReturn(itemId);
        when(menuItem.getTitle()).thenReturn(itemName);
        when(menuItem.setChecked(true)).thenReturn(menuItem);
        doNothing().when(activity).updateView(Any.class);

        activity.onNavigationItemSelected(menuItem);

        verify(menuItem).setChecked(true);
    }

    @Test
    public void testOnNavigationItemSelected_updateViewCalled() {
        NavigationActivity activity = mock(NavigationActivity.class);
        int itemId = R.id.home;
        CharSequence itemName = "name";
        MenuItem menuItem = mock(MenuItem.class);
        when(activity.onNavigationItemSelected(menuItem)).thenCallRealMethod();
        when(menuItem.getItemId()).thenReturn(itemId);
        when(menuItem.getTitle()).thenReturn(itemName);
        doNothing().when(activity).updateView(Any.class);

        activity.onNavigationItemSelected(menuItem);

        verify(activity).updateView(HomeFragment.class);
    }

    @Test
    public void testOnNavigationItemSelected_invalidItemId_returnFalse() {

        NavigationActivity activity = mock(NavigationActivity.class);
        MenuItem menuItem = mock(MenuItem.class);
        int itemId = R.id.logout;
        when(menuItem.getItemId()).thenReturn(123456789);
        when(activity.onNavigationItemSelected(menuItem)).thenCallRealMethod();

        assertFalse(activity.onNavigationItemSelected(menuItem));
    }

}
