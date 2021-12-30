package io.github.h800572003.machine;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NoBody implements IStatus {

	private String name;
	private String status;// 心情

	@Override
	public String toStatus() {
		return status;
	}

}
