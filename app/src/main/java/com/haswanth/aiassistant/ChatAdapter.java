package com.haswanth.aiassistant;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_AI = 0;
    private static final int VIEW_TYPE_USER = 1;
    private List<Message> messages;

    public ChatAdapter(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        return message.isUser ? VIEW_TYPE_USER : VIEW_TYPE_AI;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_AI) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_ai, parent, false);
            return new AIViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_user, parent, false);
            return new UserViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        if (holder instanceof AIViewHolder) {
            ((AIViewHolder) holder).aiMessage.setText(message.text);
        } else if (holder instanceof UserViewHolder) {
            ((UserViewHolder) holder).userMessage.setText(message.text);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class AIViewHolder extends RecyclerView.ViewHolder {
        TextView aiMessage;

        AIViewHolder(@NonNull View itemView) {
            super(itemView);
            aiMessage = itemView.findViewById(R.id.aiMessage);
        }
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userMessage;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userMessage = itemView.findViewById(R.id.userMessage);
        }
    }
}
