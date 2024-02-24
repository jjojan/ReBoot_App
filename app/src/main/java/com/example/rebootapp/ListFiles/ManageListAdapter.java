package com.example.rebootapp.ListFiles;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rebootapp.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;

public class ManageListAdapter extends RecyclerView.Adapter<ManageListAdapter.ViewHolder> {

    private ArrayList<UserListModel> userListModelArrayList;
    private LayoutInflater mInflater;
    private Context context;
    // Veriler ve Context ile constructor
    public ManageListAdapter(Context context, ArrayList<UserListModel> data) {
        this.mInflater = LayoutInflater.from(context);

        this.userListModelArrayList = data;
        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.row_userlist, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ManageListAdapter.ViewHolder holder, int position) {
        String listName = userListModelArrayList.get(position).getListName();
        holder.tvListName.setText(listName);
        holder.imgAdd.setImageResource(R.drawable.baseline_delete_24);

            holder.imgAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UserListModel userModel = userListModelArrayList.get(position);
                    String objectId = userModel.getObjectID();


                    new AlertDialog.Builder(mInflater.getContext())
                            .setTitle("Delete List")
                            .setMessage("Are you sure you want to delete this list?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    ParseQuery<ParseObject> query = ParseQuery.getQuery("CustomUserList");
                                    query.getInBackground(objectId, new GetCallback<ParseObject>() {
                                        @Override
                                        public void done(ParseObject object, ParseException e) {
                                            if (e == null && object != null) {

                                                object.deleteInBackground(e1 -> {
                                                    if (e1 == null) {

                                                        userListModelArrayList.remove(position);
                                                        notifyItemRemoved(position);
                                                        notifyItemRangeChanged(position, userListModelArrayList.size());
                                                        Toast.makeText(mInflater.getContext(), "List successfully deleted", Toast.LENGTH_SHORT).show();
                                                    } else {

                                                        Toast.makeText(mInflater.getContext(), "Error deleting list: " + e1.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            } else {

                                                Toast.makeText(mInflater.getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            });
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                gameListDialog(position);
                }
            });
    }
    public void gameListDialog(int position) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View customView = inflater.inflate(R.layout.layout_user_list, null);


        AlertDialog.Builder listDialog = new AlertDialog.Builder(
                new androidx.appcompat.view.ContextThemeWrapper(context, R.style.AlertDialogCustom));

        listDialog.setView(customView);
        AlertDialog userListDialogBuilder = listDialog.create();

        Button btnAddNewList=customView.findViewById(R.id.btnNewList);
        TextView tvTitleList=customView.findViewById(R.id.tvTitleList);
        tvTitleList.setText("Manage Games");
        btnAddNewList.setVisibility(View.GONE);
        Button btnClose=customView.findViewById(R.id.btnClose);
        RecyclerView recyclerView=customView.findViewById(R.id.recyclerView);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userListDialogBuilder.dismiss();
            }
        });

                    GameListAdapter gameListAdapter=
                            new GameListAdapter(context,
                                    userListModelArrayList.get(position));
                    recyclerView.setAdapter(gameListAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));


        userListDialogBuilder.show();
    }

    @Override
    public int getItemCount() {
        return userListModelArrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvListName;
        ImageView imgAdd;
        CardView cardView;
        ViewHolder(View itemView) {
            super(itemView);
            tvListName = itemView.findViewById(R.id.tvListName);
            imgAdd = itemView.findViewById(R.id.imgAdd);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
