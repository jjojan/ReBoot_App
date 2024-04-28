package com.example.rebootapp.Adapters;

import android.app.Instrumentation;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.rebootapp.Activities.GameDetailsActivity;
import com.example.rebootapp.GameModel;
import com.example.rebootapp.Models.UserListModel;
import com.example.rebootapp.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.List;

import okhttp3.Headers;

public class GameListAdapter extends RecyclerView.Adapter<GameListAdapter.ViewHolder> {

    //    private ArrayList<UserListModel> userListModelArrayList;
    private UserListModel userListModel;
    private LayoutInflater mInflater;
    private String this_user;

    public GameListAdapter(Context context,UserListModel userListModel, String userId) {
        this.mInflater = LayoutInflater.from(context);

        this.userListModel = userListModel;
        this.this_user = userId;
    }

    public GameListAdapter(Instrumentation context, UserListModel testList) {
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.row_userlist, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(GameListAdapter.ViewHolder holder, int position) {
        String listName = userListModel.getGameName().get(position);
        holder.tvListName.setText(listName);
        Glide.with(holder.itemView.getContext()).load(userListModel.getGamePreviewLink().get(position)).into(holder.imgPreview);
        if(this_user.equals(ParseUser.getCurrentUser().getObjectId())) {
            holder.imgAdd.setImageResource(R.drawable.baseline_delete_24);

            holder.imgAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String objectId = userListModel.getObjectID();


                    new AlertDialog.Builder(mInflater.getContext())
                            .setTitle("Delete CustomListGameModel")
                            .setMessage("Are you sure you want to delete this game from the list?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    ParseQuery<ParseObject> query = ParseQuery.getQuery("CustomUserList");
                                    query.getInBackground(objectId, new GetCallback<ParseObject>() {
                                        @Override
                                        public void done(ParseObject customUserList, ParseException e) {
                                            if (e == null && customUserList != null) {

                                                List<String> gameIDs = customUserList.getList("gameID");
                                                List<String> gameNames = customUserList.getList("gameName");
                                                List<String> gamePreviewLinks = customUserList.getList("gamePreviewLink");

                                                if (gameIDs != null)
                                                    gameIDs.remove(userListModel.getGameID().get(position));
                                                if (gameNames != null)
                                                    gameNames.remove(userListModel.getGameName().get(position));
                                                if (gamePreviewLinks != null)
                                                    gamePreviewLinks.remove(userListModel.getGamePreviewLink().get(position));

                                                customUserList.put("gameID", gameIDs);
                                                customUserList.put("gameName", gameNames);
                                                customUserList.put("gamePreviewLink", gamePreviewLinks);


                                                customUserList.saveInBackground(e1 -> {
                                                    if (e1 == null) {

                                                        userListModel.getGameID().remove(position);
                                                        userListModel.getGameName().remove(position);
                                                        userListModel.getGamePreviewLink().remove(position);
                                                        notifyDataSetChanged();
                                                        Toast.makeText(mInflater.getContext(), "CustomListGameModel successfully deleted from the list", Toast.LENGTH_SHORT).show();
                                                    } else {

                                                        Toast.makeText(mInflater.getContext(), "Error deleting game: " + e1.getMessage(), Toast.LENGTH_SHORT).show();
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
        }
        else{
            holder.imgAdd.setVisibility(View.INVISIBLE);
        }

        holder.imgPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String game_id = userListModel.getGameID().get(holder.getPosition());
                String search = "https://api.rawg.io/api/games/" + game_id + "?key=63502b95db9f41c99bb3d0ecf77aa811";
                AsyncHttpClient client = new AsyncHttpClient();
                client.get(search, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        JSONObject jsonobject = json.jsonObject;
                        try {
                            ;
                            GameModel gameModel = new GameModel((jsonobject));
                            Intent intent = new Intent(holder.imgPreview.getContext(), GameDetailsActivity.class);
                            intent.putExtra(GameModel.class.getSimpleName(), Parcels.wrap(gameModel));
                            holder.imgPreview.getContext().startActivity(intent);

                        } catch (JSONException ex) {
                            System.out.println("almost");
                            throw new RuntimeException(ex);

                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        System.out.println("oops");
                    }
                });
            }
        });

    }


    @Override
    public int getItemCount() {

        List<String> gameIDs = userListModel.getGameID();


        if (gameIDs == null) {
            return 0;
        }


        return gameIDs.size();
    }



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
