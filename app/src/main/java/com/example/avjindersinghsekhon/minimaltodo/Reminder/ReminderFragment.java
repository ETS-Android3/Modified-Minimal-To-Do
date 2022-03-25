package com.example.avjindersinghsekhon.minimaltodo.Reminder;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.avjindersinghsekhon.minimaltodo.AddToDo.AddToDoFragment;
import com.example.avjindersinghsekhon.minimaltodo.Analytics.AnalyticsApplication;
import com.example.avjindersinghsekhon.minimaltodo.AppDefault.AppDefaultFragment;
import com.example.avjindersinghsekhon.minimaltodo.Main.MainActivity;
import com.example.avjindersinghsekhon.minimaltodo.Main.MainFragment;
import com.example.avjindersinghsekhon.minimaltodo.R;
import com.example.avjindersinghsekhon.minimaltodo.Utility.StoreRetrieveData;
import com.example.avjindersinghsekhon.minimaltodo.Utility.ToDoItem;
import com.example.avjindersinghsekhon.minimaltodo.Utility.TodoNotificationService;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import fr.ganfra.materialspinner.MaterialSpinner;

public class ReminderFragment extends AppDefaultFragment {
    public static final String EXIT = "com.avjindersekhon.exit";
    String theme;
    AnalyticsApplication app;
    private TextView mtoDoTextTextView;
    private Button mRemoveToDoButton;
    private MaterialSpinner mSnoozeSpinner;
    private String[] snoozeOptionsArray;
    private StoreRetrieveData storeRetrieveData;
    private ArrayList<ToDoItem> mToDoItems;
    private ToDoItem mItem;
    private TextView mSnoozeTextView;
    private boolean mSpinnerInitialized;
    private LinearLayout mLinearLayout;

    public static ReminderFragment newInstance() {
        return new ReminderFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        app = (AnalyticsApplication) getActivity().getApplication();
        app.send(this);

        theme = getActivity().getSharedPreferences(MainFragment.THEME_PREFERENCES, MODE_PRIVATE).getString(MainFragment.THEME_SAVED, MainFragment.LIGHTTHEME);
        if (theme.equals(MainFragment.LIGHTTHEME)) {
            getActivity().setTheme(R.style.CustomStyle_LightTheme);
        } else {
            getActivity().setTheme(R.style.CustomStyle_DarkTheme);
        }
        storeRetrieveData = new StoreRetrieveData(getContext(), MainFragment.FILENAME);
        mToDoItems = MainFragment.getLocallyStoredData(storeRetrieveData);

        ((AppCompatActivity) getActivity()).setSupportActionBar((Toolbar) view.findViewById(R.id.toolbar));

        mSpinnerInitialized = false;
        Intent i = getActivity().getIntent();
        UUID id = (UUID) i.getSerializableExtra(TodoNotificationService.TODOUUID);
        mItem = null;
        for (ToDoItem toDoItem : mToDoItems) {
            if (toDoItem.getIdentifier().equals(id)) {
                mItem = toDoItem;
                break;
            }
        }

        snoozeOptionsArray = getResources().getStringArray(R.array.snooze_options);

        mRemoveToDoButton = (Button) view.findViewById(R.id.toDoReminderRemoveButton);
        mtoDoTextTextView = (TextView) view.findViewById(R.id.toDoReminderTextViewBody);
        mSnoozeTextView = (TextView) view.findViewById(R.id.reminderViewSnoozeTextView);
        mSnoozeSpinner = (MaterialSpinner) view.findViewById(R.id.todoReminderSnoozeSpinner);

//        mtoDoTextTextView.setBackgroundColor(item.getTodoColor());
        mtoDoTextTextView.setText(mItem.getToDoText());

        mLinearLayout = (LinearLayout) view.findViewById(R.id.toDoReminderLinearLayout);
        if (theme.equals(MainFragment.LIGHTTHEME)) {
            mSnoozeTextView.setTextColor(getResources().getColor(R.color.secondary_text));
        } else {
            mSnoozeTextView.setTextColor(Color.WHITE);
            mSnoozeTextView.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_snooze_white_24dp, 0, 0, 0
            );
            mLinearLayout.setBackgroundColor(getResources().getColor(R.color.mdtp_dark_gray));
        }

        mRemoveToDoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.send(this, "Action", "Todo Removed from Reminder Activity");
                mToDoItems.remove(mItem);
                changeOccurred();
                saveData();
                closeApp();
            }
        });

//        mSnoozeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l){
//                if (!mSpinnerInitialized) {
//                    mSpinnerInitialized = true;
//                    return;
//                }
//                Date date = setNewTimeAndDate(i);
//                mItem.setToDoDate(date);
//                mItem.setHasReminder(true);
//                Log.d("OskarSchindler", "Date Changed to: " + date);
//                changeOccurred();
//                saveData();
//                closeApp();
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });

