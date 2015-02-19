package com.vanzstuff.readdit.data;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
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

    public CommentAdapter(Context context, List<Comment> data){
        mContext = context;
        setHasStableIds(true);
        mData = sortData(data, RedditApiUtils.KIND_LINK);
    }

    /**
     * Sort comments data. After this method the data will be
     * sorted in the following structure:
     *  parentc -> parentc's child -> parentc's child -> parentc2 -> parentc2's child
     */
    private List<Comment> sortData( List<Comment> data, String parentName) {
        List<Comment> parents = new ArrayList<>();
        Map<String, List<Comment>> children = new HashMap<>();
            for (Comment c : data ){
                if (c.parent.startsWith(parentName))
                    //get root comments
                    parents.add(c);
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
            sortedData.addAll(sortData(data, parent.name));
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
        public ViewHolder(View itemView) {
            super(itemView);
            user = (TextView) itemView.findViewById(R.id.comment_user);
            time = (TextView) itemView.findViewById(R.id.comment_time);
            content = (TextView) itemView.findViewById(R.id.comment_content);
        }
    }

    public static final class Comment {
        public long id;
        public String user;
        public long timestamp;
        public String content;
        public String parent;
        public String name;
    }
}
