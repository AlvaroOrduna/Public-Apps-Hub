package io.ordunaleon.publicappshub;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;

import io.ordunaleon.publicappshub.adapter.AppAdapter;
import io.ordunaleon.publicappshub.model.App;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Get the ListView
        ListView listView = (ListView) findViewById(R.id.home_listview);

        // Add some dummy data to populate the ListView
        ArrayList<App> appsArray = new ArrayList<>();
        String lorem = getResources().getString(R.string.lorem);
        appsArray.add(new App("Mi Villavesa", lorem, "Transport"));
        appsArray.add(new App("Provincial", lorem, "Transport"));
        appsArray.add(new App("Taxi App", lorem, "Transport"));
        appsArray.add(new App("Heading", lorem, "Transport"));

        // Create and attach the AppAdapter
        AppAdapter mAdapter = new AppAdapter(this, appsArray);
        listView.setAdapter(mAdapter);
    }
}
