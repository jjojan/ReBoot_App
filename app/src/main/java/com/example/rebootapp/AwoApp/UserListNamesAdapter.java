package com.example.rebootapp.AwoApp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.rebootapp.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserListNamesAdapter extends RecyclerView.Adapter<UserListNamesAdapter.ViewHolder> {

    private ArrayList<UserListModel> userListModelArrayList;
    private LayoutInflater mInflater;
    String gameID;
    String gamePreviewLink;
    String gameName;
    // Veriler ve Context ile constructor
    public UserListNamesAdapter(Context context, ArrayList<UserListModel> data,String gameID,
                                String gameName,String gamePreviewLink) {
        this.mInflater = LayoutInflater.from(context);
        this.gameName = gameName;
        this.gamePreviewLink = gamePreviewLink;
        this.gameID = gameID;
        this.userListModelArrayList = data;
    }

    // ViewHolder'ı inflate et
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.row_userlist, parent, false);
        return new ViewHolder(view);
    }

    // Veriyi bağla
    @Override
    public void onBindViewHolder(UserListNamesAdapter.ViewHolder holder, int position) {
        String listName = userListModelArrayList.get(position).getListName();
        holder.tvListName.setText(listName);
        // Oyun ID'leri listesini al
        List<String> gameIDs = userListModelArrayList.get(position).getGameID();

// gameIDs null değilse ve gameID içeriyorsa, check icon'u göster
        if (gameIDs != null && gameIDs.contains(gameID)) {
            holder.imgAdd.setImageResource(R.drawable.baseline_playlist_add_check_24);
        } else {
            // Aksi takdirde, add icon'u göster
            holder.imgAdd.setImageResource(R.drawable.baseline_add_24);
        }
            holder.imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Mevcut UserListModel al
                UserListModel model = userListModelArrayList.get(position);
                String listObjectId = model.getObjectID(); // Güncellenecek CustomUserList nesnesinin objectId'i

                // CustomUserList sınıfından ilgili listeyi al
                ParseQuery<ParseObject> query = ParseQuery.getQuery("CustomUserList");
                query.getInBackground(listObjectId, new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject customUserList, ParseException e) {
                        if (e == null && customUserList != null) {
                            List<String> gameIDs = customUserList.getList("gameID");
                            List<String> gameNames = customUserList.getList("gameName");
                            List<String> gamePreviewLinks = customUserList.getList(
                                    "gamePreviewLink");
                            if (gameNames==null){
                                gameNames=new ArrayList<>();
                            }
                            if (gamePreviewLinks==null){
                                gamePreviewLinks=new ArrayList<>();
                            }
                            if (gameIDs==null){
                                gameIDs=new ArrayList<>();
                                // Oyun listede değilse, listeye ekle
                                gameIDs.add(gameID);
                                gameNames.add(gameName);
                                gamePreviewLinks.add(gamePreviewLink);
                                customUserList.put("gameID", gameIDs);
                                customUserList.put("gameName", gameNames);
                                customUserList.put("gamePreviewLink",gamePreviewLinks);



                                holder.imgAdd.setImageResource(R.drawable.baseline_playlist_add_check_24);
                            }else {
                                // Oyunun zaten listede olup olmadığını kontrol et
                                if (gameIDs.contains(gameID)) {
                                    // Oyun listedeyse, listeden kaldır
                                    gameIDs.remove(gameID);
                                    gameNames.remove(gameName);
                                    gamePreviewLinks.remove(gamePreviewLink);
                                    customUserList.put("gameID", gameIDs);
                                    customUserList.put("gameName", gameNames);
                                    customUserList.put("gamePreviewLink",gamePreviewLinks);


                                    holder.imgAdd.setImageResource(R.drawable.baseline_add_24);
                                } else {
                                    // Oyun listede değilse, listeye ekle
                                    gameIDs.add(gameID);
                                    gameNames.add(gameName);
                                    gamePreviewLinks.add(gamePreviewLink);
                                    customUserList.put("gameID", gameIDs);
                                    customUserList.put("gameName", gameNames);
                                    customUserList.put("gamePreviewLink",gamePreviewLinks);
                                    holder.imgAdd.setImageResource(R.drawable.baseline_playlist_add_check_24);
                                }
                            }


                            // Güncellemeleri Parse'a kaydet
                            customUserList.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e1) {
                                    if (e1 == null) {
                                        // Başarılı güncelleme
                                        Toast.makeText(view.getContext(), "List updated successfully.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Güncelleme hatası
                                        Toast.makeText(view.getContext(), "Error updating list: " + e1.getMessage(), Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                        } else {
                            // Sorgu hatası
                            Toast.makeText(view.getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

    // Toplam öğe sayısı
    @Override
    public int getItemCount() {
        return userListModelArrayList.size();
    }

    // Veri ile doldurulacak row'u tutacak ViewHolder
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvListName;
        ImageView imgAdd;
        ViewHolder(View itemView) {
            super(itemView);
            tvListName = itemView.findViewById(R.id.tvListName);
            imgAdd = itemView.findViewById(R.id.imgAdd);
        }
    }
}
