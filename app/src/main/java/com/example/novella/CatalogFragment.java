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
                "A story about love and friendship.",
                "Fantasy",
                15.00, "", "", true
        ));

        booksList.add(new Book(
                "2",
                "Pride and Prejudice",
                "Jane Austen",
                "A classic romantic novel.",
                "Romance",
                10.00, "", "", true
        ));

        booksList.add(new Book(
                "3",
                "Little Women",
                "Louisa May Alcott",
                "The story of four sisters.",
                "Fiction",
                12.00, "", "", true
        ));

        booksList.add(new Book(
                "4",
                "Frankenstein",
                "Mary Shelley",
                "A story about science and creation.",
                "Horror",
                13.00, "", "", true
        ));

        booksList.add(new Book(
                "5",
                "The Time Machine",
                "H. G. Wells",
                "A journey through time.",
                "Science Fiction",
                11.00, "", "", true
        ));
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