package com.vanzstuff.readdit.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * App contract
 */
public class ReadditContract {

    public static final String CONTENT_AUTHORITY = "com.vanzstuff.readdit.provider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_TAG = "tag";
    public static final String PATH_LINK = "post";
    public static final String PATH_LINK_BY_TAG = "post_tag" ;
    public static final String PATH_LINK_BY_TAGID = "post_tagid" ;
    public static final String PATH_LINK_BY_SUBREDDIT = "post_subreddit";
    public static final String PATH_ADD_TAG_TO_LINK = "post_add_tag";
    public static final String PATH_ADD_TAG_NAME_TO_LINK = "post_add_tag_name";
    public static final String PATH_COMMENT = "comment";
    public static final String PATH_SUBREDDIT = "subreddit";
    public static final String PATH_USER = "user";
    public static final String PATH_VOTE = "vote";
    public static final String MULTIPLE_ITEM_MIMETYPE = "vnd.android.cursor.dir/";
    public static final String SINGLE_ITEM_MIMETYPE = "vnd.android.cursor.item/";


    /**
     * table stores all tags created by the user
     */
    public static final class Tag implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TAG).build();
        public static final String CONTENT_TYPE = MULTIPLE_ITEM_MIMETYPE + CONTENT_AUTHORITY + "/" + PATH_TAG;

        public static Uri buildTagUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        /**
         * Get the tag ID from the Uri generated by buildTagUri
         * @param uri uri generated by buildTagUri
         * @return -1 if could not retrieve the the id. Otherwise, return the id
         */
        public static long getTagId(Uri uri){
            if ( uri.getPathSegments().size() == 2)
                return Long.parseLong(uri.getPathSegments().get(1));
            return -1l;
        }

        public static final String TABLE_NAME = "tag";
        public static final String COLUMN_NAME = "name";
    }

    /**
     * table stores all post tagged/saved by the user
     */
    public static final class Link implements BaseColumns{

        /** Uri used to retrieve all post */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_LINK).build();
        public static final Uri CONTENT_URI_POST_BY_TAG = BASE_CONTENT_URI.buildUpon().appendPath(PATH_LINK_BY_TAG).build();
        public static final Uri CONTENT_URI_POST_BY_TAGID = BASE_CONTENT_URI.buildUpon().appendPath(PATH_LINK_BY_TAGID).build();
        public static final Uri CONTENT_URI_POST_BY_SUBREDDIT = BASE_CONTENT_URI.buildUpon().appendPath(PATH_LINK_BY_SUBREDDIT).build();
        public static final Uri CONTENT_URI_ADD_TAG_TO_POST = BASE_CONTENT_URI.buildUpon().appendPath(PATH_ADD_TAG_TO_LINK).build();
        public static final Uri CONTENT_URI_ADD_TAG_NAME_TO_POST = BASE_CONTENT_URI.buildUpon().appendPath(PATH_ADD_TAG_NAME_TO_LINK).build();
        public static final String CONTENT_TYPE = MULTIPLE_ITEM_MIMETYPE +  CONTENT_AUTHORITY + "/" + PATH_LINK;
        public static final String CONTENT_TYPE_POST_BY_TAG = MULTIPLE_ITEM_MIMETYPE +  CONTENT_AUTHORITY + "/" + PATH_LINK_BY_TAG;


        /**
         * Build a Uri with the last segment of the path is the post ID
         * @param id
         * @return uri with the post ID
         */
        public static Uri buildPostUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        /**
         * Get the post ID from the Uri generated by buildPostUri
         * @param uri uri generated by buildPostUri
         * @return -1 if could not retrieve the the id. Otherwise, return the id
         */
        public static long getLinkId(Uri uri){
            if ( uri.getPathSegments().size() == 2)
                return Long.parseLong(uri.getPathSegments().get(1));
            return -1l;
        }

        /**
         * Build a uri to retrieve the post from a given tag
         * @param tag used to retrieve the posts
         * @return Uri to retrieve the posts
         */
        public static Uri buildLinkByTagUri(String tag){
            return CONTENT_URI_POST_BY_TAG.buildUpon().appendPath(tag).build();
        }
        
        /**
         * Get the tag from Uri created by the method buildLinkByTagUri
         * @param uri that holds the tag
         * @return tag retrieved
         */
        static String getTagFromUri(Uri uri){
            return uri.getPathSegments().get(2);
        }

        /**
         * Build the Uri used to add the given tagID to the given postID
         * @param postID
         * @param tagID
         * @return
         */
        public static Uri buildAddTagUri(long postID, long tagID){
            return CONTENT_URI_ADD_TAG_TO_POST.buildUpon()
                    .appendPath(String.valueOf(postID))
                    .appendPath(String.valueOf(tagID)).build();
        }

        public static Uri buildAddTagUri(long postID, String tag){
            return CONTENT_URI_ADD_TAG_NAME_TO_POST.buildUpon()
                    .appendPath(String.valueOf(postID))
                    .appendPath(String.valueOf(tag)).build();
        }

        /**
         * Method used to get the tag and post ids from the Uri generate by buildAddTagUri method
         * @param uri generated by buildAddTagUri
         * @return long[] where the first position contains the post id and the second one the tag
         * id
         */
        public static long[] getTagIdAndLinkIdFromUri(Uri uri){
            return new long[]{
                    Long.parseLong(uri.getPathSegments().get(1)),
                    Long.parseLong(uri.getPathSegments().get(2))
            };
        }

        /**
         * Method used to get the tag and post ids from the Uri generate by buildAddTagUri method
         * @param uri generated by buildAddTagUri
         * @return String[] where the first position contains the post id and the second one the tag
         * id
         */
        public static String[] getTagNameFromUri(Uri uri){
            return new String[]{
                    uri.getPathSegments().get(1),
                    uri.getPathSegments().get(2)
            };
        }

        /**
         * Build a uri to retrieve the post from a given tag id
         * @param tagId id used to retrieve the posts
         * @return Uri to retrieve the posts
         */
        public static Uri buildLinkByTagIdUri(long tagId){
            return CONTENT_URI_POST_BY_TAGID.buildUpon().appendPath(String.valueOf(tagId)).build();
        }

        /**
         * Get the tag from Uri created by the method buildLinkByTagIdUri
         * @param uri that holds the tag
         * @return tag retrieved
         */
        static String getTagIdFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }

        /**
         * Build a URI to get all link from a subreddit name
         * @param subreddit
         * @return correspondent uri
         */
        public static Uri buildLinkBySubredditDisplayName(String subreddit){
            return CONTENT_URI_POST_BY_SUBREDDIT.buildUpon().appendPath(subreddit).build();
        }

        /**
         * Get the subreddit name of a uri built by buildLinkBySubredditDisplayName method
         * @param uri built by buildLinkBySubredditDisplayName method
         * @return subreddit name
         */
        public static String getLinkBySubredditDisplayName(Uri uri){
            return uri.getPathSegments().get(1);
        }

        public static final String TABLE_NAME = "link";
        /**
         * the account name of the poster. null if this is a promotional link
         * Type: text
         */
        public static final String COLUMN_AUTHOR = "author";
        /**
         * the CSS class of the author's flair. subreddit specific
         * Type: text
         */
        public static final String COLUMN_AUTHOR_FLAIR_CSS_CLASS = "authon_flair_css_class";
        /**
         * the text of the author's flair. subreddit specific
         * Type: text
         */
        public static final String COLUMN_AUTHOR_FLAIR_TEXT = "authon_flair_text";
        /**
         * probably always returns false
         * Type: integer
         */
        public static final String COLUMN_CLICKED = "clicked";
        /**
         * the domain of this link. Self posts will be self.reddit.com while other examples include en.wikipedia.org and s3.amazon.com
         * Type: text
         */
        public static final String COLUMN_DOMAIN = "domain";
        /**
         * true if the post is hidden by the logged in user. false if not logged in or not hidden
         * Type: integer
         */
        public static final String COLUMN_HIDDEN = "hidden";
        /**
         * true if this link is a selfpost
         * Type: integer
         */
        public static final String COLUMN_IS_SELF = "is_self";
        /**
         * the CSS class of the link's flair
         * Type:
         */
        public static final String COLUMN_LINK_FLAIR_CSS_CLASS = "link_flair_css_class";
        /**
         * the text of the link's flair.
         * Type: text
         */
        public static final String COLUMN_LINK_FLAIR_TEXT = "link_flair_text";
        /**
         * Used for streaming video. Detailed information about the video and it's origins are placed here
         */
        public static final String COLUMN_MEDIA = "media";
        /**
         * Used for streaming video. Technical embed specific information is found here.
         */
        public static final String COLUMN_MEDIA_EMBED = "media_embed";
        /**
         * the number of comments that belong to this link. includes removed comments.
         * Type: integer
         */
        public static final String COLUMN_NUM_COMMENTS = "num_comments";
        /**
         * true if the post is tagged as NSFW. False if otherwise
         * Type: integer
         */
        public static final String COLUMN_OVER_18 = "over_18";
        /**
         * relative URL of the permanent link for this link
         * Type: text
         */
        public static final String COLUMN_PERMALINK = "permalink";
        /**
         * true if this post is saved by the logged in user
         * Type: integer
         */
        public static final String COLUMN_SAVED = "saved";
        /**
         * the net-score of the link.
         * note: A submission's score is simply the number of upvotes minus the number of downvotes.
         * If five users like the submission and three users don't it will have a score of 2.
         * Please note that the vote numbers are not "real" numbers, they have been "fuzzed" to prevent spam bots etc.
         * So taking the above example, if five users upvoted the submission, and three users downvote it,
         * the upvote/downvote numbers may say 23 upvotes and 21 downvotes, or 12 upvotes, and 10 downvotes.
         * The points score is correct, but the vote totals are "fuzzed".
         * Type: integer
         */
        public static final String COLUMN_SCORE = "score";
        /**
         * the raw text. this is the unformatted text which includes the raw markup characters such
         * as ** for bold. <, >, and & are escaped. Empty if not present.
         * Type: text
         */
        public static final String COLUMN_SELFTEXT = "selftext";
        /**
         * the formatted escaped HTML text. this is the HTML formatted version of the marked up text.
         * Items that are boldened by ** or *** will now have <em> or *** tags on them.
         * Additionally, bullets and numbered lists will now be in HTML list format.
         * NOTE: The HTML string will be escaped. You must unescape to get the raw HTML.
         * Null if not present.
         * Type: text
         */
        public static final String COLUMN_SELFTEXT_HTML = "selftext_html";
        /**
         * subreddit of thing excluding the /r/ prefix. "pics"
         * Type: text
         */
        public static final String COLUMN_SUBREDDIT = "subreddit";
        /**
         * the id of the subreddit in which the thing is located
         * Type: text
         */
        public static final String COLUMN_SUBREDDIT_ID = "subreddit_id";
        /**
         * full URL to the thumbnail for this link; "self" if this is a self post; "default" if a
         * thumbnail is not available
         * Type: text
         */
        public static final String COLUMN_THUMBNAIL = "thumbnail";
        /**
         * the title of the link. may contain newlines for some reason
         * Type: text
         */
        public static final String COLUMN_TITLE = "title";
        /**
         * the link of this post. the permalink if this is a self-post
         * Type: text
         */
        public static final String COLUMN_URL = "url";
        /**
         * Indicates if link has been edited. Will be the edit timestamp if the link has been edited
         * and return false otherwise. https://github.com/reddit/reddit/issues/581
         * Type: integer
         */
        public static final String COLUMN_EDITED = "edited";
        /**
         * to allow determining whether they have been distinguished by moderators/admins.
         * null = not distinguished. moderator = the green [M]. admin = the red [A]. special = various
         * other special distinguishes http://bit.ly/ZYI47B
         * Type: text
         */
        public static final String COLUMN_DISTINGUISHED = "distinguished";
        /**
         * true if the post is set as the sticky in its subreddit.
         * Type: integer
         */
        public static final String COLUMN_STICKIED = "stickied";
        /**
         * Type: integer
         */
        public static final String COLUMN_CREATED = "created";
        /**
         * Type: integer
         */
        public static final String COLUMN_CREATED_UTC = "created_utc";
        /**
         * the number of upvotes. (includes own)
         * Type: integer
         */
        public static final String COLUMN_UPS = "ups";
        /**
         * the number of downvotes. (includes own)
         * Type: integer
         */
        public static final String COLUMN_DOWNS = "downs";
        /**
         * true if thing is liked by the user, false if thing is disliked, null if the user has not
         * voted or you are not logged in. Certain languages such as Java may need to use a boolean
         * wrapper that supports null assignment.
         * Type: integer
         */
        public static final String COLUMN_LIKES = "likes";
        /**
         * Type: text
         */
        public static final String COLUMN_BANNED_BY = "banned_by";
        /**
         * Type: text
         */
        public static final String COLUMN_ID = "id";
        /**
         * Type: integer
         */
        public static final String COLUMN_GILDED = "gilded";
        /**
         * Type: text
         */
        public static final String COLUMN_APPROVED_BY = "approved_by";
        /**
         * Type: text
         */
        public static final String COLUMN_NAME = "name";
        /**
         * Type: integer
         */
        public static final String COLUMN_VISITED = "visited";
        /**
         * Type: integer
         */
        public static final String COLUMN_SYNC_STATUS = "sync_status";



    }

    public static final class TagXPost implements BaseColumns{
        public static final String TABLE_NAME = "tag_x_post";
        public static final String COLUMN_TAG = "tag";
        public static final String COLUMN_POST = "post";
    }

    /**
     * Table stores all comments ( and its parents ) saved by the user
     */
    public static final class Comment implements BaseColumns{
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_COMMENT).build();
        public static final String CONTENT_TYPE = MULTIPLE_ITEM_MIMETYPE + CONTENT_AUTHORITY + "/" + PATH_COMMENT;

        public static Uri buildCommentUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        /**
         * Get the user ID from the Uri generated by buildCommentUri
         * @param uri uri generated by buildCommentUri
         * @return -1 if could not retrieve the the id. Otherwise, return the id
         */
        public static long getCommentId(Uri uri){
            if ( uri.getPathSegments().size() == 2)
                return Long.parseLong(uri.getPathSegments().get(1));
            return -1l;
        }

        public static final String TABLE_NAME = "comment";
        /**
         * who approved this comment. null if nobody or you are not a mod
         * Type: text
         */
        public static final String COLUMN_APPROVED_BY = "approved_by";
        /**
         * the account name of the poster
         * Type: text
         */
        public static final String COLUMN_AUTHOR = "author";
        /**
         * the CSS class of the author's flair. subreddit specific
         * Type: text
         */
        public static final String COLUMN_AUTHOR_CSS_CLASS = "author_flair_css_class";
        /**
         * the text of the author's flair. subreddit specific
         * Type: text
         */
        public static final String COLUMN_AUTHOR_FLAIR_TEXT = "author_flair_text";
        /**
         * who removed this comment. null if nobody or you are not a mod
         * Type: text
         */
        public static final String COLUMN_BANNED_BY = "banned_by";
        /**
         * the raw text. this is the unformatted text which includes the raw markup characters
         * such as ** for bold. <, >, and & are escaped.
         * Type: text
         */
        public static final String COLUMN_BODY = "body";
        /**
         * the formatted HTML text as displayed on reddit. For example, text that is
         * emphasised by * will now have <em> tags wrapping it. Additionally,
         * bullets and numbered lists will now be in HTML list format.
         * NOTE: The HTML string will be escaped. You must unescape to get the raw HTML.
         * Type: text
         */
        public static final String COLUMN_BODY_HTML = "body_html";
        /**
         * false if not edited, edit date in UTC epoch-seconds otherwise. NOTE: for some old
         * edited comments on reddit.com, this will be set to true instead of edit date.
         * Type: ???
         */
        public static final String COLUMN_EDITED = "edited";
        /**
         * the number of times this comment received reddit gold
         * Type: int
         */
        public static final String COLUMN_GILDED = "gilded";
        /**
         * how the logged-in user has voted on the comment - True = upvoted, False = downvoted,
         * null = no vote
         * Type: integer
         */
        public static final String COLUMN_LIKES = "likes";
        /**
         * present if the comment is being displayed outside its thread
         * (user pages, /r/subreddit/comments/.json, etc.). Contains the author of the parent link
         * Type: text
         */
        public static final String COLUMN_LINK_AUTHOR = "link_author";
        /**
         * ID of the link this comment is in
         * Type: text
         */
        public static final String COLUMN_LINK_ID = "link_id";
        /**
         * present if the comment is being displayed outside its thread
         * (user pages, /r/subreddit/comments/.json, etc.). Contains the title of the parent link
         * Type: text
         */
        public static final String COLUMN_LINK_TITLE = "link_title";
        /**
         * present if the comment is being displayed outside its thread
         * (user pages, /r/subreddit/comments/.json, etc.). Contains the URL of the parent link
         * Type: text
         */
        public static final String COLUMN_LINK_URL = "link_url";
        /**
         * how many times this comment has been reported, null if not a mod
         * type: integer
         */
        public static final String COLUMN_NUM_REPORTS = "num_reports";
        /**
         * ID of the thing this comment is a reply to, either the link or a comment in it
         * type: text
         */
        public static final String COLUMN_PARENT_ID = "parent_id";
        /**
         * true if this post is saved by the logged in user
         * type: integer
         */
        public static final String COLUMN_SAVED = "saved";
        /**
         * Whether the comment's score is currently hidden
         * type: integer
         */
        public static final String COLUMN_SCORE_HIDDEN = "score_hidden";
        /**
         * subreddit of thing excluding the /r/ prefix. "pics"
         * type: text
         */
        public static final String COLUMN_SUBREDDIT = "subreddit";
        /**
         * the id of the subreddit in which the thing is located
         * type: text
         */
        public static final String COLUMN_SUBREDDIT_ID = "subreddit_id";
        /**
         * to allow determining whether they have been distinguished by moderators/admins.
         * null = not distinguished. moderator = the green [M]. admin = the red [A].
         * special = various other special distinguishes http://redd.it/19ak1b
         * type: text
         */
        public static final String COLUMN_DISTINGUISHED = "distinguished";
        /**
         * the time of creation in local epoch-second format. ex: 1331042771.0
         * type: integer
         */
        public static final String COLUMN_CREATED = "created";
        /**
         * the time of creation in UTC epoch-second format. Note that neither of these
         * ever have a non-zero fraction.
         * type: integer
         */
        public static final String COLUMN_CREATED_UTC = "created_utc";
        /**
         * the number of upvotes. (includes own)
         * type: integer
         */
        public static final String COLUMN_UPS = "ups";
        /**
         * the number of downvotes. (includes own)
         * type: integer
         */
        public static final String COLUMN_DOWNS = "downs";
        /**
         * Type:  text
         */
        public static final String COLUMN_ID = "id";
        /**
         * Type: integer
         */
        public static final String COLUMN_SCORE = "score";
        /**
         * type: integer
         */
        public static final String COLUMN_CONTROVERSIALITY = "controversiality";
        /**
         * type: text
         */
        public static final String COLUMN_NAME = "name";
        /**
         * type: integer
         */
        public static final String COLUMN_SYNC_STATUS = "sync_status";

    }

    /**
     * Table stores all user subscribes
     */
    public static final class Subreddit implements BaseColumns{
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SUBREDDIT).build();
        public static final String CONTENT_TYPE = MULTIPLE_ITEM_MIMETYPE + CONTENT_AUTHORITY + "/" + PATH_SUBREDDIT;

        public static Uri buildSubscribeUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static final String TABLE_NAME = "subreddit";
        /**
         * number of users active in last 15 minutes
         * Type: integer
         */
        public static final String COLUMN_ACCOUNTS_ACTIVE = "accounts_active";
        /**
         * number of minutes the subreddit initially hides comment scores
         * Type: integer
         */
        public static final String COLUMN_COMMENT_SCORE_HIDE_MINS= "comment_score_hide_mins";
        /**
         * sidebar text
         * Type: text
         */
        public static final String COLUMN_DESCRIPTION = "description";
        /**
         * sidebar text, escaped HTML format
         * Type: text
         */
        public static final String COLUMN_DESCRIPTION_HTML = "description_html";
        /**
         * human name of the subreddit
         * Type: text
         */
        public static final String COLUMN_DISPLAY_NAME = "display_name";
        /**
         * full URL to the header image
         * Type: text
         */
        public static final String COLUMN_HEADER_IMG = "header_img";
        /**
         * width of the header image
         * Type: integer
         */
        public static final String COLUMN_HEADER_WIDTH = "header_width";
        /**
         * height of the header image
         * Type: integer
         */
        public static final String COLUMN_HEADER_HEIGHT = "header_height";
        /**
         * description of header image shown on hover
         * Type: text
         */
        public static final String COLUMN_HEADER_TITLE = "header_title";
        /**
         * is_nsfw?
         * Type: integer
         */
        public static final String COLUMN_OVER18 = "over18";
        /**
         * Description shown in subreddit search results?
         * Type: text
         */
        public static final String COLUMN_PUBLIC_DESCRIPTION = "public_description";
        /**
         * whether the subreddit's traffic page is publicly-accessible
         * Type: integer
         */
        public static final String COLUMN_PUBLIC_TRAFFIC = "public_traffic";
        /**
         * the number of redditors subscribed to this subreddit
         * Type: integer
         */
        public static final String COLUMN_SUBSCRIBERS = "subscribers";
        /**
         * the type of submissions the subreddit allows - one of "any", "link" or "self"
         * Type: text
         */
        public static final String COLUMN_SUBMISSION_TYPE = "submission_type";
        /**
         * the subreddit's custom label for the submit link button, if any
         * Type: text
         */
        public static final String COLUMN_SUBMIT_LINK_LABEL = "submit_link_label";
        /**
         * the subreddit's custom label for the submit text button, if any
         * Type: text
         */
        public static final String COLUMN_SUBMIT_TEXT_LABEL = "submit_text_label";
        /**
         * the subreddit's type - one of "public", "private", "restricted", or in very special cases "gold_restricted" or "archived"
         * Type: text
         */
        public static final String COLUMN_SUBREDDIT_TYPE = "subreddit_type";
        /**
         * title of the main page
         * Type: text
         */
        public static final String COLUMN_TITLE = "title";
        /**
         * The relative URL of the subreddit. Ex: "/r/pics/"
         * Type: text
         */
        public static final String COLUMN_URL = "url";
        /**
         * whether the logged-in user is banned from the subreddit
         * Type: integer
         */
        public static final String COLUMN_USER_IS_BANNED= "user_is_banned";
        /**
         * whether the logged-in user is an approved submitter in the subreddit
         * Type: integer
         */
        public static final String COLUMN_USER_IS_CONTRIBUTOR = "user_is_contributor";
        /**
         * whether the logged-in user is a moderator of the subreddit
         * Type: interger
         */
        public static final String COLUMN_USER_IS_MODERATOR = "user_is_moderator";
        /**
         * whether the logged-in user is subscribed to the subreddit
         * Type: integer
         */
        public static final String COLUMN_USER_IS_SUBSCRIBER = "user_is_subscriber";
        /**
         * Type: text
         */
        public static final String COLUMN_SUBMIT_TEXT_HTML = "submit_text_html";
        /**
         * Type: text
         */
        public static final String COLUMN_ID = "id";
        /**
         * Type: text
         */
        public static final String COLUMN_SUBMIT_TEXT = "submit_text";
        /**
         * Type: integer
         */
        public static final String COLUMN_COLLAPSE_DELETED_COMMENTS = "collapse_deleted_comments";
        /**
         * Type: text
         */
        public static final String COLUMN_PUBLIC_DESCRIPTION_HTML = "public_description_html";
        /**
         * Type: text
         */
        public static final String COLUMN_NAME = "name";
        /**
         * Type: integer
         */
        public static final String COLUMN_CREATED = "created";
        /**
         * Type: integer
         */
        public static final String COLUMN_CREATED_UTC = "created_utc";
        /**
         * user subscribed in the subreddit
         * Type: integer
         */
        public static final String COLUMN_USER = "user";
        /**
         * synchronization status
         * Type: integer
         */
        public static final String COLUMN_SYNC_STATUS = "sync_status";

    }

    /**
     * Table stores the votes of the user
     */
    public static final class Vote implements BaseColumns{
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_VOTE).build();
        public static final String CONTENT_TYPE = MULTIPLE_ITEM_MIMETYPE + CONTENT_AUTHORITY + "/" + PATH_VOTE;
        public static final String TABLE_NAME = "vote";
        /**
         * The thing voted
         * Type: long
         */
        public static final String COLUMN_THING_FULLNAME = "post";
        /**
         * The user that has voted in the post
         * Type: long
         */
        public static final String COLUMN_USER = "user";
        /**
         * The direction of the vote ( up or down )
         * Type: int
         */
        public static final String COLUMN_DIRECTION = "direction";
        /**
         * Synchronization status
         * type: integer
         */
        public static final String COLUMN_SYNC_STATUS = "sync_status";


        public static Uri buildVoteUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        /**
         * Get the vote ID from the Uri generated by buildVoteUri
         * @param uri uri generated by buildVoteUri
         * @return -1 if could not retrieve the the id. Otherwise, return the id
         */
        public static long getVoteId(Uri uri){
            if ( uri.getPathSegments().size() == 2)
                return Long.parseLong(uri.getPathSegments().get(1));
            return -1l;
        }
    }

    /**
     * Table stores the the users
     */
    public static final class User implements BaseColumns{
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_USER).build();
        public static final String CONTENT_TYPE = MULTIPLE_ITEM_MIMETYPE + CONTENT_AUTHORITY + "/" + PATH_USER;
        public static final String TABLE_NAME = "user";
        /**
         * The username of the account in question.
         * This attribute overrides the superclass's name attribute. Do not confuse an account's name which is
         * the account's username with a thing's name which is the thing's FULLNAME.
         * See API: Glossary for details on what FULLNAMEs are
         * Type: string
         */
        public static final String COLUMN_NAME = "name";
        /**
         * The curent user's access token
         * Type: string
         */
        public static final String COLUMN_ACCESSTOKEN = "access_token";
        /**
         * Flag idicate if the user is the current user
         * Type: boolean
         */
        public static final String COLUMN_CURRENT = "current";
        /**
         * whether the logged-in user has this user set as a friend
         * Type: integer
         */
        public static final String COLUMN_IS_FRIEND = "is_friend";
        /**
         * Type:
         */
        public static final String COLUMN_GOLD_EXPIRATION = "gold_expiration";
        /**
         * current modhash. not present if not your account
         * Type: text
         */
        public static final String COLUMN_MODHASH = "modhash";
        /**
         * user has provided an email address and got it verified?
         * Type: integer
         */
        public static final String COLUMN_HAS_VERIFIED_EMAIL = "has_verified_email";
        /**
         * Type: integer
         */
        public static final String COLUMN_CREATED_UTC = "created_utc";
        /**
         * Type: boolean
         */
        public static final String COLUMN_HIDE_FROM_ROBOTS = "hide_from_robots";
        /**
         * user's comment karma
         * Type: integer
         */
        public static final String COLUMN_COMMENT_KARMA = "comment_karma";
        /**
         * whether this account is set to be over 18
         * Type: integer
         */
        public static final String COLUMN_OVER_18 = "over_18";
        /**
         * Type: integer
         */
        public static final String COLUMN_GOLD_CREDDITS = "gold_creddits";
        /**
         * Type: integer
         */
        public static final String COLUMN_CREATED = "created";
        /**
         * reddit gold status
         * Type: integer
         */
        public static final String COLUMN_IS_GOLD = "is_gold";
        /**
         * whether this account moderates any subreddits
         * Type: integer
         */
        public static final String COLUMN_IS_MOD = "is_mod";
        /**
         * user's link karma
         * Type: integer
         */
        public static final String COLUMN_LINK_KARMA = "link_karma";
        /**
         * ID of the account; prepend t2_ to get fullname
         * Type: text
         */
        public static final String COLUMN_ID = "id";
        /**
         * user has unread mail? null if not your account
         * Type: integer
         */
        public static final String COLUMN_HAS_MAIL = "has_mail";
        /**
         * user has unread mod mail? null if not your account
         * Type: integer
         */
        public static final String COLUMN_HAS_MOD_MAIL = "has_mod_mail";
        /**
         * Type: integer
         */
        public static final String COLUMN_SYNC_STATUS = "sync_status";



        public static Uri buildUserUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        /**
         * Get the vote ID from the Uri generated by buildUserUri
         * @param uri uri generated by buildUserUri
         * @return -1 if could not retrieve the the id. Otherwise, return the id
         */
        public static long getUserId(Uri uri){
            return ContentUris.parseId(uri);
        }
    }
}
