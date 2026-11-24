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

import com.google.firebase.storage.FirebaseStorage;

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

        loadBooks();

        filteredBooks.addAll(booksList);

        adapter = new BookAdapter(requireContext(), filteredBooks);
        listViewBooks.setAdapter(adapter);
        listViewBooks.setOnItemClickListener(
                (parent, itemView, position, id) -> {

                    Book book = filteredBooks.get(position);

                    android.content.Intent intent =
                            new android.content.Intent(
                                    requireContext(),
                                    BookDetailsActivity.class);

                    intent.putExtra("title", book.getTitle());
                    intent.putExtra("author", book.getAuthor());
                    intent.putExtra("description", book.getDescription());
                    intent.putExtra("genre", book.getGenre());
                    intent.putExtra("price", book.getPrice());
                    intent.putExtra("coverUrl", book.getCoverUrl());

                    startActivity(intent);
                });

        loadCovers();

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

        return view;
    }

    // Temporary books
    private void loadBooks() {

        booksList.add(new Book(
                "1",
                "The Little Prince",
                "Antoine de Saint-Exupery",
                "A young prince travels across the universe, discovering the meaning of love, friendship, and the things that truly matter in life.",
                "Fantasy",
                15.00, "", "", true
        ));

        booksList.add(new Book(
                "2",
                "Elizabeth Bennet challenges society's expectations while navigating love, pride, and misunderstandings in this timeless romantic classic.",
                "Jane Austen",
                "A classic romantic novel.",
                "Romance",
                10.00, "", "", true
        ));

        booksList.add(new Book(
                "3",
                "Little Women",
                "Louisa May Alcott",
                "Follow the four March sisters as they navigate love, family, ambition, and the challenges of growing up during the American Civil War.",
                "Fiction",
                12.00, "", "", true
        ));

        booksList.add(new Book(
                "4",
                "Frankenstein",
                "Mary Shelley",
                "A brilliant scientist creates life, but his ambition unleashes a tragedy that explores loneliness, responsibility, and the consequences of playing God.",
                "Horror",
                13.00, "", "", true
        ));

        booksList.add(new Book(
                "5",
                "The Time Machine",
                "H. G. Wells",
                "A daring inventor journeys thousands of years into the future, discovering a mysterious world where humanity has evolved in unexpected ways.",
                "Science Fiction",
                11.00, "", "", true
        ));
    }

    // Load book covers from Firebase Storage
    private void loadCovers() {

        String[] fileNames = {
                "The Little Prince.jpg",
                "pride_and_prejudice.jpg",
                "1868 Little Women.jpg",
                "Frankenstein.jpg",
                "The Time Machine.jpg"
        };

        for (int i = 0; i < booksList.size(); i++) {

            Book book = booksList.get(i);

            FirebaseStorage.getInstance()
                    .getReference("book-covers")
                    .child(fileNames[i])
                    .getDownloadUrl()
                    .addOnSuccessListener(uri -> {

                        book.setCoverUrl(uri.toString());
                        adapter.notifyDataSetChanged();

                    });
        }
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
