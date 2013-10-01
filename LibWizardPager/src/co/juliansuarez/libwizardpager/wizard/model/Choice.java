package co.juliansuarez.libwizardpager.wizard.model;

import android.os.Parcel;
import android.os.Parcelable;

public final class Choice implements Parcelable {
	private String sTitle;
	private int iId;
	
	public Choice (int id, String title) {
		this.sTitle = title;
		this.iId = id;
	}
	
	public String getTitle() {
		return sTitle;
	}

	public int getId() {
		return iId;
	}


	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel pc, int flags) {
		pc.writeInt(iId);
		pc.writeString(sTitle);		
	}
	
	/** Static field used to regenerate object, individually or as arrays */
	public static final Parcelable.Creator<Choice> CREATOR = new Parcelable.Creator<Choice>() {
		public Choice createFromParcel(Parcel pc) {
		    return new Choice(pc);
		}
		public Choice[] newArray(int size) {
		    return new Choice[size];
		}
	};
	
	/** Reads back the fields IN THE ORDER they were written in writeToParcel(..) */
	public Choice(Parcel pc) {
		this.iId = pc.readInt();
		this.sTitle = pc.readString();
	}
	
	
}
