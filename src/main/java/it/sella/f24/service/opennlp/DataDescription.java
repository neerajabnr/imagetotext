package it.sella.f24.service.opennlp;

public class DataDescription implements Comparable<DataDescription> {

	private String description;
	private String key;
	private int xStart;
	private int yStart;
	private int xEnd;
	private int yEnd;
	private int difference;
	private String section = "";

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public DataDescription(String description, int xStart, int yStart, int xEnd, int yEnd) {
		super();
		this.description = description;
		this.xStart = xStart;
		this.yStart = yStart;
		this.xEnd = xEnd;
		this.yEnd = yEnd;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getxStart() {
		return xStart;
	}

	public void setxStart(int xStart) {
		this.xStart = xStart;
	}

	public int getyStart() {
		return yStart;
	}

	public void setyStart(int yStart) {
		this.yStart = yStart;
	}

	public int getxEnd() {
		return xEnd;
	}

	public void setxEnd(int xEnd) {
		this.xEnd = xEnd;
	}

	public int getyEnd() {
		return yEnd;
	}

	public void setyEnd(int yEnd) {
		this.yEnd = yEnd;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + xEnd;
		result = prime * result + xStart;
		result = prime * result + yEnd;
		result = prime * result + yStart;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataDescription other = (DataDescription) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (xEnd != other.xEnd)
			return false;
		if (xStart != other.xStart)
			return false;
		if (yEnd != other.yEnd)
			return false;
		if (yStart != other.yStart)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DataDescription [description=" + description + ", key=" + key + ", xStart=" + xStart + ", yStart="
				+ yStart + ", xEnd=" + xEnd + ", yEnd=" + yEnd + "]";
	}

	@Override
	public int compareTo(DataDescription o) {
		int result = o.yEnd - this.yEnd;
		int xdiffval = 0;
		int xdifference = this.xStart - o.xStart;
		if (difference != 0)
			xdiffval = xdifference / difference;
		result = result + xdiffval;
		int result1=0;
		boolean ycomp = true;
		for(int i=o.yStart+xdiffval;i<o.yEnd+xdiffval;i++){
			for(int j=this.yStart;j<this.yEnd;j++) {
				if(i == j){
						result1 = Integer.compare(this.xStart, o.xStart);
						ycomp= false;
						break;
				}
			}
		}
		
		
//		if (result <= 5 && result >= -5) {
//			result1 = Integer.compare(this.xStart, o.xStart);
//		} else 
		
		if (ycomp){
			result1 = Integer.compare(this.yEnd, o.yEnd);
		}
		return result1;
	}

	public int getDifference() {
		return difference;
	}

	public void setDifference(int difference) {
		this.difference = difference;
	}

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

}
