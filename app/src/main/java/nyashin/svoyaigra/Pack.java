package nyashin.svoyaigra;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Maxim on 23.04.2018.
 *
 * This class contains
 * all questions from one pocket
 * packed by themes
 */

class Pack {
    private static ArrayList<Theme> list;
    private int id;
    private Context context;

    Pack(String input, Context context)
    {
        this.context = context;
        list = new ArrayList<>();
        SharedPreferences sharedPreferences = context.getSharedPreferences(MainActivity.PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        int y = sharedPreferences.getInt(MainActivity.globalFileName, -1);
        id = y / 5;

        String z[] = input.split("\n\n");
        for (String i: z)
        {
            Theme theme = new Theme(i, context);
            if (theme.isBad())
                continue;
            list.add(theme);
        }
        MainActivity.getThemeSpinner().setList(getAllThemes());
        /*for (int i = 0; i  < id; i++)
            list.get(i).setId(5);*/
        if (id >= z.length)
            MainActivity.setQuestion("Package is finished");
        else
            if (id < 0)
                MainActivity.setQuestion("There is no questions before the first.");
            else {
                try {
                    list.get(id).setId(y % 5);
                    Theme.setTheme(id);
                } catch (Exception e) {
                    Log.e(MainActivity.TAG, "Pack: id=" + id + " y=" + y + " z.length=" + z.length);
                }
            }
    }

    void nextQuestion(Context context) {
        Question q;
        try {
            q = goFront(context);
        } catch (Exception e)
        {
            if (id >= list.size() - 1)
                MainActivity.setQuestion("Package is finished");
            else {
                id++;
                if (id < 0)
                    MainActivity.setQuestion("There is no questions before the first.");
                else {
                    list.get(id).setId(-1);
                    goFront(context);
                }
            }
            return;
        }
        if (q == null)
        {
            id++;
            if (id >= list.size())
            {
                MainActivity.setQuestion("Package is finished");
                return;
            }
            list.get(id).setId(-1);
            goFront(context);
        }
    }

    private Question goFront(Context context) {
        Question q;
        Theme.setTheme(id);
        q = list.get(id).nextQuestion(id, context);
        q.write();
        return q;
    }

    void prevQuestion(Context context) {
        Question q;
        try {
            q = goBack(context);
        } catch (Exception e)
        {
            if (id > 0) {
                id--;
                if (id >= list.size())
                    MainActivity.setQuestion("Package is finished");
                else {
                    list.get(id).setId(5);
                    goBack(context);
                }
            }
            else
                MainActivity.setQuestion("There is no questions before the first.");
            return;
        }
        if (q == null)
        {
            id--;
            if (id < 0)
            {
                MainActivity.setQuestion("There is no questions before the first.");
                return;
            }
            list.get(id).setId(5);
            goBack(context);
        }
    }

    private Question goBack(Context context) {
        Question q;
        Theme.setTheme(id);
        q = list.get(id).prevQuestion(id, context);
        q.write();
        return q;
    }

    String getAnswer(){
        return list.get(id).getAnswer();
    }

    ArrayList<String> getAllThemes() {
        ArrayList<String> answer = new ArrayList<>();
        for (Theme i: list)
            answer.add(i.getTheme());
        return answer;
    }

    public void setId(int id) {
        if (this.id == id)
            return;
        this.id = id;
        list.get(id).setId(0);
        list.get(id).addSharedPreferences(id, context);
    }
}
