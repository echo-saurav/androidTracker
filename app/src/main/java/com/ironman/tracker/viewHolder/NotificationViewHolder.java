package com.ironman.tracker.viewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ironman.tracker.R;
import com.ironman.tracker.valueHolder.NotificationValueHolder;
import com.ironman.tracker.valueHolder.UserValueHolder;
import com.squareup.picasso.Picasso;

public class NotificationViewHolder extends RecyclerView.ViewHolder {
    public NotificationViewHolder(@NonNull View itemView) {
        super(itemView);
    }
    public void setViews(NotificationValueHolder notificationValueHolder){
        if(notificationValueHolder!=null){

            ((TextView)itemView.findViewById(R.id.title)).setText(notificationValueHolder.title);
            ((TextView)itemView.findViewById(R.id.time)).setText(notificationValueHolder.timeStamp.toString());

            DocumentReference documentReference= FirebaseFirestore.getInstance()
                    .collection("users").document(notificationValueHolder.uid);
            documentReference.get().addOnCompleteListener(task -> {
                if(task.isSuccessful() && task.getResult()!=null){
                    UserValueHolder userValueHolder=task.getResult().toObject(UserValueHolder.class);

                    if(userValueHolder!=null){
                        ((TextView)itemView.findViewById(R.id.subtitle)).setText(userValueHolder.userName);


                        if(userValueHolder.imageUrl!=null) {
                            if (!userValueHolder.imageUrl.isEmpty()) {

                                Picasso.get().load(userValueHolder.imageUrl).error(R.mipmap.ic_launcher_round)
                                        .into(((ImageView)itemView.findViewById(R.id.image_view)));
                            }
                        }

                    }

                }
            });
        }
    }
}
