package com.example.gameplan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private List<Event> eventList;
    private OnModifyClickListener modifyClickListener;
    private OnDeleteClickListener deleteClickListener;

    public EventAdapter(List<Event> eventList, OnModifyClickListener modifyClickListener, OnDeleteClickListener deleteClickListener) {
        this.eventList = eventList;
        this.modifyClickListener = modifyClickListener;
        this.deleteClickListener = deleteClickListener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.textViewEventName.setText(event.getTitle());

        holder.buttonModify.setOnClickListener(view -> {
            if (modifyClickListener != null) {
                modifyClickListener.onModifyClick(event);
            }
        });

        holder.buttonDelete.setOnClickListener(view -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDeleteClick(event);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView textViewEventName;
        Button buttonModify;
        Button buttonDelete;

        EventViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewEventName = itemView.findViewById(R.id.textViewEventName);
            buttonModify = itemView.findViewById(R.id.buttonModify);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }

    public interface OnModifyClickListener {
        void onModifyClick(Event event);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(Event event);
    }
}
