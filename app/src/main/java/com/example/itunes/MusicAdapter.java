package com.example.itunes;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

import static com.example.itunes.PlayerActivity.mediaPlayer;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {
    private Context context;
    static ArrayList<MusicFile> mFiles;

    MusicAdapter(Context context, ArrayList<MusicFile> mFiles){
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
        String songTitle = mFiles.get(position).getTitle();
        String artistName = mFiles.get(position).getArtist();
        holder.file_name.setText(songTitle);

        if(artistName == null) holder.artist_name.setText("Unknown Artist");
        else holder.artist_name.setText(artistName);

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
                if(mediaPlayer!=null){
                    mediaPlayer.stop();
                    mediaPlayer.release();
                }
                Intent intent = new Intent(context, PlayerActivity.class);
                intent.putExtra("position",position);
                context.startActivity(intent);
            }
        });
        holder.menuMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                PopupMenu popupMenu = new PopupMenu(context, view);
                popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener((item) -> {
                    switch (item.getItemId()) {
                        case R.id.delete:
                            deleteFile(position, view);
                            break;
                    }
                    return true;
                });
            }
        });
    }
    private void deleteFile(int position, View view){
        Uri contentUri = mFiles.get(position).getUri();
        File file = new File(contentUri.getPath());
        boolean deleted = file.delete();
        if(deleted){
            context.getContentResolver().delete(contentUri, null, null);
            mFiles.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mFiles.size());
            Snackbar.make(view, "File deleted : ", Snackbar.LENGTH_SHORT).show();
        }
        else{
            Snackbar.make(view, "File can't be deleted ", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    public class MusicViewHolder extends RecyclerView.ViewHolder{
        TextView file_name, artist_name;
        ImageView album_art, menuMore;
        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);
            file_name = itemView.findViewById(R.id.music_file_name);
            artist_name = itemView.findViewById(R.id.music_artist_name);
            album_art = itemView.findViewById(R.id.music_img);
            menuMore = itemView.findViewById(R.id.menuMore);
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

    void updateList(ArrayList<MusicFile> musicFileArrayList){
        mFiles = new ArrayList<>();
        mFiles.addAll(musicFileArrayList);
        notifyDataSetChanged();
    }
}
