package com.noureddine.kotlin2.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.noureddine.kotlin2.utel.AppConstants.TABEL_USER

@Entity(tableName = TABEL_USER)
class User: Parcelable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    var uid: String = ""
    var name: String = ""
    var img: Long = 0
    var notificationId: Int = 0
    var email: String = ""
    var state: Boolean = false

    constructor()

    // 1. كونستركتور للقراءة من الـ Parcel
    constructor(p: Parcel) : this(){
        uid = p.readString() ?: ""
        name = p.readString() ?: ""
        img = p.readLong()
        notificationId = p.readInt()
        email = p.readString() ?: ""
        state = p.readByte() != 0.toByte()
    }

    constructor(uid: String, name: String, img: Long, notificationId: Int,  email: String, state: Boolean) :this(){
        this.uid = uid
        this.name = name
        this.img = img
        this.notificationId = notificationId
        this.email = email
        this.state = state
    }

    constructor(uid: String, name: String, email: String, img: Long) :this(){
        this.uid = uid
        this.name = name
        this.email = email
        this.img = img
    }


    // 2. كتابة الحقول إلى Parcel
    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(uid)
        dest.writeString(name)
        dest.writeLong(img)
        dest.writeInt(notificationId)
        dest.writeString(email)
        dest.writeByte(if (state) 1 else 0)
    }

    override fun describeContents(): Int = 0

    // 3. CREATOR لإنشاء النسخ من Parcel
    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(p: Parcel): User = User(p)
        override fun newArray(size: Int): Array<User?> = arrayOfNulls(size)
    }
}
