package com.vanzstuff.readdit.redditapi;

import android.content.ContentValues;
import android.net.Uri;

import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class RedditApiUtils {

    public static final String SCOPE_MODPOSTS = "modposts";
    public static final String SCOPE_IDENTITY = "identity";
    public static final String SCOPE_EDIT = "edit";
    public static final String SCOPE_FLAIR = "flair";
    public static final String SCOPE_HISTORY = "history";
    public static final String SCOPE_MODCONFIG = "modconfig";
    public static final String SCOPE_MODFLAIR = "modflair";
    public static final String SCOPE_MODLOG = "modlog";
    public static final String SCOPE_MODWIKI = "modwiki";
    public static final String SCOPE_MYSUBREDDITS = "mysubreddits";
    public static final String SCOPE_READ = "read";
    public static final String SCOPE_REPORT = "report";
    public static final String SCOPE_SAVE = "save";
    public static final String SCOPE_SUBMIT = "submit";
    public static final String SCOPE_SUBSCRIBE = "subscribe";
    public static final String SCOPE_VOTE = "vote";
    public static final String SCOPE_WIKIEDIT = "wikiedit";
    public static final String SCOPE_WIKIREAD = "wikiread";
    public static final String REDIRECT_URI = "http://127.0.0.1/autorized_callback";
    public static final String AUTHORIZATION_RESPONSE_ACCESS_TOKEN = "access_token";
    public static final String AUTHORIZATION_RESPONSE_TOKEN_TYPE = "token_type";
    public static final String AUTHORIZATION_RESPONSE_SCOPE = "scope";
    public static final String AUTHORIZATION_RESPONSE_EXPIRES_in = "expires_in";
    public static final String AUTHORIZATION_RESPONSE_STATE = "state";
    public static final String AUTHORIZATION_RESPONSE_ERROR = "error";
    public static final String KIND_COMMENT = "t1";
    public static final String KIND_ACCOUNT = "t2";
    public static final String KIND_LINK = "t3";
    public static final String KIND_MESSAGE = "t4";
    public static final String KIND_SUBREDDIT = "t5";
    public static final String KIND_AWARD = "t6";
    public static final String KIND_PROMOCAMPAIGN = "t7";
    public static final String KIND_LISTING = "listing";

    private static final String sClientId = "7KxduDMf4c8Sig";



    /**
     * Convert all parameteres in objParams to a valid string to use in the request
     * @param objParams map with parameteres object to convert
     * @return a map with string values
     */
    public static Map<String, String> parserParamsToString(Map<String, Object> objParams){
        if( objParams == null || objParams.size() == 0)
            return Collections.emptyMap();
        Map<String, String> parserParams = new HashMap<String, String>(objParams.size());
        if(objParams != null ) {
            for (String key : objParams.keySet()) {
                if ( objParams.get(key).getClass().isArray()){
                    StringBuilder listParam = new StringBuilder();
                    for( Object obj : (Object[]) objParams.get(key)){
                        if ( obj instanceof String)
                            listParam.append(((String) obj).isEmpty()? "\"\"": obj);
                        else
                            listParam.append(String.valueOf(obj));
                        listParam.append(",");
                    }
                    parserParams.put(key, listParam.substring(0, listParam.length()-1).toString());
                }else {
                    parserParams.put(key, String.valueOf(objParams.get(key)));
                }
            }
        }
        return parserParams;
    }

    private static String generateScopesString(String ... scopes){
        StringBuilder scopesString = new StringBuilder();
        for( String scope : scopes){
            scopesString.append(scope);
            scopesString.append(",");
        }
        return scopesString.substring(0, scopesString.length()-1).toString();
    }

    public static Uri generateAuthorizationUri( String state, String ... scopes ){
        return Uri.parse("https://www.reddit.com/api/v1/authorize").buildUpon()
                .appendQueryParameter("client_id", sClientId)
                .appendQueryParameter("response_type", "token")
                .appendQueryParameter("state", state)
                .appendQueryParameter("redirect_uri", REDIRECT_URI)
                .appendQueryParameter("scope", generateScopesString(scopes)).build();

    }
}
