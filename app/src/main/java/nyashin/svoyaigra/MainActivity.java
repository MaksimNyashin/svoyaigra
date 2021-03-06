package nyashin.svoyaigra;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import static java.lang.Math.abs;

public class MainActivity extends AppCompatActivity {
    private Button newGame;
    @SuppressLint("StaticFieldLeak")
    private static Button showAnswer;
    //private Button prev, next;
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

    @SuppressLint("StaticFieldLeak")
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
                if (!fileDialog.isBad())
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

        /*prev = findViewById(R.id.prev);
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
        });*/
        SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        globalFileName = sharedPreferences.getString(fileString, "");
        if (!globalFileName.equals(""))
            setPack();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // TODO: 25.05.2018 add button that openes images in browser or copies link 
        getMenuInflater().inflate(R.menu.values, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        new Thread(new Runnable() {
            @Override
            public void run() {
                takeScreenshot();
            }
        }).start();
        return true;
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


    /**
     * next and previous question
     * by swiping left and right
     * or pressing in right or left side of screen
     *
     * made with help
     * https://habr.com/post/118482
     */
    private float fromPosition, fromY;

    @SuppressLint("ResourceType")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float EPS = 20;
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                fromPosition = event.getX();
                fromY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                float toPosition = event.getX();
                if (fromPosition - toPosition > EPS)
                    nextQuestion();
                else
                    if (fromPosition - toPosition < -EPS)
                        prevQuestion();
                    else
                        if (abs(fromPosition - toPosition) < EPS && abs(fromY - event.getY()) < EPS &&
                                //fromY + EPS < findViewById(R.id.question).getHeight() + findViewById(R.id.topLayout).getHeight() &&
                                //fromY + EPS + findViewById(R.id.showAnswer).getHeight() + findViewById(R.id.answer).getHeight() < findViewById(R.id.wholeLayout).getHeight()&&
                                fromY + EPS < 1024 &&
                                fromY - EPS > findViewById(R.id.question).getTop())
                            if (fromPosition + EPS < findViewById(R.id.question).getWidth() / 2)
                                prevQuestion();
                            else
                            if (fromPosition - EPS > findViewById(R.id.question).getWidth() / 2)
                                    nextQuestion();
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    private void nextQuestion() {
        try {
            pack.nextQuestion(MainActivity.this);
        }
        catch (Exception e)
        {
            Log.e(TAG, "nextQuestion: " + e);
        }
    }
    private void prevQuestion() {
        try {
            pack.prevQuestion(MainActivity.this);
        }
        catch (Exception e)
        {
            Log.e(TAG, "nextQuestion: " + e);
        }
    }

    /**
     * taking a screenshot
     *
     * made with help
     * https://stackoverflow.com/questions/2661536/how-to-programmatically-take-a-screenshot
     */
    private void takeScreenshot() {
        Date now = new Date();
        final String nowDate = (String)(android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now));
        try {
            File folder = new File(Environment.getExternalStorageDirectory() + "/Pictures/SvoyaIgra");
            if (!folder.exists()) {
                if (folder.mkdir())
                    Log.d(TAG, "takeScreenshot: Package successfully created");
                else
                    Log.e(TAG, "takeScreenshot: Failed to create package");
            }
            final String mPath = folder.getPath() + "/" + nowDate + ".png";
            View view1= getWindow().getDecorView().getRootView();
            view1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(view1.getDrawingCache());
            view1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.PNG, quality, outputStream);
            outputStream.flush();
            outputStream.close();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    notification(nowDate, mPath);
                }
            }).start();
        } catch (Exception e)
        {
            Log.e(TAG, "takeScreenshot: " + e);
        }
    }

    private void notification(String date, String path) {
        // TODO: 24.05.2018 add notifications about screenshots with redirecting into gallery + why it doesn't show saved images
        /*Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Resources res = this.getResources();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentIntent(contentIntent)
                .setSmallIcon(android.R.drawable.ic_menu_camera)
                .setContentTitle("Screenshot was made")
                .setContentText(date)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(13, builder.build());*/
    }
}

