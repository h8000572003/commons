package io.github.h800572003.machine;

import lombok.extern.slf4j.Slf4j;

/**
 * 【樂觀人】心情行為
 * 
 * @author andy tsai
 *
 */
@Slf4j
public class OptimismMoodbehavior extends StatusMachine<NoBody, NoBody> {

	public OptimismMoodbehavior() {
		IStatusActionHolder<NoBody, NoBody> sadAction = this.getStatusAction(FeelCodes.SAD);
		sadAction.register(ActionCodes.CRY, (i) -> {
			log.info("CRY");
			return i;
		});
		sadAction.register(ActionCodes.JUMP, (i) -> {
			log.info("NOT DONE");
			return i;
		});
		sadAction.register(ActionCodes.RUNN, (i) -> {
			log.info("NOT DONE");
			return i;
		});
		sadAction.register(ActionCodes.WALK, (i) -> {
			log.info("I fell ok >>HAPPY");
			i.setStatus(FeelCodes.HAPPY.name());
			return i;
		});

		IStatusActionHolder<NoBody, NoBody> happy = this.getStatusAction(FeelCodes.HAPPY);
		happy.register(ActionCodes.CRY, (i) -> {
			log.info("--");
			return i;
		});
		happy.register(ActionCodes.JUMP, (i) -> {
			log.info("HAPPY");
			return i;
		});
		happy.register(ActionCodes.RUNN, (i) -> {
			log.info("HAPPY");
			return i;
		});
		happy.register(ActionCodes.WALK, (i) -> {
			log.info("HAPPY");
			i.setStatus(FeelCodes.HAPPY.name());
			return i;
		});
		happy.register(ActionCodes.INJURIED, (i) -> {
			log.info("INJURIED >> SAD");
			i.setStatus(FeelCodes.SAD.name());
			return i;
		});
	}
}
