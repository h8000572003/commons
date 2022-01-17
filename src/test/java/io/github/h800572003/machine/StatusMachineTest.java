package io.github.h800572003.machine;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class StatusMachineTest {

	StatusMachine<NoBody, NoBody> moodbehavior = new OptimismMoodbehavior();

	@BeforeEach
	void inti() {

	}

	/**
	 * 當nobody 心情差，去走路_然後心情變好
	 */
	@Test
	void test_MaryFeel_IsSand_to_walk_then_feel_happy() {

		// GIVE
		NoBody nobody = new NoBody("MARK", FeelCodes.SAD.name());

		// WHEN
		this.moodbehavior.run(ActionCodes.WALK, nobody);

		// THEN
		assertThat(nobody.getStatus()).isEqualTo(FeelCodes.HAPPY.name());

	}

	/**
	 * 當nobody 心情好，when_受傷_然後變傷心
	 */
	@Test
	void test_nobody_give_happy_when_() {

		// GIVE
		NoBody nobody = new NoBody("MARK", FeelCodes.HAPPY.name());

		// WHEN
		this.moodbehavior.run(ActionCodes.INJURIED, nobody);

		// THEN
		assertThat(nobody.getStatus()).isEqualTo(FeelCodes.SAD.name());

	}

}
