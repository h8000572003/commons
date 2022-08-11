package io.github.h800572003.textcut;

import java.util.Collections;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
@EqualsAndHashCode
@ToString
public class TextLine {

	private List<String> lines;
	private int index;

	public TextLine(List<String> lines) {
		super();
		this.lines = lines;
	}

	public List<String> getLines() {
		return Collections.unmodifiableList(lines);
	}

	/**
	 * 取值
	 * 
	 * @return
	 */
	public String next() {
		if (index + 1 > lines.size()) {
			return "";
		} else {
			return lines.get(index++);
		}
	}

	/**
	 * 移動至第一項
	 * @return
	 */
	public String moveFirst() {
		this.index = 0;
		return this.next();
	}
	
}
