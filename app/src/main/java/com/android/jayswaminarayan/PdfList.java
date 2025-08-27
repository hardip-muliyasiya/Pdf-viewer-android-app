
package com.android.jayswaminarayan;

import static android.os.Build.VERSION.SDK_INT;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.pdftron.pdf.config.ViewerConfig;
import com.pdftron.pdf.controls.DocumentActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class PdfList extends AppCompatActivity {

    private EditText title;
    private ListView pdfList;
    private CustomAdapter myAdapter;
    public Drawable pdfIconImage;
    private ArrayList<File> fList = new ArrayList<File>();
    private ArrayList<File> sList = new ArrayList<File>();

    private File[] pdfs;
    private File[] sPdfs;
    private String[] pdfNames;
    private String[] sPdfNames;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_list);

        title = findViewById(R.id.pdfTitle);
        pdfList = findViewById(R.id.pdfList);
        int iRes = getResources().getIdentifier("@drawable/ic_picture_as_pdf_black_24dp", null, getPackageName());
        pdfIconImage = getResources().getDrawable(iRes);

        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                doSearch(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        if (checkPermissionREAD_EXTERNAL_STORAGE(this)) {
            listAllPdfs();
            setAdapter(pdfNames, pdfs);
        }
        else {
            Toast.makeText(this, "File permission required", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 1);
            if (SDK_INT >= Build.VERSION_CODES.R) {
                if(!Environment.isExternalStorageManager()) {
                    Intent i = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    Uri u = Uri.fromParts("package", getPackageName(), null);
                    i.setData(u);
                    startActivity(i);
                }
            }
        }
    }

    private void listAllPdfs() {
        searchFiles(Environment.getExternalStorageDirectory());
        pdfs = new File[fList.size()];
        pdfNames = new String[fList.size()];
        int i = 0;
        for (File f : fList) {
            pdfs[i] = f;
            pdfNames[i++] = f.getName();
        }
    }

    private void searchFiles(File dir) {
        String pdfPattern = ".pdf";
        File fileList[] = dir.listFiles();
        if (fileList != null) {
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {
                    searchFiles(fileList[i]);
                } else {
                    if (fileList[i].getName().endsWith(pdfPattern)) {
                        fList.add(fileList[i]);
                    }
                }
            }
        }

//        try {
//            String selection = "_data LIKE '%.pdf'";
//            Cursor cursor = getApplicationContext().getContentResolver().query(MediaStore.Files.getContentUri("external"), null, selection, null, "_id DESC");
//            if (cursor == null) {
//                Toast.makeText(this, "No pdfs found", Toast.LENGTH_SHORT).show();
//                return;
//            } else if (cursor.getCount() <= 0 || !cursor.moveToFirst()) {
//                Toast.makeText(this, "No pdfs found", Toast.LENGTH_SHORT).show();
//                return;
//            } else if (!cursor.moveToFirst()) {
//                Toast.makeText(this, "No pdfs found", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            Toast.makeText(this, ""+ cursor.getCount()+" col : "+cursor.getColumnCount(), Toast.LENGTH_SHORT).show();
//            pdfNames = new String[cursor.getCount()];
//            pdfs = new File[cursor.getCount()];
//            int i = 0;
//            do {
//                pdfs[i] = new File(cursor.getString(1));
//                pdfNames[i++] = new File(cursor.getString(1)).getName();
//            } while (cursor.moveToNext());
//            setAdapter(pdfNames, pdfs);
////            Toast.makeText(this, "pdfNames = "+pdfNames.length, Toast.LENGTH_SHORT).show();
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(this, "error : "+e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
    }

    private void setAdapter(String names[], File files[]) {
        if (checkPermissionREAD_EXTERNAL_STORAGE(this)) {
            myAdapter = new CustomAdapter(this, this.getAssets(), names);
            pdfList.setAdapter(myAdapter);

            if (SDK_INT >= Build.VERSION_CODES.R) {
                if(!Environment.isExternalStorageManager()) {
                    Intent i = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    Uri u = Uri.fromParts("package", getPackageName(), null);
                    i.setData(u);
                    startActivity(i);
                }
            }
        }
    
        pdfList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewerConfig config = new ViewerConfig.Builder().openUrlCachePath(PdfList.this.getCacheDir().getAbsolutePath()).build();
                final Uri fileLink = Uri.fromFile(files[position]);
                DocumentActivity.openDocument(PdfList.this, fileLink, config);
            }
        });
    }

    private void doSearch(CharSequence s) {
        sList.clear();
        for(int i=0; i<pdfNames.length; i++) {
            String lpdf = pdfNames[i].toLowerCase(Locale.ROOT);
            if(lpdf.contains(s.toString().toLowerCase()))
                sList.add(pdfs[i]);
        }
        sPdfs = new File[sList.size()];
        sPdfNames = new String[sList.size()];
        int i = 0;
        for(File f : sList) {
            sPdfs[i] = f;
            sPdfNames[i++] = f.getName();
        }
        setAdapter(sPdfNames, sPdfs);
    }

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    public boolean checkPermissionREAD_EXTERNAL_STORAGE(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,android.Manifest.permission.MANAGE_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context, android.Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[] { android.Manifest.permission.READ_EXTERNAL_STORAGE },
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[] { permission },
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    listAllPdfs();
                    setAdapter(pdfNames, pdfs);
                    if (SDK_INT >= Build.VERSION_CODES.R) {
                        if(!Environment.isExternalStorageManager()) {
                            Intent i = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                            Uri u = Uri.fromParts("package", getPackageName(), null);
                            i.setData(u);
                            startActivity(i);
                        }
                    }
                } else {
                    Toast.makeText(this, "File permission required, please provide from settings", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }
}
