package com.vanzstuff.readdit.data;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vanzstuff.readdit.redditapi.RedditApiUtils;
import com.vanzstuff.redditapp.R;

import java.util.*;

/**
 * This Adapter is used in the ExpandableListView to show the link's comments
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>  {

    private final Context mContext;
    private final List<Comment> mData;
    private final Map<Integer, Integer> mColors;

    public CommentAdapter(Context context, List<Comment> data){
        mContext = context;
        setHasStableIds(true);
        mData = sortData(data, RedditApiUtils.KIND_LINK, 0);
        mColors = new HashMap<>();
        int white = Color.argb(255, 255, 255, 255);
        mColors.put(0, white);
    }

    /**
     * Sort comments data. After this method the data will be
     * sorted in the following structure:
     *  parentc -> parentc's child -> parentc's child -> parentc2 -> parentc2's child
     */
    private List<Comment> sortData( List<Comment> data, String parentName, int currentDepth) {
        List<Comment> parents = new ArrayList<>();
        Map<String, List<Comment>> children = new HashMap<>();
            for (Comment c : data ){
                if (c.parent.startsWith(parentName)) {
                    //get root comments
                    c.depth = currentDepth;
                    parents.add(c);
                }
        }
        //We have the parents. Now, sort them by creation time
        Collections.sort(parents, new Comparator<Comment>() {
            @Override
            public int compare(Comment lhs, Comment rhs) {
                return (int) (lhs.timestamp - rhs.timestamp);
            }
        });
        List<Comment> sortedData = new ArrayList<>(data.size());
        for ( Comment parent : parents ) {
            sortedData.add(parent);
            sortedData.addAll(sortData(data, parent.name, currentDepth + 1));
        }
        return sortedData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent.inflate(mContext, R.layout.comment_view, null));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Comment c = mData.get(position);
        holder.user.setText(c.user);
        holder.time.setText(DateUtils.formatElapsedTime(c.timestamp));
        holder.content.setText(c.content);
        int depth = mContext.getResources().getInteger(R.integer.comment_depth_width);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(depth, ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMargins(depth * c.depth, 0,0,0);
        holder.depthSpace.setLayoutParams(params);
        holder.depthSpace.setBackgroundColor(getDepthColor(c.depth));
    }

    private int getDepthColor(int depth) {
        if (mColors.containsKey(depth))
            return mColors.get(depth);
        Random rnd = new Random();
        int color = Color.rgb(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        mColors.put(depth, color);
        return color;
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public long getItemId(int position) {
        return mData.get(position).id;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView user;
        public final TextView time;
        public final TextView content;
        public final LinearLayout depthSpace;
        public ViewHolder(View itemView) {
            super(itemView);
            user = (TextView) itemView.findViewById(R.id.comment_user);
            time = (TextView) itemView.findViewById(R.id.comment_time);
            content = (TextView) itemView.findViewById(R.id.comment_content);
            depthSpace = (LinearLayout) itemView.findViewById(R.id.comment_depth_space);
        }
    }

    public static final class Comment {
        public long id;
        public int depth;
        public String user;
        public long timestamp;
        public String content;
        public String parent;
        public String name;
    }
}
