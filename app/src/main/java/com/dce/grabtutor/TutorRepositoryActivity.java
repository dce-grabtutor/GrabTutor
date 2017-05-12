package com.dce.grabtutor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;
import com.dce.grabtutor.Handler.InputStreamVolleyRequest;
import com.dce.grabtutor.Model.Account;
import com.dce.grabtutor.Model.RepositoryFile;
import com.dce.grabtutor.Model.URI;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class TutorRepositoryActivity extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST = 1;
    private static final String TAG = UploadTutorActivity.class.getSimpleName();
    ProgressDialog dialog;
    RecyclerView rv;
    RepositoryFilesAdapter adapter;
    private String selectedFilePath;
    private String SERVER_URL = URI.TUTOR_UPLOAD_REQUEST + "?acc_id=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_repository);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("My Repository");

        SERVER_URL += Account.loggedAccount.getAcc_id();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });

        rv = (RecyclerView) findViewById(R.id.rvRepositoryFilesList);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        this.finish();
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter = new RepositoryFilesAdapter(this);
        rv.setAdapter(adapter);
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        //sets the select file to all types of files
        intent.setType("*/*");
        //allows to select data and return it
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //starts new activity to select file and return data
        startActivityForResult(Intent.createChooser(intent, "Choose File to Upload.."), PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_FILE_REQUEST) {
                if (data == null) {
                    //no data present
                    return;
                }

                Uri selectedFileUri = data.getData();
                selectedFilePath = FilePath.getPath(this, selectedFileUri);
                Log.i(TAG, "Selected File Path:" + selectedFilePath);

                if (selectedFilePath != null && !selectedFilePath.equals("")) {
//                    tvFileName.setText(selectedFilePath);
                    dialog = new ProgressDialog(this);
                    dialog = ProgressDialog.show(TutorRepositoryActivity.this, "", "Uploading File", true);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //creating new thread to handle Http Operations
                            uploadFile(selectedFilePath);
                        }
                    }).start();
                } else {
                    Toast.makeText(this, "Cannot retrieve file path, please check if storage permission is enabled on your settings", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //android upload file to server
    public int uploadFile(final String selectedFilePath) {
        int serverResponseCode = 0;

        HttpURLConnection connection;
        DataOutputStream dataOutputStream;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 5 * 1024 * 1024;
        File selectedFile = new File(selectedFilePath);

        String[] parts = selectedFilePath.split("/");
        final String fileName = parts[parts.length - 1];

        if (!selectedFile.isFile()) {
            dialog.dismiss();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    tvFileName.setText("Source File Doesn't Exist: " + selectedFilePath);
                }
            });
            return 0;
        } else {
            try {
                FileInputStream fileInputStream = new FileInputStream(selectedFile);
                URL url = new URL(SERVER_URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);//Allow Inputs
                connection.setDoOutput(true);//Allow Outputs
                connection.setUseCaches(false);//Don't use a cached Copy
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                connection.setRequestProperty("fileToUpload", selectedFilePath);

                //creating new dataoutputstream
                dataOutputStream = new DataOutputStream(connection.getOutputStream());

                //writing bytes to data outputstream
                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data; acc_id=\"" + Account.loggedAccount.getAcc_id() + "\";name=\"fileToUpload\";filename=\""
                        + selectedFilePath + "\"" + lineEnd);
                dataOutputStream.writeBytes(lineEnd);

                //returns no. of bytes present in fileInputStream
                bytesAvailable = fileInputStream.available();
                //selecting the buffer size as minimum of available bytes or 1 MB
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                //setting the buffer as byte array of size of bufferSize
                buffer = new byte[bufferSize];

                //reads bytes from FileInputStream(from 0th index of buffer to buffersize)
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                //loop repeats till bytesRead = -1, i.e., no bytes are left to read
                while (bytesRead > 0) {
                    //write the bytes read from inputstream
                    dataOutputStream.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                dataOutputStream.writeBytes(lineEnd);
                dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                serverResponseCode = connection.getResponseCode();
                String serverResponseMessage = connection.getResponseMessage();

                Log.i(TAG, "Server Response is: " + serverResponseMessage + ": " + serverResponseCode);

                //response code of 200 indicates the server status OK
                if (serverResponseCode == 200) {
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(connection.getInputStream()));

                    String inputLine;
                    StringBuffer response = new StringBuffer();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    final String result = response.toString();
                    System.out.println(result);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jsonObject = new JSONObject(result);

                                if (jsonObject.getBoolean("success")) {
                                    Toast.makeText(TutorRepositoryActivity.this, jsonObject.getString("file_upload"), Toast.LENGTH_SHORT).show();
                                    RepositoryFile repositoryFile = new RepositoryFile();
                                    repositoryFile.setAcc_id(Account.loggedAccount.getAcc_id());
                                    repositoryFile.setFile_name(fileName);
                                    repositoryFile.setFile_download_url(URI.REPOSITORY_FILES_DOWNLOAD + "?acc_id=" + Account.loggedAccount.getAcc_id() + "&file_name=" + fileName);

                                    RepositoryFile.repositoryFiles.add(repositoryFile);
                                    adapter.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(TutorRepositoryActivity.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
                }

                connection.disconnect();
                fileInputStream.close();
                dataOutputStream.flush();
                dataOutputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(TutorRepositoryActivity.this, "File Not Found", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(TutorRepositoryActivity.this, "URL error!", Toast.LENGTH_SHORT).show();

                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(TutorRepositoryActivity.this, "Cannot Read/Write File!", Toast.LENGTH_SHORT).show();

                    }
                });
            }
            dialog.dismiss();
            return serverResponseCode;
        }
    }

    public class RepositoryFilesAdapter extends RecyclerView.Adapter<RepositoryFilesAdapter.ViewHolder> {

        Context context;

        public RepositoryFilesAdapter(Context context) {
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_repository_files_list, viewGroup, false);
            ViewHolder vh = new ViewHolder(v);

            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final RepositoryFile repositoryFile = RepositoryFile.repositoryFiles.get(position);

            holder.tvFileName.setText(repositoryFile.getFile_name());
            holder.btnDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InputStreamVolleyRequest request = new InputStreamVolleyRequest(Request.Method.GET, repositoryFile.getFile_download_url(),
                            new Response.Listener<byte[]>() {
                                @Override
                                public void onResponse(byte[] response) {
                                    try {
                                        if (response != null) {

                                            File path = Environment.getExternalStorageDirectory();
                                            File file = new File(path, repositoryFile.getFile_name());
                                            FileOutputStream outputStream = new FileOutputStream(file);
//                                            outputStream = openFileOutput(name, Context.MODE_PRIVATE);
                                            outputStream.write(response);
                                            outputStream.close();

                                            Toast.makeText(TutorRepositoryActivity.this, "Download Complete. File is located in Internal Storage", Toast.LENGTH_LONG).show();
                                        }
                                    } catch (Exception e) {
                                        Log.d("KEY_ERROR", "UNABLE TO DOWNLOAD FILE");
                                        e.printStackTrace();
                                        Toast.makeText(context, "Download Failed. Please Check your Internet Connection.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    }, null);

                    RequestQueue requestQueue = Volley.newRequestQueue(TutorRepositoryActivity.this, new HurlStack());
                    requestQueue.add(request);
                }
            });
        }

        @Override
        public int getItemCount() {
            return RepositoryFile.repositoryFiles.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            CardView cv;

            TextView tvFileName;
            ImageButton btnDownload;

            public ViewHolder(View itemView) {
                super(itemView);

                cv = (CardView) itemView.findViewById(R.id.cv);

                tvFileName = (TextView) itemView.findViewById(R.id.tvFileName);
                btnDownload = (ImageButton) itemView.findViewById(R.id.btnDownload);
            }
        }
    }

}
