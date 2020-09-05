package net.aionstudios.jdc.server.stream;

import java.util.ArrayList;
import java.util.List;

public class StreamRange {

	private String rangeUnit;
	
	private List<Long[]> ranges;
	private long length;
	
	public StreamRange(String streamRange, long len) {
		length=len;
		ranges = new ArrayList<>();
		String[] unitRange = streamRange.split("=");
		rangeUnit = unitRange[0];
		
		String rng = unitRange[1].replaceAll("\\s+", "");
		String[] rngs = rng.split(",");
		for (String r : rngs) {
			r = " " + r + " ";
			Long[] ir = new Long[2];
			String[] ra = r.split("-");
			ra[0] = ra[0].replaceAll("\\s+", "");
			ra[1] = ra[1].replaceAll("\\s+", "");
			ir[0] = ra[0]!=null?(ra[0].length()>0 ? Long.parseLong(ra[0]) : null):null;
			ir[1] = ra[1]!=null?(ra[1].length()>0 ? Long.parseLong(ra[1]) : null):null;
			ranges.add(ir);
		}
	}
	
	public String getRangeUnit() {
		return rangeUnit;
	}
	
	public List<Long[]> getRanges() {
		return ranges;
	}
	
	public String generateContentRangeString() {
		String e = getRangeUnit() + " ";
		int next = 0;
		for (Long[] g : ranges) {
			if(next>0) {
				e = e + ",";
			}
			if (g[1]==null) {
				e = e + g[0] + "-" + (length-1);
			} else if (g[0]==null) {
				e = e + (length-g[1]) + "-" + (length-1);
			} else {
				e = e + g[0] + "-" + g[1];
			}
			next++;
		}
		return e+"/"+length;
	}
	
	public boolean validateRanges() {
		for (int i = 0; i < ranges.size(); i++) {
			for (int j = 0; j < ranges.size(); j++) {
				if (i!=j) {
					Long[] iRange = computeActualRange(ranges.get(i), length);
					Long[] jRange = computeActualRange(ranges.get(j), length);
					if (iRange[0]>=iRange[1] || jRange[0]>jRange[1]) return false;
					if (iRange[0]<=jRange[0] && jRange[0]<=iRange[1]) return false;
					if (iRange[0]<=jRange[1] && jRange[1]<=iRange[1]) return false;
				}
			}
		}
		return true;
	}
	
	private Long[] computeActualRange(Long[] range, long len) {
		Long[] newRange = new Long[2];
		if (range[1]==null) {
			newRange[0] = range[0];
			newRange[1] = len-1;
		} else if (range[0]==null) {
			newRange[0] = len-range[1];
			newRange[1] = len-1;
		} else {
			newRange[0] = range[0];
			newRange[1] = range[1];
		}
		return newRange;
	}
	
	public long getLength() {
		return length;
	}
	
}
