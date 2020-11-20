package ru.avdeev.android.a5_2_2_export_an_internal_file_to_an_external_file_and_back;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {

    private static final String FILE_NAME="fileName.txt";
    EditText editTextLogin;
    EditText editTextPassword;
    Button buttonLogin;
    Button buttonRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        editTextLogin = findViewById(R.id.edTextLogin);
        editTextPassword = findViewById(R.id.edTextPassword);
        buttonLogin = findViewById(R.id.btnLogin);
        buttonRegistration = findViewById(R.id.btnRegistration);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String login = editTextLogin.getText().toString();
                String pass = editTextPassword.getText().toString();
                if (login!="" || pass!="") {
                    String[] loginPassText = readFromInputStorage().split(",");
                    if (loginPassText[0].equals(editTextLogin.getText().toString()) && loginPassText[1].equals(editTextPassword.getText().toString())) {
                        showMyMessage(getString(R.string.successful_authorization),MainActivity.this);
                    } else {
                        showMyMessage(getString(R.string.unsuccessful_authorization), MainActivity.this);
                    }

                } else {
                    showMyMessage(getString(R.string.checkData), MainActivity.this);
                }
            }
        });

        buttonRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] loginPassText = {editTextLogin.getText().toString(), editTextPassword.getText().toString()};
                saveIntoInternalStorage(loginPassText);
            }
        });
    }

    private String readFromInputStorage() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(openFileInput(FILE_NAME)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void saveIntoInternalStorage(String[] loginPassText) {
        BufferedWriter writer = null;
        if (loginPassText[0].length()!=0 && loginPassText[1].length()!=0) {
            try {
                writer = new BufferedWriter(new OutputStreamWriter(openFileOutput(FILE_NAME, Context.MODE_PRIVATE)));
                writer.write(loginPassText[0] + "," + loginPassText[1]);
                showMyMessage(getString(R.string.userRegistered), this);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        showMyMessage(getString(R.string.incorrectData), this);
                    }
                }
            }
        } else {
            showMyMessage(getString(R.string.checkData),this);
        }
    }
    public void showMyMessage(String massage, Context context) {
        String text = massage;
        SpannableStringBuilder biggerText = new SpannableStringBuilder(text);
        biggerText.setSpan(new RelativeSizeSpan(1.35f), 0, text.length(), 0);
        Toast toast = Toast.makeText(context, biggerText, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }
}