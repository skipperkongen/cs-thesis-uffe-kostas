package dk.diku.robust.kp.gasolver;

import org.jgap.FitnessEvaluator;
import org.jgap.IChromosome;

public class RobustnessFitnessEvaluator implements FitnessEvaluator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//@Override
	public boolean isFitter(double first, double second) {
		return first > second;
	}

	//@Override
	public boolean isFitter(IChromosome a_chrom1, IChromosome a_chrom2) {
		return isFitter(a_chrom1.getFitnessValue(), a_chrom2.getFitnessValue());

	}
}
