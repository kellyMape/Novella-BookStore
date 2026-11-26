package com.example.novella;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Locale;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.widget.Toast;

public class CatalogFragment extends Fragment {

    ListView listViewBooks;
    SearchView searchBooks;
    Spinner spinnerGenre;

    ArrayList<Book> booksList;
    ArrayList<Book> filteredBooks;

    BookAdapter adapter;

    String searchText = "";
    String selectedGenre = "All";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_catalog, container, false);

        listViewBooks = view.findViewById(R.id.listViewBooks);
        searchBooks = view.findViewById(R.id.searchBooks);
        spinnerGenre = view.findViewById(R.id.spinnerGenre);

        booksList = new ArrayList<>();
        filteredBooks = new ArrayList<>();



        adapter = new BookAdapter(requireContext(), filteredBooks);
        listViewBooks.setAdapter(adapter);
        listViewBooks.setOnItemClickListener(
                (parent, itemView, position, id) -> {

                    Book book = filteredBooks.get(position);

                    android.content.Intent intent =
                            new android.content.Intent(
                                    requireContext(),
                                    BookDetailsActivity.class);
                    intent.putExtra("bookId", book.getId());
                    intent.putExtra("title", book.getTitle());
                    intent.putExtra("author", book.getAuthor());
                    intent.putExtra("description", book.getDescription());
                    intent.putExtra("genre", book.getGenre());
                    intent.putExtra("price", book.getPrice());
                    intent.putExtra("coverUrl", book.getCoverUrl());

                    startActivity(intent);
                });



        // Book genres
        String[] genres = {
                "All",
                "Romance",
                "Mystery",
                "Horror",
                "Fantasy",
                "Science Fiction",
                "Classics",
                "Fiction"
        };

        // Genre adapter
        ArrayAdapter<String> genreAdapter =
                new ArrayAdapter<String>(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        genres) {

                    @Override
                    public View getView(int position,
                                        View convertView,
                                        ViewGroup parent) {

                        TextView text = (TextView) super.getView(
                                position, convertView, parent);

                        text.setTextColor(Color.WHITE);
                        text.setTextSize(16);
                        text.setText(getItem(position) + "  ▾");

                        return text;
                    }

                    @Override
                    public View getDropDownView(int position,
                                                View convertView,
                                                ViewGroup parent) {

                        TextView text = (TextView) super.getDropDownView(
                                position, convertView, parent);

                        text.setTextColor(Color.parseColor("#183E33"));
                        text.setBackgroundColor(
                                Color.parseColor("#F5F1E7"));

                        return text;
                    }
                };

        genreAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);

        spinnerGenre.setAdapter(genreAdapter);

        // Search books
        searchBooks.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {

                    @Override
                    public boolean onQueryTextSubmit(String query) {

                        searchText = query;
                        filterBooks();

                        searchBooks.clearFocus();

                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {

                        searchText = newText;
                        filterBooks();

                        return true;
                    }
                });

        // Filter by genre
        spinnerGenre.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent,
                                               View view,
                                               int position,
                                               long id) {

                        selectedGenre = parent.getItemAtPosition(position)
                                .toString();

                        filterBooks();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
        // Load the shared book catalog from Firebase
        loadBooksFromFirebase();

        return view;
    }


    // Load books from Firebase Realtime Database
    private void loadBooksFromFirebase() {

        DatabaseReference booksReference =
                FirebaseDatabase.getInstance()
                        .getReference("books");

        booksReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                booksList.clear();

                // Read all books from Firebase
                for (DataSnapshot bookSnapshot : snapshot.getChildren()) {

                    Book book = bookSnapshot.getValue(Book.class);

                    if (book != null) {

                        // Save the real Firebase book ID
                        book.setId(bookSnapshot.getKey());

                        booksList.add(book);
                    }
                }

                // Apply current search and genre filters
                filterBooks();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                if (getContext() != null) {
                    Toast.makeText(
                            requireContext(),
                            "Error loading books: " + error.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                }
            }
        });
    }




    // Search and filter books
    private void filterBooks() {

        filteredBooks.clear();

        String query = searchText.trim()
                .toLowerCase(Locale.ROOT);

        for (Book book : booksList) {

            boolean matchesSearch =
                    book.getTitle().toLowerCase(Locale.ROOT)
                            .contains(query)
                            ||
                            book.getAuthor().toLowerCase(Locale.ROOT)
                                    .contains(query);

            boolean matchesGenre =
                    selectedGenre.equals("All")
                            ||
                            book.getGenre().equalsIgnoreCase(selectedGenre);

            if (matchesSearch && matchesGenre) {

                filteredBooks.add(book);
            }
        }

        adapter.notifyDataSetChanged();
    }
}