//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, snoozeOptionsArray);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_text_view, snoozeOptionsArray);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        mSnoozeSpinner.setAdapter(adapter);
//        mSnoozeSpinner.setSelection(0);
        mSnoozeSpinner.setSelection(mSnoozeSpinner.getCount() - 1);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected int layoutRes() {
        return R.layout.fragment_reminder;
    }

    private void closeApp() {
        Intent i = new Intent(getContext(), MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        i.putExtra(EXIT, true);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainFragment.SHARED_PREF_DATA_SET_CHANGED, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(EXIT, true);
        editor.apply();
        startActivity(i);

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getActivity().getMenuInflater().inflate(R.menu.menu_reminder, menu);
        return true;
    }

    private void changeOccurred() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainFragment.SHARED_PREF_DATA_SET_CHANGED, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(MainFragment.CHANGE_OCCURED, true);
//        editor.commit();
        editor.apply();
    }

    //    private Date addTimeToDate(int mins) {
//        app.send(this, "Action", "Snoozed", "For " + mins + " minutes");
    private Date setNewTimeAndDate(int spinnerPosition) {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(date);
//        calendar.add(Calendar.MINUTE, mins);
        switch (spinnerPosition) {
            case 0: {
                calendar.setTime(date);
                calendar.add(Calendar.MINUTE, 5);
                break;
            }
            case 1: {
                calendar.setTime(date);
                calendar.add(Calendar.MINUTE, 10);
                break;
            }
            case 2: {
                calendar.setTime(date);
                calendar.add(Calendar.MINUTE, 30);
                break;
            }
            case 3: {
                calendar.setTime(date);
                calendar.add(Calendar.MINUTE, 60);
                break;
            }
            case 4: {
                calendar.setTime(date);
                calendar.add(Calendar.HOUR, 24);
                break;
            }
            case 5: {
                calendar.setTime(date);
                calendar.add(Calendar.HOUR, 48);
                break;
            }
//            case 6: {
//                calendar = giveNextWeekday(Calendar.FRIDAY);
//                break;
//            }
//            case 7: {
//                calendar = giveNextWeekday(Calendar.MONDAY);
//                break;
//            }
            case 6: {
                calendar.setTime(date);
                calendar.add(Calendar.HOUR, 168);
                break;
            }

            default:
                throw new IllegalStateException("Unexpected value: " + spinnerPosition);
        }

        app.send(this, "Action", "Snoozed");
        return calendar.getTime();
    }

    private int valueFromSpinner() {
//        switch (mSnoozeSpinner.getSelectedItemPosition()) {
//            case 0:
//                return 10;
//            case 1:
//                return 30;
//            case 2:
//                return 60;
//            default:
//                return 0;
//        }
        return mSnoozeSpinner.getSelectedItemPosition();
    }

//    private Calendar giveNextWeekday (int weekday) {
//        Calendar today = Calendar.getInstance();
//        Date date = new Date();
//        today.setTime(date);
//        int dayOfCurrentWeek = today.get(Calendar.DAY_OF_WEEK);
//        int daysUntilWeekday = abs(weekday - dayOfCurrentWeek);
//        if (weekday < dayOfCurrentWeek ) daysUntilWeekday = 7 - daysUntilWeekday;
//        if (daysUntilWeekday == 0) daysUntilWeekday = 7;
//        Calendar nextWeekday = (Calendar)today.clone();
//        nextWeekday.add(Calendar.DAY_OF_WEEK, daysUntilWeekday);
//        return nextWeekday;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toDoReminderDoneMenuItem:
                Date date = setNewTimeAndDate(valueFromSpinner());
                mItem.setToDoDate(date);
                mItem.setHasReminder(true);
                changeOccurred();
                saveData();
                Log.d("OskarSchindler", "Date Changed to: " + date);
//                String text = getString(R.string.change_snooze) + date;
                String dateString = AddToDoFragment.formatDate("d MMM, yyyy", date);
                String timeString;
                String amPmString = "";

                if (DateFormat.is24HourFormat(getContext())) {
                    timeString = AddToDoFragment.formatDate("HH:mm", date);
                } else {
                    timeString = AddToDoFragment.formatDate("h:mm", date);
                    amPmString = AddToDoFragment.formatDate("a", date);
                }

                String text = String.format(getResources().getString(R.string.remind_date_and_time), dateString, timeString, amPmString);
                Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
                closeApp();
                Log.d("OskarSchindler", "Closed App");
                //foo
                return true;
            case R.id.toDoReminderExitMenuItem:
                closeApp();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveData() {
        try {
            storeRetrieveData.saveToFile(mToDoItems);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }
}
