package com.example.novella;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.widget.Button;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ManageBookAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Book> booksList;

    public ManageBookAdapter(Context c, ArrayList<Book> bl) {
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_manage_book,parent,false);
        }

        Book b = booksList.get(position);
        ImageView imageView = convertView.findViewById(R.id.imgItemCover);
        TextView titleText = convertView.findViewById(R.id.txtItemTitle);
        TextView authorText = convertView.findViewById(R.id.txtItemAuthor);
        TextView genreText = convertView.findViewById(R.id.txtItemGenre);
        TextView priceText = convertView.findViewById(R.id.txtItemPrice);

        titleText.setText(b.getTitle());
        authorText.setText(b.getAuthor());
        genreText.setText(b.getGenre());
        priceText.setText(b.getGenre());

        if (b.getCoverUrl() != null && !b.getCoverUrl().isEmpty()) {
            new ImageLoadTask(b.getCoverUrl(), imageView).execute();
        } else {
            imageView.setImageResource(android.R.drawable.ic_menu_report_image);
        }
        Button btnEdit = convertView.findViewById(R.id.btnEditBook);

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditBookActivity.class);
                intent.putExtra("bookId", b.getId());
                intent.putExtra("bookTitle", b.getTitle());
                intent.putExtra("bookAuthor", b.getAuthor());
                intent.putExtra("bokGenre", b.getGenre());
                intent.putExtra("booDescription", b.getDescription());
                intent.putExtra("bookPrice", b.getPrice());
                intent.putExtra("booCoverUrl", b.getCoverUrl());
                intent.putExtra("bookrecommended", b.isRecommended());
                intent.putExtra("bookAvailable", b.isAvailable());
                context.startActivity(intent);

            }
        });

        Button btnDelete = convertView.findViewById(R.id.btnDeleteBook);

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // delete the cover from Storage first
                if (b.getCoverUrl() != null && !b.getCoverUrl().isEmpty()) {
                    StorageReference imgRef = FirebaseStorage.getInstance().getReferenceFromUrl(b.getCoverUrl());
                    imgRef.delete().addOnSuccessListener(aVoid -> {
                        deleteBookRecord(b.getId());
                    }).addOnFailureListener(e -> Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show());
                } else {
                    deleteBookRecord(b.getId());
                }
            }
        });
        return convertView;
    }

    private void deleteBookRecord(String bookId) {
        FirebaseDatabase.getInstance().getReference("books")
                .child(bookId)
                .removeValue()
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Book deleted", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show());
    }

    private static class ImageLoadTask extends AsyncTask<Void, Void, Bitmap>{
        private String url;
        private  ImageView imageView;

        public ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void...voids) {
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                return BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                return  null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) imageView.setImageBitmap(bitmap);
        }
    }
}
