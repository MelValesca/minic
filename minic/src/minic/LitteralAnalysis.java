package minic;

import minic.language_minic.*;

import java.util.HashMap;
import java.util.Map;

public class LitteralAnalysis extends Walker {
	Map<NInt, Integer> values = new HashMap<>();

	@Override
	public void caseInt(NInt node) {
		int value = Integer.parseInt(node.getText());
		values.put(node, value);
	}
}
