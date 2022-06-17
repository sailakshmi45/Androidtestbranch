/**
 * ImagePrint for printing
 *
 * @author Brother Industries, Ltd.
 * @version 2.2
 */
package com.globalnest.brother.ptouch.sdk.printdemo.printprocess;

import android.content.Context;
import android.graphics.Bitmap;

import com.globalnest.brother.ptouch.sdk.printdemo.common.MsgDialog;
import com.globalnest.brother.ptouch.sdk.printdemo.common.MsgHandle;

import java.util.ArrayList;

public class ImagePrint extends BasePrint {

    private ArrayList<String> mImageFiles;
    private ArrayList<Bitmap> bitmapArrayList;
    private Bitmap bitmaps;

    public ImagePrint(Context context, MsgHandle mHandle, MsgDialog mDialog) {
        super(context, mHandle, mDialog);
    }

    /**
     * set print data
     */
    public ArrayList<String> getFiles() {
        return mImageFiles;
    }

    /**
     * set print data
     */
    public void setFiles(ArrayList<String> files) {
        mImageFiles = files;
    }

    public void setbitmapFiles(ArrayList<Bitmap> bitmaps) {
        bitmapArrayList = bitmaps;
    }

    public void setbitmap(Bitmap bitmap) {
        bitmaps = bitmap;
    }

    /**
     * do the particular print
     */
    @Override
    protected void doPrint() {
        int count = mImageFiles.size();

        for (int i = 0; i < count; i++) {

            String strFile = mImageFiles.get(i);
            /*Bitmap  bitmap = BitmapFactory.decodeFile(strFile);
            mPrintResult = mPrinter.printImage(bitmap);*/
            //mPrinter.setPrinterInfo(getPrinterInfo());
            mPrintResult = mPrinter.printFile(strFile);
            // if error, stop print next files
            /*if (mPrintResult.errorCode != PrinterInfo.ErrorCode.ERROR_NONE) {
                break;
            }*/
        }

        /* int count = bitmapArrayList.size();
        //int count = mImageFiles.size();
        for (int i = 0; i < count; i++) {
          //  String strFile = mImageFiles.get(i);

           // mPrintResult = mPrinter.printFile(strFile);
            mPrintResult = mPrinter.printImage(bitmapArrayList.get(i));
            // if error, stop print next files
           if (mPrintResult.errorCode != PrinterInfo.ErrorCode.ERROR_NONE) {
                break;
            }
        }
    }
        */


    }
}