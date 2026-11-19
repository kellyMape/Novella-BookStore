package com.example.novella;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

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

    @Override
    public View getView(int position, View convertView,
                        ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_book, parent, false);
        }

        Book b = booksList.get(position);

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

        titleText.setText(b.getTitle());
        authorText.setText(b.getAuthor());
        descriptionText.setText(b.getDescription());
        priceText.setText("$ " + b.getPrice());

        imageView.setImageResource(
                android.R.drawable.ic_menu_report_image);

        return convertView;
    }
}