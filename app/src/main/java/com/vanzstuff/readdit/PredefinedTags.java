package com.vanzstuff.readdit;

public enum PredefinedTags {

    SAVED("Saved"),
    HIDDEN("Hidden");

    private String mName;

    PredefinedTags(String name){
        mName = name;
    }

    /**
     * Get tag's name
     * @return tag's name
     */
    public String getName(){
        return mName;
    }
}
