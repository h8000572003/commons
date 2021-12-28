package io.github.h800572003.eventbuus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent.Kind;

import org.apache.commons.lang3.builder.ToStringBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileChangeEventSubscribe {
	@Subscribe
	public void handle(FileChangeEvent fileChangeEvent) {

		Kind<?> kinds = fileChangeEvent.getKinds();
		if (kinds.equals(StandardWatchEventKinds.ENTRY_MODIFY)) {
			Path path = fileChangeEvent.getPath();
			File file = path.toFile();
			boolean isOk = true;
			try (FileInputStream is = new FileInputStream(file)) {
				log.info("read");
				byte[] buffer = new byte[1024];
				while ((is.read(buffer)) != -1) {
					log.info("===>" + new String(buffer));
				}
			} catch (IOException e) {
				log.info("skip file name :{}", file.getName(), e);
				isOk = false;
			}
			if (isOk) {
				file.delete();
			}

		}
		log.info("get FileChangeEvent:{}", ToStringBuilder.reflectionToString(fileChangeEvent));
	}

}
