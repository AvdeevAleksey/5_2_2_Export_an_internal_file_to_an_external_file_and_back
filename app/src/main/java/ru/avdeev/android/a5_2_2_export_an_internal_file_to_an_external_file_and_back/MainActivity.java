package ru.avdeev.android.a5_2_2_export_an_internal_file_to_an_external_file_and_back;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    private static final String INTERNAL_STORAGE_FILE_NAME="internalStorageFileName.txt";
    private static final String EXTERNAL_STORAGE_FILE_NAME="externalStorageFileName.txt";
    public static final int REQUEST_CODE_PERMISSION_WRITE_STORAGE = 11;
    EditText editTextLogin;
    EditText editTextPassword;
    Button buttonLogin;
    Button buttonRegistration;
    CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int permissionStatus = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionStatus == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_PERMISSION_WRITE_STORAGE);
        }

        initView();
    }

    private void initView() {
        editTextLogin = findViewById(R.id.edTextLogin);
        editTextPassword = findViewById(R.id.edTextPassword);
        buttonLogin = findViewById(R.id.btnLogin);
        buttonRegistration = findViewById(R.id.btnRegistration);
        checkBox = findViewById(R.id.changeStorage);

        checkBox.setChecked(false);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkedChanger()) {
                    loginPasswordVerifier(readFromInputStorage());
                } else {
                    loginPasswordVerifier(readFromExternalStorage());
                }
            }
        });

        buttonRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] loginPassText = {editTextLogin.getText().toString(), editTextPassword.getText().toString()};
                if (!checkedChanger()) {
                    saveIntoInternalStorage(loginPassText);
                } else {
                    saveIntoExternalStorage(loginPassText);
                }
            }
        });
    }

    private String readFromInputStorage() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(openFileInput(INTERNAL_STORAGE_FILE_NAME)));
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

    private String readFromExternalStorage() {
        if (isExternalStorageWritable()) {
            File reader = null;
            String logPass = null;
            reader = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),EXTERNAL_STORAGE_FILE_NAME);
            try {
                Scanner scanner = new Scanner(reader);
                logPass = scanner.nextLine();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return logPass;
        }
        return null;
    }

    private void saveIntoInternalStorage(String[] loginPassText) {
            BufferedWriter writer = null;
            if (loginPassText[0].length() != 0 && loginPassText[1].length() != 0) {
                try {
                    writer = new BufferedWriter(new OutputStreamWriter(openFileOutput(INTERNAL_STORAGE_FILE_NAME, Context.MODE_PRIVATE)));
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
                showMyMessage(getString(R.string.checkData), this);
            }
    }

    private boolean saveIntoExternalStorage(String[] loginPassText) {
        if (isExternalStorageWritable()) {
            File fileToSave = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), EXTERNAL_STORAGE_FILE_NAME);
            FileWriter fileWriter;
            try {
                fileWriter = new FileWriter(fileToSave, false);
                fileWriter.append(loginPassText[0] + "," + loginPassText[1]);
                fileWriter.close();
                showMyMessage(getString(R.string.file_saved),this);
            } catch (IOException e) {
                e.printStackTrace();
                showMyMessage(getString(R.string.file_save_error), this);
                return false;
            }
        }
        return true;
    }

    private void loginPasswordVerifier(String logPass){
        String login = editTextLogin.getText().toString();
        String pass = editTextPassword.getText().toString();
        if (login != "" || pass != "") {
            String[] loginPassText = logPass.split(",");
            if (loginPassText[0].equals(editTextLogin.getText().toString()) && loginPassText[1].equals(editTextPassword.getText().toString())) {
                showMyMessage(getString(R.string.successful_authorization), MainActivity.this);
            } else {
                showMyMessage(getString(R.string.unsuccessful_authorization), MainActivity.this);
            }

        } else {
            showMyMessage(getString(R.string.checkData), MainActivity.this);
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

    private boolean checkedChanger() {
        if (checkBox.isChecked()==false) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }
}