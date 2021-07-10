package com.example.itunes;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.InputStream;
import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {
    private Context context;
    private ArrayList<MusicFiles> mFiles;

    MusicAdapter(Context context, ArrayList<MusicFiles> mFiles){
        this.context = context;
        this.mFiles = mFiles;
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.music_item, parent, false);
        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicAdapter.MusicViewHolder holder, int position) {
        holder.file_name.setText(mFiles.get(position).getTitle());
        holder.artist_name.setText(mFiles.get(position).getArtist());
        Bitmap image = getArtistImage(mFiles.get(position).getAlbumId());
        if(image != null){
            Glide.with(context).asBitmap()
                    .load(image)
                    .into(holder.album_art);
        }
        else{
            Glide.with(context)
                    .load(R.drawable.default_songcover)
                    .into(holder.album_art);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PlayerActivity.class);
                intent.putExtra("position",position);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    public class MusicViewHolder extends RecyclerView.ViewHolder{
        TextView file_name, artist_name;
        ImageView album_art;
        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);
            file_name = itemView.findViewById(R.id.music_file_name);
            artist_name = itemView.findViewById(R.id.music_artist_name);
            album_art = itemView.findViewById(R.id.music_img);
        }
    }

    private Bitmap getArtistImage(String albumid) {
        Bitmap artwork = null;
        try {
            Uri sArtworkUri = Uri
                    .parse("content://media/external/audio/albumart");
            Uri uri = ContentUris.withAppendedId(sArtworkUri,
                    Long.valueOf(albumid));
            ContentResolver res = context.getContentResolver();
            InputStream in = res.openInputStream(uri);
            artwork = BitmapFactory.decodeStream(in);

        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }
        return artwork;
    }
}
