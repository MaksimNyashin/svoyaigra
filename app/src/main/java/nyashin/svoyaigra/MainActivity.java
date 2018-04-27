package nyashin.svoyaigra;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    private Button newGame;
    @SuppressLint("StaticFieldLeak")
    private static Button showAnswer;
    private Button prev, next;
    @SuppressLint("StaticFieldLeak")
    private static TextView question;
    @SuppressLint("StaticFieldLeak")
    private static TextView answer;
    //private static EditText theme;
    @SuppressLint("StaticFieldLeak")
    private static ChooseThemeSpinner theme;
    public static String TAG = "MAX";
    static public final String PREFERENCES_FILE_NAME = "Settings";
    static public final String fileString = "file";
    static public String globalFileName = "";

    private static Pack pack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        question = findViewById(R.id.question);
        question.setTextColor(Color.rgb(0, 0, 0));
        answer = findViewById(R.id.answer);
        answer.setTextColor(Color.rgb(0, 0, 0));
        Spinner spinner = findViewById(R.id.theme);
        theme = new ChooseThemeSpinner(MainActivity.this, spinner);
        //theme.setTextColor(Color.rgb(0, 0, 0));

        newGame = findViewById(R.id.newGame);
        newGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenFileDialog fileDialog = new OpenFileDialog(MainActivity.this)
                        .setOpenDialogListener(openDialogListener);
                fileDialog.show();
            }
        });

        showAnswer = findViewById(R.id.showAnswer);
        showAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pack == null)
                    return;
                answer.setText(pack.getAnswer());
                showAnswer.setVisibility(View.INVISIBLE);
                answer.setVisibility(View.VISIBLE);
            }
        });

        prev = findViewById(R.id.prev);
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pack == null)
                    return;
                answer.setVisibility(View.INVISIBLE);
                pack.prevQuestion(MainActivity.this);
                showAnswer.setVisibility(View.VISIBLE);
            }
        });

        next = findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pack == null)
                    return;
                answer.setVisibility(View.INVISIBLE);
                pack.nextQuestion(MainActivity.this);
                showAnswer.setVisibility(View.VISIBLE);
            }
        });
        SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        globalFileName = sharedPreferences.getString(fileString, "");
        if (!globalFileName.equals(""))
            setPack();
    }

    public static void setQuestion(String question) {
        MainActivity.question.setText(question);
    }

    public static void setAnswer(String answer) {
        MainActivity.answer.setText(answer);
    }

    public static ChooseThemeSpinner getThemeSpinner() {
        return theme;
    }

    public static Pack getPack() {
        return MainActivity.pack;
    }

    OpenFileDialog.OpenDialogListener openDialogListener = new OpenFileDialog.OpenDialogListener() {
        @SuppressLint("CommitPrefEdits")
        @Override
        public void OnSelectedFile(String fileName) {
            //Log.d("MAX", "OnSelectedFile: " + fileName);
            globalFileName = fileName;
            SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
            sharedPreferences.edit().putString(fileString, fileName).apply();
            setPack();
        }
    };

    private void setPack() {
        InputStreamReader isr;
        try {
            isr = new InputStreamReader(new FileInputStream(new File(globalFileName)), "UTF-8");
            BufferedReader reader = new BufferedReader(isr);
            String s;
            StringBuilder rez = new StringBuilder();
            while ((s = reader.readLine()) != null)
            {
                rez.append(s);
                rez.append('\n');
            }
            //System.out.println(rez.toString());
            pack = new Pack(rez.toString(), MainActivity.this);
            pack.nextQuestion(MainActivity.this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Button getShowAnswer() {
        return MainActivity.showAnswer;
    }

    public static TextView getAnswer(){
        return answer;
    }
}
