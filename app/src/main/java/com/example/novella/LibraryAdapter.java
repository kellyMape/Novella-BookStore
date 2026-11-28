package com.example.novella;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class LibraryAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Book> books;

    public LibraryAdapter(Context context, ArrayList<Book> books) {
        this.context = context;
        this.books = books;
    }

    @Override
    public int getCount() {
        return books.size();
    }

    @Override
    public Object getItem(int position) {
        return books.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_library, parent, false);
        }

        Book b = books.get(position);

        ImageView cover = convertView.findViewById(R.id.imgLibraryCover);
        TextView titleText = convertView.findViewById(R.id.txtLibraryTitle);
        TextView authorText = convertView.findViewById(R.id.txtLibraryAuthor);
        Button btnRead = convertView.findViewById(R.id.btnReadBook);

        titleText.setText(b.getTitle());
        authorText.setText(b.getAuthor());

        if (b.getCoverUrl() != null) {
            new ImageLoadTask(b.getCoverUrl(), cover).execute();
        }

        // Open the book to read it
        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,
                        ReadBookActivity.class);
                intent.putExtra("bookTitle", b.getTitle());
                intent.putExtra("bookAuthor", b.getAuthor());
                intent.putExtra("bookDescription", b.getDescription());
                intent.putExtra("bookFileUrl", b.getFileUrl());
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    // Same ImageLoadTask as the teacher (with onPostExecute)
    private class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {
        private String url;
        private ImageView imageView;

        public ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                return BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            if (result != null) {
                imageView.setImageBitmap(result);
            }
        }
    }
}