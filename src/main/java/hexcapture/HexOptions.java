package hexcapture;

import java.io.Serializable;

import main.options.Options;
import main.options.ValueClass;
import main.options.annotations.CommandLineArgument;
import main.options.domain.Bounds;
import main.options.domain.Values;

/**
 * Options for the script capturing the hexadecimal selection in Cheat Engine.
 *
 * @author Maxime PIA
 */
public class HexOptions extends Options implements Serializable {

	/**
	 * Backward-compatible with 0.7.0
	 */
	private static final long serialVersionUID = 00000000007000000L;

	public static final double DEFAULT_STABILIZATION_THRESHOLD = 0.005;
	public static final int DEFAULT_REFRESH_DELAY = 50;
	public static final int DEFAULT_HISTORY_SIZE = 6;
	public static final HexUpdateStrategies DEFAULT_UPDATE_STRATEGY =
		HexUpdateStrategies.COMBINED;

	@CommandLineArgument(
		command = "stabilization",
		description = "Determines at which proportion of the size of the "
			+ "selection the number of differences between the current content "
			+ "of the selection and that of the history is deemed low enough "
			+ "to convert the selection."
	)
	private Double stabilizationThreshold = DEFAULT_STABILIZATION_THRESHOLD;
	public static final Bounds<Double> STABILIZATION_THRESHOLD_DOMAIN =
		new Bounds<Double>(0d, 1d);

	@CommandLineArgument(
		command = "refresh",
		description = "The number of ms to wait before capturing the selection."
	)
	private Integer refreshDelay = DEFAULT_REFRESH_DELAY;
	public static final Bounds<Integer> REFRESH_DELAY_DOMAIN =
		new Bounds<>(10, 400);

	@CommandLineArgument(
		command = "history",
		description = "The length of the array containing the previous "
			+ "selections."
	)
	private Integer historySize = DEFAULT_HISTORY_SIZE;
	public static final Bounds<Integer> HISTORY_SIZE_DOMAIN =
		new Bounds<>(1, 20);

	@CommandLineArgument(
		command = "strategy",
		description = "The strategy to use to deem the selection worthy of "
			+ "being converted."
	)
	private HexUpdateStrategies updateStrategy = DEFAULT_UPDATE_STRATEGY;
	public static final Values<HexUpdateStrategies> UPDATE_STRATEGY_DOMAIN =
		new Values<>(HexUpdateStrategies.values());
	public static final
		Class<? extends ValueClass> UPDATE_STRATEGY_VALUE_CLASS =
			HexUpdateStrategies.class;

	public HexOptions() {
		super();
	}

	/**
	 * Getter on the stabilization threshold.
	 *
	 * @return The stabilization threshold.
	 */
	public synchronized double getStabilizationThreshold() {
		return stabilizationThreshold;
	}

	/**
	 * Setter on the stabilization threshold.
	 *
	 * @param stabilizationThreshold
	 * 			The new stabilization threshold.
	 */
	public synchronized void setStabilizationThreshold(
		double stabilizationThreshold) {

		this.stabilizationThreshold = stabilizationThreshold;
	}

	/**
	 * Getter on the refresh delay.
	 *
	 * @return The refresh delay.
	 */
	public synchronized int getRefreshDelay() {
		return refreshDelay;
	}

	/**
	 * Setter on the refresh delay.
	 *
	 * @param refreshDelay
	 * 			The new refresh delay.
	 */
	public synchronized void setRefreshDelay(int refreshDelay) {
		this.refreshDelay = refreshDelay;
	}

	/**
	 * Getter on the size of the history.
	 *
	 * @return The size of the history.
	 */
	public synchronized int getHistorySize() {
		return historySize;
	}

	/**
	 * Setter on the size of the history.
	 *
	 * @param historySize
	 * 			The new size of the history.
	 */
	public synchronized void setHistorySize(int historySize) {
		this.historySize = historySize;
	}

	/**
	 * Getter on the updating strategy.
	 *
	 * @return The updating strategy.
	 */
	public synchronized HexUpdateStrategies getUpdateStrategy() {
		return updateStrategy;
	}

	/**
	 * Setter on the updating strategy.
	 *
	 * @param updateStrategy
	 * 			The new updating strategy.
	 */
	public synchronized void setUpdateStrategy(
		HexUpdateStrategies updateStrategy) {

		this.updateStrategy = updateStrategy;
	}

}
