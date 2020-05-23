package com.bsp.myimagepicker;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bsp.myimagepicker.databinding.LayoutItemImageBinding;
import com.bsp.myimagepicker.listener.ImageAdapterListener;
import com.bsp.myimagepicker.model.MyImage;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class PickerAdapter extends ListAdapter<MyImage, PickerAdapter.PickerViewHolder> {
    private Context mContext;
    private ImageAdapterListener listener;
    private Drawable imagePickedDrawable;

    private List<MyImage> mListPicked = new ArrayList<>();

    PickerAdapter(Context context, @NonNull DiffUtil.ItemCallback<MyImage> diffCallback) {
        super(diffCallback);
        this.mContext = context;
    }

    void setListener(ImageAdapterListener listener) {
        this.listener = listener;
    }

    void setImagePickedDrawable(Drawable imagePickedDrawable) {
        this.imagePickedDrawable = imagePickedDrawable;
    }

    @NonNull
    @Override
    public PickerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        LayoutItemImageBinding binding = LayoutItemImageBinding.inflate(inflater);
        return new PickerViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PickerViewHolder holder, int position) {
        holder.bind(getItem(position));
    }



    class PickerViewHolder extends RecyclerView.ViewHolder {
        private LayoutItemImageBinding binding;

        PickerViewHolder(@NonNull LayoutItemImageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(MyImage myImage) {
            Glide.with(mContext).load(myImage.getFilePath()).into(binding.ivLocalImage);
            binding.viewImagePicked.setBackground(imagePickedDrawable);
            binding.viewImagePicked.setVisibility(myImage.isPicked() ? View.VISIBLE : View.INVISIBLE);

            binding.ivLocalImage.setOnClickListener(v -> {
                myImage.setPicked(!myImage.isPicked());

                if (myImage.isPicked()) {
                    listener.onImagePicked(myImage.getFilePath());
                    mListPicked.add(myImage);
                } else {
                    listener.onImageUnPicked(myImage.getFilePath());
                    removeOldPicked(myImage.getFilePath());
                }

                notifyItemChanged(getAdapterPosition());
            });
        }
    }

    void notifyMaxCountItem() {
        for (int i = 0; i < getItemCount(); i++) {
            MyImage myImage = getItem(i);
            if (myImage.getFilePath().equals(mListPicked.get(0).getFilePath())) {
                myImage.setPicked(!myImage.isPicked());
                notifyItemChanged(i);
                mListPicked.remove(0);
                return;
            }
        }
    }

    private void removeOldPicked(String path) {
        int removePos = 0;
        for (int i = 0; i < mListPicked.size(); i++) {
            if (mListPicked.get(i).getFilePath().equals(path)) {
                removePos = i;
                break;
            }
        }
        mListPicked.remove(removePos);
    }
}
