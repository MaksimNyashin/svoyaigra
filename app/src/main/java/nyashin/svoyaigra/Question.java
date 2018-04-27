package nyashin.svoyaigra;

import android.view.View;

/**
 * Created by Maxim on 23.04.2018.
 *
 * This class contains
 * one question, including
 * task and answer
 */

public class Question {
    private String task;
    private String answer;

    Question()
    {
        task = "";
        answer = "";
    }

    void setTask(String task) {
        this.task = task;
    }
    public String getTask() {
        return task;
    }

    void setAnswer(String answer) {
        this.answer = answer;
    }
    String getAnswer() {
        return answer;
    }

    void write() {
        MainActivity.getAnswer().setVisibility(View.INVISIBLE);
        MainActivity.setQuestion(getTask());
        MainActivity.setAnswer("");
        MainActivity.getShowAnswer().setVisibility(View.VISIBLE);
    }

    @Override
    public String toString() {
        return task + "/n" + answer + "/n";
    }

}
