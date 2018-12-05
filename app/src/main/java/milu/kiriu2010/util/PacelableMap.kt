package milu.kiriu2010.util

import android.os.Parcel
import android.os.Parcelable

/*
// https://code.i-harness.com/en/q/aec1bb
class ParcelableMap<K,V>(val map: MutableMap<K,V>) : Parcelable {
    //constructor(parcel: Parcel) : this(parcel.readMap(LinkedHashMap<K,V>(),MutableMap::class.java.classLoader))
    //constructor(parcel: Parcel) : this(parcel.readMap(MutableMap<K,V>(),MutableMap::class.java.classLoader))
    //constructor(parcel: Parcel) : this(parcel.readMap(mutableMapOf<K,V>(),MutableMap::class.java.classLoader))

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeMap(map)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ParcelableMap<Any?,Any?>> {
        @JvmStatic
        override fun createFromParcel(parcel: Parcel): ParcelableMap<Any?,Any?> {
            return ParcelableMap(parcel)
        }
        @JvmStatic
        override fun newArray(size: Int): Array<ParcelableMap<Any?,Any?>?> {
            return arrayOfNulls(size)
        }
    }
}
*/
