package com.muyu.minimalism.view.imagebox;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.muyu.minimalism.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * ZzImageBox-A powerful Image Container.
 * Created by zz on 2016/10/9.
 */
public class ZzImageBox extends RecyclerView {
    
    private int mMaxLine;
    private int mImageSize;
    private int mPadding;
    private int mLeftMargin;
    private int mRightMargin;
    private int mDefaultPicId = -1;
    private int mDeletePicId = -1;
    private int mAddPicId = -1;
    private boolean mDeletable;
    private static final boolean DEFAULT_DELETABLE = true;
    private static final int DEFAULT_MAX_LINE = 1;
    private static final int DEFAULT_IMAGE_SIZE = 4;
    private static final int DEFAULT_IMAGE_PADDING = 5;
    
    private OnlineImageLoader onlineImageLoader;
    
    private List<ImageEntity> mDatas;
    private MyAdapter mAdapter;

    private Context context;
    
    private OnImageClickListener mClickListener;
    
    public ZzImageBox(Context context) {
        super(context);
        init(context, null);
    }
    
    public ZzImageBox(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }
    
    public ZzImageBox(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }
    
    public interface OnlineImageLoader {
        void onLoadImage(ImageView iv, String url);
    }
    
    public void setOnlineImageLoader(OnlineImageLoader onlineImageLoader) {
        this.onlineImageLoader = onlineImageLoader;
        if (mAdapter != null) {
            mAdapter.setImageLoader(onlineImageLoader);
        }
    }
    
    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ZzImageBox);
        mLeftMargin = a.getDimensionPixelSize(R.styleable.ZzImageBox_zib_left_margin, 0);
        mRightMargin = a.getDimensionPixelSize(R.styleable.ZzImageBox_zib_right_margin, 0);
        mMaxLine = a.getInteger(R.styleable.ZzImageBox_zib_max_line, DEFAULT_MAX_LINE);
        mImageSize = a.getInteger(R.styleable.ZzImageBox_zib_img_size_one_line, DEFAULT_IMAGE_SIZE);
        mPadding = a.getDimensionPixelSize(R.styleable.ZzImageBox_zib_img_padding, DEFAULT_IMAGE_PADDING);
        mDefaultPicId = a.getResourceId(R.styleable.ZzImageBox_zib_img_default, -1);
        mDeletePicId = a.getResourceId(R.styleable.ZzImageBox_zib_img_delete, -1);
        mAddPicId = a.getResourceId(R.styleable.ZzImageBox_zib_img_add, -1);
        mDeletable = a.getBoolean(R.styleable.ZzImageBox_zib_img_deletable, DEFAULT_DELETABLE);
        a.recycle();

        initData(context);

        setOnlineImageLoader(new OnlineImageLoader() {
            @Override
            public void onLoadImage(ImageView iv, String url) {
                Glide.with(context).load(url).into(iv);
            }
        });
    }
    
    
    private void initData(Context context) {
        mDatas = new ArrayList<>();
        setHasFixedSize(true);
        setLayoutManager(new GridLayoutManager(context, mImageSize));
        setPadding(mLeftMargin, 0, mRightMargin, 0);
        mAdapter = new MyAdapter(context, getBoxWidth(), mDatas, mImageSize, mDefaultPicId, mDeletePicId, mAddPicId, mDeletable, mPadding, mLeftMargin, mRightMargin, mMaxLine, mClickListener, onlineImageLoader);
        setAdapter(mAdapter);
        
        mAdapter.notifyDataSetChanged();
    }
    
    public void setOnImageClickListener(OnImageClickListener mClickListener) {
        this.mClickListener = mClickListener;
        mAdapter.listener = mClickListener;
    }
    
    public void setDefaultPicId(int defaultPicId) {
        this.mDefaultPicId = defaultPicId;
        mAdapter.defaultPic = defaultPicId;
        mAdapter.notifyDataSetChanged();
    }
    
    public void setDeletePicId(int deletePicId) {
        this.mDeletePicId = deletePicId;
        mAdapter.deletePic = mDeletePicId;
        mAdapter.notifyDataSetChanged();
    }
    
    public void setAddPicId(int addPicId) {
        this.mAddPicId = addPicId;
        mAdapter.addPic = mAddPicId;
        mAdapter.notifyDataSetChanged();
    }
    
    public void setDeletable(boolean deletable) {
        this.mDeletable = deletable;
        mAdapter.deletable = deletable;
        mAdapter.notifyDataSetChanged();
    }
    
    public void setDatas(List<ImageEntity> mDatas) {
        this.mDatas = mDatas;
        mAdapter.setmDatas(mDatas);
        mAdapter.notifyDataSetChanged();
    }
    
    public int getBoxWidth() {
        return getMeasuredWidth();
    }
    
    
    /**
     * Add a image.
     *
     * @param imagePath the path of image.
     */
    public void addImage(@NonNull String imagePath) {
        if (mDatas != null) {
            if (mDatas.size() < mMaxLine * this.mImageSize) {
                mAdapter.lastOne = false;
                ImageEntity entity = new ImageEntity();
                entity.setPicUrl(imagePath);
                entity.setAdd(false);
                entity.setOnLine(false);
                this.mDatas.add(this.mDatas.size() - 1, entity);
            } else {
                mAdapter.lastOne = true;
                this.mDatas.get(this.mDatas.size() - 1).setAdd(false);
                this.mDatas.get(this.mDatas.size() - 1).setPicUrl(imagePath);
            }
        }
        mAdapter.notifyDataSetChanged();
    }
    
    /**
     * Add a image with a custom path and type.
     *
     * @param imagePath the path of image.
     */
    public void addImageWithRealPathAndType(@NonNull String imagePath, String realPath, int realType) {
        if (mDatas != null) {
            if (mDatas.size() < mMaxLine * this.mImageSize) {
                mAdapter.lastOne = false;
                ImageEntity entity = new ImageEntity();
                entity.setPicUrl(imagePath);
                entity.setAdd(false);
                entity.setRealPath(realPath);
                entity.setRealType(realType);
                entity.setOnLine(false);
                this.mDatas.add(this.mDatas.size() - 1, entity);
            } else {
                mAdapter.lastOne = true;
                this.mDatas.get(this.mDatas.size() - 1).setAdd(false);
                this.mDatas.get(this.mDatas.size() - 1).setRealPath(realPath);
                this.mDatas.get(this.mDatas.size() - 1).setRealType(realType);
                this.mDatas.get(this.mDatas.size() - 1).setPicUrl(imagePath);
            }
        }
        mAdapter.notifyDataSetChanged();
    }
    
    /**
     * Add a image online.
     *
     * @param imagePath the path of image.
     */
    public void addImageOnline(@NonNull String imagePath) {
        if (mDatas != null) {
            if (mDatas.size() < mMaxLine * this.mImageSize) {
                mAdapter.lastOne = false;
                ImageEntity entity = new ImageEntity();
                entity.setPicUrl(imagePath);
                entity.setAdd(false);
                entity.setOnLine(true);
                this.mDatas.add(this.mDatas.size() - 1, entity);
            } else {
                mAdapter.lastOne = true;
                this.mDatas.get(this.mDatas.size() - 1).setAdd(false);
                this.mDatas.get(this.mDatas.size() - 1).setPicUrl(imagePath);
            }
        }
        mAdapter.notifyDataSetChanged();
    }
    
    /**
     * Add a image online with custom path and custom type.
     *
     * @param imagePath the path of image.
     * @param realPath  your custom path.
     * @param realType  your custom type.
     */
    public void addImageOnlineWithRealPathAndType(@NonNull String imagePath, String realPath, int realType) {
        if (mDatas != null) {
            if (mDatas.size() < mMaxLine * this.mImageSize) {
                mAdapter.lastOne = false;
                ImageEntity entity = new ImageEntity();
                entity.setPicUrl(imagePath);
                entity.setRealPath(realPath);
                entity.setRealType(realType);
                entity.setAdd(false);
                entity.setOnLine(true);
                this.mDatas.add(this.mDatas.size() - 1, entity);
            } else {
                mAdapter.lastOne = true;
                this.mDatas.get(this.mDatas.size() - 1).setAdd(false);
                this.mDatas.get(this.mDatas.size() - 1).setRealPath(realPath);
                this.mDatas.get(this.mDatas.size() - 1).setRealType(realType);
                this.mDatas.get(this.mDatas.size() - 1).setPicUrl(imagePath);
            }
        }
        mAdapter.notifyDataSetChanged();
    }
    
    /**
     * Remove the image at position.
     *
     * @param position position.
     */
    public void removeImage(int position) {
        if (mDatas != null) {
            if (position + 1 == mMaxLine * this.mImageSize && mAdapter.lastOne) {
                mAdapter.lastOne = false;
                mDatas.get(position).setAdd(true);
            } else {
                if (mDatas.size() == mMaxLine * this.mImageSize && !mDatas.get(mDatas.size() - 1).isAdd) {
                    mAdapter.lastOne = false;
                    ImageEntity entity = new ImageEntity();
                    entity.setAdd(true);
                    mDatas.add(entity);
                }
                mDatas.remove(position);
            }
        }
        mAdapter.notifyDataSetChanged();
    }
    
    
    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        if (mAdapter != null && mAdapter.boxWidth == 0) {
            mAdapter.boxWidth = getBoxWidth();
            mAdapter.setImageSize(mAdapter.imageSize);
            mAdapter.notifyDataSetChanged();
        }
    }
    
    private static class MyAdapter extends RecyclerView.Adapter<ViewHolder> {
        private LayoutInflater mInflater;
        private Context mContext;
        private List<ImageEntity> mDatas;
        private int defaultPic;
        private int deletePic;
        private int addPic;
        private boolean deletable;
        private int boxWidth;
        private int padding;
        private int picWidth;
        private int maxLine;
        private int imageSize;
        private int leftMargin;
        private int rightMargin;
        private boolean lastOne;
        private OnImageClickListener listener;
        private OnlineImageLoader imageLoader;
        
        MyAdapter(Context context, int boxWidth, List<ImageEntity> mDatas, int imageSize, int defaultPic, int deletePic, int addPic, boolean deletable, int padding, int leftMargin, int rightMargin, int maxLine, OnImageClickListener listener, OnlineImageLoader imageLoader) {
            mInflater = LayoutInflater.from(context);
            this.mContext = context;
            this.mDatas = mDatas;
            if (this.mDatas != null) {
                if (this.mDatas.size() > 0) {
                    if (!this.mDatas.get(this.mDatas.size() - 1).isAdd()) {
                        ImageEntity entity = new ImageEntity();
                        entity.setAdd(true);
                        this.mDatas.add(entity);
                    }
                } else {
                    ImageEntity entity = new ImageEntity();
                    entity.setAdd(true);
                    this.mDatas.add(entity);
                }
            }
            this.boxWidth = boxWidth;
            this.defaultPic = defaultPic;
            this.deletePic = deletePic;
            this.addPic = addPic;
            this.deletable = deletable;
            this.padding = padding;
            this.maxLine = maxLine;
            this.imageSize = imageSize;
            this.leftMargin = leftMargin;
            this.rightMargin = rightMargin;
            this.lastOne = false;
            this.listener = listener;
            this.imageLoader = imageLoader;
            this.picWidth = (boxWidth - leftMargin - rightMargin) / imageSize - padding * 2;
        }
        
        void setLeftMargin(int leftMargin) {
            this.leftMargin = leftMargin;
            this.picWidth = (boxWidth - this.leftMargin - rightMargin) / imageSize - padding * 2;
        }
        
        void setRightMargin(int rightMargin) {
            this.rightMargin = rightMargin;
            this.picWidth = (boxWidth - leftMargin - this.rightMargin) / imageSize - padding * 2;
        }
        
        void setImagePadding(int padding) {
            this.padding = padding;
            this.picWidth = (boxWidth - leftMargin - this.rightMargin) / imageSize - padding * 2;
        }
        
        void setImageLoader(OnlineImageLoader imageLoader) {
            this.imageLoader = imageLoader;
        }
        
        void setmDatas(List<ImageEntity> mDatas) {
            this.mDatas = mDatas;
            if (mDatas != null && mDatas.size() < maxLine * this.imageSize) {
                ImageEntity entity = new ImageEntity();
                entity.setAdd(true);
                this.mDatas.add(entity);
            }
        }
        
        public void setImageSize(int imageSize) {
            this.imageSize = imageSize;
            if (imageSize != 0)
                this.picWidth = (boxWidth - leftMargin - rightMargin) / imageSize - padding * 2;
            else
                this.picWidth = 0;
        }
        
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = mInflater.inflate(R.layout.zz_image_box_item, parent, false);
            return new ViewHolder(itemView);
        }
        
        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
            ImageView iv = (ImageView) holder.itemView.findViewById(R.id.iv_pic);
            iv.setLayoutParams(new RelativeLayout.LayoutParams(picWidth, picWidth));
            ImageView ivDel = (ImageView) holder.itemView.findViewById(R.id.iv_delete);
            ivDel.getLayoutParams().width = picWidth / 3;
            ivDel.getLayoutParams().height = picWidth / 3;
            if (holder.getAdapterPosition() == getItemCount() - 1 && !lastOne) {
                holder.ivDelete.setVisibility(GONE);
                holder.ivPic.setImageResource(addPic == -1 ? R.drawable.iv_add : addPic);
                holder.ivPic.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            listener.onAddClick();
                        }
                    }
                });
            } else {
                String url = mDatas.get(holder.getAdapterPosition()).getPicUrl();
                boolean forceOnLine = mDatas.get(holder.getAdapterPosition()).isOnLine();
                
                if (url != null && url.length() != 0) {
                    if (url.startsWith("http") || forceOnLine) {
                        if (imageLoader != null) {
                            //FIXME by zhouzhuo 时间：2017/11/22 上午11:33 修改内容：添加网络图片加载器
                            imageLoader.onLoadImage(holder.ivPic, url);
                        } else {
                            holder.ivPic.setImageResource(defaultPic == -1 ? R.drawable.iv_default : defaultPic);
                        }
                    } else {
                        holder.ivPic.setImageURI(Uri.fromFile(new File(url)));
                    }
                } else {
                    holder.ivPic.setImageResource(defaultPic == -1 ? R.drawable.iv_default : defaultPic);
                }
                if (deletable) {
                    holder.ivDelete.setVisibility(VISIBLE);
                } else {
                    holder.ivDelete.setVisibility(GONE);
                }
                holder.ivDelete.setImageResource(deletePic == -1 ? R.drawable.iv_delete : deletePic);
                holder.ivDelete.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            listener.onDeleteClick(holder.getAdapterPosition(), mDatas.get(holder.getAdapterPosition()).getPicUrl(), mDatas.get(holder.getAdapterPosition()).getRealPath(), mDatas.get(holder.getAdapterPosition()).getRealType());
                        }
                    }
                });
                holder.ivPic.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            listener.onImageClick(holder.getAdapterPosition(), mDatas.get(holder.getAdapterPosition()).getPicUrl(), mDatas.get(holder.getAdapterPosition()).getRealPath(), mDatas.get(holder.getAdapterPosition()).getRealType(), holder.ivPic);
                        }
                    }
                });
            }
            holder.rootView.setPadding(padding, padding, padding, padding);
        }
        
        @Override
        public int getItemCount() {
            return mDatas == null ? 0 : mDatas.size();
        }
    }
    
    public interface OnImageClickListener {
        
        void onImageClick(int position, String url, String realPath, int realType, ImageView iv);
        
        void onDeleteClick(int position, String url, String realPath, int realType);
        
        void onAddClick();
    }
    
    private static class ViewHolder extends RecyclerView.ViewHolder {
        
        private View rootView;
        private ImageView ivPic;
        private ImageView ivDelete;
        
        ViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            ivPic = (ImageView) itemView.findViewById(R.id.iv_pic);
            ivDelete = (ImageView) itemView.findViewById(R.id.iv_delete);
        }
    }
    
    public static class ImageEntity {
        private String picUrl;
        private boolean isAdd;
        private boolean onLine;
        private String realPath;
        private int realType;
        
        public boolean isAdd() {
            return isAdd;
        }
        
        public void setAdd(boolean add) {
            isAdd = add;
        }
        
        public void setPicUrl(String picUrl) {
            this.picUrl = picUrl;
        }
        
        public String getPicUrl() {
            return picUrl;
        }
        
        public boolean isOnLine() {
            return onLine;
        }
        
        public void setOnLine(boolean onLine) {
            this.onLine = onLine;
        }
        
        public String getRealPath() {
            return realPath;
        }
        
        public void setRealPath(String realPath) {
            this.realPath = realPath;
        }
        
        public int getRealType() {
            return realType;
        }
        
        public void setRealType(int realType) {
            this.realType = realType;
        }
    }
    
    /**
     * return all paths of images.
     *
     * @return paths of images
     */
    public List<String> getAllImages() {
        final List<String> allImages = new ArrayList<>();
        if (mDatas != null) {
            for (ImageEntity mData : mDatas) {
                if (!mData.isAdd) {
                    allImages.add(mData.getPicUrl());
                }
            }
        }
        return allImages;
    }
    
    /**
     * Get all custom paths
     *
     * @return Custom path
     */
    public List<String> getAllRealPath() {
        final List<String> allImages = new ArrayList<>();
        if (mDatas != null) {
            for (ImageEntity mData : mDatas) {
                if (!mData.isAdd) {
                    allImages.add(mData.getRealPath());
                }
            }
        }
        return allImages;
    }
    
    /**
     * Get all custom types
     *
     * @return Custom type
     */
    public List<Integer> getAllRealType() {
        final List<Integer> types = new ArrayList<>();
        if (mDatas != null) {
            for (ImageEntity mData : mDatas) {
                if (!mData.isAdd) {
                    types.add(mData.getRealType());
                }
            }
        }
        return types;
    }
    
    /**
     * Get all entity classes
     *
     * @return Entity class
     */
    public List<ImageEntity> getAllEntity() {
        final List<ImageEntity> entities = new ArrayList<>();
        if (mDatas != null) {
            for (ImageEntity mData : mDatas) {
                if (!mData.isAdd) {
                    entities.add(mData);
                }
            }
        }
        return entities;
    }
    
    /**
     * 获取图片数量
     *
     * @return Total number of images.
     */
    public int getCount() {
        int count = 0;
        if (mDatas != null) {
            for (ImageEntity mData : mDatas) {
                if (!mData.isAdd) {
                    count++;
                }
            }
        }
        return count;
    }
    
    /**
     * remove all images.
     */
    public void removeAllImages() {
        if (mDatas != null) {
            mDatas.clear();
            ImageEntity entity = new ImageEntity();
            entity.setAdd(true);
            mDatas.add(entity);
            mAdapter.lastOne = false;
        }
        mAdapter.notifyDataSetChanged();
    }
    
    /**
     * Return the image path of position.
     *
     * @param position position.
     * @return image path.
     */
    public String getImagePathAt(int position) {
        if (mDatas != null && mDatas.size() > position) {
            return mDatas.get(position).getPicUrl();
        }
        return null;
    }
    
    /**
     * Return the custom path of position.
     *
     * @param position position.
     * @return custom path.
     */
    public String getRealPathAt(int position) {
        if (mDatas != null && mDatas.size() > position) {
            return mDatas.get(position).getRealPath();
        }
        return null;
    }
    
    /**
     * Return the custom type of position.
     *
     * @param position position.
     * @return custom type, default = 0.
     */
    public int getRealTypeAt(int position) {
        if (mDatas != null && mDatas.size() > position) {
            return mDatas.get(position).getRealType();
        }
        return 0;
    }
    
    /**
     * Return the custom type of position.
     *
     * @param position position.
     * @return custom type, default = 0.
     */
    public ImageEntity getEntityAt(int position) {
        if (mDatas != null && mDatas.size() > position) {
            return mDatas.get(position);
        }
        return null;
    }
    
    /**
     * Set the max image size of one line.
     *
     * @param maxSize the max size.
     */
    public void setImageSizeOneLine(int maxSize) {
        this.mImageSize = maxSize;
        if (mAdapter != null) {
            setLayoutManager(new GridLayoutManager(getContext(), maxSize));
            mAdapter = new MyAdapter(getContext(), getBoxWidth(), mDatas, mImageSize, mDefaultPicId, mDeletePicId, mAddPicId, mDeletable, mPadding, mLeftMargin, mRightMargin, mMaxLine, mClickListener, onlineImageLoader);
            setAdapter(mAdapter);
        }
    }
    
    /**
     * Set the left margin of imageBox.
     *
     * @param leftMarginPx left margin value.
     */
    public void setLeftMarginInPixel(int leftMarginPx) {
        this.mLeftMargin = leftMarginPx;
        setPadding(this.mLeftMargin, 0, this.mRightMargin, 0);
        mAdapter.setLeftMargin(this.mLeftMargin);
        mAdapter.notifyDataSetChanged();
    }
    
    /**
     * Set the right margin of imageBox.
     *
     * @param rightMarginPx right margin value.
     */
    public void setRightMarginInPixel(int rightMarginPx) {
        this.mRightMargin = rightMarginPx;
        setPadding(this.mLeftMargin, 0, this.mRightMargin, 0);
        mAdapter.setRightMargin(this.mRightMargin);
        mAdapter.notifyDataSetChanged();
    }
    
    /**
     * Set the padding of each one image.
     *
     * @param imagePadding padding value.
     */
    public void setImagePadding(int imagePadding) {
        this.mPadding = imagePadding;
        mAdapter.setImagePadding(this.mPadding);
        mAdapter.notifyDataSetChanged();
    }
}
