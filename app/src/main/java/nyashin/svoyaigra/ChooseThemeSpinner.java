package nyashin.svoyaigra;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

/**
 * Created by Maxim on 27.04.2018.
 *
 * This class contains
 * spinner, which helps users to go between themes
 */

class ChooseThemeSpinner{
    private Spinner spinner;
    private String[] list;
    private ArrayAdapter<?> adapter;
    Context context;

    ChooseThemeSpinner(Context context, Spinner newSpinner) {
        this.context = context;
        spinner = newSpinner;
        try {
            setList(MainActivity.getPack().getAllThemes());
        } catch (Exception e)
        {
            list = new String[1];
            list[0] = "Theme";
        }
        adapterSettings();
        spinner.setOnItemSelectedListener(onItemSelectedListener);
    }

    private AdapterView.OnItemSelectedListener onItemSelectedListener= new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
            MainActivity.getPack().setId(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    void setList(ArrayList<String> list) {
        this.list = new String[list.size()];
        this.list = list.toArray(this.list);
        adapterSettings();
        adapter.notifyDataSetChanged();
    }

    void setSelection(int position) {
        spinner.setSelection(position);
    }

    void adapterSettings() {
        adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
}
