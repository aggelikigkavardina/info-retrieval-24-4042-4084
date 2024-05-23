package GUI;

import java.awt.Dimension;

public class Resolution {
	private String resolution;

	public Resolution(String resolution) {
		this.resolution = resolution;
	}

	public String getResolution() {
		return resolution;
	}

	public void setResolution(String resolution) {
		this.resolution = resolution;
	}
	
	public Dimension changeResolution(String resolution) {
		String[] dimensions = resolution.split("x");
        int width = Integer.parseInt(dimensions[0]);
        int height = Integer.parseInt(dimensions[1]);
        setResolution(resolution);
        return new Dimension(width, height);
	}
}
