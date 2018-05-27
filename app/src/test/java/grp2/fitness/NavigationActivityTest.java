package grp2.fitness;

import android.view.MenuItem;

import com.amazonaws.mobile.auth.core.IdentityManager;

import org.junit.Test;
import org.mockito.internal.matchers.Any;

import grp2.fitness.fragments.HomeFragment;
import grp2.fitness.handlers.GoogleFitApi;
import grp2.fitness.handlers.GoogleFitApi.GoogleFitApiCallback;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
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
    public void testOnNavigationItemSelected_logout_signoutCalled() {

        NavigationActivity activity = mock(NavigationActivity.class);
        MenuItem menuItem = mock(MenuItem.class);
        int itemId = R.id.logout;
        IdentityManager identityManager = mock(IdentityManager.class);
        when(menuItem.getItemId()).thenReturn(itemId);
        when(activity.onNavigationItemSelected(menuItem)).thenCallRealMethod();
        IdentityManager.setDefaultIdentityManager(identityManager);

        activity.onNavigationItemSelected(menuItem);

        verify(identityManager).signOut();
    }

    @Test
    public void testOnNavigationItemSelected_logout_finishCalled() {

        NavigationActivity activity = mock(NavigationActivity.class);
        MenuItem menuItem = mock(MenuItem.class);
        int itemId = R.id.logout;
        IdentityManager identityManager = mock(IdentityManager.class);
        when(menuItem.getItemId()).thenReturn(itemId);
        when(activity.onNavigationItemSelected(menuItem)).thenCallRealMethod();
        IdentityManager.setDefaultIdentityManager(identityManager);

        activity.onNavigationItemSelected(menuItem);

        verify(activity).finish();
    }

    @Test
    public void testOnNavigationItemSelected_invalidItemId_returnFalse() {

        NavigationActivity activity = mock(NavigationActivity.class);
        MenuItem menuItem = mock(MenuItem.class);
        when(menuItem.getItemId()).thenReturn(123456789);
        when(activity.onNavigationItemSelected(menuItem)).thenCallRealMethod();

        assertFalse(activity.onNavigationItemSelected(menuItem));
    }

    @Test
    public void testOnActivityResult_setsAuthStateFalseIfPassedGoogleRequestKey() {
        NavigationActivity activity = new NavigationActivity();
        int requestCode = GoogleFitApi.REQUEST_KEY,
                resultCode = 99999;
        GoogleFitApiCallback callback = mock(GoogleFitApiCallback.class);
        activity.getGoogleFitApi(callback);

        activity.getGoogleFitApi(callback).setAuthState(true);
        activity.onActivityResult(requestCode, resultCode, null);

        assertFalse(activity.getGoogleFitApi(callback).getAuthState());
    }

    @Test
    public void testOnActivityResult_doesNotSetAuthStateFalseIfNotPassedGoogleRequestKey() {
        NavigationActivity activity = new NavigationActivity();
        int requestCode = 12345,
                resultCode = 99999;
        GoogleFitApiCallback callback = mock(GoogleFitApiCallback.class);
        activity.getGoogleFitApi(callback);

        activity.getGoogleFitApi(callback).setAuthState(true);
        activity.onActivityResult(requestCode, resultCode, null);

        assertTrue(activity.getGoogleFitApi(callback).getAuthState());
    }


}
