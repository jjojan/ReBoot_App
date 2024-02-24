package com.example.rebootapp.AwoApp;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rebootapp.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class GameListAdapter extends RecyclerView.Adapter<GameListAdapter.ViewHolder> {

//    private ArrayList<UserListModel> userListModelArrayList;
    private UserListModel userListModel;
    private LayoutInflater mInflater;
    // Veriler ve Context ile constructor
    public GameListAdapter(Context context,UserListModel userListModel) {
        this.mInflater = LayoutInflater.from(context);

        this.userListModel = userListModel;
    }

    // ViewHolder'ı inflate et
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.row_userlist, parent, false);
        return new ViewHolder(view);
    }

    // Veriyi bağla
    @Override
    public void onBindViewHolder(GameListAdapter.ViewHolder holder, int position) {
        String listName = userListModel.getGameName().get(position);
        holder.tvListName.setText(listName);
        holder.imgAdd.setImageResource(R.drawable.baseline_delete_24);
        Glide.with(holder.itemView.getContext()).load(userListModel.getGamePreviewLink().get(position)).into(holder.imgPreview);
        holder.imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String objectId = userListModel.getObjectID(); // Güncellenecek CustomUserList nesnesinin objectId'i

                // AlertDialog ile kullanıcıdan oyunu silme işlemi için onay iste
                new AlertDialog.Builder(mInflater.getContext())
                        .setTitle("Delete Game") // Dialog başlığı
                        .setMessage("Are you sure you want to delete this game from the list?") // Dialog mesajı
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Kullanıcı "Yes" dediğinde, oyunu silme işlemi yap
                                ParseQuery<ParseObject> query = ParseQuery.getQuery("CustomUserList");
                                query.getInBackground(objectId, new GetCallback<ParseObject>() {
                                    @Override
                                    public void done(ParseObject customUserList, ParseException e) {
                                        if (e == null && customUserList != null) {
                                            // İlgili listelerden oyun bilgilerini çıkar
                                            List<String> gameIDs = customUserList.getList("gameID");
                                            List<String> gameNames = customUserList.getList("gameName");
                                            List<String> gamePreviewLinks = customUserList.getList("gamePreviewLink");

                                            if (gameIDs != null) gameIDs.remove(userListModel.getGameID().get(position));
                                            if (gameNames != null) gameNames.remove(userListModel.getGameName().get(position));
                                            if (gamePreviewLinks != null) gamePreviewLinks.remove(userListModel.getGamePreviewLink().get(position));

                                            customUserList.put("gameID", gameIDs);
                                            customUserList.put("gameName", gameNames);
                                            customUserList.put("gamePreviewLink", gamePreviewLinks);

                                            // Güncellemeleri Parse'a kaydet
                                            customUserList.saveInBackground(e1 -> {
                                                if (e1 == null) {
                                                    // Oyun silme işlemi başarılı
                                                    userListModel.getGameID().remove(position);
                                                    userListModel.getGameName().remove(position);
                                                    userListModel.getGamePreviewLink().remove(position);
                                                    notifyDataSetChanged();
                                                    Toast.makeText(mInflater.getContext(), "Game successfully deleted from the list", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    // Silme işlemi sırasında hata oluştu
                                                    Toast.makeText(mInflater.getContext(), "Error deleting game: " + e1.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        } else {
                                            // Sorgu sırasında hata oluştu
                                            Toast.makeText(mInflater.getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        })
                        .setNegativeButton(android.R.string.no, null) // Kullanıcı "No" dediğinde hiçbir şey yapma
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

    }

    // Toplam öğe sayısı
    @Override
    public int getItemCount() {
        return userListModel.getGameID().size();
    }

    // Veri ile doldurulacak row'u tutacak ViewHolder
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvListName;
        ImageView imgAdd,imgPreview;
        CardView cardView;
        ViewHolder(View itemView) {
            super(itemView);
            tvListName = itemView.findViewById(R.id.tvListName);
            imgAdd = itemView.findViewById(R.id.imgAdd);
            cardView = itemView.findViewById(R.id.cardView);
            imgPreview = itemView.findViewById(R.id.imgPreview);
        }
    }
}
