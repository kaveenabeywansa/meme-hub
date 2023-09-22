package com.app.memes.memesocialmedia.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.memes.memesocialmedia.R;
import com.app.memes.memesocialmedia.models.Post;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

public class PostListAdapter extends ArrayAdapter<Post> {
    private Context context;
    private List<Post> postList;
    private Boolean isOnline = true;

    public PostListAdapter(Context context, List<Post> posts, Boolean isOnline) {
        super(context, R.layout.post_list_item, posts);
        this.context = context;
        this.postList = posts;
        this.isOnline = isOnline;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // create new inflator
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(R.layout.post_list_item, parent, false);

        // init comps
        TextView post_author = (TextView) convertView.findViewById(R.id.post_author);
        TextView post_text = (TextView) convertView.findViewById(R.id.post_text);
        TextView post_likes = (TextView) convertView.findViewById(R.id.post_likes);
        ImageView post_image = (ImageView) convertView.findViewById(R.id.post_image);

        // set values
        post_author.setText((postList.get(position).getAuthor()));
        post_text.setText(postList.get(position).getText());
        post_likes.setText("\uD83D\uDD25" + postList.get(position).getLikes());
        if (!isOnline) {
            post_image.setBackgroundResource(R.drawable.offline_post);
        } else if (postList.get(position).getImageLink() != null && postList.get(position).getImageLink().length() > 0) {
            Picasso.with(context).load(postList.get(position).getImageLink()).into(post_image);
        } else {
            post_image.setBackgroundResource(R.drawable.notfound2);
        }

        return convertView;
    }
}
