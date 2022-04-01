package com.example.avjindersinghsekhon.minimaltodo.About;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.avjindersinghsekhon.minimaltodo.AppDefault.AppDefaultActivity;
import com.example.avjindersinghsekhon.minimaltodo.Main.MainFragment;
import com.example.avjindersinghsekhon.minimaltodo.R;


public class FeedbackActivity extends AppCompatActivity{

    private EditText email,subject,pesan;
    String theme;
    Button kirim;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        theme = getSharedPreferences(MainFragment.THEME_PREFERENCES, MODE_PRIVATE).getString(MainFragment.THEME_SAVED, MainFragment.LIGHTTHEME);
        if (theme.equals(MainFragment.DARKTHEME)) {
            Log.d("OskarSchindler", "One");
            setTheme(R.style.CustomStyle_DarkTheme);
        } else {
            Log.d("OskarSchindler", "One");
            setTheme(R.style.CustomStyle_LightTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_layout);
        email= (EditText) findViewById(R.id.email);
        subject= (EditText) findViewById(R.id.subject);
        pesan= (EditText) findViewById(R.id.pesan);
        kirim= (Button) findViewById(R.id.btn_kirim);


        kirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (subject.length() <= 0) {
                    subject.setError(getString(R.string.empty_error));
                } else if(pesan.length() <= 0){
                    pesan.setError(getString(R.string.empty_error));
                } else {
                    String emailFeedback = email.getText().toString();
                    String subjectFeedback = subject.getText().toString();
                    String pesanFeedback = pesan.getText().toString();

                    Intent kirim = new Intent(Intent.ACTION_SEND);
                    kirim.putExtra(Intent.EXTRA_EMAIL, emailFeedback);
                    kirim.putExtra(Intent.EXTRA_SUBJECT, subjectFeedback);
                    kirim.putExtra(Intent.EXTRA_TEXT, pesanFeedback);
                    kirim.setType("message/rfc822");
                    kirim.setPackage("com.google.android.gm");
                    startActivity(kirim);
                }
            }
        });
    }

}
