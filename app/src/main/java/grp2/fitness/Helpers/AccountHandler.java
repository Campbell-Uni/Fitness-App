package grp2.fitness.Helpers;


public class AccountHandler {

    private boolean isLoggedIn = true;

    public boolean isLoggedIn(){
        return isLoggedIn;
    }

    public void logout(){
        isLoggedIn = false;
    }
}
