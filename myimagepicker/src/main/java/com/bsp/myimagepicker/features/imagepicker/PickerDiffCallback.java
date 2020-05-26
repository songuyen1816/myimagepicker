package com.bsp.myimagepicker.features.imagepicker;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.bsp.myimagepicker.model.MyImage;

public class PickerDiffCallback extends DiffUtil.ItemCallback<MyImage> {
    @Override
    public boolean areItemsTheSame(@NonNull MyImage oldItem, @NonNull MyImage newItem) {
        return oldItem.getFilePath().equals(newItem.getFilePath());
    }

    @Override
    public boolean areContentsTheSame(@NonNull MyImage oldItem, @NonNull MyImage newItem) {
        return oldItem.getFilePath().equals(newItem.getFilePath());
    }
}
