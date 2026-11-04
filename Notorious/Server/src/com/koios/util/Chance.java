package com.koios.util;

import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Handles a random chance.
 *
 * @param <T> - The representation type.
 * @author Michael | Chex
 */
public class Chance<T> {

	/**
	 * The random generator.
	 */
	private static final Random random = new Random();

	/**
	 * The list of weighted objects.
	 */
	private final List<WeightedRandomObject<T>> objects;

	/**
	 * The sum of the weights.
	 */
	private final int sum;

	/**
	 * Constructs a new {@link Chance} generator.
	 *
	 * @param objects - The weighted objects list.
	 */
	public Chance(List<WeightedRandomObject<T>> objects) {
		this.objects = objects;
		this.sum = objects.stream().mapToInt(WeightedRandomObject::getWeight).sum();
	}

	/**
	 * Represents a weighted object.
	 *
	 * @author Michael | Chex
	 */
	public interface WeightedRandomObject<T> {

		/**
		 * Gets the object's weight.
		 *
		 * @return The weight.
		 */
		public int getWeight();

		/**
		 * Gets the representation of the weighted chance.
		 *
		 * @return The representation.
		 */
		public T get();

	}

	/**
	 * An item with a specified weight.
	 *
	 * @author Michael | Chex
	 */
	public static class RepresentedWeightedObject<T> implements WeightedRandomObject<T> {

		/**
		 * The representation.
		 */
		private final T representation;

		/**
		 * The weighted chance.
		 */
		private final int weight;

		/**
		 * Constructs a new weighted object.
		 *
		 * @param weight - The object's weight.
		 * @param representation - The object's representation.
		 */
		public RepresentedWeightedObject(int weight, T representation) {
			this.representation = Objects.requireNonNull(representation);
			this.weight = weight;
		}

		@Override
		public int getWeight() {
			return weight;
		}

		@Override
		public T get() {
			return representation;
		}

	}

	/**
	 * Generates a {@link WeightedRandomObject}.
	 *
	 * @return The {@link WeightedRandomObject}.
	 */
	public WeightedRandomObject<T> nextObject() {
		int rnd = random.nextInt(sum);
		int hit = 0;

		for (WeightedRandomObject<T> obj : objects) {
			hit += obj.getWeight();

			if (hit > rnd) {
				return obj;
			}
		}

		throw new AssertionError();
	}

}
