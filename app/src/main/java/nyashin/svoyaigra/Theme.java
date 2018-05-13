package nyashin.svoyaigra;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Maxim on 23.04.2018.
 *
 * This class contains
 * 5 questions packed in one theme
 * ond the topic.
 */

class Theme {
    private String name;
    private Question quest[] = new Question[5];
    private int id;
    private String extra = "";
    private Context context;

    Theme(String input, Context context)
    {
        this.context = context;
        System.out.println(input);
        String z[] = input.split("\n");
        if (z.length < 6)
        {
            id = -14313;
            return;
        }
        name = z[0];
        int q = 1;
        StringBuilder s = new StringBuilder();
        String author = "Автор";
        while ((z[q].charAt(0) < '0' || z[q].charAt(0) > '9')) {
            boolean bbb = true;
            for (int j = 0; j < 5; j++)
                if (z[q].charAt(j) != author.charAt(j))
                    bbb = false;
            if (!bbb)
                s.append(z[q]).append(" ");
            q++;
        }
        setExtra(s.toString());
        int y = -1;
        for (; q < z.length; q++)
        {
            if (z[q].charAt(0) == 'О')
            {
                quest[y].setAnswer(z[q]);
            }
            else if (z[q].charAt(0) >= '0' && z[q].charAt(0) <= '9')
            {
                y++;
                if (y >= 5)
                    Log.e(MainActivity.TAG, "Theme: " + input);
                //try {
                    quest[y] = new Question();
                /*} catch (Exception e)
                {
                    for (int i = 0; i < 5; i++)
                        System.out.println(quest[i].toString());
                    System.out.println(Arrays.toString(z));
                }*/
                String otvet = "Ответ:+";
                String z_[] = z[q].split(otvet);
                quest[y].setTask(z_[0]);
                if (z_.length > 1)
                {
                    quest[y].setAnswer(otvet + z_[1]);
                }
            }
            else {
                if (quest[y].getAnswer().equals(""))
                    quest[y].setTask(quest[y].getTask() + '\n' + z[q]);
                else
                    quest[y].setAnswer(quest[y].getAnswer() + '\n' + z[q]);

            }
        }
        id = -1;
    }

    /*public Question getQuest(int id) {
        return quest[id];
    }*/

    Question prevQuestion(int themeId, Context context)
    {
        id--;
        addSharedPreferences(themeId, context);
        if (id >= 0)
            return quest[id];
        return null;
    }

    Question nextQuestion(int themeId, Context context) {
        id++;
        addSharedPreferences(themeId, context);
        if (id < 5)
            return quest[id];
        return null;
    }

    static void setTheme(int position)
    {
        MainActivity.getThemeSpinner().setSelection(position);
    }

    boolean isBad()
    {
        return id < -13;
    }

    String getAnswer() {
        return quest[id].getAnswer();
    }

    @SuppressLint("ShowToast")
    public void setId(int id) {
        this.id = id;
        if (0 <= id && id < 5)
            quest[id].write();
        if (!extra.equals(""))
            Toast.makeText(context, extra, Toast.LENGTH_LONG).show();
    }

    void addSharedPreferences(int themeId, Context context)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MainActivity.PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(MainActivity.globalFileName, themeId * 5 + id - 1).apply();
    }

    String getTheme() {
        return name;
    }

    private void setExtra(String newExtra){
        extra = newExtra;
    }
}
