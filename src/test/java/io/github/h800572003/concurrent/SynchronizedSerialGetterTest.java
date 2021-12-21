package io.github.h800572003.concurrent;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;

import org.assertj.core.util.Lists;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.chungtsai.cmd.TestCmdService;
import io.github.chungtsai.cmd.TestCmdService.CmdRunnable;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class SynchronizedSerialGetterTest {

	private SynchronizedSerialGetter serialValueGetter = new SynchronizedSerialGetter(0, 999, 3, "0");
	private List<String> list = Lists.newArrayList();
	private Set<String> set = Sets.newHashSet();

	@BeforeEach
	void init() {
		list = Lists.newArrayList();
		set = Sets.newHashSet();
	}

	@Test
	void testLoopGetSerailequalSize() {
		TestCmdService.create()//
				.addCacheRepeat(new CmdRunnable("serialValueGetter", this::execute, 0, 0), 10)//
				.startJoin();//
		assertThat(list.size()).isEqualTo(10);
		assertThat(set.size()).isEqualTo(10);
	}
//
//	@Test
//	void testLoopGetSerailequalMore999() {
//		TestCmdService.create()//
//				.addCacheRepeat(new CmdRunnable("serialValueGetter", this::execute, 0, 0), 1000)//
//				.startJoin();//
//		assertThat(list.size()).isEqualTo(1000);
//		assertThat(set.size()).isEqualTo(1000);
//	}

	public void execute() {
		String value = serialValueGetter.getValue();
		list.add(value);
		set.add(value);
		log.info("value:{}", value);
	}

}
