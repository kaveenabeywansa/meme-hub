package com.app.memes.memesocialmedia.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.memes.memesocialmedia.CreatePost;
import com.app.memes.memesocialmedia.R;
import com.app.memes.memesocialmedia.interfaces.MemePostService;
import com.app.memes.memesocialmedia.models.Post;
import com.app.memes.memesocialmedia.services.RetrofitClient;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostListAdapter extends ArrayAdapter<Post> {
    private Context context;
    private List<Post> postList;
    private Boolean isOnline = true;

    private MemePostService memePostService;

    public PostListAdapter(Context context, List<Post> posts, Boolean isOnline) {
        super(context, R.layout.post_list_item, posts);
        this.context = context;
        this.postList = posts;
        this.isOnline = isOnline;

        memePostService = RetrofitClient.getClient().create(MemePostService.class);
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

        Button fire_btn = convertView.findViewById(R.id.btn_like_post);
        Button boo_btn = convertView.findViewById(R.id.btn_unlike_post);

        // btn events
        fire_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Post post = postList.get(position);
                post.setLikes(post.getLikes()+1);
                updateLikeCounter(post_likes, post.getLikes());
                updateUpvoteOnBackend(post.getId());
            }
        });

        boo_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Post post = postList.get(position);
                if (post.getLikes() > 0) {
                    post.setLikes(post.getLikes()-1);
                    updateLikeCounter(post_likes, post.getLikes());
                    updateDownvoteOnBackend(post.getId());
                }
            }
        });

        // set values
        post_author.setText((postList.get(position).getAuthor()));
        post_text.setText(postList.get(position).getText());
        updateLikeCounter(post_likes, postList.get(position).getLikes());
        if (!isOnline) {
            post_image.setBackgroundResource(R.drawable.offline_post);
        } else if (postList.get(position).getImageLink() != null && postList.get(position).getImageLink().length() > 0) {
            Picasso.with(context).load(postList.get(position).getImageLink()).into(post_image);
        } else {
            post_image.setBackgroundResource(R.drawable.notfound2);
        }

        return convertView;
    }

    private void updateLikeCounter(TextView likesEl, int count) {
        if (count >= 0) {
            likesEl.setText("\uD83D\uDD25" + count);
        }
    }

    private void updateUpvoteOnBackend(String postId) {
        memePostService.upVote(postId).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.i("Log", "Reacted to Meme Successful");
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.e("Error", t.toString());
            }
        });
    }

    private void updateDownvoteOnBackend(String postId) {
        memePostService.downVote(postId).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.i("Log", "Reacted to Meme Successful");
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.e("Error", t.toString());
            }
        });
    }
}
