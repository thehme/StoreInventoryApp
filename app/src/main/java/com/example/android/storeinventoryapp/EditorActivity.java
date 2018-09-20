package com.example.android.storeinventoryapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class EditorActivity extends AppCompatActivity {
    private Uri mCurrentPetUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentPetUri = intent.getData();

        if (mCurrentPetUri != null) {
            setTitle(R.string.edit_book);
        } else {
            setTitle(R.string.add_book);
        }
    }
}
