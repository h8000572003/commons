package io.github.h800572003.textcut;

import java.util.Collections;
import java.util.List;

public class TextLine {

	List<String> lines;

	public TextLine(List<String> lines) {
		super();
		this.lines = lines;
	}

	public List<String> getLines() {
		return Collections.unmodifiableList(lines);
	}

}
