package com.example.novella;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class BookAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Book> booksList;

    public BookAdapter(Context c, ArrayList<Book> bl) {
        this.context = c;
        this.booksList = bl;
    }

    @Override
    public int getCount() {
        return booksList.size();
    }

    @Override
    public Object getItem(int i) {
        return booksList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
    // Display each book in the list
    @Override
    public View getView(int position, View convertView,
                        ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_book, parent, false);
        }

        Book b = booksList.get(position);
        // Connect views
        ImageView imageView = convertView.findViewById(
                R.id.imageViewBookItem);

        TextView titleText = convertView.findViewById(
                R.id.textViewBookTitle);

        TextView authorText = convertView.findViewById(
                R.id.textViewBookAuthor);

        TextView descriptionText = convertView.findViewById(
                R.id.textViewBookDescription);

        TextView priceText = convertView.findViewById(
                R.id.textViewBookPrice);
        // Display book information
        titleText.setText(b.getTitle());
        authorText.setText(b.getAuthor());
        descriptionText.setText(b.getDescription());
        priceText.setText("$ " + b.getPrice());

        String coverUrl = b.getCoverUrl();  // Get book cover URL

        imageView.setTag(coverUrl);
        imageView.setImageResource(
                android.R.drawable.ic_menu_report_image);
        // Load book cover
        if (coverUrl != null && !coverUrl.isEmpty()) {
            new ImageLoadTask(coverUrl, imageView).execute();
        }

        return convertView;
    }
    // Download book cover
    private static class ImageLoadTask
            extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;

        public ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        @Override // Download image in background
        protected Bitmap doInBackground(Void... voids) {
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection =
                        (HttpURLConnection) urlConnection.openConnection();
                connection.setDoInput(true);
                connection.connect();

                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                input.close();
                connection.disconnect();

                return bitmap;

            } catch (Exception e) {
                return null;
            }
        }


        // Display downloaded image
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (url.equals(imageView.getTag())) {
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }
}
