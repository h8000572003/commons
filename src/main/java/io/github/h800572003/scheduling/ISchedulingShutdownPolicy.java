package io.github.h800572003.scheduling;

/**
 * 排程關閉逾時處理策略
 * <p>
 * down 時等待 closeTimeout 後執行緒池仍未終止才會呼叫，未設定時使用 {@link BlankSchedulingShutdownPolicy}，維持原有行為(不額外處理)
 *
 * @author andy tsai
 */
public interface ISchedulingShutdownPolicy {

	/**
	 * 關閉逾時處理
	 *
	 * @param context 逾時當下資訊與可執行操作
	 */
	void onTimeout(ISchedulingShutdownContext context);

	public static class BlankSchedulingShutdownPolicy implements ISchedulingShutdownPolicy {

		@Override
		public void onTimeout(ISchedulingShutdownContext context) {
			// 不執行額外行為
		}

	}
}
