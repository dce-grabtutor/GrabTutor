package com.dce.grabtutor;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dce.grabtutor.Model.FileUpload;

public class FileUploadFilesListActivity extends AppCompatActivity {

    RecyclerView rv;
    FileUploadListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_upload_files_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Repository Files");

        rv = (RecyclerView) findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter = new FileUploadListAdapter(this);
        rv.setAdapter(adapter);
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

    public class FileUploadListAdapter extends RecyclerView.Adapter<FileUploadListAdapter.ViewHolder> {

        Context context;

        FileUploadListAdapter(Context context) {
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_file_upload_list, viewGroup, false);
            ViewHolder vh = new ViewHolder(v);

            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final int final_position = position;
            FileUpload fileUpload = FileUpload.fileUploads.get(position);

            holder.cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            holder.tvFileUploadName.setText("FILE NAME");
        }

        @Override
        public int getItemCount() {
            return FileUpload.fileUploads.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            CardView cv;
            TextView tvFileUploadName;

            public ViewHolder(View itemView) {
                super(itemView);

                tvFileUploadName = (TextView) itemView.findViewById(R.id.tvFileUploadName);
                cv = (CardView) itemView.findViewById(R.id.cv);
            }
        }
    }

}
