package team20.flashbackmusic;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yujingwen199756 on 2/17/18.
 */

public class MyParcelable implements Parcelable {

        private int mData;

        public int describeContents() {
            return 0;
        }

        /** save object in parcel */
        public void writeToParcel(Parcel out, int flags) {
            out.writeInt(mData);
        }

        public static final Parcelable.Creator<team20.flashbackmusic.MyParcelable> CREATOR
                = new Parcelable.Creator<team20.flashbackmusic.MyParcelable>() {
            public team20.flashbackmusic.MyParcelable createFromParcel(Parcel in) {
                return new team20.flashbackmusic.MyParcelable(in);
            }

            public team20.flashbackmusic.MyParcelable[] newArray(int size) {
                return new team20.flashbackmusic.MyParcelable[size];
            }
        };

        /** recreate object from parcel */
        private MyParcelable(Parcel in) {
            mData = in.readInt();
        }
}
