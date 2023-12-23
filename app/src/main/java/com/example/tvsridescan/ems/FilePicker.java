package com.example.tvsridescan.ems;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tvsridescan.Library.AppVariables;
import com.example.tvsridescan.R;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FilePicker extends AppCompatActivity
{

    public final static String EXTRA_FILE_PATH = "file_path";
    public final static String EXTRA_SHOW_HIDDEN_FILES = "show_hidden_files";
    public final static String EXTRA_ACCEPTED_FILE_EXTENSIONS = "accepted_file_extensions";
    private final static String DEFAULT_INITIAL_DIRECTORY = "/sdcard/RideScan/";

    protected File Directory;
    protected ArrayList<File> Files;
    protected FilePickerListAdapter Adapter;
    protected boolean ShowHiddenFiles = false;
    protected String[] acceptedFileExtensions;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filepicker);

        listView = (ListView) findViewById(R.id.listview);

        if (Build.VERSION.SDK_INT >= 23)
        {
            if (checkPermission())
            {
                // Code for above or equal 23 API Oriented Device
                // Your Permission granted already .Do next code
            } else
             {
                requestPermission(); // Code for permission
            }
        }
        else
        {

            // Code for Below 23 API Oriented Device
            // Do next code
        }
        LayoutInflater inflator = (LayoutInflater)
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View emptyView = inflator.inflate(R.layout.empty_view, null);
        ((ViewGroup) listView.getParent()).addView(emptyView);
        listView.setEmptyView(emptyView);

        // Set initial directory
        Directory = new File(DEFAULT_INITIAL_DIRECTORY);

        // Initialize the ArrayList
        Files = new ArrayList<File>();

        // Set the ListAdapter
        Adapter = new FilePickerListAdapter(this, Files);
        listView.setAdapter(Adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AppVariables.FilePath = Files.get(i).getAbsolutePath();
                ShowDialog();
            }
        });

        // Initialize the extensions array to allow any file extensions
        acceptedFileExtensions = new String[] {".hex",".HEX"};

        // Get intent extras
        if(getIntent().hasExtra(EXTRA_FILE_PATH))
            Directory = new File(getIntent().getStringExtra(EXTRA_FILE_PATH));

        if(getIntent().hasExtra(EXTRA_SHOW_HIDDEN_FILES))
            ShowHiddenFiles = getIntent().getBooleanExtra(EXTRA_SHOW_HIDDEN_FILES, false);

        if(getIntent().hasExtra(EXTRA_ACCEPTED_FILE_EXTENSIONS)) {

            ArrayList<String> collection =
                    getIntent().getStringArrayListExtra(EXTRA_ACCEPTED_FILE_EXTENSIONS);

            acceptedFileExtensions = (String[])
                    collection.toArray(new String[collection.size()]);
        }
    }

    @Override
    protected void onResume() {
        refreshFilesList();
        super.onResume();
    }

    protected void refreshFilesList() {

        Files.clear();
        ExtensionFilenameFilter filter =
                new ExtensionFilenameFilter(acceptedFileExtensions);

        File[] files = Directory.listFiles(filter);

        if(files != null && files.length > 0) {

            for(File f : files) {

                if(f.isHidden() && !ShowHiddenFiles) {

                    continue;
                }

                Files.add(f);
            }

            Collections.sort(Files, new FileComparator());
        }

        Adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {

            finish();

        super.onBackPressed();
    }

    private class FilePickerListAdapter extends ArrayAdapter<File> {

        private List<File> mObjects;

        public FilePickerListAdapter(Context context, List<File> objects) {

            super(context, R.layout.list_item, android.R.id.text1, objects);
            mObjects = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View row = null;

            if(convertView == null) {

                LayoutInflater inflater = (LayoutInflater)
                        getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                row = inflater.inflate(R.layout.list_item, parent, false);
            }
            else
                row = convertView;

            File object = mObjects.get(position);

            ImageView imageView = (ImageView)row.findViewById(R.id.file_picker_image);
            TextView textView = (TextView)row.findViewById(R.id.file_picker_text);
            textView.setSingleLine(true);
            textView.setText(object.getName());

            if(object.isFile())
                imageView.setImageResource(R.drawable.dwnload);

            else
                imageView.setImageResource(R.drawable.foldericon);

            return row;
        }
    }

    private class FileComparator implements Comparator<File> {

        public int compare(File f1, File f2) {

            if(f1 == f2)
                return 0;

            if(f1.isDirectory() && f2.isFile())
                // Show directories above files
                return -1;

            if(f1.isFile() && f2.isDirectory())
                // Show files below directories
                return 1;

            // Sort the directories alphabetically
            return f1.getName().compareToIgnoreCase(f2.getName());
        }
    }

    private class ExtensionFilenameFilter implements FilenameFilter {

        private String[] Extensions;

        public ExtensionFilenameFilter(String[] extensions) {

            super();
            Extensions = extensions;
        }

        public boolean accept(File dir, String filename) {

            if(new File(dir, filename).isDirectory()) {

                // Accept all directory names
                return true;
            }

            if(Extensions != null && Extensions.length > 0) {

                for(int i = 0; i < Extensions.length; i++) {

                    if(filename.endsWith(Extensions[i])) {

                        // The filename ends with the extension
                        return true;
                    }
                }
                // The filename did not match any of the extensions
                return false;
            }
            // No extensions has been set. Accept all file extensions.
            return true;
        }
    }
//marshmallow permission
private static final int PERMISSION_REQUEST_CODE = 1;

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(FilePicker.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(FilePicker.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(FilePicker.this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(FilePicker.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }

    Dialog dialog;
    public void ShowDialog()
    {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_filepicker);
        final Button b1,b2;

        b1 = (Button) dialog.findViewById(R.id.submit1);
        b2 = (Button) dialog.findViewById(R.id.submit2);

        try
        {
            b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    b1.setAlpha((float) 0.5);
                    SystemClock.sleep(200);
                    dialog.hide();

                }
            });

            b2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    b1.setAlpha((float) 0.5);
                    SystemClock.sleep(200);
                    dialog.hide();
                    Intent i = new Intent(getApplicationContext(),ECUFlashing.class);
                    startActivity(i);

                }
            });
        }
        catch (Exception e)
        {

        }
        dialog.show();

    }



}