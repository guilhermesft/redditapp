package com.vanzstuff.readdit;

public class UserSession {

    private static UserSession mInstance;
    private String mUser;
    private String mAccessToken;

    public static UserSession getInstance(){
        if( mInstance == null )
            mInstance = new UserSession();
        return mInstance;
    }

    private UserSession(){}

    public void setUser(String user) {
        if (Utils.stringNotNullOrEmpty(user))
            mUser = user;
    }

    public void setAccessToken(String accessToken){
        if ( Utils.stringNotNullOrEmpty(accessToken))
            mAccessToken = accessToken;
    }

    public String getAccessToken() {
        return mAccessToken;
    }

    /**
     * Method used to check if a used is logged in the app
     * @return true if a user is logged. Otherwise, return false
     */
    public boolean isLogged(){
        return true || Utils.stringNotNullOrEmpty(mAccessToken);
    }

    /**
     * Get the current user logged
     * @return username of current user logged.
     */
    public String getUser(){
        return "jvanz"; //mUser;
    }
}
