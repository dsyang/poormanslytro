package com.example.vegetaScouter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: dsyang
 * Date: 7/28/13
 * Time: 12:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class ImageViewFragment extends Fragment {
    public static final String TAG = "ImageViewFragment";

    public static final String EXTRA_PIC_1 =
            "com.example.vegetaScouter.view1";
    public static final String EXTRA_PIC_2 =
            "com.example.vegetaScouter.view2";
    public static final String EXTRA_PIC_3 =
            "com.example.vegetaScouter.view3";
    public static final String EXTRA_PIC_4 =
            "com.example.vegetaScouter.view4";

    private ImageView mImageView;
    private ArrayList<Bitmap> mBitmaps;
    private int mIndex;



    public static ImageViewFragment newInstance() {
        return new ImageViewFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIndex = 0;
        mBitmaps = new ArrayList<Bitmap>();
        mBitmaps.add(BitmapFactory.decodeFile(getActivity().getIntent().getStringExtra(EXTRA_PIC_1)));
        mBitmaps.add(BitmapFactory.decodeFile(getActivity().getIntent().getStringExtra(EXTRA_PIC_2)));
        mBitmaps.add(BitmapFactory.decodeFile(getActivity().getIntent().getStringExtra(EXTRA_PIC_3)));
        mBitmaps.add(BitmapFactory.decodeFile(getActivity().getIntent().getStringExtra(EXTRA_PIC_4)));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_image_view, container, false);

        mImageView = (ImageView) v.findViewById(R.id.imageView);
        mImageView.setImageBitmap(mBitmaps.get(mIndex));
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIndex++;
                mImageView.setImageBitmap(mBitmaps.get((mIndex % 4)));
            }
        });

        return v;
    }
}
